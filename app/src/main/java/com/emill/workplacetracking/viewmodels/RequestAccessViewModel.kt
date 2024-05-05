package com.emill.workplacetracking.viewmodels

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emill.workplacetracking.DB.TokenDao
import com.emill.workplacetracking.MyAPI
import com.emill.workplacetracking.WorkAreaRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class RequestAccessViewModel(private val apiService: MyAPI, private val loginViewModel: LoginViewModel, private val tokenDao: TokenDao) : ViewModel() {
    init {
        getPendingWorkAreas()
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
                        getPendingWorkAreas() // Update the list after the request is made
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

    //val pendingRequests = mutableStateOf<List<WorkAreaRequest>>(emptyList())
    val pendingRequests = MutableStateFlow<List<WorkAreaRequest>>(emptyList())
    fun getPendingWorkAreas() {
        viewModelScope.launch {
            try {
                val user = loginViewModel.userData.value
                val tokenEntity = tokenDao.getToken()
                if (user != null && tokenEntity != null) {
                    val response = apiService.getPendingWorkAreas(user.id, "Bearer " + tokenEntity.token)
                    if (response.isSuccessful) {
                        Log.d("RequestAccess", "API Response: ${response.body()}")
                        pendingRequests.value = response.body() ?: emptyList()
                    } else {
                        throw Exception("Error: ${response.code()}")
                    }
                } else {
                    throw Exception("User data or token is null")
                }
            } catch (e: Exception) {
                Log.d("RequestAccess", "Exception: ${e.message}")
            }
        }
    }
}