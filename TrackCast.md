# TrackCast - Mobile Software Development Project

## Project Overview

TrackCast is a weather tracking Android application for motorsport enthusiasts, built as part of Mobile Software Development coursework. The app provides weather tracking specifically tailored for race tracks, including track surface temperature and conditions relevant to motorsport.

**Package name:** `com.example.trackcast`
**Min SDK:** 24 (Android 7.0)
**Target SDK:** 36

---

## Assignment Requirements (MSD Final Assignment 2025/26)

### Critical Deadlines

- **Template Submission:** November 24, 2025, 11:20 PM (20% of assignment - 30 points)
- **Final App Submission:** December 9, 2025, 11:30 PM (80% of assignment - 70 points)
- **Late Penalty:** 10% per day (e.g., 1 day = -10%, 2 days = -20%, etc.)

### Mandatory Technical Requirements

The app MUST implement ALL of the following:

1. **Input Screen** - Where the user can enter data
2. **Local ROOM Database** - All data must be stored in a local Room database
3. **RecyclerView List** - Populated with data from the Room database
4. **Full CRUD Operations:**
   - **INSERT** - Add new records
   - **UPDATE** - Modify existing records
   - **SELECT** - Query and display records
   - **DELETE** - Remove records
5. **Activity Lifecycle** - Save previous information when activity is paused and restarted
6. **ActivityResultLauncher** - Implement at least once for inter-activity communication
7. **Minimum Screens** - At least 1 list screen, 1 input screen, 1 extra screen
8. **Two Additional Android Features** - Examples:
   - Camera API
   - Location Services
   - Creative GUIs with touch input
   - Maps integration
   - Notifications
   - Custom views/animations
9. **Demo Video** - 2-3 minute video showing all functionalities (voice optional)

**IMPORTANT NOTES:**
- User login functionality does NOT count towards CRUD requirements
- Database code MUST use Room classes (not raw SQL)
- All code must be well-commented
- External code snippets MUST be referenced with comments

### Grading Breakdown (100 points total)

**Accuracy/Completeness (40 points):**
- List screen with RecyclerView: 12 points
- Input screen: 12 points
- Update functionality: 8 points
- Delete functionality: 8 points

**UI Quality (20 points):**
- Ease of use
- Neat layout
- Visual appeal and viability
- Follow Nielsen's 10 usability heuristics: https://www.nngroup.com/articles/ten-usability-heuristics/

**Overall Quality & Complexity (40 points):**
- App functionality and robustness
- Extra features beyond standard requirements
- Complexity of implementation

### Complexity Guidelines

**Low Complexity:**
- Single database table
- Basic CRUD with minimal UI
- Simple extra features (DatePicker, menus)
- Example: Contact manager app

**Medium Complexity:**
- Multiple database tables with relationships
- Maps integration or advanced UI widgets
- Search/filter functionality
- Algorithmic complexity (calculations)
- Example: Schools of Ireland with map visualization

**High Complexity:**
- Cloud integration (web scraping, external APIs)
- Complex features (fragments, reverse geocoding)
- Sophisticated algorithms (price comparisons, recommendations)
- Professional-grade UI/UX
- Example: Property sales tracker with cloud sync

**TrackCast Target:** Medium to High Complexity

---

## Design Documentation Requirements

The template submission (20% - due Nov 24) must include:

1. **Use Cases** - User interactions with the app
2. **App Description** - What the app does and why
3. **Screen Flow Diagrams** - Navigation between screens
4. **Class Diagrams** - Object-oriented structure
5. **Database ERD** - Entity Relationship Diagram showing tables and relationships
6. **Screen Mockups** - UI designs with navigation
7. **Technical Architecture** - System architecture (if using cloud)

**Design Tools:**
- Draw.io: https://app.diagrams.net/
- Lucidchart: https://www.lucidchart.com/pages/er-diagrams
- dbdiagram.io: https://dbdiagram.io/home
- StarUML: https://staruml.io/
- Balsamiq (screen mockups)

**Design Quality Tips:**
- Use colors, nice formatting, quality images
- Ensure clarity and level of detail
- Hand-drawn diagrams must be legible
- Minor adjustments to design in final app are allowed

---

## Code Standards

Follow Google's Java/Kotlin Style Guide: https://google.github.io/styleguide/javaguide.html

**Requirements:**
- Indented code
- Comment header blocks for files
- Tidy code with proper naming standards
- Appropriate inline comments

**Referencing External Code:**
All code snippets from online sources or books MUST be referenced:

```kotlin
// Reference: The following code is from Android example @www.example.com
val intent = Intent(this, SecondActivity::class.java)
startActivity(intent)
// Reference complete
```

**Plagiarism Warning:**
- All work must be your own
- Copied or AI-written assignments receive zero marks
- Violations escalated per TU Dublin regulations

---

## Build Commands

```bash
# Build the project
./gradlew build

# Run unit tests
./gradlew test

# Run instrumented tests (requires connected device/emulator)
./gradlew connectedAndroidTest

# Install debug APK on connected device
./gradlew installDebug

# Clean build
./gradlew clean

# Run specific test class
./gradlew test --tests com.example.trackcast.ExampleUnitTest

# Run specific instrumented test
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.trackcast.ExampleInstrumentedTest
```

---

## Architecture

This project follows **MVVM (Model-View-ViewModel)** architecture:

```
app/src/main/java/com/example/trackcast/
├── data/
│   ├── entities/       # Room database entities
│   ├── dao/           # Data Access Objects (to be implemented)
│   ├── database/      # Database instance (to be implemented)
│   └── repository/    # Repository pattern (to be implemented)
├── ui/
│   ├── tracks/        # Track list screen with RecyclerView (to be implemented)
│   ├── addtrack/      # Add track input screen (to be implemented)
│   ├── trackdetail/   # Track detail/edit screen (to be implemented)
│   └── viewmodels/    # ViewModels (to be implemented)
├── utils/             # Utility classes (to be implemented)
└── MainActivity.kt
```

### Database Architecture

The app uses **Room** with a three-entity relational structure:

1. **User** (users table) - Base entity for user accounts
   - Stores credentials, preferences (temperature/wind speed units)
   - Primary key: `userId` (auto-generated)
   - Fields: username, email, password, temperatureUnit, windSpeedUnit, dateJoined

2. **RaceTrack** (race_tracks table) - Tracks saved by users
   - Foreign key to User with CASCADE delete
   - Primary key: `trackId` (auto-generated)
   - Indexed on: `userId`
   - Fields: trackName, location, latitude, longitude, country, isFavorite, dateAdded

3. **WeatherData** (weather_data table) - Weather readings for tracks
   - Foreign key to RaceTrack with CASCADE delete
   - Primary key: `weatherId` (auto-generated)
   - Indexed on: `trackId`
   - Motorsport-specific fields: temperature, trackSurfaceTemp, humidity, windSpeed, windDirection, conditions, isDrying, timestamp

**Key Relationships:**
- User → RaceTrack (one-to-many)
- RaceTrack → WeatherData (one-to-many)
- CASCADE delete: Deleting a User removes all their RaceTracks and WeatherData
- CASCADE delete: Deleting a RaceTrack removes all its WeatherData

---

## Tech Stack

**Core:**
- Kotlin 2.0.21
- Android Gradle Plugin 8.13.1
- ViewBinding enabled

**Database & Persistence:**
- Room 2.6.1 with KSP annotation processing
- Lifecycle components (ViewModel, LiveData) 2.8.7

**Async & Concurrency:**
- Kotlin Coroutines 1.9.0

**Networking:**
- Retrofit 2.11.0 with Gson converter
- OkHttp 4.12.0 with logging interceptor

**Location Services:**
- Google Play Services Location 21.3.0

**Testing:**
- JUnit 4.13.2 (unit tests)
- AndroidX Test with Espresso (instrumented tests)

---

## Dependency Management

Dependencies are centralized in `gradle/libs.versions.toml` using version catalogs.

**Adding new dependencies:**

1. Add version to `[versions]` section
2. Add library to `[libraries]` section
3. Reference in `app/build.gradle.kts` using `libs.<name>`

Example:
```toml
[versions]
glide = "4.15.1"

[libraries]
glide = { group = "com.github.bumptech.glide", name = "glide", version.ref = "glide" }
```

Then in `app/build.gradle.kts`:
```kotlin
implementation(libs.glide)
```

---

## Key Implementation Notes

### Room Database

- KSP is used for Room annotation processing (NOT KAPT)
- Foreign key constraints enforce referential integrity
- All entities use auto-generated integer primary keys
- Timestamps stored as Long (Unix epoch milliseconds)
- Must implement DAOs with @Insert, @Update, @Delete, @Query

**DAO Example:**
```kotlin
@Dao
interface RaceTrackDao {
    @Query("SELECT * FROM race_tracks WHERE userId = :userId ORDER BY trackName ASC")
    fun getTracksByUser(userId: Int): LiveData<List<RaceTrack>>

    @Query("SELECT * FROM race_tracks WHERE trackId = :trackId")
    suspend fun getTrackById(trackId: Int): RaceTrack?

    @Insert
    suspend fun insert(track: RaceTrack): Long

    @Update
    suspend fun update(track: RaceTrack)

    @Delete
    suspend fun delete(track: RaceTrack)
}
```

### ViewBinding

- Enabled in `app/build.gradle.kts`
- Used for type-safe view access
- Binding classes generated automatically on build

**Usage Example:**
```kotlin
class TrackListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTrackListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrackListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }
}
```

### Coroutines Integration

- Room supports coroutines with `room-ktx` dependency
- DAOs use `suspend` functions for async operations
- ViewModels use `viewModelScope` for launching coroutines

### RecyclerView Implementation

- Required for list screens (Requirement #3)
- Implement ViewHolder pattern
- Use DiffUtil for efficient updates
- Observe LiveData from ViewModel to update adapter

### ActivityResultLauncher

- Replaces deprecated `startActivityForResult`
- Required for inter-activity communication
- Use for passing data between activities

**Example:**
```kotlin
private val editTrackLauncher = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult()
) { result ->
    if (result.resultCode == RESULT_OK) {
        // Handle result from edit activity
        val updatedTrack = result.data?.getParcelableExtra<RaceTrack>("track")
        // Update UI
    }
}

// Launch edit activity
val intent = Intent(this, EditTrackActivity::class.java)
intent.putExtra("trackId", trackId)
editTrackLauncher.launch(intent)
```

---

## Development Workflow

### Implementing New Features

1. **Database Changes:**
   - Update entities in `data/entities/`
   - Create/update DAOs in `data/dao/`
   - Update database version and add migrations if needed

2. **Repository Pattern:**
   - Create repository in `data/repository/`
   - Abstract data sources (database, network)

3. **ViewModel:**
   - Create in `ui/viewmodels/`
   - Expose LiveData or StateFlow
   - Use `viewModelScope` for coroutines

4. **UI Layer:**
   - Activities/Fragments with ViewBinding
   - RecyclerView for lists
   - Observe ViewModel data

5. **Navigation:**
   - Use ActivityResultLauncher for communication
   - Save/restore state in lifecycle methods

---

## Features to Implement

### Core Features (Required)

✅ **Entities Created:**
- User entity
- RaceTrack entity
- WeatherData entity

🔲 **To Implement:**
1. **DAOs** - Data Access Objects for all entities
2. **Database Instance** - Room database singleton
3. **Repository Layer** - Abstract data access
4. **ViewModels** - Business logic and UI state
5. **Track List Screen** - RecyclerView with all tracks
6. **Add Track Screen** - Input form for new tracks
7. **Track Detail/Edit Screen** - View and update track details
8. **Delete Functionality** - Swipe-to-delete or menu option
9. **Search/Filter** - Filter tracks by name, location, favorites
10. **Activity Lifecycle** - Save/restore state properly

### Extra Android Features (Choose 2+)

Options for meeting Requirement #8:

1. **Location Services** ⭐
   - Auto-populate track coordinates using GPS
   - Reverse geocoding for address lookup
   - Current location on maps

2. **Weather API Integration** ⭐
   - Fetch real weather data for tracks
   - Display current conditions
   - Weather forecasts

3. **Google Maps Integration**
   - Display tracks on map
   - Map markers for each track
   - Tap markers to view details

4. **Camera**
   - Add photos of race tracks
   - Gallery for track images

5. **Notifications**
   - Weather alerts for favorite tracks
   - Scheduled notifications

6. **Custom UI/Touch**
   - Swipe gestures
   - Custom animations
   - Touch-based interactions

**Planned Features for TrackCast:**
- ✅ Location Services (Google Play Services Location)
- ✅ Weather API Integration (Retrofit + Weather API)
- Maps Integration (optional)
- Advanced UI with Material Design

---

## Testing Strategy

### Unit Tests (`app/src/test/`)

Test business logic without Android dependencies:
- ViewModel logic
- Repository pattern
- Data transformations
- Utility functions

### Instrumented Tests (`app/src/androidTest/`)

Test with Android framework:
- Room database operations
- DAO queries
- UI interactions with Espresso
- RecyclerView functionality

**Room Testing Example:**
```kotlin
@Test
fun insertAndReadTrack() = runBlocking {
    val track = RaceTrack(
        userId = 1,
        trackName = "Silverstone",
        location = "UK",
        latitude = 52.0786,
        longitude = -1.0169,
        country = "United Kingdom"
    )

    trackDao.insert(track)
    val tracks = trackDao.getTracksByUser(1).getOrAwaitValue()

    assertThat(tracks, hasItem(track))
}
```

**Test Coverage Requirements:**
- Test all CRUD operations
- Test foreign key constraints
- Test cascading deletes
- Test data validation
- UI tests for critical user flows

---

## UI/UX Guidelines

Follow Nielsen's 10 Usability Heuristics:
https://www.nngroup.com/articles/ten-usability-heuristics/

### Key Principles

1. **Minimize Data Entry**
   - Use dropdowns instead of text input where possible
   - Auto-populate data (GPS coordinates, dates)
   - Provide defaults

2. **Avoid Screen Overcrowding**
   - Use whitespace effectively
   - Group related items
   - Progressive disclosure for complex features

3. **Clear Navigation**
   - Consistent back navigation
   - Clear action buttons
   - Breadcrumbs or titles showing current location

4. **User Feedback**
   - Loading indicators
   - Success/error messages
   - Confirmation dialogs for destructive actions

5. **Material Design**
   - Use Material Components
   - Follow Android design guidelines
   - Consistent styling

---

## Project Status

**Completed:**
- ✅ Project setup with MVVM architecture
- ✅ Room entities with foreign key relationships
- ✅ Gradle configuration with version catalogs
- ✅ Dependencies for Room, Retrofit, Location Services

**In Progress:**
- 🔲 Design documentation (due Nov 24)
- 🔲 DAOs and Database implementation
- 🔲 Repository layer
- 🔲 ViewModels

**To Do:**
- 🔲 Track List screen with RecyclerView
- 🔲 Add Track input screen
- 🔲 Track Detail/Edit screen
- 🔲 Delete functionality
- 🔲 Location Services integration
- 🔲 Weather API integration
- 🔲 Search/Filter functionality
- 🔲 Unit and instrumented tests
- 🔲 Demo video

---

## Resources

**Official Documentation:**
- Android Developers: https://developer.android.com/
- Room Database: https://developer.android.com/training/data-storage/room
- ViewModels: https://developer.android.com/topic/libraries/architecture/viewmodel
- LiveData: https://developer.android.com/topic/libraries/architecture/livedata
- Coroutines: https://kotlinlang.org/docs/coroutines-overview.html

**Style Guide:**
- Google Java/Kotlin Style: https://google.github.io/styleguide/javaguide.html

**Tools:**
- Grammarly (for documentation): https://www.grammarly.com/
