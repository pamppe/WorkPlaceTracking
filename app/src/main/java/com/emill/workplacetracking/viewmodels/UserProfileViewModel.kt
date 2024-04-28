package com.emill.workplacetracking.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.emill.workplacetracking.Account
import com.emill.workplacetracking.DB.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserProfileViewModel(
    loginViewModel: LoginViewModel,
    private val repository: Repository,
) : ViewModel() {
    // Directly use userData from LoginViewModel
    val account: StateFlow<Account?> = loginViewModel.userData

    fun logoutUser(navController: NavController) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.deleteUser()
            }
            navController.navigate("start")
        }
    }
}
