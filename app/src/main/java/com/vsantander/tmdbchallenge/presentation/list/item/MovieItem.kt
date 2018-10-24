package com.vsantander.tmdbchallenge.presentation.list.item

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.vsantander.tmdbchallenge.R
import com.vsantander.tmdbchallenge.domain.model.Movie
import com.vsantander.tmdbchallenge.presentation.base.item.ItemView
import com.vsantander.tmdbchallenge.utils.Constants
import com.vsantander.tmdbchallenge.utils.ImageUrlProvider
import kotlinx.android.synthetic.main.view_item_movie.view.*
import org.jetbrains.anko.dimen

class MovieItem @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ItemView<Movie>(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.view_item_movie, this, true)
        useCompatPadding = true
        radius = context.dimen(R.dimen.movie_item_radius).toFloat()
        cardElevation = context.dimen(R.dimen.movie_item_elevation).toFloat()
    }

    override fun bind(item: Movie) {
        titleTextView.text = item.title

        val url = ImageUrlProvider.formatUrlImageWithW500(
                item.backdropPath ?: item.posterPath ?: "")

        val options = RequestOptions()
                .placeholder(R.drawable.gradient_background)
                .centerCrop()
        Glide
                .with(context)
                .load(url)
                .transition(DrawableTransitionOptions.withCrossFade(Constants.DURATION_FADE_GLIDE))
                .apply(options)
                .into(movieImageView)
    }
}