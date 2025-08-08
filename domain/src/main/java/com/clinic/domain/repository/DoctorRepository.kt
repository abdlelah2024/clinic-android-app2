package com.clinic.domain.repository

import com.clinic.domain.model.Doctor
import kotlinx.coroutines.flow.Flow

interface DoctorRepository {
    
    fun getAllDoctors(): Flow<List<Doctor>>
    
    fun getActiveDoctors(): Flow<List<Doctor>>
    
    suspend fun getDoctorById(id: String): Doctor?
    
    suspend fun getDoctorsBySpecialization(specialization: String): List<Doctor>
    
    suspend fun addDoctor(doctor: Doctor): Result<String>
    
    suspend fun updateDoctor(doctor: Doctor): Result<Unit>
    
    suspend fun deleteDoctor(id: String): Result<Unit>
    
    suspend fun activateDoctor(id: String): Result<Unit>
    
    suspend fun deactivateDoctor(id: String): Result<Unit>
    
    suspend fun searchDoctors(query: String): List<Doctor>
    
    suspend fun getDoctorStatistics(doctorId: String, startDate: Long, endDate: Long): DoctorStatistics
}

data class DoctorStatistics(
    val totalAppointments: Int = 0,
    val completedAppointments: Int = 0,
    val canceledAppointments: Int = 0,
    val totalPatients: Int = 0,
    val newPatients: Int = 0,
    val totalRevenue: Double = 0.0,
    val averageAppointmentDuration: Int = 0, // in minutes
    val patientSatisfactionRating: Double = 0.0,
    val workingDays: Int = 0,
    val workingHours: Double = 0.0
)

