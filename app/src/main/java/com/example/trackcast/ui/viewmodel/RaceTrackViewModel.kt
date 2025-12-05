package com.example.trackcast.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.trackcast.data.database.repository.RaceTrackRepository
import com.example.trackcast.data.entities.RaceTrack
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RaceTrackViewModel @Inject constructor(
    private val raceTrackRepository: RaceTrackRepository
) : ViewModel() {

    // current user ID for loading tracks
    private val _currentUserId = MutableLiveData<Int>()

    // live list of all tracks for current user
    val raceTracks: LiveData<List<RaceTrack>> = _currentUserId.switchMap { userId ->
        raceTrackRepository.getRaceTracks(userId)
    }

    // live list of favorite tracks
    val favoriteTracks: LiveData<List<RaceTrack>> = _currentUserId.switchMap { userId ->
        raceTrackRepository.getFavoriteTracks(userId)
    }

    // search query
    private val _searchQuery = MutableLiveData<String>()

    // search results
    val searchResults: LiveData<List<RaceTrack>> = _searchQuery.switchMap { query ->
        if (query.isBlank()) {
            MutableLiveData(emptyList())
        } else {
            raceTrackRepository.searchTracks(query)
        }
    }

    // selected track for detail view
    private val _selectedTrack = MutableLiveData<RaceTrack?>()
    val selectedTrack: LiveData<RaceTrack?> = _selectedTrack

    // operation status
    private val _operationStatus = MutableLiveData<OperationStatus>()
    val operationStatus: LiveData<OperationStatus> = _operationStatus

    // set user ID to load tracks
    fun setUserId(userId: Int) {
        _currentUserId.value = userId
    }

    // search tracks
    fun searchTracks(query: String) {
        _searchQuery.value = query
    }

    // select track for detail view
    fun selectTrack(trackId: Int) {
        viewModelScope.launch {
            try {
                val track = raceTrackRepository.getTrackById(trackId)
                _selectedTrack.value = track
            } catch (e: Exception) {
                _operationStatus.value = OperationStatus.Error(e.message ?: "Failed to load track")
            }
        }
    }

    // add new track
    fun addTrack(track: RaceTrack) {
        viewModelScope.launch {
            try {
                val trackId = raceTrackRepository.insert(track)
                if (trackId > 0) {
                    _operationStatus.value = OperationStatus.Success("Track added successfully")
                } else {
                    _operationStatus.value = OperationStatus.Error("Failed to add track")
                }
            } catch (e: Exception) {
                _operationStatus.value = OperationStatus.Error(e.message ?: "Failed to add track")
            }
        }
    }

    // update existing track
    fun updateTrack(track: RaceTrack) {
        viewModelScope.launch {
            try {
                raceTrackRepository.update(track)
                _operationStatus.value = OperationStatus.Success("Track updated successfully")
            } catch (e: Exception) {
                _operationStatus.value = OperationStatus.Error(e.message ?: "Failed to update track")
            }
        }
    }

    // delete track
    fun deleteTrack(track: RaceTrack) {
        viewModelScope.launch {
            try {
                raceTrackRepository.delete(track)
                _operationStatus.value = OperationStatus.Success("Track deleted successfully")
            } catch (e: Exception) {
                _operationStatus.value = OperationStatus.Error(e.message ?: "Failed to delete track")
            }
        }
    }

    // toggle favorite status
    fun toggleFavorite(track: RaceTrack) {
        viewModelScope.launch {
            try {
                raceTrackRepository.toggleFavorite(track)
                val message = if (track.isFavorite) {
                    "Removed from favorites"
                } else {
                    "Added to favorites"
                }
                _operationStatus.value = OperationStatus.Success(message)
            } catch (e: Exception) {
                _operationStatus.value = OperationStatus.Error(e.message ?: "Failed to update favorite")
            }
        }
    }

    // clear selected track
    fun clearSelectedTrack() {
        _selectedTrack.value = null
    }

    // clear search results
    fun clearSearch() {
        _searchQuery.value = ""
    }

    // sealed class for operation status
    sealed class OperationStatus {
        data class Success(val message: String) : OperationStatus()
        data class Error(val message: String) : OperationStatus()
    }
}
