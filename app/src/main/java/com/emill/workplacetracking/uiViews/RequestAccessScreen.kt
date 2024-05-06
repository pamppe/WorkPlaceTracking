package com.emill.workplacetracking.uiViews

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.emill.workplacetracking.viewmodels.RequestAccessViewModel
import androidx.compose.runtime.collectAsState
@Composable
fun RequestAccessScreen(viewModel: RequestAccessViewModel, navController: NavController) {
    val accessCode = remember { mutableStateOf("") }
    // Get the list of pending requests from the ViewModel
    val pendingRequests = viewModel.pendingRequests.collectAsState()

    LaunchedEffect(key1 = viewModel) {
        viewModel.getPendingWorkAreas()
    }

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

        // Display the list of pending requests

        LazyColumn {
            item {
                Text(text = "Requests", fontSize = 24.sp)
            }
            items(pendingRequests.value) { request ->
                Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Work Area Name: ${request.workAreaName}", fontSize = 18.sp)
                        Text(
                            text = if (request.approved == 1) "Request approved" else "Request pending",
                            fontSize = 16.sp,
                            color = if (request.approved == 1) Color.Green else Color(0xFFFFA500) // Orange color
                        )
                    }
                }
            }
        }
    }
}