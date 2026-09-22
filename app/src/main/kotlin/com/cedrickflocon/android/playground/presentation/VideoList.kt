package com.cedrickflocon.android.playground.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.compose.PlayerSurface
import kotlinx.coroutines.flow.filter

@Composable
fun VideoList() {
    val viewModel = hiltViewModel<MainViewModel>()

    val uim by viewModel.uim.collectAsState()
    uim.let {
        when (it) {
            is ListUim.Loading -> Loading()
            is ListUim.Success -> VideoList(listUim = it)
        }
    }
}

@Composable
fun Loading(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        BasicText(text = "Loading...")
    }
}

@Composable
fun VideoList(
    modifier: Modifier = Modifier,
    listUim: ListUim.Success
) {
    val pagerState = rememberPagerState { listUim.list.size }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }.collect { page ->
            listUim.onChangePage(page)
        }
    }

    VerticalPager(
        state = pagerState,
        modifier = modifier.fillMaxSize(),
    ) { page ->
        val context = LocalContext.current
        val player = remember {
            ExoPlayer.Builder(context).build()
                .apply { repeatMode = Player.REPEAT_MODE_ONE }
        }

        DisposableEffect(Unit) {
            onDispose { player.release() }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            BasicText(
                text = listUim.list[page].title,
                style = TextStyle(color = Color.White),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(bottom = 12.dp, start = 8.dp, end = 8.dp)
                    .zIndex(1f),
            )

            PlayerSurface(
                player = player,
                modifier = Modifier
                    .fillMaxSize()
            )
        }

        LaunchedEffect(listUim.list[page].url) {
            listUim.list[page].url?.let {
                player.setMediaItem(MediaItem.fromUri(it))
                player.prepare()
            }
        }

        LaunchedEffect(Unit) {
            snapshotFlow { pagerState.settledPage }.filter { it == page }
                .collect { player.play() }
        }

        LaunchedEffect(Unit) {
            snapshotFlow { pagerState.settledPage }.filter { it != page }
                .collect { player.pause() }
        }
    }
}
