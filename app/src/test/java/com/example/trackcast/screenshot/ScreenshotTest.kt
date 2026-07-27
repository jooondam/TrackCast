package com.example.trackcast.screenshot

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.example.trackcast.R
import org.junit.Rule
import org.junit.Test

private data class SampleTrack(
    val name: String,
    val location: String,
    val airTemp: String,
    val trackTemp: String,
    val favorite: Boolean,
)

private val sampleTracks = listOf(
    SampleTrack("Spa-Francorchamps", "Stavelot, Belgium", "18°c", "24°c", favorite = true),
    SampleTrack("Silverstone Circuit", "Silverstone, United Kingdom", "15°c", "19°c", favorite = false),
    SampleTrack("Suzuka Circuit", "Suzuka, Japan", "27°c", "35°c", favorite = false),
    SampleTrack("Circuit de Monaco", "Monte Carlo, Monaco", "22°c", "29°c", favorite = true),
)

class ScreenshotTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_6,
        theme = "Theme.TrackCast",
    )

    @Test
    fun mainScreen_withTracks() {
        val root = paparazzi.inflate<ViewGroup>(R.layout.activity_main)
        val recyclerContainer = root.findViewById<ViewGroup>(R.id.recyclerViewTracks)
        populateTrackList(recyclerContainer)
        paparazzi.snapshot(root, name = "main_screen_with_tracks")
    }

    @Test
    fun mainScreen_emptyState() {
        val root = paparazzi.inflate<ViewGroup>(R.layout.activity_main)
        root.findViewById<View>(R.id.recyclerViewTracks).visibility = View.GONE
        root.findViewById<View>(R.id.emptyStateLayout).visibility = View.VISIBLE
        paparazzi.snapshot(root, name = "main_screen_empty_state")
    }

    @Test
    fun addTrackScreen() {
        val root = paparazzi.inflate<ViewGroup>(R.layout.activity_add_track)
        paparazzi.snapshot(root, name = "add_track_screen")
    }

    /**
     * RecyclerView needs a real layout/measure pass to render via a LayoutManager, which
     * Paparazzi's single-frame snapshot doesn't drive. Swap it for a plain LinearLayout of
     * inflated item_race_track rows populated with sample data instead.
     */
    private fun populateTrackList(recyclerContainer: ViewGroup) {
        val parent = recyclerContainer.parent as ViewGroup
        val index = parent.indexOfChild(recyclerContainer)
        val params = recyclerContainer.layoutParams
        parent.removeView(recyclerContainer)

        val list = LinearLayout(paparazzi.context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = params
            id = R.id.recyclerViewTracks
        }
        val inflater = LayoutInflater.from(paparazzi.context)
        sampleTracks.forEach { track ->
            val item = inflater.inflate(R.layout.item_race_track, list, false)
            item.findViewById<TextView>(R.id.textTrackName).text = track.name
            item.findViewById<TextView>(R.id.textLocation).text = track.location
            item.findViewById<TextView>(R.id.textTemperature).text = track.airTemp
            item.findViewById<TextView>(R.id.textSurfaceTemp).text = track.trackTemp
            item.findViewById<ImageView>(R.id.iconFavorite).setImageResource(
                if (track.favorite) android.R.drawable.star_on else android.R.drawable.star_off
            )
            item.findViewById<ImageView>(R.id.imageTrack).setImageResource(android.R.drawable.ic_menu_gallery)
            list.addView(item)
        }
        parent.addView(list, index)
    }
}
