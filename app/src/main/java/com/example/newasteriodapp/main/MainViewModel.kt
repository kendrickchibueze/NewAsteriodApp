package com.example.newasteriodapp.main

import android.app.Application
import androidx.lifecycle.*
import com.example.newasteriodapp.Constants
import com.example.newasteriodapp.MyUtils
import com.example.newasteriodapp.database.NasaDatabase.Companion.getDatabase
import com.example.newasteriodapp.domain.Asteroid
import com.example.newasteriodapp.network.AsteroidApiFilter
import com.example.newasteriodapp.nasaRepository.AsteroidsRepository
import com.example.newasteriodapp.nasaRepository.PicturesOfDayRepository
import kotlinx.coroutines.launch
import java.util.*



class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = getDatabase(application)
    private val filter = MutableLiveData(AsteroidApiFilter.SHOW_SAVED)
    private val picturesOfDayRepository = PicturesOfDayRepository(database)
    private val asteroidsRepository = AsteroidsRepository(database)



    // Get data from repositories
    val picOfDay = picturesOfDayRepository.pictureOfDay
    val asteroids = Transformations.switchMap(filter){ getAsteroids(it) }


    private val _moveToSelectedAsteroid = MutableLiveData<Asteroid?>()
    val navigateToSelectedProperty: LiveData<Asteroid?>
        get() {
            return _moveToSelectedAsteroid
        }




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
        val start = MyUtils.convertDateStringToFormattedString(startDate, Constants.API_QUERY_DATE_FORMAT)
        val end = MyUtils.convertDateStringToFormattedString(endDate, Constants.API_QUERY_DATE_FORMAT)
        viewModelScope.launch {
            asteroidsRepository.refreshAsteroids(start, end)
        }
    }

    init {
        viewModelScope.launch {
            DataRefresh()
        }
    }


    class Factory(private val app: Application) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (isMainViewModelClass(modelClass)) {
                return developMainViewModel() as T
            }
            throw IllegalArgumentException("cannot retrieve viewmodel")
        }

        private fun isMainViewModelClass(modelClass: Class<*>): Boolean {
            return modelClass.isAssignableFrom(MainViewModel::class.java)
        }

        private fun developMainViewModel(): MainViewModel {
            return MainViewModel(app)
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
        val startDate = MyUtils.convertDateStringToFormattedString(calendar.time, Constants.API_QUERY_DATE_FORMAT)
        calendar.add(Calendar.DAY_OF_YEAR, 7)
        val endDate = MyUtils.convertDateStringToFormattedString(calendar.time, Constants.API_QUERY_DATE_FORMAT)
        asteroidsRepository.refreshAsteroids(startDate, endDate)
    }

    private fun getAsteroids(filter: AsteroidApiFilter): LiveData<List<Asteroid>> {
        return when (filter) {
            AsteroidApiFilter.SHOW_TODAY -> asteroidsRepository.asteroidsToday
            AsteroidApiFilter.SHOW_WEEK -> asteroidsRepository.asteroidsWeek
            else -> asteroidsRepository.asteroidsSaved
        }
    }
}
