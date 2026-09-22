package com.cedrickflocon.android.playground.presentation

import kotlinx.collections.immutable.ImmutableList

sealed interface ListUim {

    data object Loading : ListUim

    data class Success(
        val list: ImmutableList<VideoUim>,
        val onChangePage: () -> Unit,
    ) : ListUim
}

data class VideoUim(
    val id: String,
    val title: String,
    val channel: String,
    val owner: String,
)
