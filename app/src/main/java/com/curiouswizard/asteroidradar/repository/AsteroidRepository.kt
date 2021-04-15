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

    val asteroids: LiveData<List<Asteroid>> = database.asteroidDao.getAsteroids()
    val asteroidsToday: LiveData<List<Asteroid>> = database.asteroidDao.getAsteroids(getToday())

    val pod: LiveData<DatabasePictureOfDay> = database.pictureOfDayDao.getPictureOfDay()

    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO){
            val json = JSONObject(
                NasaApi.nasaApiService.getAsteroidsString(
                    getToday(),
                    getEndDate(),
                    BuildConfig.NASA_API_KEY
                ))
            val asteroidList = parseAsteroidsJsonResult(json)
            database.asteroidDao.deleteYesterday(getYesterday())
            database.asteroidDao.insertAll(*asteroidList.toTypedArray())
        }
    }

    suspend fun refreshPictureOfDay() {
        withContext(Dispatchers.IO){
            val pod = NasaApi.nasaApiJsonService.getImageOfTheDay(BuildConfig.NASA_API_KEY)
            if (pod.mediaType == "image"){
                database.pictureOfDayDao.clear()
                database.pictureOfDayDao.insert(podAsDatabaseModel(pod))
            }
        }
    }

    private fun podAsDatabaseModel(pod: PictureOfDay): DatabasePictureOfDay {
        return DatabasePictureOfDay(pod.date, pod.url, pod.mediaType, pod.title)
    }
}