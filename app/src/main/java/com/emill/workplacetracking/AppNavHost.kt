package com.emill.workplacetracking

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.emill.workplacetracking.uiViews.LoginScreen
import com.emill.workplacetracking.uiViews.UserProfileScreen
import com.emill.workplacetracking.viewmodels.LoginViewModel
import com.emill.workplacetracking.viewmodels.LoginViewModelFactory
import com.emill.workplacetracking.viewmodels.UserProfileViewModel
import com.emill.workplacetracking.viewmodels.UserProfileViewModelFactory



@Composable
fun AppNavHost(navController: NavHostController) {

    val viewModelStoreOwner = LocalViewModelStoreOwner.current ?: return
    val apiService = RetrofitInstance.api
    val loginViewModel: LoginViewModel = ViewModelProvider(viewModelStoreOwner, LoginViewModelFactory(apiService)).get(LoginViewModel::class.java)
    val userProfileViewModel: UserProfileViewModel = ViewModelProvider(viewModelStoreOwner, UserProfileViewModelFactory(apiService, loginViewModel)).get(UserProfileViewModel::class.java)


    NavHost(navController = navController, startDestination = NavigationItem.Login.route) {
        composable(NavigationItem.Login.route) {
            LoginScreen(navController = navController, viewModel = loginViewModel)
        }
        composable(NavigationItem.Profile.route) {
            UserProfileScreen(navController = navController, viewModel = userProfileViewModel)
        }
    }
}


/*@Composable
fun AppNavHost(navHostController: NavHostController) {
    val viewModelStoreOwner = LocalViewModelStoreOwner.current ?: return
    val apiService = RetrofitInstance.api
    val loginViewModel: LoginViewModel = ViewModelProvider(viewModelStoreOwner, LoginViewModelFactory(apiService)).get(LoginViewModel::class.java)
    val userProfileViewModel: UserProfileViewModel = ViewModelProvider(viewModelStoreOwner, UserProfileViewModelFactory(apiService, loginViewModel)).get(UserProfileViewModel::class.java)

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController = navController, viewModel = loginViewModel)
        }
        composable("profile") {
            UserProfileScreen(navController = navController, viewModel = userProfileViewModel)
        }
    }
}*/