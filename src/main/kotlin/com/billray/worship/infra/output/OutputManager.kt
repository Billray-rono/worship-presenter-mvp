package com.billray.worship.infra.output

import com.billray.worship.domain.Slide
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.layout.StackPane
import javafx.stage.Screen
import javafx.stage.Stage
import javafx.stage.StageStyle

class OutputManager {
    private var liveStage: Stage? = null
    private var liveLabel: Label? = null

    fun attachLiveStage(owner: Stage) {
        if (liveStage != null) return

        val label = Label("Live output not started")
        label.style = "-fx-text-fill: white; -fx-font-size: 54px; -fx-font-weight: bold;"

        val root = StackPane(label)
        root.style = "-fx-background-color: black;"

        val stage = Stage(StageStyle.DECORATED)
        stage.title = "Program Output"
        stage.scene = Scene(root, 1280.0, 720.0)
        stage.initOwner(owner)

        val screens = Screen.getScreens()
        if (screens.size > 1) {
            val second = screens[1].visualBounds
            stage.x = second.minX
            stage.y = second.minY
            stage.width = second.width
            stage.height = second.height
            stage.isFullScreen = true
        }

        liveStage = stage
        liveLabel = label
        stage.show()
    }

    fun showLiveSlide(slide: Slide) {
        liveLabel?.text = slide.content
    }
}
