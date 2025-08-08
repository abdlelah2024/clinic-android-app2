package com.clinic.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Patient(
    val id: String = "",
    val name: String = "",
    val phone: String = "",
    val age: Int = 0,
    val gender: String = "",
    val address: String = "",
    val medicalHistory: String = "",
    val allergies: String = "",
    val emergencyContact: String = "",
    val avatar: String = "",
    val lastVisit: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) : Parcelable {
    
    fun getInitials(): String {
        return if (name.isNotEmpty()) {
            name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("")
        } else {
            "؟"
        }
    }
    
    fun matchesSearch(query: String): Boolean {
        val searchQuery = query.lowercase().trim()
        return name.lowercase().contains(searchQuery) || 
               phone.contains(searchQuery)
    }
}

