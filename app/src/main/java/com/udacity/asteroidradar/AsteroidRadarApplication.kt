package com.udacity.asteroidradar;

import android.app.Application
import android.os.Build
import androidx.work.*
import com.udacity.asteroidradar.work.RefreshDataWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit


class RadarApplication : Application() {

    private val applicationScope = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        Initialdelay()
    }


    private fun Initialdelay() {
        applicationScope.launch {
            RecurringWork()
        }
    }



    private fun RecurringWork() {
        val constraints = createConstraints()
        val repeatingRequest = createRepeatingRequest(constraints)

        WorkManager.getInstance().enqueueUniquePeriodicWork(
            RefreshDataWorker.NAME_OF_WORK_TO_DO,
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingRequest
        )
    }

    private fun createConstraints(): Constraints {
        return Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresBatteryNotLow(true)
            .setRequiresCharging(true)
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setRequiresDeviceIdle(true)
                }
            }.build()
    }

    private fun createRepeatingRequest(constraints: Constraints): PeriodicWorkRequest {
        return PeriodicWorkRequestBuilder<RefreshDataWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()
    }
}
