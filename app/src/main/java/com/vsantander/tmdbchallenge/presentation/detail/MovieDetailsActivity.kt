package com.vsantander.tmdbchallenge.presentation.detail

import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.vsantander.tmdbchallenge.R
import com.vsantander.tmdbchallenge.domain.model.Movie
import com.vsantander.tmdbchallenge.presentation.base.activity.BaseActivity
import com.vsantander.tmdbchallenge.utils.Constants
import com.vsantander.tmdbchallenge.utils.ImageUrlProvider
import kotlinx.android.synthetic.main.activity_movie_details.*
import kotlinx.android.synthetic.main.activity_movie_details_content.*

@BaseActivity.Animation(BaseActivity.PUSH)
class MovieDetailsActivity: BaseActivity(), AppBarLayout.OnOffsetChangedListener {

    companion object {
        const val EXTRA_MOVIE = "EXTRA_MOVIE"
    }

    private lateinit var movie: Movie

    /* Activity methods */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_details)

        intent?.extras?.getParcelable<Movie>(EXTRA_MOVIE)?.let {
            movie = it
            setUpViews()
        } ?: throw RuntimeException("bad initialization. not found some extras")
    }

    override fun onStart() {
        super.onStart()
        appbar.addOnOffsetChangedListener(this)
    }

    override fun onStop() {
        super.onStop()
        appbar.removeOnOffsetChangedListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // handle item selection
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /* AppBarLayout.OnOffsetChangedListener methods */

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        if (Math.abs(verticalOffset) == appBarLayout?.totalScrollRange) {
            appbar.contentDescription = getString(R.string.img_collapsed)
        } else if (verticalOffset == 0) {
            appbar.contentDescription = getString(R.string.img_expanded)
        } else {
            appbar.contentDescription = getString(R.string.img_collapsing)
        }
    }

    /* setUp methods */

    private fun setUpViews() {
        setUpToolbar()
        bindMovie(movie)
    }

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back_white)
        supportActionBar?.title = ""
        collapsingToolbar.title = ""
    }

    /* Owner methods */

    private fun bindMovie(movie: Movie) {
        supportActionBar?.title = movie.title
        collapsingToolbar.title = movie.title

        yearValueTextView.text = movie.year ?: getString(R.string.movie_details_no_info)

        overViewValueTextView.text = movie.overview ?: getString(R.string.movie_details_no_info)

        val url = ImageUrlProvider.formatUrlImageWithW500(
                movie.backdropPath ?: movie.posterPath ?: "")

        val options = RequestOptions()
                .placeholder(R.drawable.gradient_background)
                .centerCrop()
        Glide
                .with(this)
                .load(url)
                .transition(DrawableTransitionOptions.withCrossFade(Constants.DURATION_FADE_GLIDE))
                .apply(options)
                .into(movieImageView)
    }

}