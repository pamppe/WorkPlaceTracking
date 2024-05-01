package com.emill.workplacetracking.DB

import android.util.Log
import com.emill.workplacetracking.utils.hashPassword

class Repository(private val tokenDao: TokenDao, private val userDao: UserDao) {
    suspend fun getToken(): String? {
        return tokenDao.getToken()?.token
    }

    suspend fun saveUser(user: User) {
        /*val hashedPassword = hashPassword(user.password) // Hash the password
        val userWithHashedPassword = user.copy(password = hashedPassword) // Create a new User object with the hashed password
        userDao.saveUser(userWithHashedPassword) // Save the user with the hashed password
        Log.d("debug", "User saved: $userWithHashedPassword")*/
        userDao.saveUser(user)
    }

    suspend fun getUser(): User? {
        val user = userDao.getUser()
        Log.d("debug", "getting user: $user")
        return user

    }

    suspend fun deleteUser() {
        val user = userDao.getUser()
        Log.d("debug", "deleteUser called: $user")
        userDao.deleteUsers()
    }
}