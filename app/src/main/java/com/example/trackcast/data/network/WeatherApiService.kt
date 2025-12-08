package com.example.trackcast.data.network

import com.example.trackcast.data.network.dto.WeatherApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * retrofit interface for WeatherAPI.com integration
 *
 * provides current weather data for race track locations
 * API documentation: https://www.weatherapi.com/docs/
 */
interface WeatherApiService {

    /**
     * fetch current weather for given coordinates
     *
     * @param apiKey API key from BuildConfig.WEATHER_API_KEY
     * @param location coordinates in format "lat,lon" (e.g., "50.4372,5.9714")
     * @return WeatherApiResponse with current weather data
     */
    @GET("current.json")
    suspend fun getCurrentWeather(
        @Query("key") apiKey: String,
        @Query("q") location: String
    ): WeatherApiResponse
}
