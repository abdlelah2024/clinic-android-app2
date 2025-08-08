package com.clinic.data.mapper

import com.clinic.domain.model.Doctor
import com.google.firebase.firestore.DocumentSnapshot

fun DocumentSnapshot.toDoctor(): Doctor? {
    return try {
        Doctor(
            id = id,
            name = getString("name") ?: "",
            specialization = getString("specialization") ?: "",
            phone = getString("phone") ?: "",
            email = getString("email") ?: "",
            avatar = getString("avatar") ?: "",
            isActive = getBoolean("isActive") ?: true,
            createdAt = getLong("createdAt") ?: System.currentTimeMillis(),
            updatedAt = getLong("updatedAt") ?: System.currentTimeMillis()
        )
    } catch (e: Exception) {
        null
    }
}

fun Doctor.toDoctorMap(): Map<String, Any> {
    return mapOf(
        "name" to name,
        "specialization" to specialization,
        "phone" to phone,
        "email" to email,
        "avatar" to avatar,
        "isActive" to isActive,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt
    )
}

