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
    private val database = getDatabase(application)
    private val asteroidRepository = AsteroidRepository(database)

    private val _navigateToDetails = MutableLiveData<Asteroid?>()
    val navigateToDetails: LiveData<Asteroid?> = _navigateToDetails

    private val listFilter = MutableLiveData<ListFilter>(ListFilter.SHOW_WEEK)

    private val _status = MutableLiveData<NasaApiStatus>()
    val status: LiveData<NasaApiStatus> = _status

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

    val list = Transformations.switchMap(listFilter){
        when(it){
            ListFilter.SHOW_TODAY -> asteroidRepository.asteroidsToday
            else -> asteroidRepository.asteroids
        }
    }


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
     * Factory for constructing DevByteViewModel with parameter
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