package com.emill.workplacetracking.uiViews

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.emill.workplacetracking.viewmodels.TimerViewModel

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
