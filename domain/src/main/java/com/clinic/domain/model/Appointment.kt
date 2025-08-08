package com.clinic.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Appointment(
    val id: String = "",
    val patientId: String = "",
    val doctorId: String = "",
    val date: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val reason: String = "",
    val notes: String = "",
    val status: AppointmentStatus = AppointmentStatus.SCHEDULED,
    val cost: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) : Parcelable

@Parcelize
enum class AppointmentStatus : Parcelable {
    SCHEDULED,
    COMPLETED,
    CANCELED,
    WAITING,
    RETURN
}

@Parcelize
data class EnrichedAppointment(
    val appointment: Appointment,
    val patient: Patient,
    val doctor: Doctor
) : Parcelable {
    
    fun matchesSearch(query: String): Boolean {
        val searchQuery = query.lowercase().trim()
        return patient.name.lowercase().contains(searchQuery) ||
               doctor.name.lowercase().contains(searchQuery) ||
               appointment.reason.lowercase().contains(searchQuery)
    }
}

