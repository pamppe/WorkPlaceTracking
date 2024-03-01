package com.emill.workplacetracking

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class TimerViewModel : ViewModel() {
    private val _timerNotifications = MutableStateFlow<String?>(null)
    val timerNotifications: StateFlow<String?> = _timerNotifications

    private val _time = MutableStateFlow("00:00:00")
    val time: StateFlow<String> = _time

    private var job: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)
    private var seconds = 0
    private var isRunning = false


    fun toggleTimer() {
        if (isRunning) {
            stopTimer()
        } else {
            startTimer()
        }
    }

    private fun startTimer() {
        isRunning = true
        _timerNotifications.value = "Timer Started"
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
        _timerNotifications.value = "Timer Stopped"
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