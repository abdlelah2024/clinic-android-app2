package com.clinic.domain.repository

import com.clinic.domain.model.Patient
import kotlinx.coroutines.flow.Flow

interface PatientRepository {
    
    fun getAllPatients(): Flow<List<Patient>>
    
    suspend fun getPatientById(id: String): Patient?
    
    suspend fun searchPatients(query: String): List<Patient>
    
    suspend fun addPatient(patient: Patient): Result<String>
    
    suspend fun updatePatient(patient: Patient): Result<Unit>
    
    suspend fun deletePatient(id: String): Result<Unit>
    
    suspend fun getPatientsByPhone(phone: String): List<Patient>
}

