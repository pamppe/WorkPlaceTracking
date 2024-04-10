package com.emill.workplacetracking

import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Query

    /*
    Both @Post and @Get annotations represent different types of requests.
    However, we will treat them in a similar manner. It's essential to confirm the method (GET or POST)
    based on your API documentation.
    */

    /*
    @Query("email") : is used to tell retrofit that this field is what is called
    email in the request Queries
    */

    /*
    some apis requires key or token but that is not used in our tutorial
    */
    const val LOGIN_ENDPOINT = "auth/login"
    const val REGISTER_ENDPOINT = "auth/register"

    data class AuthResponse(
        val success: Boolean,
        val message: String,
        val account: Account
    )

interface MyAPI {

    @FormUrlEncoded
    @POST(LOGIN_ENDPOINT)
    suspend fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<AuthResponse>

    @FormUrlEncoded
    @POST(REGISTER_ENDPOINT)
    suspend fun registerUser(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("phone") phone: String,
        @Field("salary") salary: String,
        @Field("picture") picture: String
    ): Response<AuthResponse>

}