package com.emill.workplacetracking.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emill.workplacetracking.DB.TokenDao
import com.emill.workplacetracking.MyAPI
import kotlinx.coroutines.launch

class RequestAccessViewModel(private val apiService: MyAPI, private val loginViewModel: LoginViewModel, private val tokenDao: TokenDao) : ViewModel() {
    init {
        viewModelScope.launch {
            loginViewModel.userData.collect { user ->
                // Do something with user
            }
        }
    }
    fun requestAccess(code: String) {
        viewModelScope.launch {
            Log.d("RequestAccess", "requestAccess function called with code: $code")
            try {
                val user = loginViewModel.userData.value
                val tokenEntity = tokenDao.getToken()
                if (user != null && tokenEntity != null) {
                    Log.d("RequestAccess", "User data is not null. Making API call.")
                    Log.d("RequestAccess", "is there Access code: $code")
                    val response = apiService.requestAccess(user.id, code,"Bearer " + tokenEntity.token)
                    Log.d("RequestAccess", "API call made. Response: $response")
                    if (response.isSuccessful && response.body() != null) {
                        // Handle successful response
                    } else {
                        throw Exception("Error: ${response.code()}")
                    }
                } else {
                    throw Exception("User data is null")
                }
            } catch (e: Exception) {
                Log.d("RequestAccess", "Exception: ${e.message}")
            }
        }
    }
}