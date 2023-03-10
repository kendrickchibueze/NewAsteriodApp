package com.example.newasteriodapp.network

import com.example.newasteriodapp.database.DatabaseAsteroid
import com.example.newasteriodapp.database.DatabasePictureOfDay
import com.example.newasteriodapp.domain.Asteroid
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass



@JsonClass(generateAdapter = true)
data class NetworkPictureOfDay(
    val url : String,
    val date : String,
    @Json(name = "media_type") val mediaType : String,
    val title : String
)

fun NetworkPictureOfDay.asDatabaseModel(): DatabasePictureOfDay {
    return DatabasePictureOfDay(
        url,
        date,
        mediaType,
        title
    )
}

fun List<Asteroid>.asDatabaseModel(): Array<DatabaseAsteroid> {
    return map {
        DatabaseAsteroid(
            id = it.id,
            codename = it.codename,
            closeApproachDate = it.closeApproachDate,
            absoluteMagnitude = it.absoluteMagnitude,
            estimatedDiameter = it.estimatedDiameter,
            relativeVelocity = it.relativeVelocity,
            distanceFromEarth = it.distanceFromEarth,
            isPotentiallyHazardous = it.isPotentiallyHazardous
        )
    }.toTypedArray()
}
