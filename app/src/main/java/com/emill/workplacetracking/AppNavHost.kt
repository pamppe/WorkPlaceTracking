package com.emill.workplacetracking

import NavigationItem
import RegisterScreen
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.DrawerValue
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.emill.workplacetracking.DB.Repository
import com.emill.workplacetracking.DB.TokenDao
import com.emill.workplacetracking.DB.UserDao
import com.emill.workplacetracking.uiViews.BottomNavigationBar
import com.emill.workplacetracking.uiViews.GpsScreen
import com.emill.workplacetracking.uiViews.LoginScreen
import com.emill.workplacetracking.uiViews.RequestAccessScreen
import com.emill.workplacetracking.uiViews.StartScreen
import com.emill.workplacetracking.uiViews.TimerScreen
import com.emill.workplacetracking.uiViews.UserProfileScreen
import com.emill.workplacetracking.viewmodels.LoginViewModel
import com.emill.workplacetracking.viewmodels.LoginViewModelFactory
import com.emill.workplacetracking.viewmodels.RegisterViewModel
import com.emill.workplacetracking.viewmodels.RegisterViewModelFactory
import com.emill.workplacetracking.viewmodels.RequestAccessViewModel
import com.emill.workplacetracking.viewmodels.RequestAccessViewModelFactory
import com.emill.workplacetracking.viewmodels.TimerViewModel
import com.emill.workplacetracking.viewmodels.UserProfileViewModel
import com.emill.workplacetracking.viewmodels.UserProfileViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(
    navController: NavHostController,
    tokenDao: TokenDao,
    userDao: UserDao,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val viewModelStoreOwner = LocalViewModelStoreOwner.current ?: return
    val apiService = RetrofitInstance.api

    // ViewModel instances
    val timerViewModel: TimerViewModel = ViewModelProvider(viewModelStoreOwner).get(TimerViewModel::class.java)
    val repository = Repository(tokenDao, userDao)
    val registerViewModel: RegisterViewModel = ViewModelProvider(viewModelStoreOwner, RegisterViewModelFactory(apiService, context)).get(RegisterViewModel::class.java)
    val loginViewModel: LoginViewModel = ViewModelProvider(viewModelStoreOwner, LoginViewModelFactory(apiService, tokenDao, repository)).get(LoginViewModel::class.java)
    val userProfileViewModel: UserProfileViewModel = ViewModelProvider(viewModelStoreOwner, UserProfileViewModelFactory(apiService, loginViewModel, repository)).get(UserProfileViewModel::class.java)
    val requestAccessViewModel: RequestAccessViewModel = ViewModelProvider(viewModelStoreOwner, RequestAccessViewModelFactory(apiService, loginViewModel, tokenDao)).get(RequestAccessViewModel::class.java)

    val scaffoldState = rememberScaffoldState(drawerState = rememberDrawerState(initialValue = DrawerValue.Closed))
    val scope = rememberCoroutineScope()
    
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text("Workplace Tracker") },
                navigationIcon = {
                    IconButton(onClick = {
                        scope.launch {
                            if (scaffoldState.drawerState.isClosed) {
                                scaffoldState.drawerState.open()
                            } else {
                                scaffoldState.drawerState.close()
                            }
                        }
                    }) {
                        Icon(Icons.Filled.Menu, contentDescription = "Menu")
                    }
                }
            )
        },
        drawerContent = {
            DrawerContent(navController, scaffoldState, scope)
        },
        content = { padding ->
            Box(modifier = Modifier.padding(padding)) {
                NavHost(navController, startDestination = NavigationItem.Start.route) {
                    composable(NavigationItem.Start.route) { StartScreen(navController) }
                    composable(NavigationItem.Login.route) { LoginScreen(viewModel = loginViewModel, navController) }
                    composable(NavigationItem.Profile.route) { UserProfileScreen(viewModel = userProfileViewModel, navController) }
                    composable(NavigationItem.Register.route) { RegisterScreen(viewModel = registerViewModel, navController) }
                    composable(NavigationItem.Timer.route) { TimerScreen(timerViewModel, navController) }
                    composable(NavigationItem.Gps.route) { GpsScreen(navController) }
                    composable(NavigationItem.RequestAccess.route) { RequestAccessScreen(viewModel = requestAccessViewModel, navController) }
                }
            }
        }
    )
}

@Composable
fun DrawerContent(navController: NavController, scaffoldState: ScaffoldState, scope: CoroutineScope) {
    Column(modifier = Modifier.padding(16.dp)) {
        listOf(
            NavigationItem.Start to Icons.Filled.Home,
            NavigationItem.Profile to Icons.Filled.AccountCircle,
            NavigationItem.Gps to Icons.Filled.LocationOn,
            NavigationItem.RequestAccess to Icons.Filled.Add,
            NavigationItem.Timer to Icons.Filled.DateRange,
        ).forEach { (item, icon) ->
            DrawerButton(item.route.replaceFirstChar { it.uppercase() }, icon) {
                navController.navigate(item.route) {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
                scope.launch { scaffoldState.drawerState.close() }
            }
        }
    }
}

@Composable
fun DrawerButton(text: String, icon: ImageVector, onClick: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable(onClick = onClick).padding(16.dp)) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(16.dp))
        Text(text)
    }
}
