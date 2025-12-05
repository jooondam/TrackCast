package com.example.trackcast.di

import com.example.trackcast.data.dao.RaceTrackDao
import com.example.trackcast.data.dao.UserDao
import com.example.trackcast.data.dao.WeatherDataDao
import com.example.trackcast.data.database.repository.RaceTrackRepository
import com.example.trackcast.data.database.repository.UserRepository
import com.example.trackcast.data.database.repository.WeatherDataRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideUserRepository(userDao: UserDao): UserRepository {
        return UserRepository(userDao)
    }

    @Provides
    @Singleton
    fun provideRaceTrackRepository(raceTrackDao: RaceTrackDao): RaceTrackRepository {
        return RaceTrackRepository(raceTrackDao)
    }

    @Provides
    @Singleton
    fun provideWeatherDataRepository(weatherDataDao: WeatherDataDao): WeatherDataRepository {
        return WeatherDataRepository(weatherDataDao)
    }
}