package com.emill.workplacetracking.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emill.workplacetracking.Account
import com.emill.workplacetracking.AuthResponse
import com.emill.workplacetracking.MyAPI
import com.emill.workplacetracking.RequestState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val apiService: MyAPI) : ViewModel() {

    val userData: MutableStateFlow<UserData?> = MutableStateFlow(null)

    fun fetchUserData(accountId: Int) {
        viewModelScope.launch {
            try {
                val response = apiService.getUserData(accountId, token = userToken ?: throw Exception("User not logged in"))
                if (response.isSuccessful) {
                    response.body()?.let { account ->
                        val user = convertAccountToUserData(account)
                        userData.value = user
                    }
                } else {
                    throw Exception("Error: ${response.code()}")
                }
            } catch (e: Exception) {
                // Handle the error
            }
        }
    }

    fun convertAccountToUserData(account: Account): UserData {
        // Implement your conversion logic here
        // This is just a placeholder
        return UserData(account.id, account.name, account.email, account.phone, account.salary)
    }

    data class UserData(
        val id: Int,
        val name: String,
        val email: String,
        val phone: String,
        //val picture: String,
        val salary: String,
    )


    val loginRequestState: MutableStateFlow<RequestState<AuthResponse>> =
        MutableStateFlow(RequestState.Empty)

    var userToken: String? = null

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            loginRequestState.value = RequestState.Loading
            try {
                val response = apiService.loginUser(email, password)
                if (response.isSuccessful) {
                    response.body()?.let {
                        loginRequestState.value = RequestState.Success(it)
                        userToken = it.token
                        val user = convertAccountToUserData(it.account)
                        userData.value = user
                        //userToken = response.body()?.account?.token
                        // Fetch user data here
                       // fetchUserData(it.account.id)
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