package com.vsantander.tmdbchallenge.data.remote.model

import com.google.gson.annotations.SerializedName

data class MovieTO (
        @SerializedName("id")
        val id: Int,

        @SerializedName("video")
        val video: Boolean,

        @SerializedName("title")
        val title: String,

        @SerializedName("genre_ids")
        val genreIds: List<Int>,

        @SerializedName("popularity")
        val popularity: Float,

        @SerializedName("original_title")
        val originalTitle: String,

        @SerializedName("vote_count")
        val voteCount: Int,

        @SerializedName("vote_average")
        val voteAverage: Float,

        @SerializedName("adult")
        val adult: Boolean,

        @SerializedName("original_language")
        val originalLanguage: String,

        @SerializedName("overview")
        val overview: String,

        @SerializedName("backdrop_path")
        val backdropPath: String,

        @SerializedName("poster_path")
        val posterPath: String,

        @SerializedName("release_date")
        val releaseDate: String
)