package com.example.trackcast

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TrackCastApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "TrackCast application started")
        // NEED TO: initialise analytics SDK
        // Initialize any necessary libraries or components here
    }

    companion object {
        private const val TAG = "TrackCastApp"
    }

}