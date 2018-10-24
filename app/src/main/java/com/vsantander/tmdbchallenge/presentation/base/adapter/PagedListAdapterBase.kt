package com.vsantander.tmdbchallenge.presentation.base.adapter

import android.arch.paging.PagedListAdapter
import android.support.v7.util.DiffUtil
import android.view.View
import android.view.ViewGroup

abstract class PagedListAdapterBase<T>(diffCallback: DiffUtil.ItemCallback<T>) :
        PagedListAdapter<T, ViewWrapper<View>>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewWrapper<View> =
            ViewWrapper(onCreateItemView(parent, viewType))

    /**
     * Create the view that will be used
     */
    abstract fun onCreateItemView(parent: ViewGroup, viewType: Int): View

}