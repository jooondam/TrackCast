package com.example.trackcast.data.mapper

import com.example.trackcast.data.entities.WeatherData
import com.example.trackcast.data.network.dto.WeatherApiResponse
import kotlin.math.max

/**
 * maps WeatherAPI.com response to WeatherData entity
 * includes track surface temperature estimation algorithm
 */
object WeatherMapper {

    /**
     * convert API response to WeatherData entity for database
    storage
     *
     * @param response WeatherAPI.com response
     * @param trackId Associated track ID
     * @return WeatherData entity ready for Room database
     */
    fun mapToWeatherData(response: WeatherApiResponse, trackId:
    Int): WeatherData {
        val current = response.current

        return WeatherData(
            trackId = trackId,
            temperature = current.tempC,
            trackSurfaceTemp = estimateTrackSurfaceTemp(
                airTemp = current.tempC,
                uvIndex = current.uv,
                cloudCover = current.cloud,
                humidity = current.humidity
            ),
            humidity = current.humidity,
            windSpeed = current.windKph,
            windDirection = current.windDir,
            conditions = current.condition.text,
            isDrying = isDryingCondition(current.condition.text,
                current.humidity),
            timestamp = System.currentTimeMillis()
        )
    }

    /**
     * estimate track surface temperature based on weather
    conditions
     * formula: air temp + solar heat gain - humidity cooling effect
     *
     * racing-specific calculation considering:
     * - UV index (solar radiation intensity)
     * - cloud cover (shade reduction)
     * - humidity (evaporative cooling)
     */
    private fun estimateTrackSurfaceTemp(
        airTemp: Double,
        uvIndex: Double,
        cloudCover: Int,
        humidity: Int
    ): Double {
        // solar heat gain: UV index contributes to surface heating
        // cloud cover reduces solar radiation (0% clouds = full sun, 100% = overcast)
        val solarHeatGain = (uvIndex * 3.0) * (1.0 - cloudCover / 100.0)

        // humidity cooling: higher humidity = less evaporative cooling
        val humidityCooling = (100 - humidity) / 100.0 * 2.0

        // track surface temp = air temp + solar heating - humidity effect
        val surfaceTemp = airTemp + solarHeatGain - humidityCooling

        // ensure surface temp is at least air temp (can't be cooler than air)
        return max(surfaceTemp, airTemp)
    }

    /**
     * determine if track is drying based on weather conditions
     * wet track dries when: low humidity + no rain/drizzle
     */
    private fun isDryingCondition(conditions: String, humidity:
    Int): Boolean {
        val wetConditions = listOf("rain", "drizzle", "shower",
            "thunderstorm")
        val isWet = wetConditions.any {
            conditions.lowercase().contains(it) }

        // track is drying if: not currently wet AND humidity < 70%
        return !isWet && humidity < 70
    }
}