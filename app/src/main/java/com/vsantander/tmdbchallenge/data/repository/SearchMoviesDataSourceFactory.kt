package com.vsantander.tmdbchallenge.data.repository

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.DataSource
import com.vsantander.tmdbchallenge.data.remote.RestClient
import com.vsantander.tmdbchallenge.data.remote.mapper.MovieTOMapper
import com.vsantander.tmdbchallenge.domain.model.Movie
import io.reactivex.Scheduler

class SearchMoviesDataSourceFactory(
        private val search: String,
        private val itemsPerPage: Int,
        private val restClient: RestClient,
        private val movieTOMapper: MovieTOMapper,
        private val backgroundScheduler: Scheduler
) : DataSource.Factory<Int, Movie>() {

    val sourceLiveData = MutableLiveData<SearchMoviesDataSource>()

    override fun create(): DataSource<Int, Movie> {

        sourceLiveData.value?.disposables?.clear()

        val source = SearchMoviesDataSource(
                search, itemsPerPage, restClient, movieTOMapper, backgroundScheduler)
        sourceLiveData.postValue(source)
        return source
    }

}