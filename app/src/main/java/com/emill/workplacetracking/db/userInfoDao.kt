package com.emill.workplacetracking.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query


@Dao
interface UserInfoDao {
    @Insert
    suspend fun insert(userInfo: UserInfo): Long // Returns the ID of the inserted item

    @Query("SELECT * FROM UserInfo LIMIT 1")
    suspend fun getUserInfo(): UserInfo? // Directly returns UserInfo or null
}