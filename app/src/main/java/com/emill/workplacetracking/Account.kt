package com.emill.workplacetracking

data class Account(
    val created_at: String,
    val email: String,
    val id: Int,
    val name: String,
    val password: String,
    val phone: String,
    val picture: String,
    val salary: String
)