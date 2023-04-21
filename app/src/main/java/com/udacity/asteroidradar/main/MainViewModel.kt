package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.DateUtils
import com.udacity.asteroidradar.database.MyDatabaseProvider.getDatabase
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.network.ApiFilter
import com.udacity.asteroidradar.repository.AsteroidsRepository
import com.udacity.asteroidradar.repository.PicturesOfDayRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class MainViewModel(application: Application) : AndroidViewModel(application) {

    // Filter LiveData
    private val _filter = MutableLiveData(ApiFilter.SHOW_SAVED)
    val filter: LiveData<ApiFilter>
        get() = _filter


    // Repositories
    private val database = getDatabase(application)
    private val picturesOfDayRepository = PicturesOfDayRepository(database)
    private val asteroidsRepository = AsteroidsRepository(database)



    // Picture of Day LiveData
    val picOfDay = picturesOfDayRepository.pictureOfDay

    // Asteroids LiveData
    val asteroids = Transformations.switchMap(filter) { filter ->
        when (filter) {
            ApiFilter.SHOW_TODAY -> asteroidsRepository.asteroidsToday
            ApiFilter.SHOW_WEEK -> asteroidsRepository.asteroidsWeek
            else -> asteroidsRepository.asteroidsSaved
        }
    }

    // Navigation LiveData
    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid?>()
    val navigateToSelectedProperty: LiveData<Asteroid?>
        get() = _navigateToSelectedAsteroid

    init {
        refreshData()
    }

    private fun refreshData() {
        viewModelScope.launch {
            refreshPicturesOfDay()
            refreshAsteroids()
        }
    }

    private suspend fun refreshPicturesOfDay() {
        withContext(Dispatchers.IO) {
            picturesOfDayRepository.refreshPictureOfDay()
        }
    }

    private suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            val calendar = Calendar.getInstance()
            val startDate = DateUtils.format(
                calendar.time,
                Constants.API_QUERY_DATE_FORMAT
            )
            calendar.add(Calendar.DAY_OF_YEAR, 7)
            val endDate = DateUtils.format(
                calendar.time,
                Constants.API_QUERY_DATE_FORMAT
            )
            asteroidsRepository.refreshAsteroids(startDate, endDate)
        }
    }

    fun displayAsteroidDetails(asteroid: Asteroid) {
        _navigateToSelectedAsteroid.value = asteroid
    }

    fun displayAsteroidDetailsComplete() {
        _navigateToSelectedAsteroid.value = null
    }

    fun updateFilter(filter: ApiFilter) {
        _filter.value = filter
    }


   fun updateDateRange(startDate: Date, endDate: Date) = viewModelScope.launch {
       val start = DateUtils.format(startDate, Constants.API_QUERY_DATE_FORMAT)
       val end = DateUtils.format(endDate, Constants.API_QUERY_DATE_FORMAT)
       asteroidsRepository.refreshAsteroids(start, end)
   }


    class Factory(private val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass
                .takeIf { it.isAssignableFrom(MainViewModel::class.java) }
                ?.run { MainViewModel(app) as T }
                ?: throw IllegalArgumentException("Can't implement viewmodel")
        }
    }

}
