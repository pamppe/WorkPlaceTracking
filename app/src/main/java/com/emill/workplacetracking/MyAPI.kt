package com.emill.workplacetracking


import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
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

data class WorkAreaRequest(
    @SerializedName("worker_id") val workerId: Int,
    @SerializedName("workArea_id") val workAreaId: Int,
    @SerializedName("is_active") val isActive: Int,
    @SerializedName("joined_at") val joinedAt: String,
    @SerializedName("approved") val approved: Int,
    @SerializedName("workArea_name") val workAreaName: String
)

data class WorkAreaDetails(
    @SerializedName("id") val workAreaId: Int,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("radius") val radius: Float
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

    @FormUrlEncoded
    @POST("/workAreas/reguestJoinWorkArea/{userId}")
    suspend fun requestAccess(
        @Path("userId") userId: Int,
        @Field("access_code") code: String,
        @Header("Authorization") token: String
    ): Response<AuthResponse>

    @GET("/workAreas/requests/{userId}")
    suspend fun getPendingWorkAreas(
        @Path("userId") userId: Int,
        @Header("Authorization") token: String
    ): Response<List<WorkAreaRequest>>

    @GET("/workAreas/{workAreaId}")
    suspend fun getWorkAreaDetails(
        @Path("workAreaId") workAreaId: Int,
        @Header("Authorization") token: String
    ): Response<WorkAreaDetails>

    @FormUrlEncoded
    @POST("updateUserStatusInArea")
    suspend fun updateUserStatusInArea(
        @Field("workerId") workerId: Int,
        @Field("workAreaId") workAreaId: Int,
        @Field("isActive") isActive: Boolean
    ): Response<Void>


}