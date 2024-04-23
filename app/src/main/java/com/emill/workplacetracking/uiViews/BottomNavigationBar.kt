package com.emill.workplacetracking.uiViews

import NavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable


@Composable
fun BottomNavigationBar(currentRoute: String, onNavigate: (String) -> Unit) {
    val items = listOf(
        NavigationItem.RequestAccess,   // Assuming you have a Home object in NavigationItem
        NavigationItem.Profile,
        NavigationItem.Gps
    )
    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    when (item) {
                        NavigationItem.RequestAccess-> Icon(Icons.Filled.Home, contentDescription = "Home")
                        NavigationItem.Profile -> Icon(Icons.Filled.AccountCircle, contentDescription = "Profile")
                        NavigationItem.Gps -> Icon(Icons.Filled.LocationOn, contentDescription = "GPS")
                        else -> Icon(Icons.Filled.Home, contentDescription = "Default") // Default case
                    }
                },
                label = {
                    Text(when (item) {
                        NavigationItem.Timer -> "requestAccess"
                        NavigationItem.Profile -> "Profile"
                        NavigationItem.Gps -> "GPS"
                        else -> "Default" // Default label
                    })
                },
                selected = currentRoute == item.route,
                onClick = { onNavigate(item.route) }
            )
        }
    }
}
