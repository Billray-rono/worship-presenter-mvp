package com.billray.worship.application

import com.billray.worship.domain.Song
import com.billray.worship.infra.db.SongRepository

class SaveSongUseCase(private val songRepository: SongRepository) {
    fun execute(song: Song): Long = songRepository.save(song)
}

