package com.vsantander.tmdbchallenge.repository

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.DataSource
import com.vsantander.tmdbchallenge.domain.model.Movie

class FakeDataSourceFactory<T>(var items: List<T>) : DataSource.Factory<Int, T>() {
    val sourceLiveData = MutableLiveData<FakeDataSource<T>>()

    override fun create(): DataSource<Int, T> {
        val source = FakeDataSource(items)
        sourceLiveData.postValue(source)
        return source
    }

}