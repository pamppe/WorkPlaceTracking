package com.emill.workplacetracking.uiViews

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.emill.workplacetracking.db.UserInfo
import com.emill.workplacetracking.viewmodels.MainViewModel

@Composable
fun UserInfoDialog(showDialog: MutableState<Boolean>, viewModel: MainViewModel) {
    val nameState = remember { mutableStateOf("") }
    val lastNameState = remember { mutableStateOf("") } // Use this if you plan to collect the last name from the user
    val companyState = remember { mutableStateOf("") }
    val hourlyRateState = remember { mutableStateOf("") }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("Enter User Info") },
            text = {
                Column {
                    OutlinedTextField(
                        value = nameState.value,
                        onValueChange = { nameState.value = it },
                        label = { Text("Name") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = lastNameState.value, // Assuming you're also collecting the last name
                        onValueChange = { lastNameState.value = it },
                        label = { Text("Last Name") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = companyState.value, // Assuming you're also collecting the last name
                        onValueChange = { companyState.value = it },
                        label = { Text("Company Name") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = hourlyRateState.value,
                        onValueChange = { newValue ->
                            if (newValue.isEmpty() || newValue.toDoubleOrNull() != null) {
                                hourlyRateState.value = newValue
                            }
                        },
                        label = { Text("Hourly Rate") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    // Ensure conversion from String to Double for hourlyRate
                    val hourlyRate = hourlyRateState.value.toDoubleOrNull() ?: 0.0 // Provide a fallback value

                    // Now create the UserInfo object with the converted hourly rate
                    val userInfo = UserInfo(
                        firstName = nameState.value,
                        lastName = lastNameState.value,
                        company = companyState.value,
                        hourlyRate = hourlyRate // Pass the converted value
                    )

                    // Insert the userInfo into the database via ViewModel
                    viewModel.insertUserInfo(userInfo)
                    showDialog.value = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog.value = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}