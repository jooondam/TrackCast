package com.example.trackcast.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.trackcast.data.dao.RaceTrackDao
import com.example.trackcast.data.dao.UserDao
import com.example.trackcast.data.dao.WeatherDataDao
import com.example.trackcast.data.entities.RaceTrack
import com.example.trackcast.data.entities.User
import com.example.trackcast.data.entities.WeatherData

@Database(
    entities = [User::class, RaceTrack::class, WeatherData::class],
    version = 1,
    exportSchema = false
)

abstract class TrackCastDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun raceTrackDao(): RaceTrackDao
    abstract fun weatherDataDao(): WeatherDataDao

    companion object {
        @Volatile
        private var INSTANCE: TrackCastDatabase? = null

        fun getDatabase(context: Context): TrackCastDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                context.applicationContext,
                TrackCastDatabase::class.java,
                "trackcast_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}