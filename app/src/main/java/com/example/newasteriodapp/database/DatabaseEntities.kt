package com.example.newasteriodapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.newasteriodapp.domain.Asteroid
import com.example.newasteriodapp.domain.PictureOfDay


data class PictureOfDay(
    val url: String,
    val mediaType: String,
    val title: String
)

@Entity
data class DatabasePictureOfDay constructor(
    @PrimaryKey val url: String,
    val date: String,
    val mediaType: String,
    val title: String
) {
    fun asDomainModel(): PictureOfDay {
        return PictureOfDay(url, mediaType, title)
    }
}


data class Asteroid(
    val id: Long,
    val codename: String,
    val closeApproachDate: String,
    val absoluteMagnitude: Double,
    val estimatedDiameter: Double,
    val relativeVelocity: Double,
    val distanceFromEarth: Double,
    val isPotentiallyHazardous: Boolean
)

@Entity
data class DatabaseAsteroid constructor(
    @PrimaryKey val id: Long,
    val codename: String,
    val closeApproachDate: String,
    val absoluteMagnitude: Double,
    val estimatedDiameter: Double,
    val relativeVelocity: Double,
    val distanceFromEarth: Double,
    val isPotentiallyHazardous: Boolean
) {
    companion object {
        fun List<DatabaseAsteroid>.asDomainModel(): List<Asteroid> {
            return map {
                Asteroid(
                    id = it.id,
                    codename = it.codename,
                    closeApproachDate = it.closeApproachDate,
                    absoluteMagnitude = it.absoluteMagnitude,
                    estimatedDiameter = it.estimatedDiameter,
                    relativeVelocity = it.relativeVelocity,
                    distanceFromEarth = it.distanceFromEarth,
                    isPotentiallyHazardous = it.isPotentiallyHazardous
                )
            }
        }
    }
}
