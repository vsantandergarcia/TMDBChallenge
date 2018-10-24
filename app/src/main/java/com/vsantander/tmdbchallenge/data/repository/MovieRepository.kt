package com.vsantander.tmdbchallenge.data.repository

import com.vsantander.tmdbchallenge.domain.model.Movie
import io.reactivex.Completable
import io.reactivex.Single

interface MovieRepository {

    fun getPopularMoviesNoPagination(): Single<List<Movie>>

    fun saveMovies(movies: List<Movie>): Completable

}