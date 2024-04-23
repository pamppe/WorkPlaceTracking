package com.emill.workplacetracking.uiViews

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = accessCode.value,
            onValueChange = { accessCode.value = it },
            label = { Text(text = "Access Code") }
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            viewModel.requestAccess(accessCode.value)
        }) {
            Text(text = "Request Access")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Previous Requests", fontSize = 18.sp)

        // Here you can add a list of previous requests made by the user.
        // You can use a LazyColumn to display a potentially large number of items.
    }
}