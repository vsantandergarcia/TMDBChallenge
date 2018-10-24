package com.vsantander.tmdbchallenge.data.persistence.dao

import android.arch.paging.DataSource
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.vsantander.tmdbchallenge.data.persistence.model.MovieEntity

@Dao
interface MovieDao {

    /**
     * Inserts a list of MovieEntity in the database. If some of those conflict, we assume that new info has just
     * arrived, so we replace it.
     *
     * @param movieList The list of MovieEntity
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertList(movieList: List<MovieEntity>)

    /**
     * Gets a [DataSource.Factory] of [MovieEntity] that it automatically handles how to provide specific items to the UI.
     *
     * @return A reactive [MovieEntity] data source factory¡
     */
    @Query("SELECT * FROM movie ORDER BY indexInResponse ASC")
    fun movieDataFactory(): DataSource.Factory<Int, MovieEntity>

    /**
     * Gets the next index that can be used to insert in the [MovieEntity] table in the database
     *
     * @return The next index to use in [MovieEntity] table
     */
    @Query("SELECT MAX(indexInResponse) + 1 FROM movie")
    fun getNextIndex(): Int

    /**
     * Delete all movie in database
     */
    @Query("delete FROM movie")
    fun deleteAll()
}