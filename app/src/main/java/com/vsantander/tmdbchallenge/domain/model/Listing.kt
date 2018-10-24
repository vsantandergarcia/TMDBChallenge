package com.vsantander.tmdbchallenge.domain.model

import android.arch.lifecycle.LiveData
import android.arch.paging.PagedList

/**
 * Wrapper that contains all pagination info
 */
data class Listing<T>(
        // the LiveData of paged lists for the UI to observe
        val pagedList: LiveData<PagedList<T>>,
        // is the current network request status
        val resourceState: LiveData<ResourceState>,
        // is the refresh status to show to the user
        val refreshState: LiveData<ResourceState>,
        // function that refresh the whole data
        val refresh: () -> Unit,
        // function that retry any failed request
        val retry: () -> Unit
)