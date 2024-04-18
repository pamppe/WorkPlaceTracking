package com.emill.workplacetracking

data class Account(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String,
    val salary: String,
   // val picture: String, // Optional if you decide to use it later
    // val created_at: String // Optional
)
