package com.emill.workplacetracking

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.ViewModelProvider
import com.emill.workplacetracking.uiViews.RegisterScreen
import com.emill.workplacetracking.viewmodels.RegisterViewModel
import com.emill.workplacetracking.viewmodels.RegisterViewModelFactory

class RegisterActivity : AppCompatActivity() {
    private lateinit var viewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val apiService = RetrofitInstance.api
        viewModel = ViewModelProvider(this, RegisterViewModelFactory(apiService)).get(RegisterViewModel::class.java)

        setContent {
            MaterialTheme {
                RegisterScreen(viewModel)
            }
        }
    }
}