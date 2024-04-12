package com.emill.workplacetracking.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.emill.workplacetracking.MyAPI

class UserProfileViewModelFactory(private val apiService: MyAPI, private val loginViewModel: LoginViewModel) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserProfileViewModel::class.java)) {
            return UserProfileViewModel(apiService, loginViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}