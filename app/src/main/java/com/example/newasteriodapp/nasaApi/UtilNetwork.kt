package com.example.newasteriodapp.nasaApi

import com.example.newasteriodapp.Constants
import com.example.newasteriodapp.domain.Asteroid
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


interface AsteroidParser {
    fun parse(jsonResult: JSONObject): List<Asteroid>
}



fun SevenDayAsteroidParser(jsonResult: JSONObject): ArrayList<Asteroid> {
    val nearEarthObjectsJson = jsonResult.getJSONObject("near_earth_objects")
    val asteroidList = ArrayList<Asteroid>()
    val nextSevenDaysFormattedDates = getNextSevenDaysFormattedDates()
    for (formattedDate in nextSevenDaysFormattedDates) {
        val dateAsteroidJsonArray = nearEarthObjectsJson.getJSONArray(formattedDate)
        parseAsteroidData(dateAsteroidJsonArray, formattedDate, asteroidList)
    }

    return asteroidList
}

private fun parseAsteroidData(dateAsteroidJsonArray: JSONArray , formattedDate: String , asteroidList: ArrayList<Asteroid>) {
    for (i in 0 until dateAsteroidJsonArray.length()) {
        val asteroidJson = dateAsteroidJsonArray.getJSONObject(i)
        val asteroid = createAsteroidFromJson(asteroidJson, formattedDate)
        asteroidList.add(asteroid)
    }
}

private fun createAsteroidFromJson(asteroidJson: JSONObject, formattedDate: String): Asteroid {
    val id = asteroidJson.getLong("id")
    val codename = asteroidJson.getString("name")
    val absoluteMagnitude = asteroidJson.getDouble("absolute_magnitude_h")
    val estimatedDiameter = asteroidJson.getJSONObject("estimated_diameter")
        .getJSONObject("kilometers").getDouble("estimated_diameter_max")
    val closeApproachData = asteroidJson
        .getJSONArray("close_approach_data").getJSONObject(0)
    val relativeVelocity = closeApproachData.getJSONObject("relative_velocity")
        .getDouble("kilometers_per_second")
    val distanceFromEarth = closeApproachData.getJSONObject("miss_distance")
        .getDouble("astronomical")
    val isPotentiallyHazardous = asteroidJson
        .getBoolean("is_potentially_hazardous_asteroid")

    return Asteroid(
        id, codename, formattedDate, absoluteMagnitude,
        estimatedDiameter, relativeVelocity, distanceFromEarth, isPotentiallyHazardous
    )
}

private fun getNextSevenDaysFormattedDates(): ArrayList<String> {
    val formattedDateList = ArrayList<String>()
    for (i in 0..Constants.DEFAULT_END_DATE_DAYS) {
        val formattedDate = getFormattedDate(i)
        formattedDateList.add(formattedDate)
    }

    return formattedDateList
}

private fun getFormattedDate(dayIndex: Int): String {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, dayIndex)
    val currentTime = calendar.time
    val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())

    return dateFormat.format(currentTime)
}

