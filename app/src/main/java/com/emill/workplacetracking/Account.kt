package com.emill.workplacetracking

data class Account(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String,
    val salary: String,
    val picture: String?,
    // val created_at: String // Optional
)
{
    fun getImageUrl(): String? = picture?.let { "${Config.UPLOADS_PATH}$it" }
    }
