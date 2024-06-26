package com.emill.workplacetracking.uiViews

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.emill.workplacetracking.R
import com.emill.workplacetracking.RequestState
import com.emill.workplacetracking.viewmodels.LoginViewModel


@Composable
fun LoginScreen(viewModel: LoginViewModel, navController: NavController) {

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    // Observe userLiveData here (this will be triggered when the user data is available)
    LaunchedEffect(key1 = viewModel.userLiveData) {
        viewModel.userLiveData.collect { user ->
            user?.let {
                email = it.email
                password = it.password
                viewModel.loginUser(email, password)
            }
        }
    }

    // Observe loginRequestState and userData here
    LaunchedEffect(key1 = viewModel.loginRequestState) {
        viewModel.loginRequestState.collect { requestState ->
            when (requestState) {
                is RequestState.Loading -> {
                    // Show a loading spinner
                }
                is RequestState.Success -> {
                    navController.popBackStack()
                    navController.previousBackStackEntry?.savedStateHandle?.set("msg", "Login successful")
                    // Hide the loading spinner
                    // Navigate to the profile page or update the UI with the user data
                    navController.navigate("profile")

                }
                is RequestState.Error -> {
                    // Hide the loading spinner
                    // Show an error message
                }
                else -> {
                    // Handle other states
                }
            }
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = painterResource(id = R.drawable.kuva1), contentDescription = "Login image",
            modifier = Modifier.size(330.dp))

        Text(text = "Welcome back", fontSize = 28.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "Login")

        Spacer(modifier = Modifier.height(13.dp))
        OutlinedTextField(value = email, onValueChange = {
            email = it
        }, label = {
            Text(text = "Email")
        })
        Spacer(modifier = Modifier.height(13.dp))
        OutlinedTextField(value = password, onValueChange = {
            password = it
        }, label = {
            Text(text = "Password")
        })
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            Log.d("LoginScreen", "Attempting to navigate to profile")
            viewModel.loginUser(email, password)
            Log.i("Credential", "Email : $email Password : $password")
            },
            modifier = Modifier
                .width(170.dp)
                .height(45.dp)
        ) {
            Text(text = "Login")
        }

        Spacer(modifier = Modifier.height(5.dp))
        Text(text = "or", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(5.dp))
        Button(onClick = {
            navController.navigate("register")
        },
            modifier = Modifier
                .width(170.dp)
                .height(45.dp)
        ) {
            Text(text = "Register")

        }
        TextButton(onClick = {}) {
            Text(text ="Forgot your password?")
        }
    }
}
