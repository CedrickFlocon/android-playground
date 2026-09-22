package com.cedrickflocon.android.playground.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VideoDetailDto(
    @SerialName("access_id") val accessId: String,
    val id: String,
    val stream: StreamDto,
    val media: MediaDto,
    val features: FeaturesDto,
)

@Serializable
data class StreamDto(
    @SerialName("stream_type") val streamType: String,
    val url: String,
)

@Serializable
data class MediaDto(
    @SerialName("posters_url") val postersUrl: Map<String, String>,
    @SerialName("dynamic_thumbnail_url") val dynamicThumbnailUrl: String,
)

@Serializable
data class FeaturesDto(
    @SerialName("has_attention_peaks") val hasAttentionPeaks: Boolean,
    @SerialName("has_chapters") val hasChapters: Boolean,
)
