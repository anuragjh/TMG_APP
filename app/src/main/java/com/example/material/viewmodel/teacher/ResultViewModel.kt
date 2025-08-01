// ResultEntryViewModel.kt
package com.example.material.viewmodel.teacher

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.material.api.repo.ClassRepositoryForUsers
import com.google.gson.GsonBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import javax.inject.Inject


data class StudentInput(
    val username: String,
    val name: String,
    val marksObtained: String = "",
    val isCompleted: Boolean = false
)

sealed class ResultEntryUiState {
    object Loading : ResultEntryUiState()
    data class Success(val students: List<StudentInput>) : ResultEntryUiState()
    data class Error(val message: String) : ResultEntryUiState()
    object Submitted : ResultEntryUiState()
}

// Renamed from StudentResult for better clarity with backend's StudentInfo
data class ResultStudentPayload(
    val studentName: String,
    val rank: String,
    val marksObtained: Int
)

data class ResultData(
    val className: String,
    val totalMarks: Int,
    val testName: String,
    val students: List<ResultStudentPayload> // Using the new class name
)

@HiltViewModel
class ResultEntryViewModel @Inject constructor(
    private val repository: ClassRepositoryForUsers
) : ViewModel() {

    private val _uiState = MutableStateFlow<ResultEntryUiState>(ResultEntryUiState.Loading)
    val uiState: StateFlow<ResultEntryUiState> = _uiState.asStateFlow()

    private val _studentsInput = MutableStateFlow<List<StudentInput>>(emptyList())
    val studentsInput: StateFlow<List<StudentInput>> = _studentsInput.asStateFlow()

    fun loadStudents(classId: String) {
        _uiState.value = ResultEntryUiState.Loading
        viewModelScope.launch {
            try {
                val apiUsers = repository.fetchNonUsers(classId)
                Log.d("ResultEntryViewModel", "Fetched users: $apiUsers")

                val studentInputs = apiUsers
                    .filter { it.role != "TEACHER" }
                    .map { apiUser ->
                        StudentInput(
                            username = apiUser.username,
                            name = apiUser.name
                        )
                    }
                _studentsInput.value = studentInputs
                _uiState.value = ResultEntryUiState.Success(studentInputs)
            } catch (e: Exception) {
                _uiState.value = ResultEntryUiState.Error(e.message ?: "Something went wrong")
            }
        }
    }

    fun updateStudentMarks(studentInput: StudentInput, marks: String, totalMarks: Float?) {
        val currentStudents = _studentsInput.value.toMutableList()
        val index = currentStudents.indexOfFirst { it.username == studentInput.username }
        if (index != -1) {
            val marksFloat = marks.toFloatOrNull()
            val isMarksValid = marks.isNotBlank() && marksFloat != null && marksFloat >= 0 && (totalMarks == null || marksFloat <= totalMarks)
            val isCompleted = isMarksValid

            currentStudents[index] = studentInput.copy(marksObtained = marks, isCompleted = isCompleted)
            _studentsInput.value = currentStudents

            _uiState.value = ResultEntryUiState.Success(currentStudents)
        }
    }

    fun submitResults(testName: String, totalMarks: Int, classId: String) {
        viewModelScope.launch {
            try {
                _uiState.value = ResultEntryUiState.Loading
                val rankedStudents = _studentsInput.value
                    .map {
                        ResultStudentPayload(
                            studentName = it.username,
                            rank = "",
                            marksObtained = it.marksObtained.toFloatOrNull()?.roundToInt() ?: 0
                        )
                    }
                    .sortedByDescending { it.marksObtained }
                    .toMutableList()

                // Assign ranks, handling ties
                if (rankedStudents.isNotEmpty()) {
                    rankedStudents[0] = rankedStudents[0].copy(rank = "1")
                    for (i in 1 until rankedStudents.size) {
                        val currentStudent = rankedStudents[i]
                        val previousStudent = rankedStudents[i - 1]
                        val newRank = if (currentStudent.marksObtained == previousStudent.marksObtained) {
                            previousStudent.rank
                        } else {
                            (i + 1).toString()
                        }
                        rankedStudents[i] = currentStudent.copy(rank = newRank)
                    }
                }

                val resultData = ResultData(
                    className = classId,
                    totalMarks = totalMarks,
                    testName = testName,
                    students = rankedStudents
                )

                repository.createResult(resultData)

                val gson = GsonBuilder().setPrettyPrinting().create()
                val json = gson.toJson(resultData)
                Log.d("Submission", "Submitting form data:\n$json")

                _uiState.value = ResultEntryUiState.Submitted
            } catch (e: Exception) {
                Log.e("Submission", "Failed to submit results", e)
                _uiState.value = ResultEntryUiState.Error("Failed to submit results: ${e.message}")
            }
        }
    }
    fun resetState() {
        _uiState.value = ResultEntryUiState.Loading
        _studentsInput.value = emptyList()
    }
}