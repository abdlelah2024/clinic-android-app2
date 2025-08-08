package com.clinic.domain.repository

import com.clinic.domain.model.Report
import com.clinic.domain.model.ReportType
import com.clinic.domain.model.ReportFormat
import com.clinic.domain.model.DateRange
import kotlinx.coroutines.flow.Flow

interface ReportRepository {
    
    fun getAllReports(): Flow<List<Report>>
    
    fun getReportsByUser(userId: String): Flow<List<Report>>
    
    fun getReportsByType(type: ReportType): Flow<List<Report>>
    
    suspend fun getReportById(id: String): Report?
    
    suspend fun generateReport(
        type: ReportType,
        parameters: Map<String, Any>,
        dateRange: DateRange,
        format: ReportFormat,
        generatedBy: String
    ): Result<String>
    
    suspend fun updateReportStatus(reportId: String, status: com.clinic.domain.model.ReportStatus): Result<Unit>
    
    suspend fun deleteReport(id: String): Result<Unit>
    
    suspend fun downloadReport(reportId: String): Result<ByteArray>
    
    suspend fun scheduleReport(
        type: ReportType,
        parameters: Map<String, Any>,
        schedule: ReportSchedule,
        recipients: List<String>,
        generatedBy: String
    ): Result<String>
    
    suspend fun getScheduledReports(): List<ScheduledReport>
    
    suspend fun cancelScheduledReport(scheduleId: String): Result<Unit>
    
    // تقارير محددة
    suspend fun generatePatientsReport(dateRange: DateRange, parameters: Map<String, Any>): ReportData
    
    suspend fun generateAppointmentsReport(dateRange: DateRange, parameters: Map<String, Any>): ReportData
    
    suspend fun generateRevenueReport(dateRange: DateRange, parameters: Map<String, Any>): ReportData
    
    suspend fun generateDoctorsPerformanceReport(dateRange: DateRange, parameters: Map<String, Any>): ReportData
    
    suspend fun generateAuditReport(dateRange: DateRange, parameters: Map<String, Any>): ReportData
    
    suspend fun generateCustomReport(
        query: String,
        parameters: Map<String, Any>,
        dateRange: DateRange
    ): ReportData
}

data class ReportData(
    val summary: Map<String, Any> = emptyMap(),
    val data: List<Map<String, Any>> = emptyList(),
    val charts: List<ChartData> = emptyList(),
    val metadata: Map<String, Any> = emptyMap()
)

data class ChartData(
    val title: String,
    val type: String,
    val data: List<Map<String, Any>>
)

data class ReportSchedule(
    val frequency: ScheduleFrequency,
    val time: String, // HH:mm format
    val dayOfWeek: Int? = null, // 1-7 for weekly
    val dayOfMonth: Int? = null, // 1-31 for monthly
    val isActive: Boolean = true
)

enum class ScheduleFrequency(val displayName: String) {
    DAILY("يومي"),
    WEEKLY("أسبوعي"),
    MONTHLY("شهري"),
    QUARTERLY("ربع سنوي"),
    YEARLY("سنوي")
}

data class ScheduledReport(
    val id: String = "",
    val reportType: ReportType,
    val parameters: Map<String, Any> = emptyMap(),
    val schedule: ReportSchedule,
    val recipients: List<String> = emptyList(),
    val format: ReportFormat = ReportFormat.PDF,
    val createdBy: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val lastGenerated: Long? = null,
    val nextGeneration: Long = 0,
    val isActive: Boolean = true
)

