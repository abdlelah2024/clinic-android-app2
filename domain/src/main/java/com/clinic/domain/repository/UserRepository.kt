package com.clinic.domain.repository

import com.clinic.domain.model.User
import com.clinic.domain.model.UserRole
import com.clinic.domain.model.Permission
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    
    fun getAllUsers(): Flow<List<User>>
    
    fun getActiveUsers(): Flow<List<User>>
    
    suspend fun getUserById(id: String): User?
    
    suspend fun getUserByEmail(email: String): User?
    
    suspend fun getUsersByRole(role: UserRole): List<User>
    
    suspend fun addUser(user: User, password: String): Result<String>
    
    suspend fun updateUser(user: User): Result<Unit>
    
    suspend fun deleteUser(id: String): Result<Unit>
    
    suspend fun activateUser(id: String): Result<Unit>
    
    suspend fun deactivateUser(id: String): Result<Unit>
    
    suspend fun changeUserRole(userId: String, newRole: UserRole): Result<Unit>
    
    suspend fun updateUserPermissions(userId: String, permissions: List<Permission>): Result<Unit>
    
    suspend fun resetUserPassword(userId: String, newPassword: String): Result<Unit>
    
    suspend fun searchUsers(query: String): List<User>
    
    suspend fun getCurrentUser(): User?
    
    suspend fun hasPermission(userId: String, permission: Permission): Boolean
    
    suspend fun getUserPermissions(userId: String): List<Permission>
    
    suspend fun updateLastLogin(userId: String): Result<Unit>
    
    suspend fun updateUserProfile(userId: String, profileData: Map<String, Any>): Result<Unit>
}

