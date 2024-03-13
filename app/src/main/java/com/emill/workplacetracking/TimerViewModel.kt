package com.emill.workplacetracking

import android.icu.text.SimpleDateFormat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emill.workplacetracking.db.WorkEntry
import com.emill.workplacetracking.db.WorkEntryDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Date
import java.util.Locale

class TimerViewModel(
    private val WorkEntryDao : WorkEntryDao
) : ViewModel() {
    private val _timerNotifications = MutableStateFlow<String?>(null)
    val timerNotifications: StateFlow<String?> = _timerNotifications

    private val _time = MutableStateFlow("00:00:00")
    val time: StateFlow<String> = _time

    private var job: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)
    private var seconds = 0
    private var isRunning = false

    // Public method to check if the timer is running
    fun isTimerRunning(): Boolean {
        return isRunning
    }

    fun toggleTimer() {
        if (isRunning) {
            stopTimer()
        } else {
            startTimer()
        }
    }

    fun startTimer() {
        isRunning = true
        _timerNotifications.value = "You have entered the workplace. Timer started."
        job = scope.launch {
            while (isActive) {
                _time.value = formatTime(seconds)
                delay(1000)
                seconds++
            }
        }
    }

    fun stopTimer() {
        isRunning = false
        job?.cancel()
    }
    fun stopTimerAndSaveEntry(userId: Int) {
        if (isRunning) {
            val hoursWorked = seconds / 3600 // Assuming seconds is total seconds worked
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val workEntry = WorkEntry(date = date, hoursWorked = hoursWorked, userId = userId)
            viewModelScope.launch {
                WorkEntryDao.insert(workEntry)
                stopTimer() // Resets the timer
                _timerNotifications.value = "You have exited the workplace. Your work hours have been saved."
                resetTimer()
            }
        }
    }

    fun resetTimer() {
        stopTimer()
        seconds = 0
        _time.value = formatTime(seconds) // Reset the displayed time immediately
    }
    fun clearTimerNotification() {
        _timerNotifications.value = null
    }

    private fun formatTime(seconds: Int): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, secs)
    }
}