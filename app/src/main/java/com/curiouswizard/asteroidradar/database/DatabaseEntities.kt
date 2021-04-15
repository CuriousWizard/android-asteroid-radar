package com.curiouswizard.asteroidradar.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.curiouswizard.asteroidradar.model.PictureOfDay

@Entity(tableName = "picture_of_day_table")
data class DatabasePictureOfDay constructor(
        @PrimaryKey
        val date: String,
        val url: String,
        val mediaType: String,
        val title: String) {
}

fun podAsDomainModel(databasePOD: DatabasePictureOfDay): PictureOfDay {
    return PictureOfDay(
            date = databasePOD.date,
            url = databasePOD.url,
            mediaType = databasePOD.mediaType,
            title = databasePOD.title
    )
}