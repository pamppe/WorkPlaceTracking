package com.emill.workplacetracking.db

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.emill.workplacetracking.viewmodels.MainViewModel


class MainViewModelFactory(
    private val userInfoDao: UserInfoDao,
    private val workEntryDao: WorkEntryDao // Add this
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(userInfoDao, workEntryDao) as T // Pass workEntryDao here
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}