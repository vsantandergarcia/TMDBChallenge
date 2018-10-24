package com.vsantander.tmdbchallenge.data.remote

import com.vsantander.tmdbchallenge.BuildConfig
import com.vsantander.tmdbchallenge.data.remote.model.DefaultResponse
import com.vsantander.tmdbchallenge.data.remote.model.MovieTO
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface RestClient {

    @GET("movie/popular?api_key=${BuildConfig.API_ACCESS_TOKEN}")
    fun getPopularMovies(@Query("page") page: Int): Single<DefaultResponse<MovieTO>>

}