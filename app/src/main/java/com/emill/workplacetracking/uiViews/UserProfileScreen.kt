package com.emill.workplacetracking.uiViews

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.emill.workplacetracking.DB.Repository
import com.emill.workplacetracking.viewmodels.UserProfileViewModel


@Composable
fun UserProfileScreen(viewModel: UserProfileViewModel, navController: NavController) {
    Log.d("UserProfileScreen", "Composing UserProfileScreen")
    val userData by viewModel.account.collectAsState()
    Log.d("UserProfileScreen", "User data: $userData")
    var showLogoutDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp).fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        userData?.let { user ->
            user.getImageUrl()?.let { imageUrl ->
                Image(
                    painter = rememberImagePainter(imageUrl),
                    contentDescription = "Profile Image",
                    modifier = Modifier.size(150.dp)  // Adjust the size as needed
                )
            }

            Text("Name: ${user.name}")
            Text("Email: ${user.email}")
            Text("Phone: ${user.phone}")
            Text("Salary: ${user.salary}")
            Button(onClick = {
                showLogoutDialog = true
            }) {
                Text(text = "Logout")
            }

        } ?: Text("Loading...")
    }
    // Logout confirmation dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = {
                // Hide the dialog when the user cancels the logout
                showLogoutDialog = false
            },
            title = { Text("Logout") },
            text = {
                Text("Are you sure you want to logout?"
                ) },
            confirmButton = {
                Button(onClick = {
                    viewModel.logoutUser(navController)
                    showLogoutDialog = false
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                Button(onClick = {
                    showLogoutDialog = false
                }) {
                    Text("No")
                }
            }
        )
    }
}
