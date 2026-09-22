package com.cedrickflocon.android.playground.data

import com.cedrickflocon.android.playground.domain.VideoDetail
import com.cedrickflocon.android.playground.domain.VideoPage
import javax.inject.Inject

class ListRepository @Inject constructor(
    private val listService: ListService
) {

    suspend fun fetchPage(page: Int): VideoPage {
        val dto = listService.getVideos(page)
        return VideoPage(
            page = dto.page,
            limit = dto.limit,
            explicit = dto.explicit,
            total = dto.total,
            hasMore = dto.hasMore,
            list = dto.list.map { video ->
                VideoDetail(
                    id = video.id,
                    title = video.title,
                    channel = video.channel,
                    owner = video.owner,
                )
            },
        )
    }
}
