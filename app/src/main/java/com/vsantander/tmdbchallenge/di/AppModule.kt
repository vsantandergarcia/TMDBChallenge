package com.vsantander.tmdbchallenge.di

import android.arch.persistence.room.Room
import com.vsantander.tmdbchallenge.data.persistence.Database
import com.vsantander.tmdbchallenge.data.remote.RestClient
import com.vsantander.tmdbchallenge.presentation.TMDBChallengeApp
import com.vsantander.tmdbchallenge.utils.Constants
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class AppModule {

    @Singleton
    @Provides
    fun providesRestClient(): RestClient {
        val clientBuilder = OkHttpClient.Builder()
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC
        clientBuilder.addInterceptor(loggingInterceptor)
        clientBuilder.readTimeout(Constants.TIMEOUT_WS.toLong(), TimeUnit.SECONDS)
        clientBuilder.connectTimeout(Constants.TIMEOUT_WS.toLong(), TimeUnit.SECONDS)

        return Retrofit.Builder()
                .baseUrl(Constants.APP_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(clientBuilder.build())
                .build()
                .create<RestClient>(RestClient::class.java)
    }

    @Singleton
    @Provides
    fun provideMovieDatabase(app: TMDBChallengeApp): Database =
            Room.databaseBuilder(app, Database::class.java, "movie.db").build()

}