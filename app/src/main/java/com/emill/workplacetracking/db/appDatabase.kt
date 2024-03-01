package com.emill.workplacetracking.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [UserInfo::class, WorkEntry::class], version = 2) // Note the version increase
abstract class AppDatabase : RoomDatabase() {
    abstract fun userInfoDao(): UserInfoDao
    abstract fun workEntryDao(): WorkEntryDao
}
