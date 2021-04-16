package com.curiouswizard.asteroidradar.repository

import androidx.lifecycle.LiveData
import com.curiouswizard.asteroidradar.BuildConfig
import com.curiouswizard.asteroidradar.database.AppDatabase
import com.curiouswizard.asteroidradar.database.DatabasePictureOfDay
import com.curiouswizard.asteroidradar.model.Asteroid
import com.curiouswizard.asteroidradar.model.PictureOfDay
import com.curiouswizard.asteroidradar.network.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AsteroidRepository(private val database: AppDatabase){
    // Getting Asteroids from DB
    val asteroids: LiveData<List<Asteroid>> = database.asteroidDao.getAsteroids()
    val asteroidsToday: LiveData<List<Asteroid>> = database.asteroidDao.getAsteroids(getToday())

    // Getting Picture of the Day from DB
    val pod: LiveData<DatabasePictureOfDay> = database.pictureOfDayDao.getPictureOfDay()

    /**
     * Refresh list of asteroids with the latest information from the API and
     * replace local database cache. If application started offline, it only deletes
     * yesterday asteroids.
     */
    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO){
            // Delete yesterday asteroids in database even if we start offline
            database.asteroidDao.deleteYesterday(getYesterday())

            // Getting JSON from NASA's NeoWs API
            val json = JSONObject(
                NasaApi.nasaApiService.getAsteroidsString(
                    getToday(),
                    getEndDate(),
                    BuildConfig.NASA_API_KEY
                ))

            // Using support function to get a list of Asteroids
            val asteroidList = parseAsteroidsJsonResult(json)

            // Insert all fresh data from our new API response
            database.asteroidDao.insertAll(*asteroidList.toTypedArray())
        }
    }

    /**
     * Refreshing Picture of the Day
     */
    suspend fun refreshPictureOfDay() {
        withContext(Dispatchers.IO){
            // Getting PictureOfDay from NASA's APOD API
            val pod = NasaApi.nasaApiJsonService.getImageOfTheDay(BuildConfig.NASA_API_KEY)

            // Only updating POD if it's image and it's new day
            if (pod.mediaType == "image" && pod.date != database.pictureOfDayDao.getPodDate()){
                database.pictureOfDayDao.clear()
                database.pictureOfDayDao.insert(podAsDatabaseModel(pod))
            }
        }
    }

    /**
     * Create DatabasePictureOfDay from given PictureOfDay
     *
     * @param pod The PictureOfDay that you want to convert
     * @return The DatabasePictureOfDay version of PictureOfDay
     */
    private fun podAsDatabaseModel(pod: PictureOfDay): DatabasePictureOfDay {
        return DatabasePictureOfDay(pod.date, pod.url, pod.mediaType, pod.title)
    }
}