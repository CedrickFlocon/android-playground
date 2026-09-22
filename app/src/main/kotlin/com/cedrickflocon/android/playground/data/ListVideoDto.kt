package com.cedrickflocon.android.playground.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ListVideoDto(
    val page: Int,
    val limit: Int,
    val explicit: Boolean,
    val total: Int,
    @SerialName("has_more") val hasMore: Boolean,
    val list: List<VideoDto>,
)

@Serializable
data class VideoDto(
    val id: String,
    val title: String,
    val channel: String,
    val owner: String,
)
