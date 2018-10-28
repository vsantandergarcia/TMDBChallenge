package com.vsantander.tmdbchallenge.repository

import android.arch.paging.PositionalDataSource

class FakeDataSource<T>(var items: List<T>) : PositionalDataSource<T>() {
    override fun loadInitial(params: PositionalDataSource.LoadInitialParams,
                             callback: PositionalDataSource.LoadInitialCallback<T>) {
        val totalCount = items.size

        val position = PositionalDataSource.computeInitialLoadPosition(params, totalCount)
        val loadSize = PositionalDataSource.computeInitialLoadSize(params, position, totalCount)

        // for simplicity, we could return everything immediately
        val sublist = items.subList(position, position + loadSize)
        callback.onResult(sublist, position, totalCount)
    }

    override fun loadRange(params: PositionalDataSource.LoadRangeParams,
                           callback: PositionalDataSource.LoadRangeCallback<T>) {
        callback.onResult(items.subList(params.startPosition,
                params.startPosition + params.loadSize))
    }
}