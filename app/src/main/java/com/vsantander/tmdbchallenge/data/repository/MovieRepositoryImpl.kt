package com.vsantander.tmdbchallenge.data.repository

import com.vsantander.tmdbchallenge.data.persistence.Database
import com.vsantander.tmdbchallenge.data.persistence.mapper.MovieEntityMapper
import com.vsantander.tmdbchallenge.data.remote.RestClient
import com.vsantander.tmdbchallenge.data.remote.mapper.MovieTOMapper
import com.vsantander.tmdbchallenge.domain.model.Movie
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepositoryImpl @Inject constructor(
        private val restClient: RestClient,
        private val movieTOMapper: MovieTOMapper,
        private val database: Database,
        private val movieEntityMapper: MovieEntityMapper
) : MovieRepository {

    /**
     * Gets a list of popular Movies
     *
     * @return a List of the popular Movies available.
     */
    override fun getPopularMoviesNoPagination(): Single<List<Movie>> =
            restClient.getPopularMovies(1)
                    .map { movieTOMapper.toEntity(it.results) }

    override fun saveMovies(movies: List<Movie>): Completable = Completable.fromCallable {
        database.movieDao().insertList(movies.map { movieEntityMapper.toEntity(it) })
    }

}