package com.emill.workplacetracking.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/*@Entity
data class UserInfo(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val email: String
    // Add other fields as needed
)*/

@Entity
data class UserInfo(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val firstName: String,
    val lastName: String,
    val company: String,
    val hourlyRate: Double,

    // Add other fields as needed
)