package com.example.newasteriodapp;

import android.app.Application
import android.os.Build
import androidx.work.*
import com.example.newasteriodapp.workNasa.RefreshDataWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit

class AsteriodRaderApp : Application() {

    private val applicationScope = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        canSetWork()
    }


    private fun buildConstraints(): Constraints {
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

    private fun buildRepeatingRequest(constraints: Constraints): PeriodicWorkRequest {
        return PeriodicWorkRequestBuilder<RefreshDataWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()
    }

    private fun canSetWork() {
        applicationScope.launch {
            val constraints = buildConstraints()
            val repeatingRequest = buildRepeatingRequest(constraints)
            WorkManager.getInstance().enqueueUniquePeriodicWork(
                RefreshDataWorker.WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                repeatingRequest
            )
        }
    }

    companion object {
        private lateinit var instance: AsteriodRaderApp

        fun getInstance(): AsteriodRaderApp {
            synchronized(AsteriodRaderApp::class.java) {
                if (!::instance.isInitialized) {
                    instance = AsteriodRaderApp()
                }
            }
            return instance
        }
    }
}
