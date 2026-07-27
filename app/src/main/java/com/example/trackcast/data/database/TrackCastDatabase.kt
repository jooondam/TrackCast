package com.example.trackcast.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.trackcast.data.dao.RaceTrackDao
import com.example.trackcast.data.dao.UserDao
import com.example.trackcast.data.dao.WeatherDataDao
import com.example.trackcast.data.entities.RaceTrack
import com.example.trackcast.data.entities.User
import com.example.trackcast.data.entities.WeatherData

@Database(
    entities = [User::class, RaceTrack::class, WeatherData::class],
    version = 2,
    exportSchema = false
)
abstract class TrackCastDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun raceTrackDao(): RaceTrackDao
    abstract fun weatherDataDao(): WeatherDataDao
}