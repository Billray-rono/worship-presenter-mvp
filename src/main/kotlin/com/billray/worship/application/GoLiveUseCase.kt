package com.billray.worship.application

import com.billray.worship.domain.Slide
import com.billray.worship.infra.output.OutputManager

class GoLiveUseCase(private val outputManager: OutputManager) {
    fun execute(slide: Slide) {
        outputManager.showLiveSlide(slide)
    }
}
