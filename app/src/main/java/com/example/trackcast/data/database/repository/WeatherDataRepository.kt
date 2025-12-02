package com.example.trackcast.data.database.repository

import androidx.lifecycle.LiveData
import com.example.trackcast.data.dao.WeatherDataDao
import com.example.trackcast.data.entities.WeatherData

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