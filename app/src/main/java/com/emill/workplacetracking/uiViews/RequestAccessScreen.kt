package com.emill.workplacetracking.uiViews

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.emill.workplacetracking.viewmodels.RequestAccessViewModel

@Composable
fun RequestAccessScreen(viewModel: RequestAccessViewModel, navController: NavController) {
    val accessCode = remember { mutableStateOf("") }

    // Assume this is your list of pending requests
    val pendingRequests = viewModel.pendingRequests

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = accessCode.value,
            onValueChange = { accessCode.value = it },
            label = { Text(text = "Access Code") }
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            Log.d("RequestAccess", "Button clicked with access code: ${accessCode.value}")
            viewModel.requestAccess(accessCode.value)
        }) {
            Text(text = "Request Access")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Previous Requests", fontSize = 18.sp)

        // Display the list of pending requests
        LazyColumn {
            items(pendingRequests) { request ->
                Text(text = "Request ID: ${request.id}, Status: ${request.status}")
            }
        }
    }
}