package com.example.myapplication.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Performance(
    val date: String,
    val theatre_name: String,
    val title: String,
    val subtitle1: String?,
    val subtitle2: String?,
    val location: String,
    val genre: String,
    val descr_uri: String?,
    val tickets_uri: String? = null
)
