package com.emill.workplacetracking.uiViews

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BottomNavigationBar(currentRoute: String, onNavigate: (String) -> Unit) {
    NavigationBar {
        val items = listOf(
            NavigationItem("Home", Icons.Filled.Home),
            NavigationItem("Profile", Icons.Filled.AccountCircle), // Assuming you have an appropriate icon
            NavigationItem("Gps", Icons.Filled.LocationOn),
            NavigationItem("Start", Icons.Filled.LocationOn)
        )
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title, modifier = Modifier.size(18.dp)) }, // Smaller icons
                label = { Text(item.title, fontSize = 10.sp) }, // Smaller text
                selected = currentRoute == item.title,
                onClick = { onNavigate(item.title) },
                // Apply padding inside NavigationBarItem for further size adjustments
                modifier = Modifier.padding(vertical = 1.dp) // Reduce padding to make items appear smaller
            )
        }
    }
}
data class NavigationItem(val title: String, val icon: ImageVector)