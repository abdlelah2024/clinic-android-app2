package com.clinic.domain.usecase

import com.clinic.domain.model.Appointment
import com.clinic.domain.model.Patient
import com.clinic.domain.repository.AppointmentRepository
import com.clinic.domain.repository.PatientRepository
import javax.inject.Inject

class QuickAddAppointmentUseCase @Inject constructor(
    private val patientRepository: PatientRepository,
    private val appointmentRepository: AppointmentRepository
) {
    
    suspend operator fun invoke(
        searchQuery: String,
        doctorId: String,
        date: String,
        startTime: String,
        reason: String
    ): Result<String> {
        
        // البحث عن المريض أولاً
        val existingPatients = patientRepository.searchPatients(searchQuery)
        
        val patientId = if (existingPatients.isNotEmpty()) {
            // إذا وُجد المريض، استخدم أول نتيجة
            existingPatients.first().id
        } else {
            // إذا لم يوجد المريض، أنشئ مريضاً جديداً
            val newPatient = createPatientFromSearch(searchQuery)
            val addPatientResult = patientRepository.addPatient(newPatient)
            
            if (addPatientResult.isFailure) {
                return Result.failure(addPatientResult.exceptionOrNull() ?: Exception("فشل في إضافة المريض"))
            }
            
            addPatientResult.getOrThrow()
        }
        
        // إنشاء الموعد
        val appointment = Appointment(
            patientId = patientId,
            doctorId = doctorId,
            date = date,
            startTime = startTime,
            reason = reason
        )
        
        return appointmentRepository.addAppointment(appointment)
    }
    
    private fun createPatientFromSearch(searchQuery: String): Patient {
        // محاولة تحديد ما إذا كان البحث يحتوي على رقم هاتف أم اسم
        val isPhoneNumber = searchQuery.matches(Regex("^[0-9+\\-\\s()]+$"))
        
        return if (isPhoneNumber) {
            Patient(
                name = "مريض جديد",
                phone = searchQuery.trim()
            )
        } else {
            Patient(
                name = searchQuery.trim(),
                phone = ""
            )
        }
    }
}

