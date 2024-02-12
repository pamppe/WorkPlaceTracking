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
    private val _time = MutableStateFlow("00:00:00")
    val time: StateFlow<String> = _time

    private var job: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    fun startTimer() {
        job = scope.launch {
            var seconds = 0

            while (isActive) {
                _time.value = formatTime(seconds) // Update the MutableStateFlow
                delay(1000)
                seconds++
            }
        }
    }

    fun stopTimer() {
        job?.cancel()
    }

    private fun formatTime(seconds: Int): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, secs)
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}
