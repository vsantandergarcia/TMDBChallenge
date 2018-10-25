package com.vsantander.tmdbchallenge.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Movie (
        val id: Int,
        val title: String,
        val overview: String?,
        val backdropPath: String?,
        val posterPath: String?,
        val year: String?
) : Parcelable