package com.emill.workplacetracking.uiViews

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.emill.workplacetracking.viewmodels.RegisterViewModel

@Composable
fun RegisterScreen(viewModel: RegisterViewModel) {

    var email by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }
    var name by remember {
        mutableStateOf("")
    }
    var number by remember {
        mutableStateOf("")
    }
    var salary by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

       // Text(text = "Sign up", fontSize = 28.sp, fontWeight = FontWeight.Bold)

        // Spacer(modifier = Modifier.height(4.dp))
        // Text(text = "Please enter your details")

        Spacer(modifier = Modifier.height(16.dp))
        PickImageFromGallery()

        OutlinedTextField(value = name, onValueChange = {
            name = it
        }, label = {
            Text(text = "Name")
        })

        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = email, onValueChange = {
            email = it
        }, label = {
            Text(text = "Email")
        })
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = number,
            onValueChange = { text ->
                number = text
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.NumberPassword
            ),
            visualTransformation = VisualTransformation.None,
            label = { Text(text = "Phone number") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = password, onValueChange = {
            password = it
        }, label = {
            Text(text = "Password")
        })

        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = password, onValueChange = {
            password = it
        }, label = {
            Text(text = "Confirm Password")
        })

        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = salary,
            onValueChange = { text ->
                salary = text
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.NumberPassword
            ),
            visualTransformation = VisualTransformation.None,
            label = { Text(text = "Hourly salary") }
        )

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            viewModel.registerUser(email, password, name, number, salary)
            Log.i("Credential", "Email: $email, Password: $password, name: $name, number: $number")
        }, modifier = Modifier
            .width(170.dp)
            .height(45.dp)
        ) {
            Text(text = "Register")
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}
