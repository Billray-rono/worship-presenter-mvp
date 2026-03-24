package com.billray.worship.domain

data class Slide(
    val id: Long? = null,
    val title: String,
    val content: String,
    val type: SlideType = SlideType.TEXT
)
