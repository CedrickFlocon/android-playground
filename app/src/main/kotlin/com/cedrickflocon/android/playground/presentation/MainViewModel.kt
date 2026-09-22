package com.cedrickflocon.android.playground.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cedrickflocon.android.playground.data.ListRepository
import com.cedrickflocon.android.playground.domain.VideoDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val listRepository: ListRepository
) : ViewModel() {

    private val internalState = MutableStateFlow(InternalState(currentPage = 1, listVideo = null))

    val onPageChange = { page: Int ->

    }

    val uim = internalState
        .onSubscription {
            val page = listRepository.fetchPage(internalState.value.currentPage)
            internalState.value = internalState.value.copy(listVideo = page.list)
        }
        .map { state ->
            val videos = state.listVideo ?: return@map ListUim.Loading
            ListUim.Success(
                list = videos.map { video ->
                    VideoUim(
                        id = video.id,
                        title = video.title,
                        channel = video.channel,
                        owner = video.owner,
                    )
                }.toImmutableList(),
                onChangePage = {},
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ListUim.Loading)

    private data class InternalState(
        val currentPage: Int,
        val listVideo: List<VideoDetail>?,
    )
}
