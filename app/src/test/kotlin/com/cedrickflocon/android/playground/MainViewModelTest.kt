package com.cedrickflocon.android.playground

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import io.kotest.core.spec.style.DescribeSpec

class MainViewModelTest : DescribeSpec({

    describe("MainViewModel") {
        val viewModel = MainViewModel()

        it("should expose initial uim state") {
            viewModel.uim.test {
                assertThat(awaitItem()).isEqualTo("Hello from ViewModel")
            }
        }
    }
})
