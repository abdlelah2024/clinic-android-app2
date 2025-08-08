package com.clinic.data.mapper

import com.clinic.domain.model.Appointment
import com.clinic.domain.model.AppointmentStatus
import com.google.firebase.firestore.DocumentSnapshot

fun DocumentSnapshot.toAppointment(): Appointment? {
    return try {
        Appointment(
            id = id,
            patientId = getString("patientId") ?: "",
            doctorId = getString("doctorId") ?: "",
            date = getString("date") ?: "",
            startTime = getString("startTime") ?: "",
            endTime = getString("endTime") ?: "",
            reason = getString("reason") ?: "",
            notes = getString("notes") ?: "",
            status = AppointmentStatus.valueOf(getString("status") ?: "SCHEDULED"),
            cost = getDouble("cost") ?: 0.0,
            createdAt = getLong("createdAt") ?: System.currentTimeMillis(),
            updatedAt = getLong("updatedAt") ?: System.currentTimeMillis()
        )
    } catch (e: Exception) {
        null
    }
}

fun Appointment.toAppointmentMap(): Map<String, Any> {
    return mapOf(
        "patientId" to patientId,
        "doctorId" to doctorId,
        "date" to date,
        "startTime" to startTime,
        "endTime" to endTime,
        "reason" to reason,
        "notes" to notes,
        "status" to status.name,
        "cost" to cost,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt
    )
}

