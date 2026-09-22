package com.cedrickflocon.android.playground

import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    val mediaItems = listOf(
        MediaItem.fromUri("https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"),
        MediaItem.fromUri("https://hls-harbor-livepush.akamaized.net/live_cdn/nsqIStpj8PaG-Ev/emcQJ0pGpremocy/index.m3u8"),
        MediaItem.fromUri("https://devstreaming-cdn.apple.com/videos/streaming/examples/bipbop_16x9/bipbop_16x9_variant.m3u8"),
        MediaItem.fromUri("https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"),
        MediaItem.fromUri("https://hls-harbor-livepush.akamaized.net/live_cdn/nsqIStpj8PaG-Ev/emcQJ0pGpremocy/index.m3u8"),
        MediaItem.fromUri("https://devstreaming-cdn.apple.com/videos/streaming/examples/bipbop_16x9/bipbop_16x9_variant.m3u8"),
    )
}
