package com.cedrickflocon.android.playground.data

import retrofit2.http.GET
import retrofit2.http.Query

interface ListService {

    @GET("videos")
    suspend fun getVideos(@Query("page") page: Int): ListVideoDto
}
