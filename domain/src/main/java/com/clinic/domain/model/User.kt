package com.clinic.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val role: UserRole = UserRole.STAFF,
    val permissions: List<Permission> = emptyList(),
    val avatar: String = "",
    val isActive: Boolean = true,
    val lastSeen: Long = 0L,
    val isOnline: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) : Parcelable {
    
    fun getInitials(): String {
        return if (name.isNotEmpty()) {
            name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("")
        } else {
            email.firstOrNull()?.toString()?.uppercase() ?: "؟"
        }
    }
    
    fun hasPermission(permission: Permission): Boolean {
        return permissions.contains(permission) || role == UserRole.ADMIN
    }
}

@Parcelize
enum class UserRole : Parcelable {
    ADMIN,
    DOCTOR,
    STAFF
}

@Parcelize
enum class Permission : Parcelable {
    VIEW_PATIENTS,
    ADD_PATIENT,
    EDIT_PATIENT,
    DELETE_PATIENT,
    VIEW_APPOINTMENTS,
    ADD_APPOINTMENT,
    EDIT_APPOINTMENT,
    DELETE_APPOINTMENT,
    VIEW_DOCTORS,
    ADD_DOCTOR,
    EDIT_DOCTOR,
    DELETE_DOCTOR,
    VIEW_USERS,
    ADD_USER,
    EDIT_USER,
    DELETE_USER,
    VIEW_REPORTS,
    VIEW_AUDIT_LOG,
    SEND_MESSAGES
}

