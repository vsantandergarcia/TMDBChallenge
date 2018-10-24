package com.vsantander.tmdbchallenge.data.repository

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PagedList
import android.support.annotation.MainThread
import com.vsantander.tmdbchallenge.data.persistence.dao.MovieDao
import com.vsantander.tmdbchallenge.data.remote.RestClient
import com.vsantander.tmdbchallenge.data.remote.mapper.MovieTOMapper
import com.vsantander.tmdbchallenge.domain.model.Movie
import com.vsantander.tmdbchallenge.domain.model.ResourceState
import com.vsantander.tmdbchallenge.utils.extension.logd
import com.vsantander.tmdbchallenge.utils.extension.loge
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy

class PopularMoviesBoundaryCallback(
        private val itemsPerPage: Int,
        private val restClient: RestClient,
        private val dao: MovieDao,
        private val handleResponse: (List<Movie>?) -> Unit,
        private val mapper: MovieTOMapper,
        private val backgroundScheduler: Scheduler
) : PagedList.BoundaryCallback<Movie>() {

    val resourceState = MutableLiveData<ResourceState>()

    private var retryCompletable: Completable? = null
    private var reachEnd: Boolean = false

    /**
     * Database returned 0 items. We should query the remote source for more items.
     */
    @MainThread
    override fun onZeroItemsLoaded() {
        resourceState.postValue(ResourceState.INITIAL_LOADING)
        Single.fromCallable { (dao.getNextIndex() / itemsPerPage) + 1 }
                .subscribeOn(backgroundScheduler)
                .flatMap { restClient.getPopularMovies(it) }
                .map { mapper.toEntity(it.results) }
                .subscribeBy(
                        onSuccess = {
                            logd("onZeroItemsLoaded.onSuccess")
                            /**
                             * every time it gets new items, boundary callback simply inserts them into the database and
                             * paging library takes care of refreshing the list if necessary.
                             */
                            handleResponse.invoke(it)
                            resourceState.postValue(ResourceState.LOADED)
                        },
                        onError = {
                            loge("onZeroItemsLoaded.onError", it)
                            retryCompletable = Completable.fromAction { onZeroItemsLoaded() }
                            resourceState.postValue(ResourceState.error(it))
                        }
                )
    }

    /**
     * User reached at the end of the list.
     */
    @MainThread
    override fun onItemAtEndLoaded(itemAtEnd: Movie) {
        if (!reachEnd) {
            resourceState.postValue(ResourceState.NEXT_LOADING)
            Single.fromCallable { dao.getNextIndex() }
                    .subscribeOn(backgroundScheduler)
                    .map { (dao.getNextIndex() / itemsPerPage) + 1 }
                    .flatMap { restClient.getPopularMovies(it) }
                    .subscribeBy(
                            onSuccess = { response->
                                logd("onItemAtEndLoaded.onSuccess")
                                handleResponse.invoke(response.results.map { mapper.toEntity(it) })
                                when {
                                    response.totalPages == response.page -> reachEnd = true
                                }
                                resourceState.postValue(ResourceState.LOADED)
                            },
                            onError = {
                                loge("onItemAtEndLoaded.onError", it)
                                retryCompletable = Completable.fromAction { onItemAtEndLoaded(itemAtEnd) }
                                resourceState.postValue(ResourceState.error(it))
                            }
                    )
        }
    }

    override fun onItemAtFrontLoaded(itemAtFront: Movie) {
        // ignored, since we only ever append to what's in the DB
    }

    fun retry() = retryCompletable?.observeOn(backgroundScheduler)?.subscribe { }
}