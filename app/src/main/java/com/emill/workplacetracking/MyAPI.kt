package com.emill.workplacetracking


import com.emill.workplacetracking.viewmodels.RequestAccessViewModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

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
    val account: Account,
    val token: String
)


interface MyAPI {
    //Log
    @FormUrlEncoded
    @POST(LOGIN_ENDPOINT)
    suspend fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<AuthResponse>

    @Multipart
    @POST(REGISTER_ENDPOINT)
    suspend fun registerUser(
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part("salary") salary: RequestBody,
        @Part picture: MultipartBody.Part?
    ): Response<AuthResponse>

        @POST("auth/reguestJoinWorkArea/{userId}")
        suspend fun requestAccess(
            @Path("userId") userId: String,
            @Part("access_code") code: String
        ): Response<AuthResponse>

}