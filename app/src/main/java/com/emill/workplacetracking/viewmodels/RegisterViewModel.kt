package com.emill.workplacetracking.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emill.workplacetracking.AuthResponse
import com.emill.workplacetracking.MyAPI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import com.emill.workplacetracking.RequestState

class RegisterViewModel (private val apiService: MyAPI) : ViewModel() {
    val registerRequestState: MutableStateFlow<RequestState<AuthResponse>> =
        MutableStateFlow(RequestState.Empty)

    fun registerUser(email: String, password: String, name: String, phone: String, salary: String, picture: String) {
        viewModelScope.launch {
            registerRequestState.value = RequestState.Loading
            try {
                val response = apiService.registerUser(name, email, phone, picture, salary, password)
                if (response.isSuccessful) {
                    response.body()?.let {
                        registerRequestState.value = RequestState.Success(it)
                    }
                } else {
                    throw Exception("Error: ${response.code()}")
                }
            } catch (e: Exception) {
                registerRequestState.value = RequestState.Error(e)
            }
        }
    }
}