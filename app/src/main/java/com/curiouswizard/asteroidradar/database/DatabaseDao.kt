package com.curiouswizard.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.curiouswizard.asteroidradar.model.Asteroid

@Dao
interface AsteroidDao {
    @Query("SELECT * FROM asteroids_table ORDER BY closeApproachDate ASC")
    fun getAsteroids(): LiveData<List<Asteroid>>

    @Query("SELECT * FROM asteroids_table WHERE closeApproachDate = :today")
    fun getAsteroids(today: String): LiveData<List<Asteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroids: Asteroid)

    @Query("SELECT * FROM asteroids_table WHERE id = :asteroidId")
    fun getAsteroidWithId(asteroidId: Long): LiveData<Asteroid>

    @Query("DELETE FROM asteroids_table WHERE closeApproachDate = :yesterday")
    fun deleteYesterday(yesterday: String)
}

@Dao
interface PictureOfDayDao {
    @Query("SELECT * FROM picture_of_day_table")
    fun getPictureOfDay(): LiveData<DatabasePictureOfDay>

    @Query("SELECT date FROM picture_of_day_table")
    fun getPodDate(): String

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg pod: DatabasePictureOfDay)

    @Query("DELETE FROM picture_of_day_table")
    fun clear()
}