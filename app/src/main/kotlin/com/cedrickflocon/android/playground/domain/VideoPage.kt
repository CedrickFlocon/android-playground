package com.cedrickflocon.android.playground.domain

data class VideoPage(
    val page: Int,
    val limit: Int,
    val explicit: Boolean,
    val total: Int,
    val hasMore: Boolean,
    val list: List<VideoDetail>,
)
