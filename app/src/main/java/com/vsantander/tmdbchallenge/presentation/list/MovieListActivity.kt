package com.vsantander.tmdbchallenge.presentation.list

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import com.vsantander.tmdbchallenge.R
import com.vsantander.tmdbchallenge.presentation.base.activity.BaseActivity
import kotlinx.android.synthetic.main.activity_movie_list.*
import javax.inject.Inject

@BaseActivity.Animation(BaseActivity.FADE)
class MovieListActivity : BaseActivity() {

    companion object {
        private const val LIST_SPAN_COUNT = 2
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: MovieListViewModel

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

        // TODO Adapter recycler
        swipeRefreshLayout.setOnRefreshListener { }
        recyclerView.apply {
            layoutManager = GridLayoutManager(context,
                    LIST_SPAN_COUNT) as RecyclerView.LayoutManager
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

       // TODO finish load ui info from viewmodel
    }

}