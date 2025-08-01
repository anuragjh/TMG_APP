package com.example.material.api

import com.example.material.api.ApiService.AttendanceEntry
import org.json.JSONArray
import org.json.JSONObject

fun AttendanceEntry.toJsonObject(): JSONObject {
    return JSONObject().apply {
        put("attendanceId", attendanceId)
        put("className", className)
        put("teacherUsername", teacherUsername)
        put("topicCovered", topicCovered)
        put("status", status)
        put("date", JSONObject().apply {
            put("seconds", date.seconds)
            put("nanos", date.nanos)
        })
        put("startTime", startTime)
        put("endTime", endTime)
    }
}

fun AttendanceList.toJsonObject(): JSONObject {
    val json = JSONObject()
    val attendanceArray = JSONArray()

    this.attendance.forEach { entry ->
        attendanceArray.put(entry.toJsonObject())
    }

    json.put("attendance", attendanceArray)
    return json
}
