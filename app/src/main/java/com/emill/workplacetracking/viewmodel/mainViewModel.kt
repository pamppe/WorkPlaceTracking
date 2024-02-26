package com.emill.workplacetracking.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.emill.workplacetracking.db.UserInfo
import kotlinx.coroutines.launch
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.emill.workplacetracking.db.DatabaseBuilder
import com.emill.workplacetracking.db.UserInfoDao


class MainViewModel(
    private val userInfoDao: UserInfoDao
) : ViewModel() {
    // Using liveData builder to call suspend function
    val userInfo = liveData {
        val data = userInfoDao.getUserInfo() // This is a suspend function call
        emit(data) // Emitting the result to LiveData
    }

    fun insertUserInfo(userInfo: UserInfo) {
        viewModelScope.launch {
            userInfoDao.insert(userInfo)
            // No need to manually post value to userInfo LiveData,
            // as it will be automatically updated on the next query due to the liveData builder block
        }
    }
}