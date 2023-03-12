package com.example.newasteriodapp.nasaRepository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.newasteriodapp.Constants
import com.example.newasteriodapp.database.MyDatabase
import com.example.newasteriodapp.domain.PictureOfDay
import com.example.newasteriodapp.network.Network
import com.example.newasteriodapp.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber




class PicturesOfDayRepository(private val database: MyDatabase) {


    val pictureOfDay: LiveData<PictureOfDay>
        get() = getPictureOfDayFromDatabase()


    suspend fun refreshPictureOfDay() {
        try {
            refreshPictureOfDayFromNetwork()
        } catch (e: Exception) {
            Timber.e(e)
        }
    }


    private fun getPictureOfDayFromDatabase(): LiveData<PictureOfDay> {
        return Transformations.map(database.pictureOfDayDao.getPictureOfDay()) {
            it?.asDomainModel()
        }
    }


    private suspend fun refreshPictureOfDayFromNetwork() {
        withContext(Dispatchers.IO) {
            val pictureOfDay = Network.radarApi.getPictureOfDay(Constants.API_KEY).await()
            database.pictureOfDayDao.insertPictureOfDay(pictureOfDay.asDatabaseModel())
        }
    }
}
