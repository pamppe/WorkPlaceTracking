package com.emill.workplacetracking.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.emill.workplacetracking.db.UserInfo
import kotlinx.coroutines.launch
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.emill.workplacetracking.db.DatabaseBuilder
import com.emill.workplacetracking.db.UserInfoDao
import com.emill.workplacetracking.db.WorkEntry
import com.emill.workplacetracking.db.WorkEntryDao
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters


class MainViewModel(
    private val userInfoDao: UserInfoDao,
    private val workEntryDao: WorkEntryDao // Add this
) : ViewModel() {
    // Using liveData builder to call suspend function
   /* val userInfo = liveData {
        val data = userInfoDao.getUserInfo() // This is a suspend function call
        emit(data) // Emitting the result to LiveData
    }*/
    val userInfo = userInfoDao.getUserInfo1()
    private val _userId = MutableLiveData<Int>()
    val userId: LiveData<Int> = _userId

    fun insertUserInfo(userInfo: UserInfo) {
        viewModelScope.launch {
            val id = userInfoDao.insert(userInfo).toInt()
            _userId.postValue(id) // Update LiveData with the newly inserted user ID
        }
    }
    // Function to insert a new work entry
    fun insertWorkEntry(workEntry: WorkEntry) {
        viewModelScope.launch {
            workEntryDao.insert(workEntry)
        }
    }

    // Function to get all work entries for a user
    fun getWorkEntriesForUser(userId: Int): LiveData<List<WorkEntry>> = liveData {
        emit(workEntryDao.getWorkEntriesForUser(userId))
    }
    fun getWorkEntriesForCurrentWeek(userId: Int): LiveData<List<WorkEntry>> = liveData {
        val allEntries = workEntryDao.getWorkEntriesForUser(userId)
        val currentWeekEntries = allEntries.filter {
            val entryDate = LocalDate.parse(it.date) // Assumes ISO format: yyyy-MM-dd
            val now = LocalDate.now()
            val startOfWeek = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            val endOfWeek = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
            !entryDate.isBefore(startOfWeek) && !entryDate.isAfter(endOfWeek)
        }
        emit(currentWeekEntries)
    }
    fun getWorkEntriesForCurrentMonth(userId: Int): LiveData<List<WorkEntry>> = liveData {
        val allEntries = workEntryDao.getWorkEntriesForUser(userId) // Assuming this is a suspend function
        val currentMonthEntries = allEntries.filter {
            val entryDate = LocalDate.parse(it.date) // Assumes ISO format: yyyy-MM-dd
            val now = LocalDate.now()
            entryDate.month == now.month && entryDate.year == now.year
        }
        emit(currentMonthEntries)
    }
    fun getTotalHoursAllTime(userId: Int): LiveData<Int> = liveData {
        val allEntries = workEntryDao.getWorkEntriesForUser(userId) // Assuming this is a suspend function
        val totalHours = allEntries.sumOf { it.hoursWorked }
        emit(totalHours)
    }


}