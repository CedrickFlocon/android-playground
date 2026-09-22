package com.cedrickflocon.android.playground

import com.cedrickflocon.android.playground.presentation.MainViewModel
import com.google.common.truth.Truth.assertThat
import io.kotest.core.spec.style.DescribeSpec

class MainViewModelTest : DescribeSpec({

    describe("MainViewModel") {
        val viewModel = MainViewModel()

        it("should have media items") {
            assertThat(viewModel.mediaItems).isNotEmpty()
        }
    }
})
