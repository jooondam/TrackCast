package com.example.trackcast.data.database.repository

import androidx.lifecycle.LiveData
import com.example.trackcast.data.dao.RaceTrackDao
import com.example.trackcast.data.entities.RaceTrack

class RaceTrackRepository(private val raceTrackDao: RaceTrackDao) {

    fun getRaceTracks(userId: Int): LiveData<List<RaceTrack>> {
        return raceTrackDao.getRaceTracks(userId)
    }

    suspend fun getTrackById(trackId: Int): RaceTrack? {
        return raceTrackDao.getTrackById(trackId)
    }

    suspend fun insert(track: RaceTrack): Long {
        return raceTrackDao.insert(track)
    }

    suspend fun update(track: RaceTrack) {
        raceTrackDao.update(track)
    }

    suspend fun delete(track: RaceTrack) {
        raceTrackDao.delete(track)
    }

    fun searchTracks(query: String): LiveData<List<RaceTrack>> {
        // add wildcard characters to the query for SQL LIKE
        val searchQuery = "%$query%"
        return raceTrackDao.searchTracks(searchQuery)
    }

    fun getFavoriteTracks(userId: Int): LiveData<List<RaceTrack>> {
        return raceTrackDao.getFavoriteTracks(userId)
    }

    // toggle favorite status
    suspend fun toggleFavorite(track: RaceTrack) {
        val updatedTrack = track.copy(isFavorite = !track.isFavorite)
        raceTrackDao.update(updatedTrack)
    }
}