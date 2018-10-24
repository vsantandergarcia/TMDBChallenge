package com.vsantander.tmdbchallenge.presentation.list

import com.vsantander.tmdbchallenge.data.repository.MovieRepositoryImpl
import com.vsantander.tmdbchallenge.presentation.base.viewmodel.AbsentLiveData
import com.vsantander.tmdbchallenge.presentation.base.viewmodel.BaseViewModel
import com.vsantander.tmdbchallenge.utils.Constants
import com.vsantander.tmdbchallenge.utils.extension.switchMap
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MovieListViewModel @Inject constructor(
        repository: MovieRepositoryImpl
): BaseViewModel() {

    private val repoResult = AbsentLiveData.create(
            repository.getPopularMovies(
                    Constants.NUMBER_OF_ITEMS_PER_PAGE, Schedulers.io())
    )

    val popularMovies = repoResult.switchMap { it.pagedList }

    val resourceState = repoResult.switchMap { it.resourceState }

    val refreshState = repoResult.switchMap { it.refreshState }

    fun retry() = repoResult.value?.retry?.invoke()

    fun refresh() = repoResult.value?.refresh?.invoke()

}