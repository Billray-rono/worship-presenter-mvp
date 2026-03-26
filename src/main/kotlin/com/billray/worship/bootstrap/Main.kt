package com.billray.worship.bootstrap

import com.billray.worship.application.DeleteSongUseCase
import com.billray.worship.application.GoLiveUseCase
import com.billray.worship.application.LoadServiceUseCase
import com.billray.worship.application.LoadSongsUseCase
import com.billray.worship.application.SaveServiceUseCase
import com.billray.worship.application.SaveSongUseCase
import com.billray.worship.infra.db.DatabaseFactory
import com.billray.worship.infra.db.ServiceSetRepository
import com.billray.worship.infra.db.SongRepository
import com.billray.worship.infra.media.MediaService
import com.billray.worship.infra.output.OutputManager
import com.billray.worship.ui.MainApp

fun main() {
    DatabaseFactory.init()

    val serviceRepository = ServiceSetRepository()
    val songRepository = SongRepository()
    val outputManager = OutputManager()
    val mediaService = MediaService()

    MainApp.start(
        loadSongsUseCase = LoadSongsUseCase(songRepository),
        saveSongUseCase = SaveSongUseCase(songRepository),
        deleteSongUseCase = DeleteSongUseCase(songRepository),
        goLiveUseCase = GoLiveUseCase(outputManager),
        loadServiceUseCase = LoadServiceUseCase(serviceRepository),
        saveServiceUseCase = SaveServiceUseCase(serviceRepository),
        mediaService = mediaService,
        outputManager = outputManager
    )
}
