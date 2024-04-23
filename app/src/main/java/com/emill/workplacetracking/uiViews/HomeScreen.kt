package com.emill.workplacetracking.uiViews

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.emill.workplacetracking.viewmodels.TimerViewModel
import com.emill.workplacetracking.viewmodels.MainViewModel

@Composable
fun HomeScreen(mainViewModel: MainViewModel, timerViewModel: TimerViewModel, navController: NavController) {
    //val userInfo by mainViewModel.userInfo.observeAsState()
  //  val userId by mainViewModel.userId.observeAsState()

    // Track if the initial data fetch has been completed.
    val dataFetchCompleted = remember { mutableStateOf(false) }
    val showDialog = remember { mutableStateOf(false) }

    // Adjust LaunchedEffect to set dataFetchCompleted to true once userInfo is observed.
   /* LaunchedEffect(key1 = userInfo) {
        dataFetchCompleted.value = true
        showDialog.value = userInfo == null
    }*/

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
            TimerScreen(TimerViewModel(), navController, timerViewModel)
            Spacer(modifier = Modifier.height(16.dp))

           // if (userInfo != null) {

                //WorkedHoursDisplay(mainViewModel, userInfo!!.id)
            }
            //else if (showDialog.value) {
                // Conditionally display the UserInfoDialog based on showDialog state.
                //UserInfoDialog(showDialog = showDialog, viewModel = mainViewModel)
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

