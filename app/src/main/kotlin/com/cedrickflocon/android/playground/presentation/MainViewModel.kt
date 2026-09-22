package com.cedrickflocon.android.playground.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cedrickflocon.android.playground.data.ListRepository
import com.cedrickflocon.android.playground.data.VideoRepository
import com.cedrickflocon.android.playground.domain.Video
import com.cedrickflocon.android.playground.domain.VideoPage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val listRepository: ListRepository,
    private val videoRepository: VideoRepository,
) : ViewModel() {

    private val internalState =
        MutableStateFlow(InternalState(currentPage = 1, videoPage = null, emptyMap()))

    val onPageChange = { page: Int ->
        internalState.update { it.onCurrentPageChange(page) }
    }

    val uim = merge(
        internalState
            .onSubscription {
                val page = listRepository.fetchPage(internalState.value.currentPage)
                internalState.value = internalState.value.copy(videoPage = page)
            },

        //Fetch Video detail
        internalState
            .distinctUntilChangedBy { it.currentPage }
            .filter { it.canFetchVideo(it.currentPage) }
            .flatMapMerge { state ->
                flow {
                    val video = try {
                        videoRepository.fetchVideo(state.videoPage!!.list[state.currentPage].id)
                    } catch (e: Exception) {
                        Log.e(
                            TAG,
                            "Error fetch video info, should update internal state to allow the ui display an error on the current page",
                            e
                        )
                        return@flow
                    }
                    internalState.update { it.onRetreiveVideo(video) }
                }
            },

        //Next Page
        internalState
            .distinctUntilChangedBy { it.currentPage }
            .onEach {
                if (!it.canFetchNextPage()) return@onEach

                val nextPage = try {
                    listRepository.fetchPage(it.videoPage!!.page + 1)
                } catch (e: Exception) {
                    Log.e(
                        TAG,
                        "Error fetch next page, should update internal state to allow the ui display the last page + 1 with a retry button",
                        e
                    )
                    return@onEach
                }
                internalState.update { it.onNextPageReceive(nextPage) }
            }
            .flatMapLatest { emptyFlow() }
    )
        .distinctUntilChanged()
        .map { internalState ->
            val videoPage = internalState.videoPage ?: return@map ListUim.Loading
            ListUim.Success(
                list = videoPage.list.map { video ->
                    VideoUim(
                        id = video.id,
                        title = video.title,
                        channel = video.channel,
                        owner = video.owner,
                        url = internalState.videos.get(video.id)?.streamUrl
                    )
                }.toImmutableList(),
                onChangePage = onPageChange,
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ListUim.Loading)

    private data class InternalState(
        val currentPage: Int,
        val videoPage: VideoPage?,
        val videos: Map<String, Video>
    ) {
        fun canFetchNextPage(): Boolean {
            return videoPage != null && videoPage.hasMore && currentPage >= videoPage.list.size - 3
        }

        fun canFetchVideo(page: Int): Boolean {
            return videoPage?.list[page]?.id?.let { !videos.contains(it) } ?: false
        }
    }

    private fun InternalState.onCurrentPageChange(page: Int): InternalState {
        return this.copy(currentPage = page)
    }

    private fun InternalState.onNextPageReceive(nextPage: VideoPage): InternalState {
        return this.copy(videoPage = nextPage.copy(list = this.videoPage!!.list + nextPage.list))
    }

    private fun InternalState.onRetreiveVideo(video: Video): InternalState {
        return this.copy(videos = videos + (video.id to video))
    }

    companion object {
        private val TAG = "MainViewModel"
    }
}
