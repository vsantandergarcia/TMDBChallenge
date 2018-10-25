package com.vsantander.tmdbchallenge.di

import com.vsantander.tmdbchallenge.presentation.detail.MovieDetailsActivity
import com.vsantander.tmdbchallenge.presentation.list.MovieListActivity
import com.vsantander.tmdbchallenge.presentation.search.SearchMovieListActivity
import com.vsantander.tmdbchallenge.presentation.splash.SplashActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module(includes = [(ViewModelModule::class)])
abstract class ActivityModule {

    @ContributesAndroidInjector
    internal abstract fun contributeSplashActivity(): SplashActivity

    @ContributesAndroidInjector
    internal abstract fun contributeMovieListActivity(): MovieListActivity

    @ContributesAndroidInjector
    internal abstract fun contributeMovieDetailsActivity(): MovieDetailsActivity

    @ContributesAndroidInjector
    internal abstract fun contributeSearchMovieListActivity(): SearchMovieListActivity

}