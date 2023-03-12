package com.example.newasteriodapp.workNasa

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.newasteriodapp.Constants
import com.example.newasteriodapp.MyUtils
import com.example.newasteriodapp.database.MyDatabase.Companion.getDatabase
import com.example.newasteriodapp.nasaRepository.AsteroidsRepository
import com.example.newasteriodapp.nasaRepository.PicturesOfDayRepository
import retrofit2.HttpException
import java.util.*

class RefreshDataWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {



    private suspend fun refreshPictureOfDay(repository: PicturesOfDayRepository) {
        repository.refreshPictureOfDay()
    }

    private suspend fun refreshAsteroids(repository: AsteroidsRepository) {
        val startDate = MyUtils.DateStringConversion(
            Calendar.getInstance().time,
            Constants.API_QUERY_DATE_FORMAT
        )
        val endDate = MyUtils.DateStringConversion(
            MyUtils.addDaysToDate(
                Calendar.getInstance().time,
                7
            ),
            Constants.API_QUERY_DATE_FORMAT
        )
        repository.refreshAsteroids(startDate, endDate)
    }



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

    companion object {
        const val WORK_NAME = "MyDataWorkerRefresher"
    }

}
