package com.cedrickflocon.android.playground.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cedrickflocon.android.playground.data.ListRepository
import com.cedrickflocon.android.playground.domain.VideoPage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val listRepository: ListRepository
) : ViewModel() {

    private val internalState =
        MutableStateFlow(InternalState(currentPage = 1, videoPage = null))

    val onPageChange = { page: Int ->
        internalState.update { it.onCurrentPageChange(page) }
    }

    val uim = merge(
        internalState
            .onSubscription {
                val page = listRepository.fetchPage(internalState.value.currentPage)
                internalState.value = internalState.value.copy(videoPage = page)
            },

        internalState
            .distinctUntilChangedBy { it.currentPage }
            .flatMapConcat {
                if (!it.canFetchNextPage()) return@flatMapConcat emptyFlow()

                val nextPage = listRepository.fetchPage(it.videoPage!!.page + 1)
                internalState.update { it.onNextPageReceive(nextPage) }
                emptyFlow()
            }
    )
        .map { it.videoPage }
        .distinctUntilChanged()
        .map {
            val videoPage = it ?: return@map ListUim.Loading
            ListUim.Success(
                list = videoPage.list.map { video ->
                    VideoUim(
                        id = video.id,
                        title = video.title,
                        channel = video.channel,
                        owner = video.owner,
                    )
                }.toImmutableList(),
                onChangePage = onPageChange,
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ListUim.Loading)

    private data class InternalState(
        val currentPage: Int,
        val videoPage: VideoPage?,
    ) {
        fun canFetchNextPage(): Boolean {
            return videoPage != null && videoPage.hasMore && currentPage >= videoPage.list.size - 3
        }
    }

    private fun InternalState.onCurrentPageChange(page: Int): InternalState {
        return this.copy(currentPage = page)
    }

    private fun InternalState.onNextPageReceive(nextPage: VideoPage): InternalState {
        return this.copy(videoPage = nextPage.copy(list = this.videoPage!!.list + nextPage.list))
    }
}
