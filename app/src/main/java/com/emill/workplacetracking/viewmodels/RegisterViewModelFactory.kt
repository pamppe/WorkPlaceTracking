package com.emill.workplacetracking.viewmodels
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.emill.workplacetracking.MyAPI

class RegisterViewModelFactory(
    private val apiService: MyAPI,
    private val context: Context  // Add context here
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            // Pass the context to the ViewModel
            return RegisterViewModel(apiService, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
