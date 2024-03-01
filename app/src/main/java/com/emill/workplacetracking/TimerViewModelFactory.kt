package com.emill.workplacetracking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.emill.workplacetracking.db.WorkEntryDao

class TimerViewModelFactory(private val workEntryDao: WorkEntryDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TimerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TimerViewModel(workEntryDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
