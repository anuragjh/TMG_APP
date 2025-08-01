package com.example.material.api


import com.example.material.pages.commons.ChatMessage
import com.example.material.pages.commons.Importance
import com.example.material.pages.teacher.NoteItem
import com.example.material.viewmodel.teacher.ResultData
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming

data class LoginRequest(val username: String, val password: String)
data class LoginResponse(
    val jwt: String?,
    val role: String?,
    val username: String?,
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
data class AttendanceSummary(
    val attendanceId: String,
    val className: String,
    val teacherUsername: String,
    val date: TimestampData
)

data class TimestampData(val seconds: Long, val nanos: Int)

data class AttendanceDetail(
    val attendanceId: String,
    val className: String,
    val teacherUsername: String,
    val topicCovered: String,
    val present: List<String>,
    val absent: List<String>,
    val date: TimestampData,
    val startTime: String,
    val endTime: String
)

//teacher home
data class OngoingClass(
    val teacherUsername: String,
    val stage: String,
    val className: String
)
data class Student(
    val username: String,
    val name: String
)
data class AttendanceRequest(
    val present: List<String>,
    val absent: List<String>
)

data class EndClassRequest(
    val topicCovered: String
)

// api/model/ClassResponse.kt
data class ClassResponse(
    val className: String,
    val students: List<String>      // not used yet
)
data class FileMeta(
    val name: String,
    val className: String,
    val students: List<String>
)

data class NoticeDto(
    val role       : String,
    val body       : String,
    val topic      : String,
    val importance : Importance,
    val date       : TimestampDto
)
data class NoticeRequest(
    val role: String,        // "STUDENT" | "TEACHER"
    val topic: String,
    val body: String,
    val importance: String   // "NORMAL" | "MEDIUM" | "HIGH"
)
data class TimestampDto(val seconds: Long, val nanos: Int)



data class ChatRoomResponse(
    val className: String,
    val canEveryoneMessage: Boolean,
    val lastMessage: LastMessage?,
    val username: String? = null
)

data class LastMessage(
    val id: String,
    val className: String,
    val senderUsername: String,
    val message: String,
    val timestamp: String
)
data class UpdateResponse(
    val updateRequired: Boolean,
    val data: UpdateData?
)

data class UpdateData(
    val id: String,
    val version: String,
    val apkUrl: String,
    val releaseMonth: String,
    val size: String,
    val description: String
)

data class AttendanceList(
    val attendance : List<ApiService.AttendanceEntry>
)
data class Message(
    val message: String
)
data class RoutineEntry(
    val id: String,
    val className: String,
    val startTime: String,
    val teacherName: String,
    val dayOfWeek: DAYS // e.g., "MONDAY"
)
enum class DAYS {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY
}

data class StudentResult(
    val testName: String,
    val teacherName: String,
    val rank: String,
    val examDate: String,
    val marksObtained: Int,
    val totalMarks: Int,
    val remark: String
)
data class PTMRequester(
    var id: String? = null,
    var requestedBy: String,
    var dateToBeAppearedBy: String,
    var status: String,
    var attendeName: String
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
    data class MyAttendanceResponse(
        val totalClasses: Int,
        val attendedClasses: Int,
        val attendance: List<AttendanceEntry>
    )

    data class AttendanceEntry(
        val attendanceId: String,
        val className: String,
        val teacherUsername: String,
        val topicCovered: String,
        val status: String,
        val date: DateWrapper,
        val startTime: String,
        val endTime: String
    )

    data class DateWrapper(
        val seconds: Long,
        val nanos: Int
    )


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

    //attendance
    @GET("/api/attendance-summary")
    suspend fun getAttendanceSummary(): List<AttendanceSummary>
    @GET("/api/attendance/{attendanceId}")
    suspend fun getAttendanceDetail(@Path("attendanceId") attendanceId: String): AttendanceDetail
    //teacher home
    @GET("/api/ongoing-classes")
    suspend fun getOngoingClasses(): List<OngoingClass>
    @GET("/api/my-classes")
    suspend fun getMyClasses(): List<String>
    @POST("/api/start-class")
    suspend fun startClass(@Query("className") className: String): ResponseBody
    @GET("/api/valid-students")
    suspend fun getValidStudents(@Query("attendanceDocId") classId: String): List<Student>
    @PUT("api/giveAttendance")
    suspend fun giveAttendance(
        @Query("classDocId") id: String,
        @Body request: AttendanceRequest
    ): ResponseBody
    @PUT("/api/endClass")
    suspend fun putEndClass(
        @Query("classDocId") classId: String,
        @Body request: EndClassRequest
    ): ResponseBody

    @GET("api/user-profile-jwt")
    suspend fun getUserProfilejwt(): Response<UserProfile>
    @GET("api/files")
    suspend fun getNotes(): Response<List<NoteItem>>
    @GET("/api/all-classes-student")
    suspend fun getAllClasses(): List<ClassResponse>

    @Multipart
    @POST("/api/files")
    suspend fun uploadNote(
        @Part file: MultipartBody.Part,
        @Part("meta") meta: RequestBody
    ): ResponseBody
    @HTTP(method = "DELETE", path = "/api/files", hasBody = true)
    suspend fun deleteNotes(@Body names: List<String>): retrofit2.Response<okhttp3.ResponseBody>

    @GET("/api/notice")
    suspend fun getNotices(): Response<List<NoticeDto>>
    @POST("/api/notice")
    suspend fun postNotice(
        @Body req: NoticeRequest      // ← your DTO
    ): Response<ResponseBody>

    @GET("api/chatroom/mychatrooms")
    suspend fun getMyChatRooms(): List<ChatRoomResponse>

    @GET("api/chat/{className}/history")
    suspend fun getMessages(@Path("className") className: String): List<ChatMessage>
    @GET("api/myUsername")
    suspend fun getMyUsername(): Response<ResponseBody>
    @GET("api/update/check")
    suspend fun checkUpdate(@Query("version") version: String): UpdateResponse
    @GET("/api/my-attendance")
    suspend fun getMyAttendance(): MyAttendanceResponse
    @POST("/api/generate-pdf")
    @Streaming
    suspend fun generatePdf(
        @Body request: RequestBody
    ): Response<Message>
    @GET("/api/routines")
    suspend fun getRoutines(): List<RoutineEntry>

    @PUT("api/auth/update-password")
    suspend fun updatePassword(
        @Body body: Map<String, String>
    ): Response<Void>

    @POST("/create-result")
    suspend fun createResult(
        @Body request: ResultData
    ):Response<Message>
    @GET("/get-result-by-teacher")
    suspend fun getTeacherResults(): List<ResultData>

    @GET("/get-result")
    suspend fun getStudentResults(): List<StudentResult>

    @GET("/api/ptm/by-requester")
    suspend fun getPtmRequesters(): List<PTMRequester>

    @GET("/api/ptm/by-attendee")
    suspend fun getPtmRequestersByAttendee(): List<PTMRequester>
}

