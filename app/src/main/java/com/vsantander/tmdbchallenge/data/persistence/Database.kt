package com.vsantander.tmdbchallenge.data.persistence

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.vsantander.tmdbchallenge.data.persistence.dao.MovieDao
import com.vsantander.tmdbchallenge.data.persistence.model.MovieEntity

@Database(entities = [(MovieEntity::class)], version = 1, exportSchema = false)
abstract class Database : RoomDatabase() {

    abstract fun movieDao(): MovieDao

}