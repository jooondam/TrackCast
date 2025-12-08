package com.example.trackcast.data.network

import android.util.Log

/**
 * Sealed class for handling network operation results
 * Provides type-safe error handling for API calls
 */
sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(val message: String, val code: Int? = null) :
        NetworkResult<Nothing>()
    object Loading : NetworkResult<Nothing>()
}

/**
 * Extension function for safe API calls with error handling
 * Wraps suspend API calls in try-catch and returns NetworkResult
 */
suspend fun <T> safeApiCall(apiCall: suspend () -> T):
        NetworkResult<T> {
    return try {
        Log.d(TAG, "safeApiCall: Making API call...")
        val result = apiCall()
        Log.d(TAG, "safeApiCall: API call successful")
        NetworkResult.Success(result)
    } catch (e: retrofit2.HttpException) {
        val errorMessage = when (e.code()) {
            401, 403 -> "Invalid API key"
            404 -> "Location not found"
            429 -> "Rate limit exceeded"
            else -> "Network error: ${e.message()}"
        }
        Log.e(TAG, "safeApiCall: HttpException - Code ${e.code()}: $errorMessage")
        NetworkResult.Error(errorMessage, e.code())
    } catch (e: java.io.IOException) {
        Log.e(TAG, "safeApiCall: IOException - ${e.message}")
        NetworkResult.Error("Network connection failed. Check your internet.")
    } catch (e: Exception) {
        Log.e(TAG, "safeApiCall: Exception - ${e.javaClass.simpleName}: ${e.localizedMessage}")
        NetworkResult.Error("Unknown error: ${e.localizedMessage}")
    }
}

private const val TAG = "NetworkResult"