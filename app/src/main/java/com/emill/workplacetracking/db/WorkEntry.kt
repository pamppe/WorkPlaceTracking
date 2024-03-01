package com.emill.workplacetracking.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class WorkEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String, // Consider using a more suitable date type or format
    val hoursWorked: Int,
    val userId: Int // Assuming you want to link this entry to a specific user
)
