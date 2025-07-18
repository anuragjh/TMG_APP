package com.example.material.api.repo

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.material.api.*
import com.example.material.pages.commons.Notice
import com.squareup.moshi.Moshi
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TeacherClassRepository @Inject constructor(
    private val service: ApiService,
    @ApplicationContext private val ctx: Context,
    moshi: Moshi
) {

    /* --- helpers --------------------------------------------------- */
    private val metaAdapter = moshi.adapter(FileMeta::class.java)

    /* --- existing endpoints --------------------------------------- */
    suspend fun fetchOngoingClasses(): List<OngoingClass> =
        withContext(Dispatchers.IO) { service.getOngoingClasses() }
    suspend fun getUsernameFromApi(): Response<ResponseBody> {
        return service.getMyUsername()
    }

    suspend fun getMyClasses(): List<ClassNameResponse> =
        service.getMyClasses().map { ClassNameResponse(it) }

    suspend fun startClass(className: String): String =
        service.startClass(className).string()

    suspend fun fetchStudents(classId: String): List<Student> =
        service.getValidStudents(classId)

    suspend fun submitAttendance(classId: String, req: AttendanceRequest): ResponseBody =
        service.giveAttendance(classId, req)

    suspend fun endClass(classId: String, req: EndClassRequest): String {
        val rsp = service.putEndClass(classId, req)
        Log.d("API_CALL", "PUT /api/endClass?classDocId=$classId")
        return rsp.string()
    }

    suspend fun fetchNotes() = service.getNotes()
    suspend fun getAllClasses(): List<ClassResponse> = service.getAllClasses()

    /* --- NEW: upload a note --------------------------------------- */
    suspend fun uploadNote(classInfo: ClassResponse, uri: Uri, docName: String) =
        withContext(Dispatchers.IO) {

            /* file part ------------------------------------------------ */
            val mime = ctx.contentResolver.getType(uri) ?: "application/octet-stream"
            val bytes = ctx.contentResolver.openInputStream(uri)!!.use { it.readBytes() }

            val filePart = MultipartBody.Part.createFormData(
                name = "file",
                filename = docName,
                body = bytes.toRequestBody(mime.toMediaType())
            )

            /* meta part ------------------------------------------------ */
            val metaJson = metaAdapter.toJson(
                FileMeta(
                    name = docName,
                    className = classInfo.className,
                    students = classInfo.students
                )
            )
            val metaPart = metaJson.toRequestBody("application/json".toMediaType())

            /* backend call --------------------------------------------- */
            service.uploadNote(file = filePart, meta = metaPart)
        }

    suspend fun deleteNotes(names: List<String>) =
        service.deleteNotes(names)

// TeacherClassRepository (or whatever class owns the call)

    suspend fun fetch(): Result<List<Notice>> = runCatching {
        Log.d("NoticeFlow", "🌐 GET /api/notice")
        val res = service.getNotices()

        Log.d("NoticeFlow", "↩️  HTTP ${res.code()}  body=${res.body()?.size}")

        if (!res.isSuccessful || res.body() == null)
            error(res.errorBody()?.string() ?: "Server error")

        res.body()!!.map { dto ->
            Log.d("NoticeFlow", "📄 DTO  $dto")

            Notice(
                date = Instant.ofEpochSecond(dto.date.seconds)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate(),
                topic = dto.topic,
                body  = dto.body,
                importance = dto.importance        // <- will crash if enum stripped
            ).also { mapped ->
                Log.d("NoticeFlow", "✅ Mapped ${mapped.importance}")
            }
        }
    }.onFailure {
        Log.e("NoticeFlow", "🔥 fetch() failed", it)
    }

    suspend fun createNotice(req: NoticeRequest): Result<Unit> = runCatching {
        val res = service.postNotice(req)

        // HTTP failure → throw
        if (!res.isSuccessful) error(res.errorBody()?.string() ?: "HTTP ${res.code()}")

        // body is optional – we only care that it succeeded
        Unit
    }
}
