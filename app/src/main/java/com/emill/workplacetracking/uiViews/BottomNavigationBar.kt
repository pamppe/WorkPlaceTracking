package com.emill.workplacetracking.uiViews

import NavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable


@Composable
fun BottomNavigationBar(currentRoute: String, onNavigate: (String) -> Unit) {
    val items = listOf(
        NavigationItem.Timer,
        NavigationItem.Profile,
        NavigationItem.Gps,
        NavigationItem.RequestAccess,
    )
    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    when (item) {
                        NavigationItem.RequestAccess-> Icon(Icons.Filled.CheckCircle, contentDescription = "Request Access")
                        NavigationItem.Profile -> Icon(Icons.Filled.AccountCircle, contentDescription = "Profile")
                        NavigationItem.Gps -> Icon(Icons.Filled.LocationOn, contentDescription = "GPS")
                        NavigationItem.Timer -> Icon(Icons.Filled.Info, contentDescription = "Timer")
                        else -> Icon(Icons.Filled.Home, contentDescription = "Default") // Default case
                    }
                },
                label = {
                    Text(when (item) {
                        NavigationItem.Timer -> "Timer"
                        NavigationItem.Profile -> "Profile"
                        NavigationItem.Gps -> "GPS"
                        NavigationItem.RequestAccess -> "Request Access"
                        else -> "Default" // Default label
                    })
                },
                selected = currentRoute == item.route,
                onClick = { onNavigate(item.route) }
            )
        }
    }
}
