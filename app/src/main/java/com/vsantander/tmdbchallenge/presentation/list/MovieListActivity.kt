package com.vsantander.tmdbchallenge.presentation.list

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import androidx.core.view.isVisible
import com.vsantander.tmdbchallenge.R
import com.vsantander.tmdbchallenge.domain.model.ResourceState
import com.vsantander.tmdbchallenge.domain.model.Status
import com.vsantander.tmdbchallenge.presentation.base.activity.BaseActivity
import com.vsantander.tmdbchallenge.presentation.detail.MovieDetailsActivity
import com.vsantander.tmdbchallenge.presentation.list.adapter.PopularMoviesPagedAdapter
import com.vsantander.tmdbchallenge.utils.extension.logd
import com.vsantander.tmdbchallenge.utils.extension.observe
import kotlinx.android.synthetic.main.activity_movie_list.*
import org.jetbrains.anko.startActivity
import javax.inject.Inject

@BaseActivity.Animation(BaseActivity.FADE)
class MovieListActivity : BaseActivity() {

    companion object {
        private const val LIST_SPAN_COUNT = 2
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: MovieListViewModel

    private lateinit var adapter: PopularMoviesPagedAdapter

    /* Activity methods */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_list)
        setUpViews()
        setUpViewModels()
    }

    /* setUp methods */

    private fun setUpViews() {
        setUpToolbar()

        adapter = PopularMoviesPagedAdapter {
            logd("item movie click with id:${it.id}")
            startActivity<MovieDetailsActivity>(Pair(MovieDetailsActivity.EXTRA_MOVIE, it))
        }

        swipeRefreshLayout.setOnRefreshListener { viewModel.refresh() }
        recyclerView.apply {
            layoutManager = GridLayoutManager(context,
                    LIST_SPAN_COUNT) as RecyclerView.LayoutManager
            adapter = this@MovieListActivity.adapter
        }
    }

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    private fun setUpViewModels() {
        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(MovieListViewModel::class.java)

        viewModel.popularMovies.observe(this) {
            adapter.submitList(it)
        }
        viewModel.resourceState.observe(this, adapter.resourceStateObserver)
        viewModel.resourceState.observe(this) { resourceState ->
            resourceState ?: return@observe
            progressBar.isVisible = (resourceState == ResourceState.INITIAL_LOADING)

            if (resourceState.status == Status.FAILED) {
                Snackbar.make(recyclerView, R.string.network_error,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.retry) { viewModel.retry() }
                        .show()
            }
        }
        viewModel.refreshState.observe(this) { resourceState ->
            swipeRefreshLayout.isRefreshing = resourceState == ResourceState.LOADING
            if (resourceState?.status == Status.FAILED) {
                Snackbar.make(recyclerView, R.string.network_error, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.retry) { viewModel.retry() }
                        .show()
            }
        }
    }

}