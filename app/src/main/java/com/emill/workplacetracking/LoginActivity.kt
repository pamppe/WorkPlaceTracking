package com.emill.workplacetracking

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.emill.workplacetracking.uiViews.LoginScreen
import com.emill.workplacetracking.viewmodels.LoginViewModel
import com.emill.workplacetracking.viewmodels.LoginViewModelFactory

class LoginActivity : AppCompatActivity() {
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val apiService = RetrofitInstance.api
        viewModel = ViewModelProvider(this, LoginViewModelFactory(apiService)).get(LoginViewModel::class.java)

        setContent {
            MaterialTheme {

                // Create a NavController
                val navController = rememberNavController()

                // Pass the NavController to the LoginScreen
                LoginScreen(viewModel, navController)
               // LoginScreen(viewModel)
            }
        }
    }
}