package com.udacity.asteroidradar.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.DateUtils
import com.udacity.asteroidradar.database.MyDatabaseProvider.getDatabase
import com.udacity.asteroidradar.repository.AsteroidsRepository
import com.udacity.asteroidradar.repository.PicturesOfDayRepository
import retrofit2.HttpException
import java.util.*

class RefreshDataWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {


    override suspend fun doWork(): Result {
        val database = getDatabase(applicationContext)
        val pictureOfDayRepository = PicturesOfDayRepository(database)
        val asteroidsRepository = AsteroidsRepository(database)

        return try {
            refreshPictureOfDay(pictureOfDayRepository)
            refreshAsteroids(asteroidsRepository)

            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }
    }

    private suspend fun refreshPictureOfDay(repository: PicturesOfDayRepository) {
        repository.refreshPictureOfDay()
    }

    private suspend fun refreshAsteroids(repository: AsteroidsRepository) {
        val today = DateUtils.format(Calendar.getInstance().time, Constants.API_QUERY_DATE_FORMAT)
        val week = DateUtils.format(DateUtils.addDays(Calendar.getInstance().time, 7), Constants.API_QUERY_DATE_FORMAT)

        repository.refreshAsteroids(today, week)
    }


    companion object {
        const val NAME_OF_WORK_TO_DO = "RefreshDataWorker"
    }

}