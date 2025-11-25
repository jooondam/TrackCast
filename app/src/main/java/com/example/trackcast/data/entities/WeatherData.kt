package com.example.trackcast.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "weather_data",
    foreignKeys = [
        ForeignKey(
            entity = RaceTrack::class,
            parentColumns = ["trackId"],
            childColumns = ["trackId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["trackId"])]
)
data class WeatherData(
    @PrimaryKey(autoGenerate = true)
    val weatherId: Int = 0,

    @ColumnInfo(name = "trackId")
    val trackId: Int,

    @ColumnInfo(name = "temperature")
    val temperature: Double,

    @ColumnInfo(name = "track_surface_temp")
    val trackSurfaceTemp: Double,

    @ColumnInfo(name = "humidity")
    val humidity: Int,

    @ColumnInfo(name = "wind_speed")
    val windSpeed: Double,

    @ColumnInfo(name = "wind_direction")
    val windDirection: String,

    @ColumnInfo(name = "conditions")
    val conditions: String,

    @ColumnInfo(name = "is_drying")
    val isDrying: Boolean = false,

    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis()
)