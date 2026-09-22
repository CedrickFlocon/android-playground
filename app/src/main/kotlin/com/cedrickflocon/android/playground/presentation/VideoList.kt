package com.cedrickflocon.android.playground.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 12.dp, start = 8.dp, end = 8.dp),
            contentAlignment = Alignment.BottomStart,
        ) {
            BasicText(text = listUim.list[page].title)
        }
    }
}
