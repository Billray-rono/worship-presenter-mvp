package com.billray.worship.ui

import com.billray.worship.application.GoLiveUseCase
import com.billray.worship.application.LoadServiceUseCase
import com.billray.worship.application.SaveServiceUseCase
import com.billray.worship.domain.Slide
import com.billray.worship.domain.SlideType
import com.billray.worship.infra.media.MediaService
import com.billray.worship.infra.output.OutputManager
import javafx.application.Application
import javafx.collections.FXCollections
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.TextArea
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.stage.Stage

class MainApp : Application() {
    override fun start(primaryStage: Stage) {
        val deps = DependenciesHolder.dependencies
            ?: error("Dependencies were not initialized. Call MainApp.start(...) from main().")

        val librarySlides = FXCollections.observableArrayList(
            Slide(title = "Welcome", content = "Welcome to worship", type = SlideType.TEXT),
            Slide(title = "Song 1", content = "Amazing grace...", type = SlideType.LYRICS),
            Slide(title = "Announcement", content = "Prayer meeting at 6 PM", type = SlideType.TEXT)
        )

        val listView = ListView(librarySlides)
        listView.setCellFactory {
            object : javafx.scene.control.ListCell<Slide>() {
                override fun updateItem(item: Slide?, empty: Boolean) {
                    super.updateItem(item, empty)
                    text = if (empty || item == null) "" else "${item.title} (${item.type})"
                }
            }
        }

        val preview = TextArea()
        preview.isEditable = false
        preview.promptText = "Slide preview"

        listView.selectionModel.selectedItemProperty().addListener { _, _, selected ->
            preview.text = selected?.content ?: ""
        }

        val goLiveButton = Button("Go Live")
        goLiveButton.setOnAction {
            val selected = listView.selectionModel.selectedItem ?: return@setOnAction
            deps.goLiveUseCase.execute(selected)
        }

        val openOutputButton = Button("Open Program Output")
        openOutputButton.setOnAction {
            deps.outputManager.attachLiveStage(primaryStage)
        }

        val mediaCheckButton = Button("Check Sample Media")
        val statusLabel = Label("Ready")
        mediaCheckButton.setOnAction {
            val supported = deps.mediaService.canPlay("sample.mp4")
            statusLabel.text = if (supported) "sample.mp4 supported" else "sample.mp4 not supported"
        }

        val controls = HBox(10.0, goLiveButton, openOutputButton, mediaCheckButton, statusLabel)
        controls.padding = Insets(10.0)

        val leftPane = VBox(8.0, Label("Library"), listView)
        leftPane.padding = Insets(10.0)
        VBox.setVgrow(listView, Priority.ALWAYS)

        val centerPane = VBox(8.0, Label("Preview"), preview)
        centerPane.padding = Insets(10.0)
        VBox.setVgrow(preview, Priority.ALWAYS)

        val root = BorderPane()
        root.left = leftPane
        root.center = centerPane
        root.bottom = controls

        primaryStage.title = "Worship Presenter MVP"
        primaryStage.scene = Scene(root, 1100.0, 700.0)
        primaryStage.show()
    }

    companion object {
        fun start(
            goLiveUseCase: GoLiveUseCase,
            loadServiceUseCase: LoadServiceUseCase,
            saveServiceUseCase: SaveServiceUseCase,
            mediaService: MediaService,
            outputManager: OutputManager
        ) {
            DependenciesHolder.dependencies = Dependencies(
                goLiveUseCase = goLiveUseCase,
                loadServiceUseCase = loadServiceUseCase,
                saveServiceUseCase = saveServiceUseCase,
                mediaService = mediaService,
                outputManager = outputManager
            )
            launch(MainApp::class.java)
        }
    }
}

data class Dependencies(
    val goLiveUseCase: GoLiveUseCase,
    val loadServiceUseCase: LoadServiceUseCase,
    val saveServiceUseCase: SaveServiceUseCase,
    val mediaService: MediaService,
    val outputManager: OutputManager
)

object DependenciesHolder {
    var dependencies: Dependencies? = null
}
