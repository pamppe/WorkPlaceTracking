package com.emill.workplacetracking

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.emill.workplacetracking.ui.theme.WorkPlaceTrackingTheme

import com.emill.workplacetracking.db.UserInfo
import com.emill.workplacetracking.viewmodel.MainViewModel

import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.room.Room
import com.emill.workplacetracking.db.AppDatabase
import com.emill.workplacetracking.db.MainViewModelFactory


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize your database and DAO here
        val appDatabase: AppDatabase = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "workTrackerDba"
        )
            .fallbackToDestructiveMigration().build()
        val userInfoDao = appDatabase.userInfoDao()

        // Initialize your ViewModelFactory with the DAO
        val factory = MainViewModelFactory(userInfoDao)

        setContent {
            val context = LocalContext.current
            WorkPlaceTrackingTheme {
                // Use ViewModelProvider to get your ViewModel instance
                val mainViewModel: MainViewModel = viewModel(
                    factory = factory
                )
                MyApp(mainViewModel)
            }
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApp(mainViewModel: MainViewModel) {
    val userInfo by mainViewModel.userInfo.observeAsState()
    val showDialog = remember { mutableStateOf(userInfo == null) } // Show dialog if userInfo is null
    val showSettingsDialog = remember { mutableStateOf(false) } // Separate state for settings dialog

    // React to userInfo changes to close the dialog if userInfo is not null
    LaunchedEffect(userInfo) {
        showDialog.value = userInfo == null
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Workplace Tracker") }) },
        floatingActionButton = {
            // Settings button
            FloatingActionButton(onClick = { showSettingsDialog.value = true }) {
                Icon(Icons.Filled.Settings, contentDescription = "Settings")
            }
            // User info button (optional, if you want a separate button to trigger user info dialog)
            // You might want to show this only if userInfo is not null
            // FloatingActionButton(onClick = { showDialog.value = true }) {
            //     Icon(Icons.Filled.AccountCircle, contentDescription = "User Info")
            // }
        }
    ) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (userInfo != null) {
                    Text(text = "Hello, ${userInfo!!.firstName}!", modifier = Modifier.padding(16.dp))
                } else {
                    Text("Please enter your information", modifier = Modifier.padding(16.dp))
                }

                // User Info Dialog
                if (showDialog.value) {
                    UserInfoDialog(showDialog = showDialog, viewModel = mainViewModel)
                }

                // Settings Dialog
                if (showSettingsDialog.value) {
                    SettingsDialog(showDialog = showSettingsDialog) {
                        // Actions to perform when the settings dialog is dismissed, if any
                    }
                }
            }
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


