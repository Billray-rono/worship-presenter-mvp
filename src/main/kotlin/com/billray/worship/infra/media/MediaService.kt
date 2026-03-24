package com.billray.worship.infra.media

class MediaService {
    private val javaAdapter = BasicMediaAdapter()

    fun canPlay(path: String): Boolean = javaAdapter.isSupported(path)
}
