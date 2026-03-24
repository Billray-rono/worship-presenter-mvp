package com.billray.worship.domain

data class ServiceSet(
    val id: Long? = null,
    val name: String,
    val items: List<ServiceItem> = emptyList()
)
