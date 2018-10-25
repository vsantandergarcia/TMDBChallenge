package com.vsantander.tmdbchallenge.presentation.adapter

import android.arch.lifecycle.Observer
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.vsantander.tmdbchallenge.domain.model.Movie
import com.vsantander.tmdbchallenge.domain.model.ResourceState
import com.vsantander.tmdbchallenge.presentation.base.adapter.PagedListAdapterBase
import com.vsantander.tmdbchallenge.presentation.base.adapter.ViewWrapper
import com.vsantander.tmdbchallenge.presentation.item.MovieItem

class MoviesPagedAdapter (private val onItemClick: (movie: Movie) -> Unit
) : PagedListAdapterBase<Movie>(diffCallback) {
    private var hasExtraRow = false

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.layoutManager!!.canScrollVertically()
    }

    // Create regular MovieItem or ProgressBar that indicates that we are loading more info
    override fun onCreateItemView(parent: ViewGroup, viewType: Int): View {
        return when (viewType) {
            VIEW_TYPE_PROGRESS -> ProgressBar(parent.context)
            VIEW_TYPE_MOVIE -> MovieItem(parent.context)
            else -> throw RuntimeException("bad type view")
        }.apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    override fun onBindViewHolder(holder: ViewWrapper<View>, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_MOVIE) {
            (holder.view as MovieItem).apply {
                val item = getItem(position)
                if (item == null) {
                    setOnClickListener(null)
                } else {
                    setOnClickListener { onItemClick(item) }
                    bind(item)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow && position == itemCount - 1) VIEW_TYPE_PROGRESS else VIEW_TYPE_MOVIE
    }

    override fun getItemCount(): Int = super.getItemCount() + if (hasExtraRow) 1 else 0

    // Observe changes in resource state. So if it starts loading for more objects we set the extra row with the
    // progress bar. And when it finish we remove it from the bottom.
    val resourceStateObserver = Observer<ResourceState> {
        it?.let { state ->
            hasExtraRow = when (state) {
                ResourceState.NEXT_LOADING -> {
                    notifyItemInserted(super.getItemCount()); true
                }
                else -> {
                    if (hasExtraRow) notifyItemRemoved(itemCount); false
                }
            }
        }
    }

    companion object {
        private const val VIEW_TYPE_PROGRESS = 1
        private const val VIEW_TYPE_MOVIE = 2

        private val diffCallback = object : DiffUtil.ItemCallback<Movie>() {

            // Lets assume that the id is a unique identifier
            override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean =
                    oldItem.id == newItem.id

            // Repo is a data class so it has predefined equals method where each
            // field is used to define if two objects are equals
            override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean =
                    oldItem == newItem
        }
    }
}