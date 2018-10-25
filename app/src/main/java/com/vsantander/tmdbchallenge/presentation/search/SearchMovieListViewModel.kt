package com.vsantander.tmdbchallenge.presentation.search

import com.vsantander.tmdbchallenge.data.repository.MovieRepositoryImpl
import com.vsantander.tmdbchallenge.presentation.base.viewmodel.BaseViewModel
import javax.inject.Inject

class SearchMovieListViewModel @Inject constructor(
        private val repository: MovieRepositoryImpl
): BaseViewModel() {


}