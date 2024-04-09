package com.emill.workplacetracking.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emill.workplacetracking.AuthResponse
import com.emill.workplacetracking.MyAPI
import com.emill.workplacetracking.RequestState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val apiService: MyAPI) : ViewModel() {
    val loginRequestState: MutableStateFlow<RequestState<AuthResponse>> =
        MutableStateFlow(RequestState.Empty)

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            loginRequestState.value = RequestState.Loading
            try {
                val response = apiService.loginUser(email, password)
                if (response.isSuccessful) {
                    response.body()?.let {
                        loginRequestState.value = RequestState.Success(it)
                    }
                } else {
                    throw Exception("Error: ${response.code()}")
                }
            } catch (e: Exception) {
                loginRequestState.value = RequestState.Error(e)
            }
        }
    }
}