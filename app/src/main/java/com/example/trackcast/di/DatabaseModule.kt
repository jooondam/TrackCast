package com.example.trackcast.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.trackcast.data.dao.RaceTrackDao
import com.example.trackcast.data.dao.UserDao
import com.example.trackcast.data.dao.WeatherDataDao
import com.example.trackcast.data.database.TrackCastDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// hilt module that provides database-related dependencies
// creates and provides the Room database instance and all DAOs
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    // provides singleton instance of TrackCast Room database
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): TrackCastDatabase {
        return Room.databaseBuilder(
            context,
            TrackCastDatabase::class.java,
            "trackcast_database"
        )
        .fallbackToDestructiveMigration()
        .addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // insert default user directly using SQL
                db.execSQL("INSERT INTO users (userId, username, email, password, temperature_unit, wind_speed_unit, date_joined) VALUES (1, 'default_user', 'user@trackcast.com', '', 'Celsius', 'kph', ${System.currentTimeMillis()})")
            }
        })
        .build()
    }

    // provides UserDao for user database operations
    @Provides
    fun provideUserDao(database: TrackCastDatabase): UserDao {
        return database.userDao()
    }

    // provides RaceTrackDao for race track database operations
    @Provides
    fun provideRaceTrackDao(database: TrackCastDatabase): RaceTrackDao {
        return database.raceTrackDao()
    }

    // provides WeatherDataDao for weather data database operations
    @Provides
    fun provideWeatherDataDao(database: TrackCastDatabase): WeatherDataDao {
        return database.weatherDataDao()
    }
}