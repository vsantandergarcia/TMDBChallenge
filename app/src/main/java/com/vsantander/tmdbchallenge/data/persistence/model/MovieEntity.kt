package com.vsantander.tmdbchallenge.data.persistence.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "movie")
data class MovieEntity(
        @PrimaryKey
        val id: Int,
        @ColumnInfo(name = "title")
        val title: String,
        @ColumnInfo(name = "overview")
        val overview: String?,
        @ColumnInfo(name = "backdropPath")
        val backdropPath: String?,
        @ColumnInfo(name = "posterPath")
        val posterPath: String?,
        @ColumnInfo(name = "year")
        val year: String?
) {
    // To be consistent with changing backend order, we need to keep data like this
    var indexInResponse: Int = -1
}