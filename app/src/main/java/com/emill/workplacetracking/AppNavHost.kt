package com.emill.workplacetracking

import RegisterScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.emill.workplacetracking.uiViews.LoginScreen
import com.emill.workplacetracking.uiViews.StartScreen
import com.emill.workplacetracking.uiViews.UserProfileScreen
import com.emill.workplacetracking.viewmodels.LoginViewModel
import com.emill.workplacetracking.viewmodels.LoginViewModelFactory
import com.emill.workplacetracking.viewmodels.RegisterViewModel
import com.emill.workplacetracking.viewmodels.RegisterViewModelFactory
import com.emill.workplacetracking.viewmodels.UserProfileViewModel
import com.emill.workplacetracking.viewmodels.UserProfileViewModelFactory


/*@Composable
fun AppNavHost(navController: NavHostController, viewModel: LoginViewModel) {
    val viewModelStoreOwner = LocalViewModelStoreOwner.current ?: return
    val apiService = RetrofitInstance.api
    val userProfileViewModel: UserProfileViewModel = ViewModelProvider(viewModelStoreOwner, UserProfileViewModelFactory(apiService, viewModel)).get(UserProfileViewModel::class.java)

    NavHost(navController = navController, startDestination = NavigationItem.Login.route) {
        composable(NavigationItem.Login.route) {
            LoginScreen(navController = navController, viewModel = viewModel)
        }
        composable(NavigationItem.Profile.route) {
            UserProfileScreen(navController = navController, viewModel = userProfileViewModel)
        }
    }
}*/

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
    val RegisterViewModel: RegisterViewModel = ViewModelProvider(viewModelStoreOwner, RegisterViewModelFactory(apiService, context)).get(RegisterViewModel::class.java)
    val loginViewModel: LoginViewModel = ViewModelProvider(viewModelStoreOwner, LoginViewModelFactory(apiService)).get(LoginViewModel::class.java)
    val userProfileViewModel: UserProfileViewModel = ViewModelProvider(viewModelStoreOwner, UserProfileViewModelFactory(apiService, loginViewModel)).get(UserProfileViewModel::class.java)

    // Set up the navigation host
    NavHost(
        navController = navController,
        startDestination = NavigationItem.Start.route,
        modifier = modifier
    ) {
        composable(NavigationItem.Login.route) {
            // Pass the ViewModel and NavController to the LoginScreen
            LoginScreen(viewModel = loginViewModel, navController = navController)
        }
        composable(NavigationItem.Profile.route) {
            // Pass the ViewModel and NavController to the UserProfileScreen
            UserProfileScreen(viewModel = userProfileViewModel, navController = navController)
        }
        composable(NavigationItem.Register.route) {
            // Pass the ViewModel and NavController to the UserProfileScreen
            RegisterScreen(viewModel = RegisterViewModel, navController = navController)
        }
        composable(NavigationItem.Start.route) {
            StartScreen(navController = navController)
        }
    }
}
