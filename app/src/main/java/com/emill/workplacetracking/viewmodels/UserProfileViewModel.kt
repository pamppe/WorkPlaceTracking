package com.emill.workplacetracking.viewmodels

import androidx.lifecycle.ViewModel
import com.emill.workplacetracking.MyAPI
import com.emill.workplacetracking.Account
import kotlinx.coroutines.flow.StateFlow

class UserProfileViewModel(private val apiService: MyAPI, private val loginViewModel: LoginViewModel) : ViewModel() {
    // Directly use userData from LoginViewModel
    val account: StateFlow<Account?> = loginViewModel.userData
}
