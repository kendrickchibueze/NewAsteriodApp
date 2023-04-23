package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.database.MyDatabase
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.domain.PictureOfDay
import com.udacity.asteroidradar.network.Network
import com.udacity.asteroidradar.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class PicturesOfDayRepository(private val database: MyDatabase) {


    val pictureOfDay: LiveData<PictureOfDay> = getPictureOfDay()

    @JvmName("getPictureOfDay1")
    private fun getPictureOfDay(): LiveData<PictureOfDay> {
        return Transformations.map(database.pictureOfDayDao.getPictureOfDay()) {
            it?.asDomainModel()
        }
    }

    suspend fun refreshPictureOfDay() {
        Timber.i("refreshPictureOfDay() called")
        val pictureOfDay = withContext(Dispatchers.IO) {

            try {

                Network.radarApi.getPictureOfDay(Constants.API_KEY)

            } catch (e: Exception) {
                Timber.e(e)
                null
            }
        }
        pictureOfDay?.let {
            database.pictureOfDayDao.insertPictureOfDay(it.asDatabaseModel())
        }
        Timber.i("refreshPictureOfDay() finished")
    }
}