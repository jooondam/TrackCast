package com.example.trackcast.data.entities


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")

data class User(
    @PrimaryKey(autoGenerate = true)
    val userId: Int = 0,

    @ColumnInfo(name = "username")
    val username: String,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "password")
    val password: String,

    @ColumnInfo(name = "temperature_unit")
    val temperatureUnit: String = "Celsius",

    @ColumnInfo(name = "wind_speed_unit")
    val windSpeedUnit: String = "kph",

    @ColumnInfo(name = "date_joined")
    val dateJoined: Long = System.currentTimeMillis()
)