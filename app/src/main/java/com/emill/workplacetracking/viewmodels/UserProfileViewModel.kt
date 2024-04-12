package com.emill.workplacetracking.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emill.workplacetracking.Account
import com.emill.workplacetracking.MyAPI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class UserProfileViewModel(private val apiService: MyAPI, private val loginViewModel: LoginViewModel) : ViewModel() {
    // Define your LiveData or StateFlow variables here
    val userData: MutableStateFlow<LoginViewModel.UserData?> = MutableStateFlow(null)

    fun fetchUserData(userId: Int) {
        viewModelScope.launch {
            val token = loginViewModel.userToken ?: throw Exception("User not logged in")
            val response = apiService.getUserData(userId, token)
            if (response.isSuccessful) {
                response.body()?.let { account ->
                    val user = convertAccountToUserData(account)
                    userData.value = user
                }
            } else {
                throw Exception("Error: ${response.code()}")
            }
        }
        }
    fun convertAccountToUserData(account: Account): LoginViewModel.UserData {
        // Implement your conversion logic here
        // This is just a placeholder
        return LoginViewModel.UserData(account.id, account.name, account.email, account.phone, account.salary)
    }
    }
// Add more functions as needed to handle user profile related logic