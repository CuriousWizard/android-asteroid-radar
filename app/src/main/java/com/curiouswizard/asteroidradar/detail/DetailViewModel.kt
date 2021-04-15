package com.curiouswizard.asteroidradar.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.curiouswizard.asteroidradar.database.AsteroidDao
import com.curiouswizard.asteroidradar.model.Asteroid

class DetailViewModel( selectedAsteroid: Asteroid, dataSource: AsteroidDao): ViewModel() {

    private var asteroid : LiveData<Asteroid> = dataSource.getAsteroidWithId(selectedAsteroid.id)
    fun getAsteroid() = asteroid

    class DetailViewModelFactory(
            private val selectedAsteroid: Asteroid,
            private val dataSource: AsteroidDao) : ViewModelProvider.Factory {
        @Suppress("unchecked_cast")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
                return DetailViewModel(selectedAsteroid, dataSource) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}