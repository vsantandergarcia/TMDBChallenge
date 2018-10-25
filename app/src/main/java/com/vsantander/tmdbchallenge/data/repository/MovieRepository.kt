package com.vsantander.tmdbchallenge.data.repository

import com.vsantander.tmdbchallenge.domain.model.Listing
import com.vsantander.tmdbchallenge.domain.model.Movie
import io.reactivex.Scheduler

interface MovieRepository {

    /**
     * Gets a [Listing] of Movie
     *
     * @param itemsPerPage The number of items that we want to retrieve
     * @param backgroundScheduler The scheduler of background processing
     * @return [Listing]  a Listing wrapper with the Movie popular information.
     */
    fun getPopularMovies(itemsPerPage: Int,
                         backgroundScheduler: Scheduler): Listing<Movie>

    /**
     * Gets a [Listing] of Movie by search
     *
     * @param search The filter words
     * @param itemsPerPage The number of items that we want to retrieve
     * @param backgroundScheduler The scheduler of background processing
     * @return [Listing]  a Listing wrapper with the Movie search information.
     */
    fun getSearchMovies(
            search: String,
            itemsPerPage: Int,
            prefetchDistance: Int,
            backgroundScheduler: Scheduler): Listing<Movie>
}