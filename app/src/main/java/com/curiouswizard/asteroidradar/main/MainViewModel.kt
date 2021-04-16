package com.curiouswizard.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.curiouswizard.asteroidradar.database.getDatabase
import com.curiouswizard.asteroidradar.model.Asteroid
import com.curiouswizard.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.launch

enum class ListFilter(val value: String) {SHOW_WEEK("week"), SHOW_TODAY("today")}
enum class NasaApiStatus {LOADING, ERROR, DONE}

class MainViewModel(application: Application) : ViewModel() {
    // Get access to data through repository
    private val database = getDatabase(application)
    private val asteroidRepository = AsteroidRepository(database)

    // Encapsulated LiveData to handle navigation properly
    private val _navigateToDetails = MutableLiveData<Asteroid?>()
    val navigateToDetails: LiveData<Asteroid?> = _navigateToDetails

    private val listFilter = MutableLiveData(ListFilter.SHOW_WEEK)

    // Encapsulated LiveData to get API Request status
    private val _status = MutableLiveData<NasaApiStatus>()
    val status: LiveData<NasaApiStatus> = _status

    /**
     * Called immediately when this ViewModel is created. It tries to refresh data using AsteroidRepository.
     */
    init{
        viewModelScope.launch {
            _status.value = NasaApiStatus.LOADING
            try {
                asteroidRepository.refreshAsteroids()
                asteroidRepository.refreshPictureOfDay()
                _status.value = NasaApiStatus.DONE
            } catch (e: Exception) {
                _status.value = NasaApiStatus.ERROR
            }
        }
    }

    // Switching list of asteroids according to listFilter option
    val list = Transformations.switchMap(listFilter){
        when(it){
            ListFilter.SHOW_TODAY -> asteroidRepository.asteroidsToday
            else -> asteroidRepository.asteroids
        }
    }

    // Receive Picture of the Day from AsteroidRepository
    val pod = asteroidRepository.pod

    fun onAsteroidClicked(asteroid: Asteroid) {
        _navigateToDetails.value = asteroid
    }

    fun doneNavigating() {
        _navigateToDetails.value = null
    }

    fun updateList(filter: ListFilter){
        listFilter.value = filter
    }

    /**
     * Factory for constructing MainViewModel with parameter
     */
    class MainViewModelFactory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}