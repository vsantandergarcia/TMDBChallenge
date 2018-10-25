package com.vsantander.tmdbchallenge.data.persistence.mapper

import com.vsantander.tmdbchallenge.data.persistence.model.MovieEntity
import com.vsantander.tmdbchallenge.domain.model.Movie
import javax.inject.Inject

class MovieEntityMapper @Inject constructor() {

    fun fromEntity(value: MovieEntity): Movie = Movie(
            id = value.id,
            title = value.title,
            overview = value.overview,
            backdropPath = value.backdropPath,
            posterPath = value.posterPath,
            year = value.year)

    fun fromEntity(values: List<MovieEntity>): List<Movie> = values.map { fromEntity(it) }

    fun toEntity(value: Movie): MovieEntity = MovieEntity(
            id = value.id,
            title = value.title,
            overview = value.overview,
            backdropPath = value.backdropPath,
            posterPath = value.posterPath,
            year = value.year)

    fun toEntity(values: List<Movie>): List<MovieEntity> = values.map { toEntity(it) }

}