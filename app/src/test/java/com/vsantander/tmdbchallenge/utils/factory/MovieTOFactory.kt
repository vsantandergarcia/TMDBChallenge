package com.vsantander.tmdbchallenge.utils.factory

import com.vsantander.tmdbchallenge.data.remote.model.MovieTO

/**
 * Factory class for MovieTO related instances
 */
class MovieTOFactory {

    companion object {

        fun makeMovieTOList(count: Int): List<MovieTO> {
            val breedList = mutableListOf<MovieTO>()
            repeat(count) {
                breedList.add(makeMovieTOModel())
            }
            return breedList
        }

        fun makeMovieTOModel(): MovieTO {
            return MovieTO(
                    id = DataFactory.randomInt(),
                    video = DataFactory.randomBoolean(),
                    title = DataFactory.randomUuid(),
                    genreIds = listOf(DataFactory.randomInt()),
                    popularity = DataFactory.randomFloat(),
                    originalTitle = DataFactory.randomUuid(),
                    voteCount = DataFactory.randomInt(),
                    voteAverage = DataFactory.randomFloat(),
                    adult = DataFactory.randomBoolean(),
                    originalLanguage = DataFactory.randomUuid(),
                    overview = DataFactory.randomUuid(),
                    backdropPath = DataFactory.randomUuid(),
                    posterPath = DataFactory.randomUuid(),
                    releaseDate = DataFactory.randomUuid()
            )
        }
    }

}