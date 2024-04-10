package com.emill.workplacetracking

// Defining the ApiService Interface

/*Now that we have our singleton pattern in place for the Retrofit instance,
let’s define the ApiService interface that outlines the API endpoints and their respective HTTP methods.*/

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("auth/login")
    suspend fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): AuthResponse

    @POST("auth/register")
    suspend fun registerUser(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("phone") phone: String,
        @Field("picture") picture: String,
        @Field("salary") salary: String,
        @Field("password") password: String
    ): AuthResponse
}