package com.cedrickflocon.android.playground

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.compose.PlayerSurface
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filter

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel = hiltViewModel<MainViewModel>()
            val pagerState = rememberPagerState() { viewModel.mediaItems.size }

            val context = LocalContext.current

            VerticalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
            ) { page ->
                val player = remember {
                    ExoPlayer.Builder(context).build()
                        .apply {
                            repeatMode = Player.REPEAT_MODE_ONE
                            setMediaItem(viewModel.mediaItems[page])
                            prepare()
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

                PlayerSurface(
                    player = player,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }

    override fun onStop() {
        super.onStop()

    }
}
