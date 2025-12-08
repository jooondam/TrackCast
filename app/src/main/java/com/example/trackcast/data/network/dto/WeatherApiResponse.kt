package com.example.trackcast.data.network.dto

import com.google.gson.annotations.SerializedName

/*
 Data Transfer Objects for WeatherAPI.com response
 API endpoint: https://api.weatherapi.com/v1/current.json
 */

data class WeatherApiResponse(
    @SerializedName("location")
    val location: LocationDto,

    @SerializedName("current")
    val current: CurrentWeatherDto
)

data class LocationDto(
    @SerializedName("name")
    val name: String,

    @SerializedName("region")
    val region: String,

    @SerializedName("country")
    val country: String,

    @SerializedName("lat")
    val lat: Double,

    @SerializedName("lon")
    val lon: Double,
)

data class CurrentWeatherDto(
    @SerializedName("temp_c")
    val tempC: Double,

    @SerializedName("temp_f")
    val tempF: Double,

    @SerializedName("condition")
    val condition: ConditionDto,

    @SerializedName("wind_kph")
    val windKph: Double,

    @SerializedName("wind_dir")
    val windDir: String,

    @SerializedName("humidity")
    val humidity: Int,

    @SerializedName("cloud")
    val cloud: Int,

    @SerializedName("feelslike_c")
    val feelslikeC: Double,

    @SerializedName("uv")
    val uv: Double
)

data class ConditionDto(
    @SerializedName("text")
    val text: String,

    @SerializedName("icon")
    val icon: String,

    @SerializedName("code")
    val code: Int
)

