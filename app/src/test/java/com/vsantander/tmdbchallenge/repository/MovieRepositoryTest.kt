package com.vsantander.tmdbchallenge.repository

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import android.arch.paging.PagedList
import com.vsantander.tmdbchallenge.data.persistence.Database
import com.vsantander.tmdbchallenge.data.persistence.dao.MovieDao
import com.vsantander.tmdbchallenge.data.persistence.mapper.MovieEntityMapper
import com.vsantander.tmdbchallenge.data.persistence.model.MovieEntity
import com.vsantander.tmdbchallenge.data.remote.RestClient
import com.vsantander.tmdbchallenge.data.remote.mapper.MovieTOMapper
import com.vsantander.tmdbchallenge.data.remote.model.DefaultResponse
import com.vsantander.tmdbchallenge.data.repository.MovieRepositoryImpl
import com.vsantander.tmdbchallenge.domain.model.Listing
import com.vsantander.tmdbchallenge.domain.model.Movie
import com.vsantander.tmdbchallenge.domain.model.ResourceState
import com.vsantander.tmdbchallenge.utils.factory.MovieEntityFactory
import com.vsantander.tmdbchallenge.utils.factory.MovieTOFactory
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MovieRepositoryTest {

    companion object {
        private const val NUMBER_ITEMS = 5
    }

    @Suppress("unused")
    @get:Rule // used to make all live data calls sync
    val instantExecutor = InstantTaskExecutorRule()

    @Mock
    lateinit var restClient: RestClient

    @Mock
    lateinit var database: Database

    @Mock
    lateinit var movieDao: MovieDao

    @Mock
    lateinit var resourceObserver: Observer<ResourceState>

    lateinit var movieTOMapper: MovieTOMapper

    lateinit var movieEntityMapper: MovieEntityMapper

    lateinit var repository: MovieRepositoryImpl

    @Before
    fun setUp() {
        movieTOMapper = MovieTOMapper()
        movieEntityMapper = MovieEntityMapper()
        repository = MovieRepositoryImpl(restClient, movieTOMapper, database, movieEntityMapper)
    }

    @Test
    fun getPopularMoviesEmptyOk() {
        Mockito.`when`(database.movieDao()).thenReturn(movieDao)
        Mockito.`when`(database.movieDao().getNextIndex()).thenReturn(1)
        Mockito.`when`(database.movieDao().movieDataFactory())
                .thenReturn(FakeDataSourceFactory(arrayListOf()))

        val listing = repository.getPopularMovies(
                NUMBER_ITEMS, Schedulers.trampoline())
        val pagedList = getPagedList(listing)
        MatcherAssert.assertThat(pagedList.size, CoreMatchers.`is`(0))
    }

    @Test
    fun getPopularMoviesOk() {
        val dbItemsFirstPage = MovieEntityFactory.makeMovieEntityList(NUMBER_ITEMS)
        val itemsFirstPage = dbItemsFirstPage.map { movieEntityMapper.fromEntity(it) }
        val fakeRoomDataSourceFactory = FakeDataSourceFactory(items = dbItemsFirstPage)
        Mockito.`when`(database.movieDao()).thenReturn(movieDao)
        Mockito.`when`(database.movieDao().getNextIndex()).thenReturn(1)
        Mockito.`when`(database.movieDao().movieDataFactory())
                .thenReturn(fakeRoomDataSourceFactory)

        val listing = repository.getPopularMovies(NUMBER_ITEMS, Schedulers.trampoline())

        MatcherAssert.assertThat(getPagedList(listing).size, CoreMatchers.`is`(NUMBER_ITEMS))
        assert(getPagedList(listing) == itemsFirstPage)

        val dbItemsTwoPage = MovieEntityFactory.makeMovieEntityList(NUMBER_ITEMS*2)
        val itemsTwoPage = dbItemsTwoPage.map { movieEntityMapper.fromEntity(it) }
        fakeRoomDataSourceFactory.items = dbItemsTwoPage
        fakeRoomDataSourceFactory.sourceLiveData.value?.invalidate()

        // trigger loading of the whole list
        getPagedList(listing).loadAround(itemsTwoPage.size)

        MatcherAssert.assertThat(getPagedList(listing).size, CoreMatchers.`is`(NUMBER_ITEMS*2))
        assert(getPagedList(listing) == itemsTwoPage)
    }

    @Test
    fun getPopularMoviesWithRetryOk() {
        val fakeRoomDataSourceFactory = FakeDataSourceFactory<MovieEntity>(arrayListOf())
        Mockito.`when`(restClient.getPopularMovies(Mockito.anyInt()))
                .thenReturn(Single.error((RuntimeException("error"))))
        Mockito.`when`(database.movieDao()).thenReturn(movieDao)
        Mockito.`when`(database.movieDao().getNextIndex()).thenReturn(1)
        Mockito.`when`(database.movieDao().movieDataFactory())
                .thenReturn(fakeRoomDataSourceFactory)

        val listing = repository.getPopularMovies(NUMBER_ITEMS, Schedulers.trampoline())
        listing.resourceState.observeForever(resourceObserver)

        MatcherAssert.assertThat(getPagedList(listing).size, CoreMatchers.`is`(0))

        val wsResponse = DefaultResponse(
                page = 1,
                totalResults = NUMBER_ITEMS,
                totalPages = 3,
                results = MovieTOFactory.makeMovieTOList(NUMBER_ITEMS)
        )
        Mockito.`when`(restClient.getPopularMovies(Mockito.anyInt()))
                .thenReturn(Single.just(wsResponse))

        listing.retry()

        fakeRoomDataSourceFactory.items = MovieEntityFactory.makeMovieEntityList(NUMBER_ITEMS)
        val finalItems = fakeRoomDataSourceFactory.items.map { movieEntityMapper.fromEntity(it) }
        fakeRoomDataSourceFactory.sourceLiveData.value?.invalidate()

        MatcherAssert.assertThat(getPagedList(listing).size, CoreMatchers.`is`(NUMBER_ITEMS))
        MatcherAssert.assertThat(getResourceState(listing), CoreMatchers.`is`(ResourceState.LOADED))
        assert(getPagedList(listing) == finalItems)

        val inOrder = Mockito.inOrder(resourceObserver)
        inOrder.verify(resourceObserver).onChanged(ResourceState.error("error"))
        inOrder.verify(resourceObserver).onChanged(ResourceState.INITIAL_LOADING)
        inOrder.verify(resourceObserver).onChanged(ResourceState.LOADED)
        inOrder.verify(resourceObserver).onChanged(ResourceState.NEXT_LOADING)
        inOrder.verify(resourceObserver).onChanged(ResourceState.LOADED)
        inOrder.verifyNoMoreInteractions()
    }

    @Test
    fun getPopularMoviesWithRefreshOk() {
        val dbItemsFirstPage = MovieEntityFactory.makeMovieEntityList(NUMBER_ITEMS)
        val itemsFirstPage = dbItemsFirstPage.map { movieEntityMapper.fromEntity(it) }
        val fakeRoomDataSourceFactory = FakeDataSourceFactory<MovieEntity>(items = dbItemsFirstPage)
        Mockito.`when`(database.movieDao()).thenReturn(movieDao)
        Mockito.`when`(database.movieDao().getNextIndex()).thenReturn(1)
        Mockito.`when`(database.movieDao().movieDataFactory())
                .thenReturn(fakeRoomDataSourceFactory)

        val listing = repository.getPopularMovies(NUMBER_ITEMS, Schedulers.trampoline())
        listing.resourceState.observeForever(resourceObserver)

        MatcherAssert.assertThat(getPagedList(listing).size, CoreMatchers.`is`(NUMBER_ITEMS))
        assert(getPagedList(listing) == itemsFirstPage)

        listing.refresh()

        MatcherAssert.assertThat(getPagedList(listing).size, CoreMatchers.`is`(NUMBER_ITEMS))
        assert(getPagedList(listing) == itemsFirstPage)
    }

    @Test
    fun getSearchMoviesError() {
        Mockito.`when`(restClient.getSearchMovies(Mockito.anyString(), Mockito.anyInt()))
                .thenReturn(Single.error(RuntimeException()))

        val listing = repository.getSearchMovies(Mockito.anyString(),
                NUMBER_ITEMS, Mockito.anyInt(), Schedulers.trampoline())
        val pagedList = getPagedList(listing)
        MatcherAssert.assertThat(pagedList.size, CoreMatchers.`is`(0))
    }

    @Test
    fun getSearchMoviesOk() {
        val wsItemsFirstPage = MovieTOFactory.makeMovieTOList(NUMBER_ITEMS)
        val wsResponse = DefaultResponse(
                page = 1,
                totalResults = NUMBER_ITEMS,
                totalPages = 1,
                results = wsItemsFirstPage
        )
        val itemsFirstPage = wsItemsFirstPage.map { movieTOMapper.toEntity(it) }
        Mockito.`when`(restClient.getSearchMovies(Mockito.anyString(), Mockito.anyInt()))
                .thenReturn(Single.just(wsResponse))

        val listing = repository.getSearchMovies(Mockito.anyString(),
                NUMBER_ITEMS, Mockito.anyInt(), Schedulers.trampoline())

        MatcherAssert.assertThat(getPagedList(listing).size, CoreMatchers.`is`(itemsFirstPage.size))
        assert(getPagedList(listing) == itemsFirstPage)

        val wsItemsSecondPage = MovieTOFactory.makeMovieTOList(NUMBER_ITEMS)
        val wsResponseSecondPage = DefaultResponse(
                page = 1,
                totalResults = NUMBER_ITEMS,
                totalPages = 1,
                results = wsItemsSecondPage
        )
        val itemsSecondPage = wsItemsSecondPage.map { movieTOMapper.toEntity(it) }
        Mockito.`when`(restClient.getSearchMovies(Mockito.anyString(), Mockito.anyInt()))
                .thenReturn(Single.just(wsResponseSecondPage))

        getPagedList(listing).loadAround(itemsSecondPage.size)

        MatcherAssert.assertThat(getPagedList(listing).size, CoreMatchers.`is`(NUMBER_ITEMS*2))
        assert(getPagedList(listing) == itemsFirstPage+itemsSecondPage)
    }

    @Test
    fun getSearchMoviesRetryOk() {
        val wsItemsFirstPage = MovieTOFactory.makeMovieTOList(NUMBER_ITEMS)
        val wsResponse = DefaultResponse(
                page = 1,
                totalResults = NUMBER_ITEMS,
                totalPages = 1,
                results = wsItemsFirstPage
        )
        val itemsFirstPage = wsItemsFirstPage.map { movieTOMapper.toEntity(it) }
        Mockito.`when`(restClient.getSearchMovies(Mockito.anyString(), Mockito.anyInt()))
                .thenReturn(Single.just(wsResponse))

        val listing = repository.getSearchMovies(Mockito.anyString(), NUMBER_ITEMS,
                1, Schedulers.trampoline())
        listing.resourceState.observeForever(resourceObserver)

        MatcherAssert.assertThat(getPagedList(listing).size, CoreMatchers.`is`(NUMBER_ITEMS))
        MatcherAssert.assertThat(getResourceState(listing), CoreMatchers.`is`(ResourceState.LOADED))
        assert(getPagedList(listing) == itemsFirstPage)

        listing.retry()

        MatcherAssert.assertThat(getPagedList(listing).size, CoreMatchers.`is`(NUMBER_ITEMS))
        MatcherAssert.assertThat(getResourceState(listing), CoreMatchers.`is`(ResourceState.LOADED))
        assert(getPagedList(listing) == wsResponse.results.map { movieTOMapper.toEntity(it) })

        val inOrder = Mockito.inOrder(resourceObserver)
        inOrder.verify(resourceObserver).onChanged(ResourceState.LOADING)
        inOrder.verify(resourceObserver).onChanged(ResourceState.LOADED)
        inOrder.verifyNoMoreInteractions()
    }

    @Test
    fun getSearchMoviesRefreshOk() {
        val wsItemsFirstPage = MovieTOFactory.makeMovieTOList(NUMBER_ITEMS)
        val wsResponse = DefaultResponse(
                page = 1,
                totalResults = NUMBER_ITEMS,
                totalPages = 1,
                results = wsItemsFirstPage
        )
        val itemsFirstPage = wsItemsFirstPage.map { movieTOMapper.toEntity(it) }
        Mockito.`when`(restClient.getSearchMovies(Mockito.anyString(), Mockito.anyInt()))
                .thenReturn(Single.just(wsResponse))

        val listing = repository.getSearchMovies(Mockito.anyString(), NUMBER_ITEMS,
                1, Schedulers.trampoline())
        listing.resourceState.observeForever(resourceObserver)

        MatcherAssert.assertThat(getPagedList(listing).size, CoreMatchers.`is`(NUMBER_ITEMS))
        MatcherAssert.assertThat(getResourceState(listing), CoreMatchers.`is`(ResourceState.LOADED))
        assert(getPagedList(listing) == itemsFirstPage)

        listing.refresh()

        MatcherAssert.assertThat(getPagedList(listing).size, CoreMatchers.`is`(NUMBER_ITEMS))
        MatcherAssert.assertThat(getResourceState(listing), CoreMatchers.`is`(ResourceState.LOADED))
        assert(getPagedList(listing) == wsResponse.results.map { movieTOMapper.toEntity(it) })
    }

    /**
     * extract the latest paged list from the listing
     */
    private fun getPagedList(listing: Listing<Movie>): PagedList<Movie> {
        val observer = LoggingObserver<PagedList<Movie>>()
        listing.pagedList.observeForever(observer)
        MatcherAssert.assertThat(observer.value, CoreMatchers.`is`(CoreMatchers.notNullValue()))
        return observer.value!!
    }

    /**
     * extract the latest resource state from the listing
     */
    private fun getResourceState(listing: Listing<Movie>): ResourceState? {
        val resourceObserver = LoggingObserver<ResourceState>()
        listing.resourceState.observeForever(resourceObserver)
        return resourceObserver.value
    }

    /**
     * simple observer that logs the latest value it receives
     */
    private class LoggingObserver<T> : Observer<T> {
        var value: T? = null
        override fun onChanged(t: T?) {
            this.value = t
        }
    }

}