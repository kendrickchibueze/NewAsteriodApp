package com.example.newasteriodapp.main

import android.app.Application
import androidx.lifecycle.*
import com.example.newasteriodapp.Constants
import com.example.newasteriodapp.MyUtils
import com.example.newasteriodapp.database.MyDatabase.Companion.getDatabase
import com.example.newasteriodapp.domain.Asteroid
import com.example.newasteriodapp.network.AsteroidApiFilter
import com.example.newasteriodapp.nasaRepository.AsteroidsRepository
import com.example.newasteriodapp.nasaRepository.PicturesOfDayRepository
import kotlinx.coroutines.launch
import java.util.*



class MainViewModel(application: Application) : AndroidViewModel(application) {



    private val _moveToSelectedAsteroid = MutableLiveData<Asteroid?>()
    val navigateToSelectedProperty: LiveData<Asteroid?>
        get() {
            return _moveToSelectedAsteroid
        }

    private val database = getDatabase(application)
    private val filter = MutableLiveData(AsteroidApiFilter.SAVED)
    private val picturesOfDayRepository = PicturesOfDayRepository(database)
    private val asteroidsRepository = AsteroidsRepository(database)

    // Get data from repositories
    val picOfDay = picturesOfDayRepository.pictureOfDay
    val asteroids = Transformations.switchMap(filter){ getAsteroids(it) }



    fun displayAsteroidDetails(asteroid: Asteroid) {
        _moveToSelectedAsteroid.value = asteroid
    }

    fun displayAsteroidDetailsComplete() {
        _moveToSelectedAsteroid.value = null
    }

    // Filter and date range update
    fun updateFilter(filter: AsteroidApiFilter) {
        this.filter.value = filter
    }

    fun updateDateRange(startDate: Date, endDate: Date) {
        val start = MyUtils.DateStringConversion(startDate, Constants.API_QUERY_DATE_FORMAT)
        val end = MyUtils.DateStringConversion(endDate, Constants.API_QUERY_DATE_FORMAT)
        viewModelScope.launch {
            asteroidsRepository.refreshAsteroids(start, end)
        }
    }

    init {
        viewModelScope.launch {
            DataRefresh()
        }
    }





    // Private helper methods
    private suspend fun DataRefresh() {
        refreshPictureOfDay()
        refreshAsteroids()
    }

    private suspend fun refreshPictureOfDay() {
        picturesOfDayRepository.refreshPictureOfDay()
    }

    private suspend fun refreshAsteroids() {
        val calendar = Calendar.getInstance()
        val startDate = MyUtils.DateStringConversion(calendar.time, Constants.API_QUERY_DATE_FORMAT)
        calendar.add(Calendar.DAY_OF_YEAR, 7)
        val endDate = MyUtils.DateStringConversion(calendar.time, Constants.API_QUERY_DATE_FORMAT)
        asteroidsRepository.refreshAsteroids(startDate, endDate)
    }

    private fun getAsteroids(filter: AsteroidApiFilter): LiveData<List<Asteroid>> {
        return when (filter) {
            AsteroidApiFilter.TODAY -> asteroidsRepository.asteroidsToday
            AsteroidApiFilter.WEEK -> asteroidsRepository.asteroidsWeek
            else -> asteroidsRepository.asteroidsSaved
        }
    }

}
