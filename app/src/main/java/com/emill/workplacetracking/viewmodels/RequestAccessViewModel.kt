package com.emill.workplacetracking.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emill.workplacetracking.Account
import kotlinx.coroutines.launch
import com.emill.workplacetracking.MyAPI
import kotlinx.coroutines.flow.MutableStateFlow

class RequestAccessViewModel(private val apiService: MyAPI, private val userId: String) : ViewModel() {
    val userData: MutableStateFlow<Account?> = MutableStateFlow(null)
    fun requestAccess(code: String) {
        viewModelScope.launch {
            try {
                val response = apiService.requestAccess(userId, code) // Pass userId here
                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    userData.value = authResponse.account
                } else {
                    throw Exception("Error: ${response.code()}")
                }
            } catch (e: Exception) {
                // Handle exception
            }
        }
    }
}