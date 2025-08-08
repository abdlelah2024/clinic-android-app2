package com.clinic.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Doctor(
    val id: String = "",
    val name: String = "",
    val specialization: String = "",
    val phone: String = "",
    val email: String = "",
    val avatar: String = "",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) : Parcelable {
    
    fun getInitials(): String {
        return if (name.isNotEmpty()) {
            name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("")
        } else {
            "د"
        }
    }
}

