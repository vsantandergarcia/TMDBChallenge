package com.vsantander.tmdbchallenge.domain.model


data class ResourceState constructor(
        val status: Status, val msg: String? = null
) {
    companion object {
        val LOADED = ResourceState(Status.SUCCESS)
        val LOADING = ResourceState(Status.LOADING)

        // to use on PagedList.BoundaryCallback
        val INITIAL_LOADING = ResourceState(Status.INITIAL_LOADING)
        val NEXT_LOADING = ResourceState(Status.NEXT_LOADING)

        fun error(msg: String?) = ResourceState(Status.FAILED, msg)
        fun error(throwable: Throwable) = ResourceState(Status.FAILED, throwable.message)
    }
}