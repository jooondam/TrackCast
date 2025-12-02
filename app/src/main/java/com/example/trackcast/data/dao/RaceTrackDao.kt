package com.example.trackcast.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.trackcast.data.entities.RaceTrack

@Dao
interface RaceTrackDao {
    // gets all the race tracks for a user
    @Query("SELECT * FROM race_tracks WHERE userId = :userId ORDER BY track_name ASC")
    fun getRaceTracks(userId: Int): LiveData<List<RaceTrack>>

    // gets a specific race track by its ID
    @Query("SELECT * FROM race_tracks WHERE trackId = :trackId")
    suspend fun getTrackById(trackId: Int): RaceTrack?

    @Insert
    suspend fun insert(track: RaceTrack): Long

    @Update
    suspend fun update(track: RaceTrack): Int

    @Delete
    suspend fun delete(track: RaceTrack): Int

    // searches race tracks by name
    @Query("SELECT * FROM race_tracks WHERE track_name LIKE :query ORDER BY track_name ASC")
    fun searchTracks(query: String): LiveData<List<RaceTrack>>

    // gets all the race tracks that are favorited by a user
    @Query("SELECT * FROM race_tracks WHERE userId = :userId AND is_favorite = 1 ORDER BY track_name ASC")
    fun getFavoriteTracks(userId: Int): LiveData<List<RaceTrack>>

}


