package com.emill.workplacetracking.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emill.workplacetracking.Account
import com.emill.workplacetracking.AuthResponse
import com.emill.workplacetracking.DB.Repository
import com.emill.workplacetracking.DB.Token
import com.emill.workplacetracking.DB.TokenDao
import com.emill.workplacetracking.DB.User
import com.emill.workplacetracking.MyAPI
import com.emill.workplacetracking.RequestState

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val apiService: MyAPI,
    private val repository: Repository,
    private val tokenDao: TokenDao
) : ViewModel() {

    val userData: MutableStateFlow<Account?> = MutableStateFlow(null)
    val tokenData: MutableStateFlow<String?> = MutableStateFlow(null)
    val loginRequestState: MutableStateFlow<RequestState<AuthResponse>> = MutableStateFlow(RequestState.Empty)
    val userLiveData: MutableStateFlow<User?> = MutableStateFlow(null)

    init {
        viewModelScope.launch {
            val user = repository.getUser()
            user?.let {
                userLiveData.value = it
                loginUser(it.email, it.password)
            }
        }
    }

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            loginRequestState.value = RequestState.Loading
            try {
                val response = apiService.loginUser(email, password)
                if (response.isSuccessful && response.body() != null) {
                    response.body()?.let {
                        repository.saveUser(User(email = email, password = password))
                    }
                    val authResponse = response.body()!!
                    userData.value = authResponse.account // Directly assign Account
                    tokenData.value = authResponse.token
                    loginRequestState.value = RequestState.Success(authResponse)
                    Log.d("User logged in:", tokenData.value ?: "No token")
                    // Save the token to the database
                    val token = Token(token = authResponse.token)
                    tokenDao.saveToken(token)
                } else {
                    throw Exception("Error: ${response.code()}")
                }
            } catch (e: Exception) {
                loginRequestState.value = RequestState.Error(e)
            }
        }
    }
}
