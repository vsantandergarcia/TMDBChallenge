package com.vsantander.tmdbchallenge.data.remote.model

import com.google.gson.annotations.SerializedName

data class DefaultResponse<T> (
        @SerializedName("page")
        val page: Int,

        @SerializedName("total_results")
        val totalResults: Int,

        @SerializedName("total_pages")
        val totalPages: Int,

        @SerializedName("results")
        val results: List<T>
)