# TrackCast

An Android app for tracking live weather and track-surface conditions at your favorite race circuits.

## Screenshots

| Track list | Empty state | Add track |
|---|---|---|
| ![Track list](app/src/test/snapshots/images/com.example.trackcast.screenshot_ScreenshotTest_mainScreen_withTracks_main_screen_with_tracks.png) | ![Empty state](app/src/test/snapshots/images/com.example.trackcast.screenshot_ScreenshotTest_mainScreen_emptyState_main_screen_empty_state.png) | ![Add track](app/src/test/snapshots/images/com.example.trackcast.screenshot_ScreenshotTest_addTrackScreen_add_track_screen.png) |

## Features

- Save race tracks with location, coordinates, and an optional image
- Live air temperature and track-surface temperature per track
- Favorite tracks and filter the list down to favorites
- Search across saved tracks
- Use current device location when adding a track
- Light and dark themes

## Tech stack

- Kotlin, MVVM, Hilt for dependency injection
- Room for local persistence
- Retrofit + OkHttp for networking, [WeatherAPI.com](https://www.weatherapi.com/) for weather data
- Material 3 components with an iOS-inspired glassmorphism design
- Paparazzi for screenshot tests (renders app screens on the JVM, no device required)

## Getting started

### Requirements

- Android Studio (or the command-line SDK tools)
- JDK 17+
- A free [WeatherAPI.com](https://www.weatherapi.com/) API key

### Setup

1. Clone the repository.
2. Add your WeatherAPI.com key to `local.properties` (create the file if it doesn't exist):

   ```properties
   WEATHER_API_KEY=your_api_key_here
   ```

3. Open the project in Android Studio, or build from the command line:

   ```bash
   ./gradlew assembleDebug
   ```

The app requests location permission to support "use current location" when adding a track; it is optional and only used for that feature.

## Testing

Run unit and screenshot tests with:

```bash
./gradlew testDebugUnitTest
```

Screenshot tests live under `app/src/test/java/.../screenshot/` and their golden images under `app/src/test/snapshots/`. To update the golden images after a UI change:

```bash
./gradlew recordPaparazziDebug
```
