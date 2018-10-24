package com.vsantander.tmdbchallenge.presentation.list

import com.vsantander.tmdbchallenge.data.repository.MovieRepositoryImpl
import com.vsantander.tmdbchallenge.domain.model.Movie
import com.vsantander.tmdbchallenge.presentation.base.viewmodel.BaseViewModel
import com.vsantander.tmdbchallenge.utils.extension.logd
import com.vsantander.tmdbchallenge.utils.extension.loge
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MovieListViewModel @Inject constructor(
        private val movieRepository: MovieRepositoryImpl
): BaseViewModel() {

    fun loadPopularMovies() {

        disposables += movieRepository.getPopularMoviesNoPagination()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onSuccess = {
                            logd("loadPopularMovies.onSuccess")
                            saveMovies(it)
                        },
                        onError = {
                            loge("loadPopularMovies.onError", it)
                        }
                )
    }

    private fun saveMovies(movies: List<Movie>) {

        disposables += movieRepository.saveMovies(movies)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onComplete = {
                            logd("saveMovies.onSuccess")
                        },
                        onError = {
                            loge("saveMovies.onError", it)
                        }
                )
    }
}