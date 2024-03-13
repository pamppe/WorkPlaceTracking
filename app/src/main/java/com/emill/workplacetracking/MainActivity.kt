package com.emill.workplacetracking

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
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
import com.emill.workplacetracking.db.WorkEntry
import com.emill.workplacetracking.ui.theme.WorkPlaceTrackingTheme
import com.emill.workplacetracking.viewmodel.MainViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale


class MainActivity : ComponentActivity() {
    companion object {
        private const val REQUEST_CODE_POST_NOTIFICATIONS_PERMISSION = 1001
        private const val locationPermissionRequestCode = 1000
        private const val backgroundLocationRequestCode = 1002 // Define this
    }
    private lateinit var gpsManager: GPSManager
    private val timerViewModel : TimerViewModel by viewModels()

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
    private fun checkAndRequestLocationPermissions() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED -> {
                // Foreground location permission has not been granted yet, request it
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    locationPermissionRequestCode)
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED -> {
                // On Android 10 and above, request background location permission separately
                // This request must come AFTER foreground permission has been granted
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                    backgroundLocationRequestCode)
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
        override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
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
    val userInfo by mainViewModel.userInfo.observeAsState()
    // Use an additional state to track whether the userInfo fetch has been attempted.
    val fetchAttempted = remember { mutableStateOf(false) }

    // Observe userInfo and update fetchAttempted accordingly.
    LaunchedEffect(userInfo) {
        fetchAttempted.value = true
    }

    // Determine loading state based on whether a fetch attempt has been made, rather than userInfo content.
    val isLoading = !fetchAttempted.value

    if (isLoading) {
        LoadingScreen("Loading, please wait...")
    } else {
        Scaffold(
            topBar = { TopAppBar(title = { Text("Workplace Tracker") }) },
            floatingActionButton = {
                if (currentScreen.value == "Home") {
                    FloatingActionButton(onClick = { /* Implement action */ }) {
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
                    "Profile" -> UserProfileScreen(mainViewModel) // Placeholder, implement your logic here
                    "Gps" -> GpsScreen() // Placeholder, implement your logic here
                    // Add other cases as needed
                }
            }
        }
    }
}



@Composable
fun HomeScreen(mainViewModel: MainViewModel, timerViewModel: TimerViewModel) {
    val userInfo by mainViewModel.userInfo.observeAsState()
    val userId by mainViewModel.userId.observeAsState()

    // Track if the initial data fetch has been completed.
    val dataFetchCompleted = remember { mutableStateOf(false) }
    val showDialog = remember { mutableStateOf(false) }

    // Adjust LaunchedEffect to set dataFetchCompleted to true once userInfo is observed.
    LaunchedEffect(key1 = userInfo) {
        dataFetchCompleted.value = true
        showDialog.value = userInfo == null
    }

    if (!dataFetchCompleted.value) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.width(64.dp),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            TimerScreen(timerViewModel = timerViewModel)
            Spacer(modifier = Modifier.height(16.dp))

            if (userInfo != null) {

                WorkedHoursDisplay(mainViewModel, userInfo!!.id)
            }
        else if (showDialog.value) {
                // Conditionally display the UserInfoDialog based on showDialog state.
                UserInfoDialog(showDialog = showDialog, viewModel = mainViewModel)
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
@Composable
fun LoadingScreen(message: String = "Loading...") {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = message, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
@Composable
fun AnimatedLoadingScreen(isLoading: Boolean, message: String = "Loading...") {
    AnimatedVisibility(
        visible = isLoading,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        LoadingScreen(message)
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
    val companyState = remember { mutableStateOf("") }
    val hourlyRateState = remember { mutableStateOf("") }

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
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = companyState.value, // Assuming you're also collecting the last name
                        onValueChange = { companyState.value = it },
                        label = { Text("Company Name") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = hourlyRateState.value,
                        onValueChange = { newValue ->
                            if (newValue.isEmpty() || newValue.toDoubleOrNull() != null) {
                                hourlyRateState.value = newValue
                            }
                        },
                        label = { Text("Hourly Rate") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    // Ensure conversion from String to Double for hourlyRate
                    val hourlyRate = hourlyRateState.value.toDoubleOrNull() ?: 0.0 // Provide a fallback value

                    // Now create the UserInfo object with the converted hourly rate
                    val userInfo = UserInfo(
                        firstName = nameState.value,
                        lastName = lastNameState.value,
                        company = companyState.value,
                        hourlyRate = hourlyRate // Pass the converted value
                    )

                    // Insert the userInfo into the database via ViewModel
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
    val workEntries by mainViewModel.getAggregatedWorkEntriesForUser(userId).observeAsState(initial = emptyList())

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(color = LightBlue),
        contentAlignment = Alignment.Center
    ) {
        if (workEntries.isEmpty()) {
            Text(
                text = "No work entries available for this week yet.",
                fontSize = 20.sp,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyColumn(modifier = Modifier.padding(16.dp)) {
                items(workEntries) { entry ->
                    val displayDate = formatDate(entry.date) // Use formatDate to transform date string for display
                    Text(text = "$displayDate - ${entry.totalHoursWorked}h", fontSize = 20.sp)
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
            NavigationItem("Profile", Icons.Filled.AccountCircle), // Assuming you have an appropriate icon
            NavigationItem("Gps", Icons.Filled.LocationOn)
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
fun UserProfileScreen(viewModel: MainViewModel) {
    val userInfo by viewModel.userInfo.observeAsState()
    val userId = userInfo?.id
    val hourlyRate = userInfo?.hourlyRate ?: 0.0

    val weeklyEntries by viewModel.getWorkEntriesForCurrentWeek(userId ?: -1).observeAsState(initial = emptyList())
    val weeklyHours = weeklyEntries.sumOf { it.hoursWorked }
    val weeklyEarnings = weeklyHours * hourlyRate

    val currentMonthEntries by viewModel.getWorkEntriesForCurrentMonth(userId ?: -1).observeAsState(initial = emptyList())
    val totalHoursAllTime by viewModel.getTotalHoursAllTime(userId ?: -1).observeAsState(initial = 0)

    val weeklyHoursTransformed = transformEntriesToWeeklyHours(currentMonthEntries)

    Column(modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        UserInformationHeader(userInfo = userInfo)

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        HoursRecordedThisWeekCard(weeklyHours = weeklyHours, weeklyEarnings = weeklyEarnings)
        Spacer(modifier = Modifier.height(8.dp))

        HoursRecordedThisMonthCard(weeklyHours = weeklyHoursTransformed, hourlyRate = hourlyRate)
        Spacer(modifier = Modifier.height(8.dp))

        TotalHoursRecordedCard(totalHours = totalHoursAllTime, hourlyRate = hourlyRate)
    }
}

@Composable
fun UserInformationHeader(userInfo: UserInfo?) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "${userInfo?.firstName ?: "User"} ${userInfo?.lastName ?: ""}",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = userInfo?.company ?: "No company",
            style = MaterialTheme.typography.titleSmall
        )
    }
}
fun transformEntriesToWeeklyHours(entries: List<WorkEntry>): List<Pair<String, Int>> {
    val weekFields = WeekFields.of(Locale.getDefault())

    // Group entries by week of the month
    val entriesByWeek = entries.groupBy {
        val date = LocalDate.parse(it.date, DateTimeFormatter.ISO_DATE)
        date.get(weekFields.weekOfMonth())
    }

    // Sum hours for each week and map to a list of pairs
    return entriesByWeek.map { (weekOfMonth, entries) ->
        "Week $weekOfMonth" to entries.sumOf { it.hoursWorked }
    }.sortedBy { it.first } // Ensure the weeks are in order
}
@Composable
fun HoursRecordedThisWeekCard(weeklyHours: Int, weeklyEarnings: Double) {
    val numberFormat = DecimalFormat.getCurrencyInstance()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Hours Recorded This Week", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "$weeklyHours hours", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "This Week's Earnings: ${numberFormat.format(weeklyEarnings)}", style = MaterialTheme.typography.bodyLarge)
        }
    }
}
@Composable
fun HoursRecordedThisMonthCard(weeklyHours: List<Pair<String, Int>>, hourlyRate: Double?) {
    val numberFormat = DecimalFormat.getCurrencyInstance()

    // Calculate total hours for the month
    val totalHoursThisMonth = weeklyHours.sumOf { it.second }
    // Calculate earnings for the month
    val monthlyEarnings = totalHoursThisMonth * (hourlyRate ?: 0.0)


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Hours Recorded This Month",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            weeklyHours.forEach { week ->
                Text(
                    "${week.second} hours - ${week.first}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Total Hours: $totalHoursThisMonth hours",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "This Month's Earnings: ${numberFormat.format(monthlyEarnings)}",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
@Composable
fun TotalHoursRecordedCard(totalHours: Int, hourlyRate: Double) {
    val earnings = totalHours * hourlyRate
    val numberFormat = DecimalFormat.getCurrencyInstance()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Total Hours Recorded",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "$totalHours hours",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Total Earnings: ${numberFormat.format(earnings)}",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}


val workplaceGeoPoint = GeoPoint(60.158215, 24.879721) // Convert workplace location to GeoPoint
const val workplaceRadius = 20.0 // meters
@Composable
fun OsmMapViewWithLocationAndAreaWithButton(context: Context, workplaceLocation: GeoPoint, workplaceRadius: Double) {
    Log.d("Map", "Map Composable triggered")
    val mapView = remember { MapView(context) }
    val userLocationMarker = remember { Marker(mapView) }
    mapView.overlays.clear()
    Column {
        AndroidView(
            factory = { mapView },
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            update = { mapView ->
                mapView.apply {
                    isHorizontalMapRepetitionEnabled = false
                    isVerticalMapRepetitionEnabled = false
                    minZoomLevel = 3.5

                    setTileSource(TileSourceFactory.MAPNIK)
                    controller.setCenter(workplaceLocation)
                    setMultiTouchControls(true)

                    // Add marker for workplace
                    overlays.add(Marker(mapView).apply {
                        position = workplaceLocation
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = "Workplace"
                       // icon = context.getDrawable(R.drawable.ic_workplace_marker) // Custom icon for workplace
                    })

                    // Add polygon for workplace radius
                    overlays.add(Polygon().apply {
                        points = Polygon.pointsAsCircle(workplaceLocation, workplaceRadius)
                        fillColor = 0x25FF0000 // Example semi-transparent fill
                    })

                    // Overlay for user's location
                    val myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), this).apply {
                        enableMyLocation()
                    }
                    overlays.add(myLocationOverlay)
                    // Example to update user location marker based on location updates

                }
            }
        )
        Button(
            onClick = {
                mapView.controller.setZoom(18.0)
                mapView.controller.setCenter(workplaceLocation)
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Navigate to Workplace")
        }
    }
}

@Composable
fun GpsScreen(context: Context = LocalContext.current) {
    Log.d("Gps Screen","Gps Screen triggered")
    // Call OsmMapViewWithLocationAndArea to display the map with user location and workplace area
    OsmMapViewWithLocationAndAreaWithButton(context, workplaceGeoPoint, workplaceRadius)
}
fun formatDate(dateStr: String): String {
    val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
    val outputFormatter = DateTimeFormatter.ofPattern("EEEE, dd/MM", Locale.getDefault())
    val date = LocalDate.parse(dateStr, inputFormatter)
    return date.format(outputFormatter)
}
