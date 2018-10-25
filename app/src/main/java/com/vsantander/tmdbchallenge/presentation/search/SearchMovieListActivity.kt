package com.vsantander.tmdbchallenge.presentation.search

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import com.vsantander.tmdbchallenge.R
import com.vsantander.tmdbchallenge.presentation.adapter.MoviesPagedAdapter
import com.vsantander.tmdbchallenge.presentation.base.activity.BaseActivity
import com.vsantander.tmdbchallenge.presentation.detail.MovieDetailsActivity
import com.vsantander.tmdbchallenge.utils.extension.textWatcherOnArfterTextChanged
import kotlinx.android.synthetic.main.activity_search_movie_list.*
import org.jetbrains.anko.startActivity
import javax.inject.Inject

@BaseActivity.Animation(BaseActivity.MODAL)
class SearchMovieListActivity: BaseActivity() {

    companion object {
        private const val LIST_SPAN_COUNT = 2
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: SearchMovieListViewModel

    private lateinit var adapter: MoviesPagedAdapter

    /* Activity methods */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_movie_list)
        setUpViews()
        setUpViewModels()
    }

    /* setUp methods */

    private fun setUpViews() {
        search.textWatcherOnArfterTextChanged {
        }

        adapter = MoviesPagedAdapter {
            startActivity<MovieDetailsActivity>(Pair(MovieDetailsActivity.EXTRA_MOVIE, it))
        }
        recyclerView.apply {
            layoutManager = GridLayoutManager(context,
                    LIST_SPAN_COUNT) as RecyclerView.LayoutManager
            adapter = this@SearchMovieListActivity.adapter
        }

        closeButton.setOnClickListener { finish() }
    }

    private fun setUpViewModels() {
        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(SearchMovieListViewModel::class.java)

    }
}