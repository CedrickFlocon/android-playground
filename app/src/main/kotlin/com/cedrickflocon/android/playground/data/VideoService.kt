package com.cedrickflocon.android.playground.data

import retrofit2.http.GET
import retrofit2.http.Path

interface VideoService {

    @GET("videos/{id}")
    suspend fun getVideo(@Path("id") id: String): VideoDetailDto
}
