package com.example.trackcast.data.database.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.trackcast.BuildConfig
import com.example.trackcast.data.dao.WeatherDataDao
import com.example.trackcast.data.entities.WeatherData
import com.example.trackcast.data.mapper.WeatherMapper
import com.example.trackcast.data.network.NetworkResult
import com.example.trackcast.data.network.WeatherApiService
import com.example.trackcast.data.network.safeApiCall
import javax.inject.Inject

/**
 * Repository for weather data operations.
 *
 * Handles integration with WeatherAPI.com for fetching real-world weather data
 * and provides data access abstraction for ViewModels.
 *
 * API Selection: WeatherAPI.com was chosen over OpenWeatherMap due to:
 * - Higher rate limits (1M calls/month vs 30K/month)
 * - Direct access to UV index and cloud cover for track surface temp calculations
 * - Simpler JSON response structure for racing-specific metrics
 *
 * API Endpoint: https://api.weatherapi.com/v1/current.json
 * Authentication: API key via BuildConfig (stored in local.properties)
 * Rate Limits: 1,000,000 calls/month on free tier
 *
 * Data Staleness: Weather data older than 1 hour is considered stale and triggers refresh
 *
 * @see com.example.trackcast.ui.viewmodel.WeatherViewModel for fetch trigger points
 * @see com.example.trackcast.data.network.WeatherApiService for API interface definition
 *
 * Reference: API selection rationale documented in TrackCast.md
 * API documentation: https://www.weatherapi.com/docs/
 */
class WeatherDataRepository @Inject constructor(
    private val weatherDataDao: WeatherDataDao,
    private val weatherApiService: WeatherApiService
) {

    fun getWeatherForTrack(trackId: Int): LiveData<List<WeatherData>> {
        return weatherDataDao.getWeatherForTrack(trackId)
    }

    fun getLatestWeatherForTrack(trackId: Int): LiveData<WeatherData?> {
        return weatherDataDao.getLatestWeatherForTrack(trackId)
    }

    fun getAllLatestWeather(): LiveData<List<WeatherData>> {
        return weatherDataDao.getAllLatestWeather()
    }

    suspend fun getWeatherById(weatherId: Int): WeatherData? {
        return weatherDataDao.getWeatherById(weatherId)
    }

    suspend fun insert(weather: WeatherData): Long {
        return weatherDataDao.insert(weather)
    }

    suspend fun update(weather: WeatherData) {
        weatherDataDao.update(weather)
    }

    suspend fun delete(weather: WeatherData) {
        weatherDataDao.delete(weather)
    }

    suspend fun deleteOldWeatherData(trackId: Int) {
        weatherDataDao.deleteOldWeatherData(trackId)
    }

    // insert new weather and clean up old data in one operation
    suspend fun insertAndCleanup(weather: WeatherData): Long {
        val weatherId = weatherDataDao.insert(weather)
        weatherDataDao.deleteOldWeatherData(weather.trackId)
        return weatherId
    }

    // check if weather data is stale
    suspend fun isWeatherStale(trackId: Int, maxAgeHours: Int = 1): Boolean {
        val latestWeather = weatherDataDao.getWeatherById(trackId)
        if (latestWeather == null) {
            return true
        }

        val currentTime = System.currentTimeMillis()
        val weatherAge = currentTime - latestWeather.timestamp
        val maxAgeMillis = maxAgeHours * 60 * 60 * 1000

        return weatherAge > maxAgeMillis
    }

    /**
     * fetch weather from WeatherAPI.com and store in database
     */
    suspend fun fetchAndStoreWeather(
        trackId: Int,
        latitude: Double,
        longitude: Double
    ): NetworkResult<WeatherData> {
        val location = "$latitude,$longitude"
        Log.d(TAG, "fetchAndStoreWeather: Calling API for track $trackId at location $location")
        Log.d(TAG, "fetchAndStoreWeather: API key starts with: ${BuildConfig.WEATHER_API_KEY.take(10)}...")

        return when (val result = safeApiCall {
            weatherApiService.getCurrentWeather(
                apiKey = BuildConfig.WEATHER_API_KEY,
                location = location
            )
        }) {
            is NetworkResult.Success -> {
                Log.d(TAG, "fetchAndStoreWeather: API call SUCCESS for track $trackId")
                val weatherData = WeatherMapper.mapToWeatherData(result.data, trackId)
                Log.d(TAG, "fetchAndStoreWeather: Mapped weather data - Air: ${weatherData.temperature}°C, Track: ${weatherData.trackSurfaceTemp}°C")
                val weatherId = insertAndCleanup(weatherData)
                Log.d(TAG, "fetchAndStoreWeather: Stored in database with ID $weatherId")
                NetworkResult.Success(weatherData)
            }
            is NetworkResult.Error -> {
                Log.e(TAG, "fetchAndStoreWeather: API call FAILED for track $trackId - ${result.message}")
                result
            }
            is NetworkResult.Loading -> {
                Log.d(TAG, "fetchAndStoreWeather: API call LOADING for track $trackId")
                result
            }
        }
    }

    companion object {
        private const val TAG = "WeatherDataRepository"
    }
}