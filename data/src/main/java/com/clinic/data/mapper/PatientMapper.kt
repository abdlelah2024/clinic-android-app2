package com.clinic.data.mapper

import com.clinic.domain.model.Patient
import com.google.firebase.firestore.DocumentSnapshot

fun DocumentSnapshot.toPatient(): Patient? {
    return try {
        Patient(
            id = id,
            name = getString("name") ?: "",
            phone = getString("phone") ?: "",
            age = getLong("age")?.toInt() ?: 0,
            gender = getString("gender") ?: "",
            address = getString("address") ?: "",
            medicalHistory = getString("medicalHistory") ?: "",
            allergies = getString("allergies") ?: "",
            emergencyContact = getString("emergencyContact") ?: "",
            avatar = getString("avatar") ?: "",
            lastVisit = getString("lastVisit") ?: "",
            createdAt = getLong("createdAt") ?: System.currentTimeMillis(),
            updatedAt = getLong("updatedAt") ?: System.currentTimeMillis()
        )
    } catch (e: Exception) {
        null
    }
}

fun Patient.toPatientMap(): Map<String, Any> {
    return mapOf(
        "name" to name,
        "phone" to phone,
        "age" to age,
        "gender" to gender,
        "address" to address,
        "medicalHistory" to medicalHistory,
        "allergies" to allergies,
        "emergencyContact" to emergencyContact,
        "avatar" to avatar,
        "lastVisit" to lastVisit,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt
    )
}

