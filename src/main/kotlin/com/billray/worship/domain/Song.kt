package com.billray.worship.domain

data class Song(
    val id: Long? = null,
    val title: String,
    val lyrics: String,
    val author: String = "",
    val tags: List<String> = emptyList()
)

