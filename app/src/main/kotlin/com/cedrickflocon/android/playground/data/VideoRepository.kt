package com.cedrickflocon.android.playground.data

import com.cedrickflocon.android.playground.domain.Video
import javax.inject.Inject

class VideoRepository @Inject constructor(
    private val videoService: VideoService
) {

    suspend fun fetchVideo(id: String): Video {
        val dto = videoService.getVideo(id)
        return Video(
            id = dto.id,
            streamUrl = dto.stream.url,
            posterUrl = dto.media.postersUrl.maxByOrNull { it.key.toIntOrNull() ?: 0 }?.value,
        )
    }
}
