package com.vsantander.tmdbchallenge.utils.factory

import com.vsantander.tmdbchallenge.domain.model.Movie

/**
 * Factory class for Movie related instances
 */
class MovieFactory {

    companion object {

        fun makeMovieList(count: Int): List<Movie> {
            val breedList = mutableListOf<Movie>()
            repeat(count) {
                breedList.add(makeMovieModel())
            }
            return breedList
        }

        fun makeMovieModel(): Movie {
            return Movie(
                    id = DataFactory.randomInt(),
                    title = DataFactory.randomUuid(),
                    overview = DataFactory.randomUuid(),
                    backdropPath = DataFactory.randomUuid(),
                    posterPath = DataFactory.randomUuid(),
                    year = DataFactory.randomUuid()
            )
        }
    }

}