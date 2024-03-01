package com.emill.workplacetracking.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface WorkEntryDao {
    @Insert
    suspend fun insert(workEntry: WorkEntry): Long

    @Query("SELECT * FROM WorkEntry WHERE userId = :userId ORDER BY date DESC")
    suspend fun getWorkEntriesForUser(userId: Int): List<WorkEntry>


}