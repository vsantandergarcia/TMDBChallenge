package com.vsantander.tmdbchallenge.data.remote.mapper

import com.vsantander.tmdbchallenge.data.remote.model.MovieTO
import com.vsantander.tmdbchallenge.domain.model.Movie
import javax.inject.Inject

class MovieTOMapper @Inject constructor() {

    fun toEntity(value: MovieTO): Movie = Movie(
            id = value.id,
            title = value.title,
            overview = value.overview,
            backdropPath = value.backdropPath,
            posterPath = value.posterPath,
            year = value.releaseDate.split("-")[0])

    fun toEntity(values: List<MovieTO>): List<Movie> = values.map { toEntity(it) }
}