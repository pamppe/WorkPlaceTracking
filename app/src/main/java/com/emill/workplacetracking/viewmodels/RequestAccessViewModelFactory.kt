package com.emill.workplacetracking.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.emill.workplacetracking.DB.TokenDao
import com.emill.workplacetracking.MyAPI

class RequestAccessViewModelFactory (private val apiService: MyAPI, private val loginViewModel: LoginViewModel, val tokenDao: TokenDao) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RequestAccessViewModel::class.java)) {
                return RequestAccessViewModel(apiService, loginViewModel, tokenDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
