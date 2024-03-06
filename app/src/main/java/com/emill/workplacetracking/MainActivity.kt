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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.emill.workplacetracking.db.AppDatabase
import com.emill.workplacetracking.db.MainViewModelFactory
import com.emill.workplacetracking.db.UserInfo
import com.emill.workplacetracking.ui.theme.WorkPlaceTrackingTheme
import com.emill.workplacetracking.viewmodel.MainViewModel
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    companion object {
        private const val REQUEST_CODE_POST_NOTIFICATIONS_PERMISSION = 1001
    }

    private lateinit var gpsManager: GPSManager
    private val locationPermissionRequestCode = 1000

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let { location ->
                // Use your location here
                // Example: Check if within workplace
                val workplaceLocation = Location("").apply {
                    latitude = 60.158243 // Workplace latitude change to your workplace latitude
                        longitude = 24.879649 // Workplace longitude change to your workplace longitude
                }
                val isWithinWorkplace = gpsManager.isWithinWorkplace(location, workplaceLocation, 100f) // Radius in meters

                if (isWithinWorkplace) {
                    showNotification("Welcome to Work. Don't forget to clock in!")
                    // log event for applications database for record keeping or future reference?
                    // updateWorkStatusUI(true) UI changes when in work?
                    // Handle user being within workplace
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gpsManager = GPSManager(this)
        // Don't forget to request permissions before starting location updates
        // Check for location permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionRequestCode)
        } else {
            // Permission is granted, you can start location updates
            startLocationTracking()
        }

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

                  /*  val testUserId = 1

                 lifecycleScope.launch {
                        TestDataGenerator.addTestData(this,workEntryDao, testUserId)
                    }
                    */

            WorkPlaceTrackingTheme {
                // Here we pass the method as a lambda function
                TimerNotificationObserver(
                    timerViewModel = timerViewModel,
                    showNotification = { message -> showNotification(message) }
                )
                MyApp(mainViewModel,timerViewModel)
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
        override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            if (requestCode == REQUEST_CODE_POST_NOTIFICATIONS_PERMISSION) {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted
                    // You can now proceed with showing notifications or any other logic that requires this permission
                } else {
                    // Permission was denied
                    // Handle the denial accordingly, possibly by informing the user of the functionality they're missing out on
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

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WorkPlaceTrackingTheme {
        Greeting("Android")
    }
}
val LightBlue = Color(0xFF5263b7)



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApp(mainViewModel: MainViewModel, timerViewModel: TimerViewModel) {
    val currentScreen = remember { mutableStateOf("Home") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Workplace Tracker") }) },
        floatingActionButton = {
            if (currentScreen.value == "Home") {
                FloatingActionButton(onClick = { /* Action */ }) {
                    Icon(Icons.Filled.Settings, contentDescription = "Settings")
                }
            }
        },
        bottomBar = {
            BottomNavigationBar(currentScreen.value) { screen ->
                currentScreen.value = screen
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (currentScreen.value) {
                "Home" -> HomeScreen(mainViewModel = mainViewModel, timerViewModel = timerViewModel)
                "Profile" -> UserProfileScreen() // Adjust this with actual implementation
                "Settings" -> SettingsScreen() // Adjust this with actual implementation
                // Add other cases as needed
            }


        }
    }
}

@Composable
fun HomeScreen(mainViewModel: MainViewModel, timerViewModel: TimerViewModel) {
    val userInfo by mainViewModel.userInfo.observeAsState()
    val userId = userInfo?.id
    val showDialog = remember { mutableStateOf(false) }
    val showSettingsDialog = remember { mutableStateOf(false) }

    // Initialize dialog states based on userInfo
    LaunchedEffect(userInfo) {
        Log.d("HomeScreen", "UserInfo changed: $userInfo")
        showDialog.value = userInfo == null
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // TimerScreen, not using weight because we don't want it to expand more than its content
        TimerScreen(timerViewModel = timerViewModel)

        Spacer(modifier = Modifier.height(16.dp))

        // Conditional display based on userId presence
        if (userId != null) {
            // Using Modifier.weight(1f) to make WorkedHoursDisplay take up remaining space
            WorkedHoursDisplay(mainViewModel, userId)
        } else {
            Box(
                modifier = Modifier
                    .weight(1f) // Use weight to make the Box take up remaining space
                    .fillMaxWidth(), // Fill the available width
                contentAlignment = Alignment.Center // Center content both horizontally and vertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.width(64.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }

    // Display dialogs based on their respective state
    if (showDialog.value) {
        UserInfoDialog(showDialog = showDialog, viewModel = mainViewModel)
    }

    if (showSettingsDialog.value) {
        SettingsDialog(showDialog = showSettingsDialog) {
            // Handle dialog dismissal actions here
        }
    }
}

@Composable
fun WorkedHoursDisplay(mainViewModel: MainViewModel, userId: Int) {
    // Apply Modifier.weight(1f) to fill remaining space and Modifier.fillMaxWidth() to fill width
    Box(modifier = Modifier
        .fillMaxWidth() // Fill the width of its parent
         // Use weight to make it fill the remaining space
        .fillMaxHeight()
        .padding(16.dp)) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("Tehdyt tunnit", fontSize = 20.sp)
            // Implementation for displaying work entries
            WorkEntriesDisplay(mainViewModel = mainViewModel, userId = userId)
        }
    }
}
@Composable
fun TimerScreen(timerViewModel: TimerViewModel = viewModel()) {
    val time by timerViewModel.time.collectAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth() // Use fillMaxWidth instead of fillMaxSize
            .padding(vertical = 16.dp) // Add vertical padding if needed
    ) {
        Text(
            text = time,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .padding(16.dp)
                .clickable { timerViewModel.toggleTimer() }
        )
        Button(
            onClick = { timerViewModel.resetTimer() },
            // Consider removing or adjusting the bottom padding if it's too large
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text("Reset")
        }
    }
}


@Composable
fun SettingsDialog(showDialog: MutableState<Boolean>, onDismiss: () -> Unit) {
    // Assuming you store these settings in a ViewModel or similar
    val isGPSTrackingEnabled = remember { mutableStateOf(false) }
    val isNotificationAlertsEnabled = remember { mutableStateOf(false) }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = {
                // Handle dismiss actions like closing the dialog or updating state
                onDismiss()
            },
            title = { Text("Settings") },
            text = {
                Column {
                    SwitchRow(switchText = "GPS Tracking", isChecked = isGPSTrackingEnabled)
                    SwitchRow(switchText = "Notification Alerts", isChecked = isNotificationAlertsEnabled)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Save the settings and dismiss the dialog
                        // For example, update your ViewModel or shared preferences
                        showDialog.value = false
                        onDismiss()
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog.value = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SwitchRow(switchText: String, isChecked: MutableState<Boolean>) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(switchText)
        Switch(
            checked = isChecked.value,
            onCheckedChange = { isChecked.value = it }
        )
    }
}
@Composable
fun UserInfoDialog(showDialog: MutableState<Boolean>, viewModel: MainViewModel) {
    val nameState = remember { mutableStateOf("") }
    val lastNameState = remember { mutableStateOf("") } // Use this if you plan to collect the last name from the user

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("Enter User Info") },
            text = {
                Column {
                    OutlinedTextField(
                        value = nameState.value,
                        onValueChange = { nameState.value = it },
                        label = { Text("Name") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = lastNameState.value, // Assuming you're also collecting the last name
                        onValueChange = { lastNameState.value = it },
                        label = { Text("Last Name") }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    // Create a UserInfo object with the collected name and last name
                    val userInfo = UserInfo(firstName = nameState.value, lastName = lastNameState.value)
                    viewModel.insertUserInfo(userInfo)
                    showDialog.value = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog.value = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
@Composable
fun TimerNotificationObserver(
    timerViewModel: TimerViewModel,
    showNotification: (String) -> Unit // Add this parameter
) {
    val notificationText by timerViewModel.timerNotifications.collectAsState()
    LaunchedEffect(notificationText) {
        notificationText?.let {
            showNotification(it) // Use the passed function
            timerViewModel.clearTimerNotification()
        }
    }
}


@Composable
fun WorkEntriesDisplay(mainViewModel: MainViewModel, userId: Int) {
    // Assuming getWorkEntriesForCurrentWeek returns LiveData or State<List<WorkEntry>>
    val workEntries by mainViewModel.getWorkEntriesForCurrentWeek(userId).observeAsState(initial = emptyList())

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(color = LightBlue),
        contentAlignment = Alignment.Center // Center the content vertically
    ) {
        if (workEntries.isEmpty()) {
            // If no work entries available, display a message
            Text(
                text = "No work entries available for this week yet.",
                fontSize = 20.sp,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            // If work entries available, display the list of entries
            LazyColumn(modifier = Modifier.padding(16.dp)) {
                items(workEntries) { entry ->
                    Text(text = "${entry.date} - ${entry.hoursWorked}h", fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}
@Composable
fun BottomNavigationBar(currentRoute: String, onNavigate: (String) -> Unit) {
    NavigationBar {
        val items = listOf(
            NavigationItem("Home", Icons.Filled.Home),
            NavigationItem("Profile", Icons.Filled.Favorite), // Assuming you have an appropriate icon
            NavigationItem("Settings", Icons.Filled.Settings)
        )
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentRoute == item.title,
                onClick = { onNavigate(item.title) }
            )
        }
    }
}
data class NavigationItem(val title: String, val icon: ImageVector)

@Composable
fun UserProfileScreen() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Name: Testi timo", style = MaterialTheme.typography.headlineLarge)
        Text(text = "Email: testi2", style = MaterialTheme.typography.bodyMedium)
        Text(text = "Job: Opiskelija", style = MaterialTheme.typography.bodyMedium)
        Text(text = "Location: Helsinki", style = MaterialTheme.typography.bodyMedium)
    }
}
// Placeholder for SettingsScreen
@Composable
fun SettingsScreen() {
    // Your Settings Screen content goes here
}
