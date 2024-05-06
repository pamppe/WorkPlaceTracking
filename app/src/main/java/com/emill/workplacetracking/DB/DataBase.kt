package com.emill.workplacetracking.DB

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Database
import androidx.room.RoomDatabase
import com.emill.workplacetracking.utils.hashPassword


@Database(entities = [Token::class, User::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tokenDao(): TokenDao
    abstract fun userDao(): UserDao
}

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

    @Query("DELETE FROM Token")
    suspend fun deleteToken()
}


@Entity
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val email: String,
    var password: String
)

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUser(user: User)

    @Query("SELECT * FROM User LIMIT 1")
    suspend fun getUser(): User?

    @Query("DELETE FROM User")
    suspend fun deleteUsers()
}