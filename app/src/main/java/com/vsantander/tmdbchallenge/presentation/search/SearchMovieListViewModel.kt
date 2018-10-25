package com.vsantander.tmdbchallenge.presentation.search

import android.arch.lifecycle.MutableLiveData
import com.vsantander.tmdbchallenge.data.repository.MovieRepositoryImpl
import com.vsantander.tmdbchallenge.domain.model.Listing
import com.vsantander.tmdbchallenge.domain.model.Movie
import com.vsantander.tmdbchallenge.presentation.base.viewmodel.BaseViewModel
import com.vsantander.tmdbchallenge.utils.Constants
import com.vsantander.tmdbchallenge.utils.extension.logd
import com.vsantander.tmdbchallenge.utils.extension.loge
import com.vsantander.tmdbchallenge.utils.extension.switchMap
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SearchMovieListViewModel @Inject constructor(
        private val repository: MovieRepositoryImpl
): BaseViewModel() {

    companion object {
        private const val DEBOUNCE_TIMEOUT = 300
    }

    private val repoResult = MutableLiveData<Listing<Movie>>()
    private val searchPublishSubject = PublishSubject.create<String>()

    val searchMovies = repoResult.switchMap { it.pagedList }

    val resourceState = repoResult.switchMap { it.resourceState }

    fun retry() = repoResult.value?.retry?.invoke()

    fun onSearchInputStateChanged(query: String) {
        searchPublishSubject.onNext(query)
    }

    fun setUpSearchAsObserver() {
        searchPublishSubject
                .debounce(DEBOUNCE_TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .map {
                    repository.getSearchMovies(it,
                            Constants.NUMBER_OF_ITEMS_PER_PAGE,
                            Constants.NUMBER_OF_ITEMS_PREFETCH,
                            Schedulers.io())
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onNext = {
                            logd("searchPublishSubject.onSuccess")
                            repoResult.value = it
                        },
                        onError = { loge("searchPublishSubject.onError",it) },
                        onComplete = { logd("searchPublishSubject.onComplete") }
                )
    }

}