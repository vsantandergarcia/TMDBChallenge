package com.vsantander.tmdbchallenge.utils.factory

import com.vsantander.tmdbchallenge.data.persistence.model.MovieEntity

/**
 * Factory class for MovieEntity related instances
 */
class MovieEntityFactory {

    companion object {

        fun makeMovieEntityList(count: Int): List<MovieEntity> {
            val breedList = mutableListOf<MovieEntity>()
            repeat(count) {
                breedList.add(makeMovieEntityModel())
            }
            return breedList
        }

        fun makeMovieEntityModel(): MovieEntity {
            return MovieEntity(
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