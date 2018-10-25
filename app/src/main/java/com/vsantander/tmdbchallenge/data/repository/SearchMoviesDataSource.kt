package com.vsantander.tmdbchallenge.data.repository

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PositionalDataSource
import com.vsantander.tmdbchallenge.data.remote.RestClient
import com.vsantander.tmdbchallenge.data.remote.mapper.MovieTOMapper
import com.vsantander.tmdbchallenge.domain.model.Movie
import com.vsantander.tmdbchallenge.domain.model.ResourceState
import com.vsantander.tmdbchallenge.utils.extension.logd
import com.vsantander.tmdbchallenge.utils.extension.loge
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy

class SearchMoviesDataSource(
        private val search: String,
        private val itemsPerPage: Int,
        private val restClient: RestClient,
        private val movieTOMapper: MovieTOMapper,
        private val backgroundScheduler: Scheduler
) : PositionalDataSource<Movie>() {

    val resourceState = MutableLiveData<ResourceState>()

    var disposables = CompositeDisposable()

    private var retryCompletable: Completable? = null
    private var reachEnd: Boolean = false

    /**
     * Load initial list data. This method is called to load the initial page(s) from the DataSource.
     */
    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<Movie>) {
        resourceState.postValue(ResourceState.LOADING)
        disposables += restClient.getSearchMovies(search, 1)
                .subscribeOn(backgroundScheduler)
                .subscribeBy(
                        onSuccess = {
                            logd("loadInitial.onSuccess")
                            if (it.totalPages == it.page) reachEnd = true
                            resourceState.postValue(ResourceState.LOADED)
                            callback.onResult(movieTOMapper.toEntity(it.results), 0, it.results.size)
                        },
                        onError = {
                            loge("loadInitial.onError", it)
                            resourceState.postValue(ResourceState.error(it))
                            retryCompletable = Completable.fromAction {
                                loadInitial(params, callback)
                            }
                        }
                )
    }

    /**
     * Called to load a range of data from the DataSource.
     * This method is called to load additional pages from the DataSource after the LoadInitialCallback
     * passed to dispatchLoadInitial has initialized a PagedList.
     */
    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<Movie>) {
        val nextPage = (params.startPosition / itemsPerPage) + 1
        resourceState.postValue(ResourceState.LOADING)
        disposables += restClient.getSearchMovies(search, nextPage)
                .subscribeOn(backgroundScheduler)
                .subscribeBy(
                        onSuccess = {
                            logd("loadInitial.onSuccess")
                            if (it.totalPages == it.page) reachEnd = true
                            resourceState.postValue(ResourceState.LOADED)
                            callback.onResult(movieTOMapper.toEntity(it.results))
                        },
                        onError = {
                            loge("loadInitial.onError", it)
                            resourceState.postValue(ResourceState.error(it))
                            retryCompletable = Completable.fromAction {
                                loadRange(params, callback)
                            }
                        }
                )
    }

    fun retry() = retryCompletable?.observeOn(backgroundScheduler)?.subscribe { }
}