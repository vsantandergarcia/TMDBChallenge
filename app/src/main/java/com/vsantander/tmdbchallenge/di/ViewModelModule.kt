package com.vsantander.tmdbchallenge.di

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.vsantander.tmdbchallenge.presentation.base.viewmodel.ViewModelFactory
import com.vsantander.tmdbchallenge.presentation.list.MovieListViewModel
import com.vsantander.tmdbchallenge.presentation.search.SearchMovieListViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(MovieListViewModel::class)
    abstract fun bindMovieListViewModel(viewModel: MovieListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SearchMovieListViewModel::class)
    abstract fun bindSearchMovieListViewModel(viewModel: SearchMovieListViewModel): ViewModel
}