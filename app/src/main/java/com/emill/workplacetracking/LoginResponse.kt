package com.emill.workplacetracking

data class LoginResponse(
    val account: Account,
    val token: String
)