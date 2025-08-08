package com.clinic.domain.repository

import com.clinic.domain.model.AuditLog
import com.clinic.domain.model.AuditAction
import com.clinic.domain.model.EntityType
import kotlinx.coroutines.flow.Flow

interface AuditLogRepository {
    
    fun getAllAuditLogs(): Flow<List<AuditLog>>
    
    fun getAuditLogsByUser(userId: String): Flow<List<AuditLog>>
    
    fun getAuditLogsByEntity(entityType: EntityType, entityId: String): Flow<List<AuditLog>>
    
    fun getAuditLogsByAction(action: AuditAction): Flow<List<AuditLog>>
    
    fun getAuditLogsByDateRange(startDate: Long, endDate: Long): Flow<List<AuditLog>>
    
    suspend fun addAuditLog(auditLog: AuditLog): Result<String>
    
    suspend fun getAuditLogById(id: String): AuditLog?
    
    suspend fun searchAuditLogs(
        query: String,
        userId: String? = null,
        entityType: EntityType? = null,
        action: AuditAction? = null,
        startDate: Long? = null,
        endDate: Long? = null
    ): List<AuditLog>
    
    suspend fun getAuditLogStatistics(
        startDate: Long,
        endDate: Long
    ): AuditLogStatistics
    
    suspend fun deleteOldAuditLogs(olderThanDays: Int): Result<Int>
    
    suspend fun exportAuditLogs(
        startDate: Long,
        endDate: Long,
        format: String = "csv"
    ): Result<String>
}

data class AuditLogStatistics(
    val totalLogs: Int = 0,
    val successfulActions: Int = 0,
    val failedActions: Int = 0,
    val uniqueUsers: Int = 0,
    val mostActiveUser: String = "",
    val mostCommonAction: AuditAction? = null,
    val mostAffectedEntity: EntityType? = null,
    val actionBreakdown: Map<AuditAction, Int> = emptyMap(),
    val entityBreakdown: Map<EntityType, Int> = emptyMap(),
    val userBreakdown: Map<String, Int> = emptyMap(),
    val dailyActivity: Map<String, Int> = emptyMap()
)

