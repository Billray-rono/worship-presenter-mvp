package com.billray.worship.ui

import com.billray.worship.application.DeleteSongUseCase
import com.billray.worship.application.GoLiveUseCase
import com.billray.worship.application.LoadServiceUseCase
import com.billray.worship.application.LoadSongsUseCase
import com.billray.worship.application.SaveServiceUseCase
import com.billray.worship.application.SaveSongUseCase
import com.billray.worship.domain.Slide
import com.billray.worship.domain.SlideType
import com.billray.worship.domain.Song
import com.billray.worship.infra.media.MediaService
import com.billray.worship.infra.output.OutputManager
import javafx.application.Application
import javafx.collections.FXCollections
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.control.SplitPane
import javafx.scene.control.TextField
import javafx.scene.control.TextArea
import javafx.scene.layout.BorderPane
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.stage.Stage

class MainApp : Application() {
    override fun start(primaryStage: Stage) {
        val deps = DependenciesHolder.dependencies
            ?: error("Dependencies were not initialized. Call MainApp.start(...) from main().")

        val songItems = FXCollections.observableArrayList(deps.loadSongsUseCase.execute())
        val songListView = ListView(songItems)
        songListView.setCellFactory {
            object : ListCell<Song>() {
                override fun updateItem(item: Song?, empty: Boolean) {
                    super.updateItem(item, empty)
                    text = if (empty || item == null) "" else item.title
                }
            }
        }

        val titleField = TextField()
        titleField.promptText = "Title"

        val authorField = TextField()
        authorField.promptText = "Author"

        val tagsField = TextField()
        tagsField.promptText = "Tags (comma separated)"

        val lyricsArea = TextArea()
        lyricsArea.promptText = "Lyrics"
        lyricsArea.isWrapText = true

        val preview = TextArea()
        preview.isEditable = false
        preview.isWrapText = true
        preview.promptText = "Song preview"

        var selectedSongId: Long? = null

        fun renderPreview() {
            val title = titleField.text.trim()
            val author = authorField.text.trim()
            val tags = tagsField.text.trim()
            val lyrics = lyricsArea.text.trim()
            val authorLine = if (author.isNotBlank()) "Author: $author\n" else ""
            val tagsLine = if (tags.isNotBlank()) "Tags: $tags\n" else ""
            preview.text = buildString {
                appendLine(if (title.isNotBlank()) title else "Untitled song")
                if (authorLine.isNotBlank()) {
                    append(authorLine)
                }
                if (tagsLine.isNotBlank()) {
                    append(tagsLine)
                }
                if (lyrics.isNotBlank()) {
                    appendLine()
                    append(lyrics)
                }
            }.trim()
        }

        fun clearForm() {
            selectedSongId = null
            titleField.clear()
            authorField.clear()
            tagsField.clear()
            lyricsArea.clear()
            songListView.selectionModel.clearSelection()
            renderPreview()
        }

        fun currentSong(): Song {
            return Song(
                id = selectedSongId,
                title = titleField.text.trim(),
                lyrics = lyricsArea.text.trim(),
                author = authorField.text.trim(),
                tags = tagsField.text
                    .split(",")
                    .map(String::trim)
                    .filter(String::isNotBlank)
            )
        }

        val statusLabel = Label("Ready")

        fun refreshSongs(selectedId: Long? = selectedSongId) {
            songItems.setAll(deps.loadSongsUseCase.execute())
            val selectedIndex = songItems.indexOfFirst { it.id == selectedId }
            if (selectedIndex >= 0) {
                songListView.selectionModel.select(selectedIndex)
            }
        }

        songListView.selectionModel.selectedItemProperty().addListener { _, _, selected ->
            if (selected == null) {
                return@addListener
            }
            selectedSongId = selected.id
            titleField.text = selected.title
            authorField.text = selected.author
            tagsField.text = selected.tags.joinToString(", ")
            lyricsArea.text = selected.lyrics
            renderPreview()
        }

        val newSongButton = Button("New Song")
        newSongButton.setOnAction {
            clearForm()
            statusLabel.text = "Creating a new song"
        }

        val saveSongButton = Button("Save Song")
        saveSongButton.setOnAction {
            val song = currentSong()
            if (song.title.isBlank()) {
                statusLabel.text = "Title is required"
                return@setOnAction
            }
            if (song.lyrics.isBlank()) {
                statusLabel.text = "Lyrics are required"
                return@setOnAction
            }

            val savedId = deps.saveSongUseCase.execute(song)
            selectedSongId = savedId
            refreshSongs(savedId)
            statusLabel.text = "Saved ${song.title}"
        }

        val deleteSongButton = Button("Delete Song")
        deleteSongButton.setOnAction {
            val songId = selectedSongId
            if (songId == null) {
                statusLabel.text = "Select a song to delete"
                return@setOnAction
            }

            deps.deleteSongUseCase.execute(songId)
            clearForm()
            refreshSongs()
            statusLabel.text = "Song deleted"
        }

        val goLiveButton = Button("Go Live")
        goLiveButton.setOnAction {
            val song = currentSong()
            if (song.title.isBlank() || song.lyrics.isBlank()) {
                statusLabel.text = "Save or select a song before going live"
                return@setOnAction
            }
            deps.goLiveUseCase.execute(
                Slide(title = song.title, content = song.lyrics, type = SlideType.LYRICS)
            )
            statusLabel.text = "${song.title} sent live"
        }

        val openOutputButton = Button("Open Program Output")
        openOutputButton.setOnAction {
            deps.outputManager.attachLiveStage(primaryStage)
            statusLabel.text = "Program output opened"
        }

        val mediaCheckButton = Button("Check Sample Media")
        mediaCheckButton.setOnAction {
            val supported = deps.mediaService.canPlay("sample.mp4")
            statusLabel.text = if (supported) "sample.mp4 supported" else "sample.mp4 not supported"
        }

        titleField.textProperty().addListener { _, _, _ -> renderPreview() }
        authorField.textProperty().addListener { _, _, _ -> renderPreview() }
        tagsField.textProperty().addListener { _, _, _ -> renderPreview() }
        lyricsArea.textProperty().addListener { _, _, _ -> renderPreview() }

        val controls = HBox(
            10.0,
            newSongButton,
            saveSongButton,
            deleteSongButton,
            goLiveButton,
            openOutputButton,
            mediaCheckButton,
            statusLabel
        )
        controls.padding = Insets(10.0)

        val leftPane = VBox(8.0, Label("Song Library"), songListView)
        leftPane.padding = Insets(10.0)
        VBox.setVgrow(songListView, Priority.ALWAYS)

        val form = GridPane()
        form.hgap = 10.0
        form.vgap = 10.0
        form.add(Label("Title"), 0, 0)
        form.add(titleField, 1, 0)
        form.add(Label("Author"), 0, 1)
        form.add(authorField, 1, 1)
        form.add(Label("Tags"), 0, 2)
        form.add(tagsField, 1, 2)
        form.add(Label("Lyrics"), 0, 3)
        form.add(lyricsArea, 1, 3)
        GridPane.setHgrow(titleField, Priority.ALWAYS)
        GridPane.setHgrow(authorField, Priority.ALWAYS)
        GridPane.setHgrow(tagsField, Priority.ALWAYS)
        GridPane.setHgrow(lyricsArea, Priority.ALWAYS)
        GridPane.setVgrow(lyricsArea, Priority.ALWAYS)

        val editorPane = VBox(8.0, Label("Song Editor"), form)
        editorPane.padding = Insets(10.0)
        VBox.setVgrow(form, Priority.ALWAYS)

        val previewPane = VBox(8.0, Label("Preview"), preview)
        previewPane.padding = Insets(10.0)
        VBox.setVgrow(preview, Priority.ALWAYS)

        val centerPane = SplitPane(editorPane, previewPane)
        centerPane.dividerPositions = 0.65

        val root = BorderPane()
        root.left = leftPane
        root.center = centerPane
        root.bottom = controls

        primaryStage.title = "Worship Presenter MVP"
        primaryStage.scene = Scene(root, 1320.0, 760.0)
        primaryStage.show()

        renderPreview()
        refreshSongs()
    }

    companion object {
        fun start(
            loadSongsUseCase: LoadSongsUseCase,
            saveSongUseCase: SaveSongUseCase,
            deleteSongUseCase: DeleteSongUseCase,
            goLiveUseCase: GoLiveUseCase,
            loadServiceUseCase: LoadServiceUseCase,
            saveServiceUseCase: SaveServiceUseCase,
            mediaService: MediaService,
            outputManager: OutputManager
        ) {
            DependenciesHolder.dependencies = Dependencies(
                loadSongsUseCase = loadSongsUseCase,
                saveSongUseCase = saveSongUseCase,
                deleteSongUseCase = deleteSongUseCase,
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
    val loadSongsUseCase: LoadSongsUseCase,
    val saveSongUseCase: SaveSongUseCase,
    val deleteSongUseCase: DeleteSongUseCase,
    val goLiveUseCase: GoLiveUseCase,
    val loadServiceUseCase: LoadServiceUseCase,
    val saveServiceUseCase: SaveServiceUseCase,
    val mediaService: MediaService,
    val outputManager: OutputManager
)

object DependenciesHolder {
    var dependencies: Dependencies? = null
}
