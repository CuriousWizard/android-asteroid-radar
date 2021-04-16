package com.curiouswizard.asteroidradar

import android.app.Application
import android.os.Build
import androidx.work.*
import com.curiouswizard.asteroidradar.work.RefreshDataWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class AsteroidRadarApplication: Application() {

    /**
     * Launching work on default CoroutineScope
     */
    private fun delayedInit() {
        CoroutineScope(Dispatchers.Default).launch {
            setupRecurringWork()
        }
    }

    /**
     * Function to setup daily background work using WorkManager
     */
    private fun setupRecurringWork() {
        // Setup conditions in which case do the work
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresBatteryNotLow(true)
            .setRequiresCharging(true)
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setRequiresDeviceIdle(true)
                }
            }.build()

        // Create a repeating work request
        val repeatingRequest
                = PeriodicWorkRequestBuilder<RefreshDataWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()

        // Giving WorkManager our work
        WorkManager.getInstance(baseContext).enqueueUniquePeriodicWork(
            RefreshDataWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingRequest)
    }

    /**
     * Use onCreate to setup any background tasks, running expensive setup operations
     * in a background thread to avoid delaying app start.
     */
    override fun onCreate() {
        super.onCreate()
        delayedInit()
    }
}