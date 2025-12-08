package com.example.trackcast.util

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

/**
 * Utility class for managing theme preferences
 * Uses SharedPreferences to persist user's theme choice
 */
object ThemePreferences {
    private const val PREFS_NAME = "theme_prefs"
    private const val KEY_THEME_MODE = "theme_mode"

    // Theme mode constants
    const val MODE_LIGHT = AppCompatDelegate.MODE_NIGHT_NO
    const val MODE_DARK = AppCompatDelegate.MODE_NIGHT_YES
    const val MODE_SYSTEM = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Save the theme mode preference
     */
    fun saveThemeMode(context: Context, mode: Int) {
        getPreferences(context).edit().putInt(KEY_THEME_MODE, mode).apply()
    }

    /**
     * Get the saved theme mode, defaults to system theme
     */
    fun getThemeMode(context: Context): Int {
        return getPreferences(context).getInt(KEY_THEME_MODE, MODE_SYSTEM)
    }

    /**
     * Apply the saved theme
     */
    fun applyTheme(context: Context) {
        val mode = getThemeMode(context)
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    /**
     * Check if dark mode is currently active
     */
    fun isDarkMode(context: Context): Boolean {
        val mode = getThemeMode(context)
        return when (mode) {
            MODE_DARK -> true
            MODE_LIGHT -> false
            else -> {
                // Check system setting
                val nightMode = context.resources.configuration.uiMode and
                    android.content.res.Configuration.UI_MODE_NIGHT_MASK
                nightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES
            }
        }
    }

    /**
     * Toggle between light and dark mode
     */
    fun toggleTheme(context: Context) {
        val currentMode = getThemeMode(context)
        val newMode = if (isDarkMode(context)) MODE_LIGHT else MODE_DARK
        saveThemeMode(context, newMode)
        AppCompatDelegate.setDefaultNightMode(newMode)
    }
}
