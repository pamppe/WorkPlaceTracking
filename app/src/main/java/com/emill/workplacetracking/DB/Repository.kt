package com.emill.workplacetracking.DB

import android.util.Log

class Repository(private val tokenDao: TokenDao, private val userDao: UserDao) {
    suspend fun getToken(): String? {
        return tokenDao.getToken()?.token
    }

    suspend fun saveUser(user: User) {
        userDao.saveUser(user)
    }

    suspend fun getUser(): User? {
        return userDao.getUser()
    }

    suspend fun deleteUser() {
        Log.d("debug", "deleteUser called")
        userDao.deleteUsers()
    }
}