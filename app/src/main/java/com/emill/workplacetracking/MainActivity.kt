package com.emill.workplacetracking

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.emill.workplacetracking.db.AppDatabase
import com.emill.workplacetracking.db.MainViewModelFactory
import com.emill.workplacetracking.ui.theme.WorkPlaceTrackingTheme
import com.emill.workplacetracking.uiViews.MyApp
import com.emill.workplacetracking.uiViews.TimerNotificationObserver
import com.emill.workplacetracking.utils.ForegroundService
import com.emill.workplacetracking.utils.GPSManager
import com.emill.workplacetracking.utils.LocationCheckWorker
import com.emill.workplacetracking.viewmodels.MainViewModel
import com.emill.workplacetracking.viewmodels.TimerViewModel
import com.emill.workplacetracking.viewmodels.TimerViewModelFactory
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import org.osmdroid.config.Configuration
import java.util.LinkedList
import java.util.concurrent.TimeUnit


class MainActivity : ComponentActivity() {
    companion object {
        private const val REQUEST_CODE_POST_NOTIFICATIONS_PERMISSION = 1001
        private const val REQUEST_CODE_FINE_LOCATION_PERMISSION = 1002
        private const val REQUEST_CODE_COARSE_LOCATION_PERMISSION = 1003
    }

    private lateinit var gpsManager: GPSManager
    private val timerViewModel: TimerViewModel by viewModels()
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let { location ->
                // Use your location here
                val workplaceLocation = Location("").apply {
                    //Emil
                    /*latitude = 60.158215 // Workplace latitude
                    longitude = 24.879721 // Workplace longitude*/
                    //Leo
                    latitude = 60.218764 // Workplace latitude
                    longitude = 24.747425 // Workplace longitude
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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().userAgentValue = "com.emill.workplacetracking"

        gpsManager = GPSManager(this)

        // Requesting location permissions
        // Don't forget to request permissions before starting location updates
        // Check for location permissions
        checkAndRequestPermissions()


        // Initialize your database and DAO here
        val appDatabase: AppDatabase = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "workTrackerDba"
        ).fallbackToDestructiveMigration().build()

        val userInfoDao = appDatabase.userInfoDao()
        val workEntryDao = appDatabase.workEntryDao()

        // Initialize ViewModel factories
        val mainViewModelFactory = MainViewModelFactory(userInfoDao, workEntryDao)
        val timerViewModelFactory = TimerViewModelFactory(workEntryDao)

        //create notification channel for foreground
        val channel = NotificationChannel(
            "foreground_channel",
            "Foreground Notification",
            NotificationManager.IMPORTANCE_HIGH
        )
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        // Create the notification channel
        createNotificationChannel()



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

    private val permissionsQueue = LinkedList<Int>()
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkAndRequestPermissions() {
        if (permissionsQueue.isEmpty()) {
            // Add permissions to the queue
            permissionsQueue.add(REQUEST_CODE_FINE_LOCATION_PERMISSION)
            permissionsQueue.add(REQUEST_CODE_COARSE_LOCATION_PERMISSION)
            permissionsQueue.add(REQUEST_CODE_POST_NOTIFICATIONS_PERMISSION)
        }

        // Request the first permission in the queue
        requestNextPermission()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestNextPermission() {
        if (permissionsQueue.isNotEmpty()) {
            when (val requestCode = permissionsQueue.peek()) {
                REQUEST_CODE_FINE_LOCATION_PERMISSION -> {
                    if (ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        //start location tracking
                        startLocationTracking()
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            REQUEST_CODE_FINE_LOCATION_PERMISSION
                        )
                    } else {
                        permissionsQueue.poll()
                        requestNextPermission()
                    }
                }

                REQUEST_CODE_COARSE_LOCATION_PERMISSION -> {
                    if (ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        //start location tracking
                        startLocationTracking()
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                            REQUEST_CODE_COARSE_LOCATION_PERMISSION
                        )
                    } else {
                        permissionsQueue.poll()
                        requestNextPermission()
                    }
                }

                REQUEST_CODE_POST_NOTIFICATIONS_PERMISSION -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        if (ActivityCompat.checkSelfPermission(
                                this,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            //start foreground service
                            startForegroundService()
                            ActivityCompat.requestPermissions(
                                this,
                                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                                REQUEST_CODE_POST_NOTIFICATIONS_PERMISSION
                            )
                        } else {
                            permissionsQueue.poll()
                            requestNextPermission()
                        }
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Handle permission result

        // Remove the handled permission from the queue
        permissionsQueue.poll()

        // Request the next permission in the queue
        requestNextPermission()
    }


    private fun startForegroundService() {
        val intent = Intent(this, ForegroundService::class.java).apply {
            action = ForegroundService.Actions.START.toString()
        }
        startForegroundService(intent)
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

        // Start the foreground service
        val intent = Intent(this, ForegroundService::class.java).apply {
            action = ForegroundService.Actions.START.toString()
        }
        startForegroundService(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Stop location updates when the activity is no longer active
        gpsManager.stopLocationUpdates(locationCallback)
        // Stop the foreground service
        val intent = Intent(this, ForegroundService::class.java).apply {
            action = ForegroundService.Actions.STOP.toString()
        }
        startService(intent)

        // Cancel all work for LocationCheckWorker
        WorkManager.getInstance(this).cancelAllWorkByTag(LocationCheckWorker::class.java.name)
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


