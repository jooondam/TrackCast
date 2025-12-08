package com.example.trackcast.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.trackcast.data.database.repository.WeatherDataRepository
import com.example.trackcast.data.entities.WeatherData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherDataRepository: WeatherDataRepository
) : ViewModel() {

    // current track ID for loading weather
    private val _currentTrackId = MutableLiveData<Int>()

    // current weather data list for a track
    val weatherList: LiveData<List<WeatherData>> = _currentTrackId.switchMap { trackId ->
        weatherDataRepository.getWeatherForTrack(trackId)
    }

    // latest weather for a track
    val latestWeather: LiveData<WeatherData?> = _currentTrackId.switchMap { trackId ->
        weatherDataRepository.getLatestWeatherForTrack(trackId)
    }

    // all latest weather data (one per track) for display in track list
    val allLatestWeather: LiveData<List<WeatherData>> = weatherDataRepository.getAllLatestWeather()

    // weather staleness status
    private val _isWeatherStale = MutableLiveData<Boolean>()
    val isWeatherStale: LiveData<Boolean> = _isWeatherStale

    // loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // operation status
    private val _operationStatus = MutableLiveData<OperationStatus>()
    val operationStatus: LiveData<OperationStatus> = _operationStatus

    // set track ID to load weather data
    fun setTrackId(trackId: Int) {
        _currentTrackId.value = trackId
    }

    // check if weather data is stale (needs refresh)
    fun checkWeatherStaleness(trackId: Int, maxAgeHours: Int = 1) {
        viewModelScope.launch {
            try {
                val isStale = weatherDataRepository.isWeatherStale(trackId, maxAgeHours)
                _isWeatherStale.value = isStale
            } catch (e: Exception) {
                _operationStatus.value = OperationStatus.Error(e.message ?: "Failed to check weather staleness")
            }
        }
    }

    // add new weather data
    fun addWeatherData(weather: WeatherData) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val weatherId = weatherDataRepository.insert(weather)
                if (weatherId > 0) {
                    _isLoading.value = false
                    _operationStatus.value = OperationStatus.Success("Weather data added successfully")
                } else {
                    _isLoading.value = false
                    _operationStatus.value = OperationStatus.Error("Failed to add weather data")
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _operationStatus.value = OperationStatus.Error(e.message ?: "Failed to add weather data")
            }
        }
    }

    // add weather data and clean up old data
    fun addWeatherDataWithCleanup(weather: WeatherData) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val weatherId = weatherDataRepository.insertAndCleanup(weather)
                if (weatherId > 0) {
                    _isLoading.value = false
                    _operationStatus.value = OperationStatus.Success("Weather data updated and old data cleaned")
                } else {
                    _isLoading.value = false
                    _operationStatus.value = OperationStatus.Error("Failed to update weather data")
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _operationStatus.value = OperationStatus.Error(e.message ?: "Failed to update weather data")
            }
        }
    }

    // update existing weather data
    fun updateWeatherData(weather: WeatherData) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                weatherDataRepository.update(weather)
                _isLoading.value = false
                _operationStatus.value = OperationStatus.Success("Weather data updated successfully")
            } catch (e: Exception) {
                _isLoading.value = false
                _operationStatus.value = OperationStatus.Error(e.message ?: "Failed to update weather data")
            }
        }
    }

    // delete weather data
    fun deleteWeatherData(weather: WeatherData) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                weatherDataRepository.delete(weather)
                _isLoading.value = false
                _operationStatus.value = OperationStatus.Success("Weather data deleted successfully")
            } catch (e: Exception) {
                _isLoading.value = false
                _operationStatus.value = OperationStatus.Error(e.message ?: "Failed to delete weather data")
            }
        }
    }

    // manually delete old weather data for a track
    fun deleteOldWeatherData(trackId: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                weatherDataRepository.deleteOldWeatherData(trackId)
                _isLoading.value = false
                _operationStatus.value = OperationStatus.Success("Old weather data cleaned up")
            } catch (e: Exception) {
                _isLoading.value = false
                _operationStatus.value = OperationStatus.Error(e.message ?: "Failed to clean up old data")
            }
        }
    }

    // clear weather data
    fun clearWeatherData() {
        _currentTrackId.value = null
        _isWeatherStale.value = false
    }

    // sealed class for operation status
    sealed class OperationStatus {
        data class Success(val message: String) : OperationStatus()
        data class Error(val message: String) : OperationStatus()
    }

    /**
     * fetch weather from API and store in database
     * called when adding/editing tracks or when data is stale
     */
    fun fetchWeatherForTrack(trackId: Int, latitude: Double,
                             longitude: Double) {
        Log.d(TAG, "fetchWeatherForTrack: Starting fetch for track $trackId at ($latitude, $longitude)")
        viewModelScope.launch {
            _isLoading.value = true

            val result = weatherDataRepository.fetchAndStoreWeather(trackId,
                latitude, longitude)

            when (result) {
                is
                com.example.trackcast.data.network.NetworkResult.Success -> {
                    Log.d(TAG, "fetchWeatherForTrack: SUCCESS for track $trackId")
                    _operationStatus.value =
                        OperationStatus.Success("weather updated successfully")
                }
                is
                com.example.trackcast.data.network.NetworkResult.Error -> {
                    Log.e(TAG, "fetchWeatherForTrack: ERROR for track $trackId - ${result.message}")
                    _operationStatus.value =
                        OperationStatus.Error(result.message)
                }
                is
                com.example.trackcast.data.network.NetworkResult.Loading -> {
                    Log.d(TAG, "fetchWeatherForTrack: LOADING for track $trackId")
                    // already loading
                }
            }

            _isLoading.value = false
        }
    }

    /**
     * refresh stale weather data for all tracks
     * called on app startup to ensure fresh data
     */
    fun refreshStaleWeather(tracks:
                            List<com.example.trackcast.data.entities.RaceTrack>) {
        viewModelScope.launch {
            tracks.forEach { track ->
                val isStale =
                    weatherDataRepository.isWeatherStale(track.trackId)
                if (isStale) {
                    weatherDataRepository.fetchAndStoreWeather(track.trackId,
                        track.latitude, track.longitude)
                }
            }
        }
    }

    companion object {
        private const val TAG = "WeatherViewModel"
    }
}