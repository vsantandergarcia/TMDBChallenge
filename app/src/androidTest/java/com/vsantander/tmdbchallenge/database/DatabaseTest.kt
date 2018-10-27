package com.vsantander.tmdbchallenge.database

import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.vsantander.tmdbchallenge.data.persistence.Database
import com.vsantander.tmdbchallenge.data.persistence.model.MovieEntity
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*
import java.util.UUID.randomUUID

@RunWith(AndroidJUnit4::class)
class DatabaseTest {

    companion object {
        private const val NUMBER_ITEMS = 5

        private val items = (1..NUMBER_ITEMS).map {
            MovieEntity(
                    id = Random().nextInt(),
                    title = randomUUID().toString(),
                    overview = java.util.UUID.randomUUID().toString(),
                    backdropPath = java.util.UUID.randomUUID().toString(),
                    posterPath = java.util.UUID.randomUUID().toString(),
                    year = java.util.UUID.randomUUID().toString()
            ).apply {
                indexInResponse = it
            }
        }
    }

    private lateinit var db: Database

    @Before
    fun initDb() {
        db = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                Database::class.java).build()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(InterruptedException::class)
    fun insertListOk() {
        db.movieDao().insertList(items)

        val index = db.movieDao().getNextIndex()
        MatcherAssert.assertThat(index, CoreMatchers.`is`(NUMBER_ITEMS + 1))
    }

    @Test
    @Throws(InterruptedException::class)
    fun deleteAllOk() {
        db.movieDao().insertList(items)

        db.movieDao().deleteAll()

        val index = db.movieDao().getNextIndex()
        MatcherAssert.assertThat(index, CoreMatchers.`is`(0))
    }

}