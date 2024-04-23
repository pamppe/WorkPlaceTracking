package com.emill.workplacetracking.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.emill.workplacetracking.DB.TokenDao
import com.emill.workplacetracking.MyAPI

class LoginViewModelFactory(private val apiService: MyAPI, private val tokenDao: TokenDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(apiService, tokenDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}