package com.example.material.api

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response
import okhttp3.ResponseBody
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

data class LoginRequest(val username: String, val password: String)
data class LoginResponse(
    val jwt: String?,
    val role: String?
)

data class UpdatePasswordRequest(
    val key: String,
    val password: String
)

data class UserDto(
    val username: String,
    val name: String
)

data class ClassCreationRequest(
    val className: String,
    val fees: Int,
    val teachers: List<String>,
    val students: List<String>
)

data class ClassNameResponse(
    val className: String
)
data class NonUserResponse(
    val username: String,
    val name: String,
    val role: String
)

data class AddUserRequest(
    val username: String,
    val role: String
)

data class MessageResponse(val message: String)

data class Timestamp(
    val seconds: Long,
    val nanos: Int
)

data class ClassDetails(
    val className: String,
    val fees: Int,
    val createdAt: Timestamp
)

//user
data class CreateUserRequest(
    val role: String,
    val name: String,
    val gmail: String,
    val phone: String,
    val username: String,
    val password: String
)

data class CreateUserResponse(
    val id: String,
    val role: String,
    val name: String,
    val gmail: String,
    val phone: String,
    val joinedAt: String,
    val username: String
)

data class User(
    val name: String,
    val username: String,
    val role: String
)

data class UserProfile(
    val username: String,
    val name: String,
    val gmail: String,
    val phone: String,
    val classes: List<String>
)

data class UserProfileUpdateRequest(
    val name: String,
    val gmail: String,
    val phone: String
)


interface ApiService {

    @POST("/api/auth/login")
    suspend fun login(@Body body: LoginRequest): Response<LoginResponse>



    @POST("/api/auth/forgot-password")
    suspend fun forgotPassword(@Query("email") email: String): Response<ResponseBody>

    @POST("api/auth/verify-otp")
    suspend fun verifyOtp(
        @Query("email") email: String,
        @Query("otp") otp: String
    ): Response<Map<String, String>>


    @POST("api/auth/new-password")
    suspend fun updatePassword(
        @Body request: UpdatePasswordRequest
    ): Response<ResponseBody>

    @GET("api/getAllUsers")
    suspend fun getAllUsers(
        @Query("role") role: String
    ): List<UserDto>

    @POST("/api/create-class")
    suspend fun createClass(@Body request: ClassCreationRequest): Response<MessageResponse>

    @GET("/api/getClassNamesOnly")
    suspend fun getClassNames(): List<ClassNameResponse>

    @GET("api/{className}/all-nonusers")
    suspend fun getNonUsers(@Path("className") className: String): List<NonUserResponse>

    @GET("api/{className}/all-users")
    suspend fun getUsers(@Path("className") className: String): List<NonUserResponse>

    @PUT("api/{className}/add-users")
    suspend fun addUsersToClass(
        @Path("className") className: String,
        @Body users: List<AddUserRequest>
    ): Response<ResponseBody>

    @HTTP(method = "DELETE", path = "api/{className}/remove-users", hasBody = true)
    suspend fun removeUsersFromClass(
        @Path("className") className: String,
        @Body users: List<AddUserRequest>
    ): Response<ResponseBody>

    @GET("api/{className}")
    suspend fun getClassDetails(@Path("className") className: String): ClassDetails

    @HTTP(method = "DELETE", path = "api/delete-class/{className}", hasBody = false)
    suspend fun deleteClass(@Path("className") className: String): Response<ResponseBody>

    @POST("/api/auth/create-user")
    suspend fun createUser(@Body body: CreateUserRequest): Response<CreateUserResponse>

    @GET("/api/getAllUsersavaliable")
    suspend fun getAllUsers(): List<User>

    @GET("api/user-profile")
    suspend fun getUserProfile(@Query("username") username: String): Response<UserProfile>

    @PUT("api/user-profile/{username}")
    suspend fun updateUserProfile(
        @Path("username") username: String,
        @Body request: UserProfileUpdateRequest
    ): Response<ResponseBody>

    @HTTP(method = "DELETE", path = "api/user-profile/{username}", hasBody = false)
    suspend fun deleteUser(@Path("username") username: String): Response<ResponseBody>

}
