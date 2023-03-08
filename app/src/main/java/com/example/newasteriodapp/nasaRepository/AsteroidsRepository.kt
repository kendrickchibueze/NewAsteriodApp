package com.example.newasteriodapp.nasaRepository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.example.newasteriodapp.domain.Asteroid;
import com.example.newasteriodapp.Constants;
import com.example.newasteriodapp.MyUtils
import com.example.newasteriodapp.nasaApi.SevenDayAsteroidParser
import com.example.newasteriodapp.database.DatabaseAsteroid.Companion.asDomainModel
import com.example.newasteriodapp.database.NasaDatabase;
//import com.example.newasteriodapp.database.asDomainModel
import com.example.newasteriodapp.network.Network;
import com.example.newasteriodapp.network.asDatabaseModel

import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber;
import java.util.*

class AsteroidsRepository(private val database: NasaDatabase) {
    private val today: String
    private val week: String

    val asteroidsSaved: LiveData<List<Asteroid>> by lazy {
        initializeAsteroidLiveData()
    }

    val asteroidsWeek: LiveData<List<Asteroid>> by lazy {
        initializeAsteroidLiveData()
    }

    val asteroidsToday: LiveData<List<Asteroid>> by lazy {
        initializeAsteroidLiveData()
    }

    init {
        today = MyUtils.convertDateStringToFormattedString(Calendar.getInstance().time, Constants.API_QUERY_DATE_FORMAT)
        week = MyUtils.convertDateStringToFormattedString(
            MyUtils.addDaysToDate(Calendar.getInstance().time, 7),
            Constants.API_QUERY_DATE_FORMAT
        )
    }

    private fun initializeAsteroidLiveData(): LiveData<List<Asteroid>> {
        return Transformations.map(database.asteroidDao.getAllAsteroids()) {
            it.asDomainModel()
        }
    }

    suspend fun refreshAsteroids(startDate: String, endDate: String) {
        withContext(Dispatchers.IO) {
            try {
                val asteroids = Network.radarApi.getAsteroids(startDate, endDate, Constants.API_KEY).await()
                database.asteroidDao.insertAll(*SevenDayAsteroidParser(JSONObject(asteroids)).asDatabaseModel())
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }
}
