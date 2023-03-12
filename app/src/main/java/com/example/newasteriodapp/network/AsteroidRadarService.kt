package com.example.newasteriodapp.network

import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Query


interface AsteroidRadarService {
    @GET(value = "planetary/apod")
    fun getPictureOfDay(@Query("api_key") apiKey: String): Deferred<NetworkPictureOfDay>

    @GET(value = "neo/rest/v1/feed")
    fun getAsteroids(@Query("start_date") startDate: String, @Query("end_date") endDate: String, @Query("api_key") apiKey: String): Deferred<String>
}

object Network {
    val radarApi = RetrofitClient.createRetrofitInstance().create(AsteroidRadarService::class.java)
}

enum class AsteroidApiFilter(val value: String) {
    SAVED("saved"),
    TODAY("today"),
    WEEK("week")
}

