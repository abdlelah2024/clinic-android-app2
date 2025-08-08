package com.clinic.domain.usecase

import com.clinic.domain.model.Patient
import com.clinic.domain.repository.PatientRepository
import javax.inject.Inject

class SearchPatientsUseCase @Inject constructor(
    private val patientRepository: PatientRepository
) {
    
    suspend operator fun invoke(query: String): List<Patient> {
        if (query.isBlank()) {
            return emptyList()
        }
        
        return patientRepository.searchPatients(query.trim())
    }
}

