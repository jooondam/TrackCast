package com.example.trackcast

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trackcast.data.entities.RaceTrack
import com.example.trackcast.data.entities.WeatherData
import com.example.trackcast.databinding.ActivityMainBinding
import com.example.trackcast.ui.adapter.SwipeToDeleteCallback
import com.example.trackcast.ui.adapter.TrackAdapter
import com.example.trackcast.ui.viewmodel.RaceTrackViewModel
import com.example.trackcast.ui.viewmodel.WeatherViewModel
import com.example.trackcast.util.ThemePreferences
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var trackAdapter: TrackAdapter
    private val viewModel: RaceTrackViewModel by viewModels()
    private val weatherViewModel: WeatherViewModel by viewModels()
    private var isShowingFavoritesOnly = false
    private var weatherDataMap = mapOf<Int, WeatherData>()
    private var suppressNextSuccessMessage = false

    /* ActivityResultLauncher for add/edit track functionality
       using modern Android API to replace deprecated startActivityForResult
       reference: https://developer.android.com/training/basics/intents/result */
    private val addEditTrackLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            // track was successfully added or edited, list will update automatically via LiveData
            Snackbar.make(binding.root, "track saved", Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Apply saved theme before calling super.onCreate()
        ThemePreferences.applyTheme(this)

        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: MainActivity starting")

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d(TAG, "onCreate: View binding complete")

        setSupportActionBar(binding.toolbar)
        setupRecyclerView()
        setupSwipeToDelete()
        setupFilters()
        setupFab()
        observeViewModel()

        // restore filter state on configuration change (e.g., screen rotation)
        // this ensures the user's filter preference is maintained across lifecycle events
        if (savedInstanceState != null) {
            isShowingFavoritesOnly = savedInstanceState.getBoolean(KEY_FAVORITES_FILTER, false)
            binding.chipFavorites.isChecked = isShowingFavoritesOnly
        }

        // set user id (for now using dummy id 1, later will come from auth)
        viewModel.setUserId(1)
        Log.d(TAG, "onCreate: User ID set")

        // fetch weather for all tracks on startup
        Log.d(TAG, "onCreate: About to call fetchWeatherForAllTracks()")
        fetchWeatherForAllTracks()
        Log.d(TAG, "onCreate: MainActivity onCreate complete")
    }

    private fun fetchWeatherForAllTracks() {
        Log.d(TAG, "fetchWeatherForAllTracks: Setting up observer")
        viewModel.raceTracks.observe(this) { tracks ->
            Log.d(TAG, "fetchWeatherForAllTracks: Received ${tracks.size} tracks")
            tracks.forEach { track ->
                Log.d(TAG, "fetchWeatherForAllTracks: Fetching weather for track ${track.trackId} - ${track.trackName} at (${track.latitude}, ${track.longitude})")
                weatherViewModel.fetchWeatherForTrack(
                    track.trackId,
                    track.latitude,
                    track.longitude
                )
            }
        }
    }

    private fun setupRecyclerView() {
        trackAdapter = TrackAdapter(
            onTrackClick = { track ->
                // open edit screen using ActivityResultLauncher
                val intent = Intent(this, AddTrackActivity::class.java)
                intent.putExtra("TRACK_ID", track.trackId)
                addEditTrackLauncher.launch(intent)
            },
            onFavoriteClick = { track ->
                toggleFavorite(track)
            },
            getWeatherForTrack = { trackId ->
                weatherDataMap[trackId]
            }
        )

        binding.recyclerViewTracks.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = trackAdapter
        }
    }

    /* swipe to delete implementation
       adapted from android itemtouchhelper docs */
    private fun setupSwipeToDelete() {
        val swipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                val track = trackAdapter.currentList[position]
                deleteTrack(track)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(binding.recyclerViewTracks)
    }

    private fun setupFilters() {
        binding.chipFavorites.setOnCheckedChangeListener { _, isChecked ->
            isShowingFavoritesOnly = isChecked
            // trigger list update by manually calling the observers
            if (isChecked) {
                viewModel.favoriteTracks.value?.let { updateTrackList(it) }
            } else {
                viewModel.raceTracks.value?.let { updateTrackList(it) }
            }
        }

        // todo: add search functionality
        binding.searchBar.setOnClickListener {
            Snackbar.make(binding.root, "search coming soon", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun setupFab() {
        binding.fabAddTrack.setOnClickListener {
            val intent = Intent(this, AddTrackActivity::class.java)
            addEditTrackLauncher.launch(intent)
        }
    }

    private fun observeViewModel() {
        // observe all latest weather data
        weatherViewModel.allLatestWeather.observe(this) { weatherList ->
            Log.d(TAG, "observeViewModel: Received ${weatherList.size} weather data items")
            weatherList.forEach { weather ->
                Log.d(TAG, "observeViewModel: Weather for track ${weather.trackId}: ${weather.temperature}°C air, ${weather.trackSurfaceTemp}°C track")
            }

            // create map of trackId -> latest weather data
            weatherDataMap = weatherList.associateBy { it.trackId }

            // refresh adapter to show updated weather
            trackAdapter.notifyDataSetChanged()
        }

        // observe all tracks
        viewModel.raceTracks.observe(this) { tracks ->
            if (!isShowingFavoritesOnly) {
                updateTrackList(tracks)
            }
        }

        // observe favorite tracks
        viewModel.favoriteTracks.observe(this) { favTracks ->
            if (isShowingFavoritesOnly) {
                updateTrackList(favTracks)
            }
        }

        // observe operation status
        viewModel.operationStatus.observe(this) { status ->
            when (status) {
                is RaceTrackViewModel.OperationStatus.Success -> {
                    // suppress success message if we're showing custom snackbar (like for delete with undo)
                    if (!suppressNextSuccessMessage) {
                        Snackbar.make(binding.root, status.message, Snackbar.LENGTH_SHORT).show()
                    }
                    suppressNextSuccessMessage = false
                }
                is RaceTrackViewModel.OperationStatus.Error -> {
                    Snackbar.make(binding.root, "error: ${status.message}", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun updateTrackList(tracks: List<RaceTrack>) {
        trackAdapter.submitList(tracks)
        updateEmptyState(tracks.isEmpty())
    }

    private fun toggleFavorite(track: RaceTrack) {
        viewModel.toggleFavorite(track)
    }

    private fun deleteTrack(track: RaceTrack) {
        // suppress the ViewModel's success message since we're showing our own with undo
        suppressNextSuccessMessage = true

        // show undo snackbar immediately (before calling viewModel)
        Snackbar.make(binding.root, "${track.trackName} deleted", Snackbar.LENGTH_LONG)
            .setAction("UNDO") {
                viewModel.addTrack(track)
            }.show()

        // delete from database
        viewModel.deleteTrack(track)
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        binding.emptyStateLayout.visibility = if (isEmpty) android.view.View.VISIBLE else android.view.View.GONE
        binding.recyclerViewTracks.visibility = if (isEmpty) android.view.View.GONE else android.view.View.VISIBLE
    }

    /* save instance state for activity lifecycle
       this preserves user's filter preference when activity is paused/resumed
       handles configuration changes like screen rotation
       reference: https://developer.android.com/guide/components/activities/activity-lifecycle */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_FAVORITES_FILTER, isShowingFavoritesOnly)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        updateThemeIcon(menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_theme_toggle -> {
                toggleTheme()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun toggleTheme() {
        ThemePreferences.toggleTheme(this)
        // The activity will be recreated automatically, applying the new theme
        recreate()
    }

    private fun updateThemeIcon(menu: Menu?) {
        val themeItem = menu?.findItem(R.id.action_theme_toggle)
        val isDark = ThemePreferences.isDarkMode(this)
        themeItem?.setIcon(if (isDark) R.drawable.ic_light_mode else R.drawable.ic_dark_mode)
        themeItem?.title = getString(if (isDark) R.string.light_mode else R.string.dark_mode)
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val KEY_FAVORITES_FILTER = "key_favorites_filter"
    }
}
