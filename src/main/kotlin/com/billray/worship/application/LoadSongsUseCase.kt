package com.billray.worship.application

import com.billray.worship.domain.Song
import com.billray.worship.infra.db.SongRepository

class LoadSongsUseCase(private val songRepository: SongRepository) {
    fun execute(): List<Song> = songRepository.findAll()
}

