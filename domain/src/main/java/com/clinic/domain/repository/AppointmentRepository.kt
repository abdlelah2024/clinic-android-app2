package com.clinic.domain.repository

import com.clinic.domain.model.Appointment
import com.clinic.domain.model.EnrichedAppointment
import kotlinx.coroutines.flow.Flow

interface AppointmentRepository {
    
    fun getAllAppointments(): Flow<List<EnrichedAppointment>>
    
    suspend fun getAppointmentById(id: String): Appointment?
    
    suspend fun getAppointmentsByPatient(patientId: String): List<Appointment>
    
    suspend fun getAppointmentsByDoctor(doctorId: String): List<Appointment>
    
    suspend fun getAppointmentsByDate(date: String): List<Appointment>
    
    suspend fun addAppointment(appointment: Appointment): Result<String>
    
    suspend fun updateAppointment(appointment: Appointment): Result<Unit>
    
    suspend fun deleteAppointment(id: String): Result<Unit>
    
    suspend fun searchAppointments(query: String): List<EnrichedAppointment>
}

