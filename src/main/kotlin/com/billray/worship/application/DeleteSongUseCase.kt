package com.billray.worship.application

import com.billray.worship.infra.db.SongRepository

class DeleteSongUseCase(private val songRepository: SongRepository) {
    fun execute(songId: Long) {
        songRepository.delete(songId)
    }
}

