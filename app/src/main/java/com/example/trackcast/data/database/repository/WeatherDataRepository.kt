package com.example.trackcast.data.database.repository

import androidx.lifecycle.LiveData
import com.example.trackcast.data.dao.WeatherDataDao
import com.example.trackcast.data.entities.WeatherData

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
class WeatherDataRepository(private val weatherDataDao: WeatherDataDao) {

    fun getWeatherForTrack(trackId: Int): LiveData<List<WeatherData>> {
        return weatherDataDao.getWeatherForTrack(trackId)
    }

    fun getLatestWeatherForTrack(trackId: Int): LiveData<WeatherData?> {
        return weatherDataDao.getLatestWeatherForTrack(trackId)
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
}