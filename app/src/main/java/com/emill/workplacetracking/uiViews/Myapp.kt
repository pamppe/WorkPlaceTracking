package com.emill.workplacetracking.uiViews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.emill.workplacetracking.DB.Repository
import com.emill.workplacetracking.customFontFamily
import com.emill.workplacetracking.viewmodels.MainViewModel
import com.emill.workplacetracking.viewmodels.TimerViewModel
import com.emill.workplacetracking.viewmodels.UserProfileViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApp(mainViewModel: MainViewModel, timerViewModel: TimerViewModel, userProfileViewModel: UserProfileViewModel) {

    val navController = rememberNavController()
    val currentScreen = remember { mutableStateOf("Home") }
   // val userInfo by mainViewModel.userInfo.observeAsState()
    // Use an additional state to track whether the userInfo fetch has been attempted.
    val fetchAttempted = remember { mutableStateOf(false) }

    // Observe userInfo and update fetchAttempted accordingly.
    LaunchedEffect(mainViewModel) {
        fetchAttempted.value = true
    }

    // Determine loading state based on whether a fetch attempt has been made, rather than userInfo content.
    val isLoading = !fetchAttempted.value

    if (isLoading) {
        LoadingScreen("Loading, please wait...")
    } else {
        Scaffold(
            topBar = { TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Workplace Tracker",
                            style = customTextStyle,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF5263b7) // Set the background color here
                )
            )
            },
            bottomBar = {
                BottomNavigationBar(currentScreen.value) { screen ->
                    currentScreen.value = screen
                }
            },
            containerColor = MaterialTheme.colorScheme.background,
            content = { innerPadding ->
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .background(LightBlue)
                ) {
                    NavHost(navController, startDestination = "Home") {
                        composable("Home") {
                            HomeScreen(mainViewModel = mainViewModel, timerViewModel = timerViewModel, navController = navController)
                        }
                        composable("Profile") {
                            UserProfileScreen(userProfileViewModel, navController)
                        }
                        composable("Gps") {
                            GpsScreen(navController)
                        }
                        // Add other destinations as needed
                    }
                }
            }
        )
    }
}

val LightBlue = Color(0xFF5263b7)

val customTextStyle = TextStyle(
    fontFamily = customFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 25.sp
)


