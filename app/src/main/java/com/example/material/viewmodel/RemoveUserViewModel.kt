package com.example.material.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.material.api.AddUserRequest
import com.example.material.api.NonUserResponse
import com.example.material.api.repo.ClassRepositoryForUsers
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class RemoveUserViewModel @Inject constructor(
    private val repository: ClassRepositoryForUsers
) : ViewModel() {

    private val _nonTeachers = mutableStateListOf<NonUserResponse>()
    val nonTeachers: List<NonUserResponse> get() = _nonTeachers

    private val _nonStudents = mutableStateListOf<NonUserResponse>()
    val nonStudents: List<NonUserResponse> get() = _nonStudents

    private val _loading = mutableStateOf(false)
    val loading: State<Boolean> get() = _loading

    private val _updating = mutableStateOf(false)
    val updating: State<Boolean> get() = _updating

    fun loadUsers(className: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val result = repository.fetchNonUsers(className) // Consider renaming to fetchUsers()
                _nonTeachers.clear()
                _nonStudents.clear()
                result.forEach {
                    when (it.role.uppercase()) {
                        "TEACHER" -> _nonTeachers.add(it)
                        "STUDENT" -> _nonStudents.add(it)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _loading.value = false
            }
        }
    }

    fun removeSelectedUsers(
        className: String,
        selected: List<Pair<String, String>>,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        viewModelScope.launch {
            _updating.value = true
            try {
                val requestList = selected.map { AddUserRequest(it.first, it.second) }

                val response: Response<ResponseBody> =
                    repository.removeUsersFromClass(className, requestList)
                val body = response.body()?.string()?.trim()

                if (response.isSuccessful && body == "Users removed successfully") {
                    onSuccess()
                } else {
                    onError(Exception("Unexpected response: ${response.code()} $body"))
                }
            } catch (e: Exception) {
                onError(e)
            } finally {
                _updating.value = false
            }
        }
    }
}
