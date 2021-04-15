package com.curiouswizard.asteroidradar.model

import com.squareup.moshi.Json

data class PictureOfDay(
    val date: String,
    @Transient val explanation: String = "",
    @Transient val hdurl: String = "",
    @Json(name = "media_type") val mediaType: String,
    val title: String,
    val url: String)
