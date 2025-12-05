package com.example.trackcast.di

import android.content.Context
import androidx.room.Room
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

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

@Provides
@Singleton
fun provideDatabase(
    @ApplicationContext context: Context
): TrackCastDatabase {
    return Room.databaseBuilder(
        context,
        TrackCastDatabase::class.java,
        "trackcast_database"
    ).build()
}

@Provides
fun provideUserDao(database: TrackCastDatabase): UserDao {
    return database.userDao()
}

@Provides
fun provideRaceTrackDao(database: TrackCastDatabase):
        RaceTrackDao {
    return database.raceTrackDao()
}

@Provides
fun provideWeatherDataDao(database: TrackCastDatabase):
        WeatherDataDao {
    return database.weatherDataDao()
}
}