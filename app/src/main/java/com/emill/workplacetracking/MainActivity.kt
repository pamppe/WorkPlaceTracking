package com.emill.workplacetracking

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.emill.workplacetracking.db.AppDatabase
import com.emill.workplacetracking.db.MainViewModelFactory
import com.emill.workplacetracking.ui.theme.WorkPlaceTrackingTheme
import com.emill.workplacetracking.uiViews.MyApp
import com.emill.workplacetracking.uiViews.TimerNotificationObserver
import com.emill.workplacetracking.utils.GPSManager
import com.emill.workplacetracking.viewmodels.MainViewModel
import com.emill.workplacetracking.viewmodels.TimerViewModel
import com.emill.workplacetracking.viewmodels.TimerViewModelFactory
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import org.osmdroid.config.Configuration


class MainActivity : ComponentActivity() {
    companion object {
        private const val REQUEST_CODE_POST_NOTIFICATIONS_PERMISSION = 1001
        private const val locationPermissionRequestCode = 1000
        private const val backgroundLocationRequestCode = 1002 // Define this
    }

    private lateinit var gpsManager: GPSManager
    private val timerViewModel: TimerViewModel by viewModels()

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let { location ->
                // Use your location here
                val workplaceLocation = Location("").apply {
                    latitude = 60.158215 // Workplace latitude
                    longitude = 24.879721 // Workplace longitude
                }
                val distanceToWorkplace = location.distanceTo(workplaceLocation)

                if (distanceToWorkplace <= 20f) { // User is within the workplace area
                    if (!timerViewModel.isTimerRunning()) {
                        timerViewModel.startTimer()
                        Log.d("LocationUpdates", "Timer started - within workplace area")
                    }
                } else { // User has left the workplace area
                    if (timerViewModel.isTimerRunning()) {
                        timerViewModel.stopTimerAndSaveEntry(1)
                        Log.d("LocationUpdates", "Timer stopped - outside workplace area")
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().userAgentValue = "com.emill.workplacetracking"

        gpsManager = GPSManager(this)

        // Requesting location permissions
        checkAndRequestLocationPermissions()
        // Don't forget to request permissions before starting location updates
        // Check for location permissions

        // Initialize your database and DAO here
        val appDatabase: AppDatabase = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "workTrackerDba"
        ).fallbackToDestructiveMigration().build()

        val userInfoDao = appDatabase.userInfoDao()
        val workEntryDao = appDatabase.workEntryDao()
        // Initialize your ViewModel factories
        val mainViewModelFactory = MainViewModelFactory(userInfoDao, workEntryDao)
        val timerViewModelFactory = TimerViewModelFactory(workEntryDao)

        // Create the notification channel
        createNotificationChannel()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_CODE_POST_NOTIFICATIONS_PERMISSION
                )
            }
        }

        setContent {
            val mainViewModel: MainViewModel = viewModel(factory = mainViewModelFactory)

            // Provide the custom factory when retrieving the TimerViewModel
            val timerViewModel: TimerViewModel = viewModel(factory = timerViewModelFactory)

            //---- Add test data to the database ---//
            /*
                    val testUserId = 1

                 lifecycleScope.launch {
                        com.emill.workplacetracking.utils.TestDataGenerator.addTestData(this,workEntryDao, testUserId)
                    }
            */

            WorkPlaceTrackingTheme {
                // Here we pass the method as a lambda function
                TimerNotificationObserver(
                    timerViewModel = timerViewModel,
                    showNotification = { message -> showNotification(message) }
                )
                MyApp(mainViewModel, timerViewModel)
            }
        }
    }

    private fun checkAndRequestLocationPermissions() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED -> {
                // Foreground location permission has not been granted yet, request it
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    locationPermissionRequestCode
                )
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ) !=
                    PackageManager.PERMISSION_GRANTED -> {
                // On Android 10 and above, request background location permission separately
                // This request must come AFTER foreground permission has been granted
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                    backgroundLocationRequestCode
                )
            }

            else -> {
                // All necessary permissions have been granted, start location tracking
                startLocationTracking()
            }
        }
    }

    private fun startLocationTracking() {
        // Implement your logic to start location updates using GPSManager
        gpsManager.startLocationUpdates(locationCallback)
    }

    override fun onStart() {
        super.onStart()
        gpsManager.startLocationUpdates(locationCallback)
    }

    override fun onStop() {
        super.onStop()
        // Stop location updates when the activity is no longer visible
        gpsManager.stopLocationUpdates(locationCallback)
    }

    // Override onRequestPermissionsResult to handle permission request responses
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            locationPermissionRequestCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Foreground location permission granted, now check/request background location permission
                    checkAndRequestLocationPermissions()
                } else {
                    // Handle the case where the user denies the foreground location permission
                }
            }

            backgroundLocationRequestCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Background location permission granted
                    startLocationTracking()
                } else {
                    // Handle the case where the user denies the background location permission
                }
            }
        }
    }

    private fun createNotificationChannel() {
        val name = getString(R.string.channel_name) // Define this in your strings.xml
        val descriptionText = getString(R.string.channel_description) // And this
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("TIMER_CHANNEL_ID", name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun showNotification(contentText: String) {
        val builder = NotificationCompat.Builder(this, "TIMER_CHANNEL_ID")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Use an appropriate icon for your app
            .setContentTitle("Timer Notification")
            .setContentText(contentText)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)) {
            // Consider using a more specific ID than 0, to allow for updating or cancelling
            if (ActivityCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            notify(System.currentTimeMillis().toInt(), builder.build())
        }
    }
}

val customFontFamily = FontFamily(Font(R.font.koulenregular))