package com.emill.workplacetracking

import RegisterScreen
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.emill.workplacetracking.uiViews.BottomNavigationBar
import com.emill.workplacetracking.uiViews.GpsScreen
import com.emill.workplacetracking.uiViews.LoginScreen
import com.emill.workplacetracking.uiViews.StartScreen
import com.emill.workplacetracking.uiViews.TimerScreen
import com.emill.workplacetracking.uiViews.UserProfileScreen
import com.emill.workplacetracking.viewmodels.LoginViewModel
import com.emill.workplacetracking.viewmodels.LoginViewModelFactory
import com.emill.workplacetracking.viewmodels.RegisterViewModel
import com.emill.workplacetracking.viewmodels.RegisterViewModelFactory
import com.emill.workplacetracking.viewmodels.TimerViewModel
import com.emill.workplacetracking.viewmodels.UserProfileViewModel
import com.emill.workplacetracking.viewmodels.UserProfileViewModelFactory


@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    // Obtain the nearest ViewModelStoreOwner, typically the activity or nav host
    val viewModelStoreOwner = LocalViewModelStoreOwner.current ?: return
    val apiService = RetrofitInstance.api // Make sure RetrofitInstance is correctly set up

    // Create view models using the ViewModelProvider and their factories
    val TimerViewModel: TimerViewModel = ViewModelProvider(viewModelStoreOwner).get(TimerViewModel::class.java)
    val RegisterViewModel: RegisterViewModel = ViewModelProvider(viewModelStoreOwner, RegisterViewModelFactory(apiService, context)).get(RegisterViewModel::class.java)
    val loginViewModel: LoginViewModel = ViewModelProvider(viewModelStoreOwner, LoginViewModelFactory(apiService)).get(LoginViewModel::class.java)
    val userProfileViewModel: UserProfileViewModel = ViewModelProvider(viewModelStoreOwner, UserProfileViewModelFactory(apiService, loginViewModel)).get(UserProfileViewModel::class.java)

    // Current Route State
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val showBottomBar = currentRoute in listOf("timer", "profile", "gps")

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(currentRoute ?: "", navController::navigate)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavigationItem.Start.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavigationItem.Start.route) { StartScreen(navController = navController) }
            composable(NavigationItem.Login.route) { LoginScreen(viewModel = loginViewModel, navController = navController) }
            composable(NavigationItem.Profile.route) { UserProfileScreen(viewModel = userProfileViewModel, navController = navController) }
            composable(NavigationItem.Register.route) { RegisterScreen(viewModel = RegisterViewModel, navController = navController) }
            composable(NavigationItem.Timer.route) { TimerScreen(viewModel = TimerViewModel, navController = navController) }
            composable(NavigationItem.Gps.route) {  GpsScreen(navController = navController)  }
            // Define GPS and other screens as needed
        }
    }
}