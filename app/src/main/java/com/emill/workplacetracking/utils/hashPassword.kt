package com.emill.workplacetracking.utils

import android.util.Log
import java.math.BigInteger
import java.security.MessageDigest

fun hashPassword(password: String): String {
    //cant use hash for password since the automatic login pastes the password
    // from room and it's hashed but the server expects the password in plain text.
    val md = MessageDigest.getInstance("SHA-256")
    val hashedPassword = BigInteger(1, md.digest(password.toByteArray())).toString(16).padStart(32, '0')

    Log.d("HashPassword", "Hashed password: $hashedPassword")

    return hashedPassword
}