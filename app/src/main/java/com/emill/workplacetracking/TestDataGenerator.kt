// TestDataGenerator.kt

import androidx.lifecycle.viewModelScope
import com.emill.workplacetracking.db.WorkEntry
import com.emill.workplacetracking.db.WorkEntryDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object TestDataGenerator {
    fun addTestData(coroutineScope: CoroutineScope, workEntryDao: WorkEntryDao, userId: Int) {
        // Create some sample WorkEntry objects
        val workEntry1 = WorkEntry(date = "2024-03-01", hoursWorked = 8, userId = userId) // Sample entry for March 1st, 2024
        val workEntry2 = WorkEntry(date = "2024-03-02", hoursWorked = 7, userId = userId) // Sample entry for March 2nd, 2024
        val workEntry3 = WorkEntry(date = "2024-03-03", hoursWorked = 6, userId = userId) // Sample entry for March 3rd, 2024

        // Insert the sample WorkEntry objects into the database
        coroutineScope.launch {
            workEntryDao.insert(workEntry1)
            workEntryDao.insert(workEntry2)
            workEntryDao.insert(workEntry3)
        }
    }
}

