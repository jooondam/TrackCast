package com.example.trackcast

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.trackcast.data.entities.RaceTrack
import com.example.trackcast.databinding.ActivityAddTrackBinding
import com.example.trackcast.ui.viewmodel.RaceTrackViewModel
import com.example.trackcast.ui.viewmodel.WeatherViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddTrackActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTrackBinding
    private val viewModel: RaceTrackViewModel by viewModels()
    private val weatherViewModel: WeatherViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var editingTrackId: Int? = null
    private var isEditMode = false
    private var pendingWeatherFetch: Pair<Double, Double>? = null

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                getCurrentLocation()
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                getCurrentLocation()
            }
            else -> {
                Snackbar.make(binding.root, "location permission denied", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddTrackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setupToolbar()
        setupButtons()
        observeViewModel()

        // check if editing existing track
        editingTrackId = intent.getIntExtra("TRACK_ID", -1).takeIf { it != -1 }
        editingTrackId?.let { trackId ->
            isEditMode = true
            binding.toolbar.title = getString(R.string.edit_track_title)
            loadTrackData(trackId)
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupButtons() {
        binding.buttonCancel.setOnClickListener {
            finish()
        }

        binding.buttonSave.setOnClickListener {
            saveTrack()
        }

        binding.buttonUseCurrentLocation.setOnClickListener {
            requestLocationPermission()
        }
    }

    private fun observeViewModel() {
        viewModel.selectedTrack.observe(this) { track ->
            track?.let { populateFields(it) }
        }

        viewModel.operationStatus.observe(this) { status ->
            when (status) {
                is RaceTrackViewModel.OperationStatus.Success -> {
                    // fetch weather for newly added/updated track
                    pendingWeatherFetch?.let { (lat, lon) ->
                        // get the track id from the success message or use editingTrackId
                        val trackId = editingTrackId ?: viewModel.raceTracks.value?.lastOrNull()?.trackId
                        trackId?.let { id ->
                            weatherViewModel.fetchWeatherForTrack(id, lat, lon)
                        }
                        pendingWeatherFetch = null
                    }

                    setResult(RESULT_OK)
                    finish()
                }
                is RaceTrackViewModel.OperationStatus.Error -> {
                    Snackbar.make(binding.root, "error: ${status.message}", Snackbar.LENGTH_LONG).show()
                }
            }
        }

        // observe weather fetch status
        weatherViewModel.operationStatus.observe(this) { status ->
            when (status) {
                is WeatherViewModel.OperationStatus.Success -> {
                    // weather fetched successfully, no action needed (will show in track list)
                }
                is WeatherViewModel.OperationStatus.Error -> {
                    // weather fetch failed, but don't block user from continuing
                    // they can manually refresh later
                }
            }
        }
    }

    private fun loadTrackData(trackId: Int) {
        viewModel.selectTrack(trackId)
    }

    private fun populateFields(track: RaceTrack) {
        binding.apply {
            editTextTrackName.setText(track.trackName)
            editTextLocation.setText(track.location)
            editTextCountry.setText(track.country)
            editTextLatitude.setText(track.latitude.toString())
            editTextLongitude.setText(track.longitude.toString())
            editTextImageUrl.setText(track.imageUrl ?: "")
        }
    }

    private fun saveTrack() {
        binding.apply {
            val trackName = editTextTrackName.text.toString().trim()
            val location = editTextLocation.text.toString().trim()
            val country = editTextCountry.text.toString().trim()
            val latitudeStr = editTextLatitude.text.toString().trim()
            val longitudeStr = editTextLongitude.text.toString().trim()
            val imageUrl = editTextImageUrl.text.toString().trim().ifEmpty { null }

            // validate inputs
            if (trackName.isEmpty()) {
                textInputLayoutTrackName.error = getString(R.string.error_empty_track_name)
                return
            }
            if (location.isEmpty()) {
                textInputLayoutLocation.error = getString(R.string.error_empty_location)
                return
            }
            if (latitudeStr.isEmpty() || longitudeStr.isEmpty()) {
                Snackbar.make(root, getString(R.string.error_invalid_coordinates), Snackbar.LENGTH_SHORT).show()
                return
            }

            val latitude = latitudeStr.toDoubleOrNull()
            val longitude = longitudeStr.toDoubleOrNull()

            if (latitude == null || longitude == null) {
                Snackbar.make(root, getString(R.string.error_invalid_coordinates), Snackbar.LENGTH_SHORT).show()
                return
            }

            // create or update track
            val track = RaceTrack(
                trackId = editingTrackId ?: 0,
                userId = 1, // todo: get from actual user session
                trackName = trackName,
                location = location,
                country = country,
                latitude = latitude,
                longitude = longitude,
                imageUrl = imageUrl
            )

            // store coordinates for weather fetch after successful save
            pendingWeatherFetch = Pair(latitude, longitude)

            if (isEditMode) {
                viewModel.updateTrack(track)
            } else {
                viewModel.addTrack(track)
            }
        }
    }

    private fun requestLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                getCurrentLocation()
            }
            else -> {
                locationPermissionRequest.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    private fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                binding.editTextLatitude.setText(it.latitude.toString())
                binding.editTextLongitude.setText(it.longitude.toString())
                Snackbar.make(binding.root, "location updated", Snackbar.LENGTH_SHORT).show()
            } ?: run {
                Snackbar.make(binding.root, "unable to get location", Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}
