package com.example.trackcast.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.trackcast.data.entities.WeatherData

@Dao
interface WeatherDataDao {

    // get all weather data for a track (for history/graph)
    @Query("SELECT * FROM weather_data WHERE trackId = :trackId ORDER BY timestamp DESC")
    fun getWeatherForTrack(trackId: Int): LiveData<List<WeatherData>>

    // get latest weather data for a track for current condition display
    @Query("SELECT * FROM weather_data WHERE trackId = :trackId ORDER BY timestamp DESC LIMIT 1")
    fun getLatestWeatherForTrack(trackId: Int): LiveData<WeatherData?>

    // get all latest weather data (one per track) for display in track list
    @Query("SELECT * FROM weather_data WHERE weatherId IN (SELECT MAX(weatherId) FROM weather_data GROUP BY trackId)")
    fun getAllLatestWeather(): LiveData<List<WeatherData>>

    // get weather by ID
    @Query("SELECT * FROM weather_data WHERE weatherId = :weatherId")
    suspend fun getWeatherById(weatherId: Int): WeatherData?

    // insert new weather when fetching from API
    @Insert
    suspend fun insert(weather: WeatherData): Long

    @Update
    suspend fun update(weather: WeatherData): Int

    @Delete
    suspend fun delete(weather: WeatherData): Int

    //delete old weather data (only keep last 100 records per track
    @Query("DELETE FROM weather_data WHERE trackId = :trackId AND weatherId NOT IN (SELECT weatherId FROM weather_data WHERE trackId = :trackId ORDER BY timestamp DESC LIMIT 100)")
    suspend fun deleteOldWeatherData(trackId: Int)



}