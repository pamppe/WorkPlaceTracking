package com.emill.workplacetracking.utils// com.emill.workplacetracking.utils.TestDataGenerator.kt

import androidx.lifecycle.viewModelScope
import com.emill.workplacetracking.db.WorkEntry
import com.emill.workplacetracking.db.WorkEntryDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object TestDataGenerator {
    fun addTestData(coroutineScope: CoroutineScope, workEntryDao: WorkEntryDao, userId: Int) {
        // Create some sample WorkEntry objects
        val workEntry1 = WorkEntry(date = "2024-03-11", hoursWorked = 8, userId = userId) // March 11th
        val workEntry2 = WorkEntry(date = "2024-03-11", hoursWorked = 7, userId = userId) // Duplicate for March 11th
        val workEntry3 = WorkEntry(date = "2024-03-12", hoursWorked = 6, userId = userId) // March 12th
        val workEntry4 = WorkEntry(date = "2024-03-12", hoursWorked = 5, userId = userId) // Duplicate for March 12th
        val workEntry5 = WorkEntry(date = "2024-03-13", hoursWorked = 6, userId = userId) // March 13th
        val workEntry6 = WorkEntry(date = "2024-03-14", hoursWorked = 10, userId = userId) // March 14th
        val workEntry7 = WorkEntry(date = "2024-03-15", hoursWorked = 9, userId = userId) // March 15th
        val workEntry8 = WorkEntry(date = "2024-03-16", hoursWorked = 8, userId = userId) // March 16th
        val workEntry9 = WorkEntry(date = "2024-03-17", hoursWorked = 3, userId = userId) // March 17th
        val workEntry10 = WorkEntry(date = "2024-03-04", hoursWorked = 2, userId = userId) // Sample entry for March 3rd, 2024
        // Insert the sample WorkEntry objects into the database
        coroutineScope.launch {
            workEntryDao.insert(workEntry1)
            workEntryDao.insert(workEntry2)
            workEntryDao.insert(workEntry3)
            workEntryDao.insert(workEntry4)
            workEntryDao.insert(workEntry5)
            workEntryDao.insert(workEntry6)
            workEntryDao.insert(workEntry7)
            workEntryDao.insert(workEntry8)
            workEntryDao.insert(workEntry9)
            workEntryDao.insert(workEntry10)
        }
    }
}

