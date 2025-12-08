package com.example.trackcast.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.trackcast.R
import com.example.trackcast.data.entities.RaceTrack
import com.example.trackcast.data.entities.WeatherData
import com.example.trackcast.databinding.ItemRaceTrackBinding

/* recyclerview adapter for race track list with ios-style glassmorphism design
   uses listadapter for efficient diffing, standard android pattern
   image loading powered by coil library: https://coil-kt.github.io/coil/ */
class TrackAdapter(
    private val onTrackClick: (RaceTrack) -> Unit,
    private val onFavoriteClick: (RaceTrack) -> Unit,
    private val getWeatherForTrack: (Int) -> WeatherData?
) : ListAdapter<RaceTrack, TrackAdapter.TrackViewHolder>(TrackDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding = ItemRaceTrackBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TrackViewHolder(binding, onTrackClick, onFavoriteClick, getWeatherForTrack)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TrackViewHolder(
        private val binding: ItemRaceTrackBinding,
        private val onTrackClick: (RaceTrack) -> Unit,
        private val onFavoriteClick: (RaceTrack) -> Unit,
        private val getWeatherForTrack: (Int) -> WeatherData?
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(track: RaceTrack) {
            binding.apply {
                textTrackName.text = track.trackName
                textLocation.text = track.location

                // set favorite icon with visual state
                iconFavorite.setImageResource(
                    if (track.isFavorite) android.R.drawable.star_big_on
                    else android.R.drawable.star_big_off
                )
                // change tint to show filled vs unfilled state more clearly
                iconFavorite.setColorFilter(
                    if (track.isFavorite) {
                        binding.root.context.getColor(R.color.favorite_gold)
                    } else {
                        binding.root.context.getColor(android.R.color.darker_gray)
                    }
                )

                // load track image with coil library for famous tracks like spa, le mans, road atlanta
                // coil provides efficient image loading with caching: https://coil-kt.github.io/coil/
                imageTrack.load(track.imageUrl) {
                    crossfade(true)
                    placeholder(R.drawable.ic_launcher_background)
                    error(R.drawable.ic_launcher_background)
                    transformations(RoundedCornersTransformation(16f))
                }

                // display real weather data from WeatherAPI.com
                val weather = getWeatherForTrack(track.trackId)
                if (weather != null) {
                    textTemperature.text = "${weather.temperature.toInt()}°c"
                    textSurfaceTemp.text = "${weather.trackSurfaceTemp.toInt()}°c"
                } else {
                    // fallback when no weather data available yet
                    textTemperature.text = "--°c"
                    textSurfaceTemp.text = "--°c"
                }

                // click listeners
                root.setOnClickListener { onTrackClick(track) }
                iconFavorite.setOnClickListener { onFavoriteClick(track) }
            }
        }
    }

    /* diffutil for efficient list updates
       standard recyclerview pattern from android docs */
    private class TrackDiffCallback : DiffUtil.ItemCallback<RaceTrack>() {
        override fun areItemsTheSame(oldItem: RaceTrack, newItem: RaceTrack): Boolean {
            return oldItem.trackId == newItem.trackId
        }

        override fun areContentsTheSame(oldItem: RaceTrack, newItem: RaceTrack): Boolean {
            return oldItem == newItem
        }
    }
}
