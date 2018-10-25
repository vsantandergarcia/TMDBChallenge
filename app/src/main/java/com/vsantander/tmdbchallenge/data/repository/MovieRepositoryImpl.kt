package com.vsantander.tmdbchallenge.data.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import com.vsantander.tmdbchallenge.data.persistence.Database
import com.vsantander.tmdbchallenge.data.persistence.mapper.MovieEntityMapper
import com.vsantander.tmdbchallenge.data.remote.RestClient
import com.vsantander.tmdbchallenge.data.remote.mapper.MovieTOMapper
import com.vsantander.tmdbchallenge.domain.model.Listing
import com.vsantander.tmdbchallenge.domain.model.Movie
import com.vsantander.tmdbchallenge.domain.model.ResourceState
import com.vsantander.tmdbchallenge.utils.extension.switchMap
import io.reactivex.Scheduler
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepositoryImpl @Inject constructor(
        private val restClient: RestClient,
        private val movieTOMapper: MovieTOMapper,
        private val database: Database,
        private val movieEntityMapper: MovieEntityMapper
) : MovieRepository {

    override fun getPopularMovies(itemsPerPage: Int,
                                  backgroundScheduler: Scheduler): Listing<Movie> {

        // create a boundary callback in order to observe when the user reaches to the edges of
        // the list and update the database with extra data.
        val boundaryCallback = PopularMoviesBoundaryCallback(
                restClient = restClient,
                dao = database.movieDao(),
                itemsPerPage = itemsPerPage,
                handleResponse = this::insertMoviesListIntoDb,
                mapper = movieTOMapper,
                backgroundScheduler = backgroundScheduler
        )

        // data source factory from Room
        val builder =
                LivePagedListBuilder(
                        database.movieDao().movieDataFactory()
                                .map { movieEntityMapper.fromEntity(it) },
                        itemsPerPage)
                        .setBoundaryCallback(boundaryCallback)

        // use mutable livedata in order to refresh method and gets a new livedata.
        val refreshTrigger = MutableLiveData<Unit>()
        val refreshState = refreshTrigger.switchMap {
            refreshPopularMovies(backgroundScheduler)
        }

        return Listing(
                pagedList = builder.build(),
                resourceState = boundaryCallback.resourceState,
                retry = {
                    boundaryCallback.retry()
                },
                refresh = {
                    refreshTrigger.value = null
                },
                refreshState = refreshState)

    }

    override fun getSearchMovies(search: String, itemsPerPage: Int, prefetchDistance: Int,
                                 backgroundScheduler: Scheduler): Listing<Movie> {

        // create a source factory that creates the DataSource wrapper
        val sourceFactory = SearchMoviesDataSourceFactory(search,
                itemsPerPage,
                restClient,
                movieTOMapper,
                backgroundScheduler)
        // set the configuration of the pagedList
        val pagedListConfig = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(prefetchDistance)
                .setPageSize(itemsPerPage)
                .build()
        // create the pagedList using the factory
        val pagedList = LivePagedListBuilder(sourceFactory, pagedListConfig)
                .build()

        val resourceState = sourceFactory.sourceLiveData.switchMap { it.resourceState }
        return Listing(
                pagedList = pagedList,
                resourceState = resourceState,
                retry = { sourceFactory.sourceLiveData.value?.retry() },
                refresh = {},
                refreshState = resourceState
        )
    }

    /**
     * Inserts the response into the database while also assigning position indices to items.
     */
    private fun insertMoviesListIntoDb(response: List<Movie>?) {
        response?.let {
            val nextIndex = database.movieDao().getNextIndex()
            val items = movieEntityMapper.toEntity(it)
            items.forEachIndexed { index, item -> item.indexInResponse = nextIndex + index }
            database.movieDao().insertList(items)
        }
    }

    /**
     * When refresh is called, simply run a fresh network request and when it arrives,
     * clear the database table and insert all new items in a transaction.
     * <p>
     * Since the PagedList already uses a database bound data source, it will automatically be
     * updated after the database transaction is finished.
     */
    private fun refreshPopularMovies(backgroundScheduler: Scheduler): LiveData<ResourceState> {
        val resourceState = MutableLiveData<ResourceState>()
        resourceState.value = ResourceState.LOADING
        restClient.getPopularMovies(1)
                .map { movieTOMapper.toEntity(it.results) }
                .subscribeOn(backgroundScheduler)
                .subscribeBy(
                        onSuccess = {
                            database.runInTransaction {
                                database.movieDao().deleteAll()
                                insertMoviesListIntoDb(it)
                            }

                            // since we are in bg thread now, post the result.
                            resourceState.postValue(ResourceState.LOADED)
                        },
                        onError = {
                            // retrofit calls this on main thread so safe to call set value
                            resourceState.postValue(ResourceState.error(it))
                        }
                )
        return resourceState
    }

}