package com.curiouswizard.asteroidradar.network

import com.curiouswizard.asteroidradar.Constants
import com.curiouswizard.asteroidradar.model.PictureOfDay
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofitJson = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(Constants.BASE_URL)
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(Constants.BASE_URL)
    .build()

/**
 * Retrofit interface for getting response only in String
 */
interface NasaApiService {
    @GET("neo/rest/v1/feed")
    suspend fun getAsteroidsString(
        @Query("start_date") start: String,
        @Query("end_date") end: String,
        @Query("api_key") apiKey: String
    ): String
}

/**
 * Retrofit interface for JSON parse using Moshi
 */
interface NasaApiJsonService {
    @GET("planetary/apod")
    suspend fun getImageOfTheDay(@Query("api_key")apiKey: String): PictureOfDay
}

object NasaApi {
    val nasaApiService: NasaApiService by lazy {
        retrofit.create(NasaApiService::class.java)
    }

    val nasaApiJsonService: NasaApiJsonService by lazy {
        retrofitJson.create(NasaApiJsonService::class.java)
    }
}