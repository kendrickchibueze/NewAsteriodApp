package com.udacity.asteroidradar.repository;

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.DateUtils
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.MyDatabase
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.network.Network
import com.udacity.asteroidradar.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber
import java.util.*


class AsteroidsRepository(private val database: MyDatabase) {

    private val today: String
    private val week: String
    val asteroidsSaved: LiveData<List<Asteroid>>
    val asteroidsWeek: LiveData<List<Asteroid>>
    val asteroidsToday: LiveData<List<Asteroid>>

    init {
        today = DateUtils.format(
            Calendar.getInstance().time,
            Constants.API_QUERY_DATE_FORMAT
        )
        week = DateUtils.format(
            DateUtils.addDays(Calendar.getInstance().time, 7),
            Constants.API_QUERY_DATE_FORMAT
        )
        asteroidsSaved = database.asteroidDao.getAllAsteroids().map {
            it.asDomainModel()
        }
        asteroidsWeek = database.asteroidDao.getWeeklyAsteroids(today, week).map {
            it.asDomainModel()
        }
        asteroidsToday = database.asteroidDao.getTodayAsteroids(today).map {
            it.asDomainModel()
        }
    }

    suspend fun refreshAsteroids(startDate: String, endDate: String) {
        withContext(Dispatchers.IO) {
            try {
                val asteroids = Network.radarApi.getAsteroids(startDate, endDate, Constants.API_KEY)
                database.asteroidDao.insertAll(*parseAsteroidsJsonResult(JSONObject(asteroids)).asDatabaseModel())
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }



}
