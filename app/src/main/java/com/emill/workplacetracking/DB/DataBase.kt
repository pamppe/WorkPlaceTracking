package com.emill.workplacetracking.DB

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Database
import androidx.room.RoomDatabase

@Entity
data class Token(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val token: String
)

@Dao
interface TokenDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveToken(token: Token)

    @Query("SELECT * FROM Token LIMIT 1")
    suspend fun getToken(): Token?
}

@Database(entities = [Token::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tokenDao(): TokenDao
}
