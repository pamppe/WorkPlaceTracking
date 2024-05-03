package com.emill.workplacetracking.uiViews

import NavigationItem
import RegisterScreen
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.emill.workplacetracking.viewmodels.LoginViewModel
import com.emill.workplacetracking.viewmodels.RegisterViewModel
import com.emill.workplacetracking.viewmodels.TimerViewModel
import com.emill.workplacetracking.viewmodels.UserProfileViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApp() {
    val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text("Workplace Tracker", style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp)) },
                navigationIcon = {
                    IconButton(onClick = {
                        scope.launch {
                            scaffoldState.drawerState.open()
                        }
                    }) {
                        Icon(Icons.Filled.Menu, contentDescription = "Menu")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        },
        drawerContent = {
            DrawerContent(navController, scaffoldState, scope)
        },
        content = { padding ->
            Box(modifier = Modifier.padding(padding)) {
                NavHost(navController, startDestination = NavigationItem.Start.route) {
                    composable(NavigationItem.Start.route) { StartScreen(navController) }
                    composable(NavigationItem.Login.route) {
                        // Assume LoginScreen requires a LoginViewModel
                        val loginViewModel: LoginViewModel = viewModel()
                        LoginScreen(loginViewModel, navController)
                    }
                    composable(NavigationItem.Profile.route) {
                        // Assume UserProfileScreen requires a UserProfileViewModel
                        val userProfileViewModel: UserProfileViewModel = viewModel()
                        UserProfileScreen(userProfileViewModel, navController)
                    }
                    composable(NavigationItem.Register.route) {
                        // Assume RegisterScreen requires a RegisterViewModel
                        val registerViewModel: RegisterViewModel = viewModel()
                        RegisterScreen(registerViewModel, navController)
                    }
                    composable(NavigationItem.Timer.route) {
                        // Assume TimerScreen requires a TimerViewModel
                        val timerViewModel: TimerViewModel = viewModel()
                        TimerScreen(timerViewModel, navController)
                    }
                    composable(NavigationItem.Gps.route) { GpsScreen(navController) }
                    // Additional navigational routes as required
                }
            }
        }
    )
}

@Composable
fun DrawerContent(navController: NavController, scaffoldState: ScaffoldState, scope: CoroutineScope) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // List of drawer items
        listOf(
            NavigationItem.Start to Icons.Filled.Home,
            NavigationItem.Profile to Icons.Filled.AccountCircle,
            NavigationItem.Gps to Icons.Filled.LocationOn,
            NavigationItem.Timer to Icons.Filled.DateRange,
            // Additional items
        ).forEach { (item, icon) ->
            DrawerButton(item.route.replaceFirstChar { it.uppercase() }, icon) {
                scope.launch {
                    scaffoldState.drawerState.close()
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            }
        }
    }
}

@Composable
fun DrawerButton(text: String, icon: ImageVector, onClick: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable(onClick = onClick).padding(8.dp)) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(16.dp))
        Text(text, style = TextStyle(fontSize = 18.sp))
    }
}

fun navigateToScreen(navController: NavController, route: String, scaffoldState: ScaffoldState, scope: CoroutineScope) {
    scope.launch {
        scaffoldState.drawerState.close()
        navController.navigate(route) {
            popUpTo(navController.graph.startDestinationId)
            launchSingleTop = true
        }
    }
}