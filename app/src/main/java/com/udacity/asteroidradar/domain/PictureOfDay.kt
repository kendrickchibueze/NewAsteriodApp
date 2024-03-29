package com.udacity.asteroidradar.domain

import com.squareup.moshi.Json




data class PictureOfDay(
    val url: String,
    @field:Json(name = "media_type")
    val mediaType: String,
    val title: String
)
