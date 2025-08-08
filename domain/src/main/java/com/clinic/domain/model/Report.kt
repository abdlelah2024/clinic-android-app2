package com.clinic.domain.model

import java.util.*

data class Report(
    val id: String = "",
    val title: String = "",
    val type: ReportType = ReportType.PATIENTS_SUMMARY,
    val description: String = "",
    val parameters: Map<String, Any> = emptyMap(),
    val data: ReportData = ReportData(),
    val generatedBy: String = "",
    val generatedByName: String = "",
    val generatedAt: Long = System.currentTimeMillis(),
    val dateRange: DateRange = DateRange(),
    val status: ReportStatus = ReportStatus.PENDING,
    val filePath: String = "",
    val fileSize: Long = 0,
    val format: ReportFormat = ReportFormat.PDF
)

enum class ReportType(val displayName: String, val description: String) {
    // تقارير المرضى
    PATIENTS_SUMMARY("ملخص المرضى", "تقرير شامل عن المرضى المسجلين"),
    NEW_PATIENTS("المرضى الجدد", "تقرير المرضى المسجلين حديثاً"),
    PATIENT_VISITS("زيارات المرضى", "تقرير زيارات المرضى"),
    
    // تقارير المواعيد
    APPOINTMENTS_SUMMARY("ملخص المواعيد", "تقرير شامل عن المواعيد"),
    DAILY_APPOINTMENTS("المواعيد اليومية", "تقرير المواعيد لليوم الحالي"),
    WEEKLY_APPOINTMENTS("المواعيد الأسبوعية", "تقرير المواعيد للأسبوع"),
    MONTHLY_APPOINTMENTS("المواعيد الشهرية", "تقرير المواعيد للشهر"),
    CANCELED_APPOINTMENTS("المواعيد الملغية", "تقرير المواعيد الملغية"),
    
    // تقارير الأطباء
    DOCTORS_PERFORMANCE("أداء الأطباء", "تقرير أداء الأطباء"),
    DOCTOR_SCHEDULE("جدول الأطباء", "تقرير جداول الأطباء"),
    
    // التقارير المالية
    REVENUE_REPORT("تقرير الإيرادات", "تقرير الإيرادات والمدفوعات"),
    PAYMENTS_SUMMARY("ملخص المدفوعات", "تقرير ملخص المدفوعات"),
    OUTSTANDING_PAYMENTS("المدفوعات المعلقة", "تقرير المدفوعات غير المسددة"),
    
    // تقارير النشاط
    USER_ACTIVITY("نشاط المستخدمين", "تقرير نشاط المستخدمين"),
    SYSTEM_USAGE("استخدام النظام", "تقرير استخدام النظام"),
    AUDIT_REPORT("تقرير التدقيق", "تقرير سجلات التدقيق"),
    
    // تقارير مخصصة
    CUSTOM_REPORT("تقرير مخصص", "تقرير مخصص حسب المعايير المحددة")
}

enum class ReportStatus(val displayName: String) {
    PENDING("قيد الإنشاء"),
    GENERATING("جاري الإنشاء"),
    COMPLETED("مكتمل"),
    FAILED("فشل"),
    EXPIRED("منتهي الصلاحية")
}

enum class ReportFormat(val displayName: String, val extension: String, val mimeType: String) {
    PDF("PDF", "pdf", "application/pdf"),
    EXCEL("Excel", "xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
    CSV("CSV", "csv", "text/csv"),
    JSON("JSON", "json", "application/json")
}

data class ReportData(
    val summary: Map<String, Any> = emptyMap(),
    val charts: List<ChartData> = emptyList(),
    val tables: List<TableData> = emptyList(),
    val metadata: Map<String, Any> = emptyMap()
)

data class ChartData(
    val title: String = "",
    val type: ChartType = ChartType.BAR,
    val data: List<ChartPoint> = emptyList(),
    val xAxisLabel: String = "",
    val yAxisLabel: String = ""
)

data class ChartPoint(
    val label: String = "",
    val value: Double = 0.0,
    val color: String = ""
)

enum class ChartType(val displayName: String) {
    BAR("مخطط أعمدة"),
    LINE("مخطط خطي"),
    PIE("مخطط دائري"),
    AREA("مخطط منطقة")
}

data class TableData(
    val title: String = "",
    val headers: List<String> = emptyList(),
    val rows: List<List<String>> = emptyList(),
    val totalRows: Int = 0
)

data class DateRange(
    val startDate: Long = 0,
    val endDate: Long = System.currentTimeMillis(),
    val preset: DateRangePreset = DateRangePreset.CUSTOM
)

enum class DateRangePreset(val displayName: String, val days: Int) {
    TODAY("اليوم", 1),
    YESTERDAY("أمس", 1),
    LAST_7_DAYS("آخر 7 أيام", 7),
    LAST_30_DAYS("آخر 30 يوم", 30),
    THIS_MONTH("هذا الشهر", 30),
    LAST_MONTH("الشهر الماضي", 30),
    THIS_YEAR("هذا العام", 365),
    LAST_YEAR("العام الماضي", 365),
    CUSTOM("مخصص", 0)
}

// فئة مساعدة لبناء التقارير
object ReportBuilder {
    fun createPatientsReport(
        dateRange: DateRange,
        generatedBy: String,
        generatedByName: String
    ): Report {
        return Report(
            title = "تقرير المرضى",
            type = ReportType.PATIENTS_SUMMARY,
            description = "تقرير شامل عن المرضى المسجلين في النظام",
            dateRange = dateRange,
            generatedBy = generatedBy,
            generatedByName = generatedByName,
            parameters = mapOf(
                "includeInactive" to false,
                "groupBy" to "month"
            )
        )
    }
    
    fun createAppointmentsReport(
        dateRange: DateRange,
        doctorId: String? = null,
        generatedBy: String,
        generatedByName: String
    ): Report {
        return Report(
            title = "تقرير المواعيد",
            type = ReportType.APPOINTMENTS_SUMMARY,
            description = "تقرير شامل عن المواعيد",
            dateRange = dateRange,
            generatedBy = generatedBy,
            generatedByName = generatedByName,
            parameters = buildMap {
                put("includeAllStatuses", true)
                doctorId?.let { put("doctorId", it) }
            }
        )
    }
    
    fun createRevenueReport(
        dateRange: DateRange,
        generatedBy: String,
        generatedByName: String
    ): Report {
        return Report(
            title = "تقرير الإيرادات",
            type = ReportType.REVENUE_REPORT,
            description = "تقرير الإيرادات والمدفوعات",
            dateRange = dateRange,
            generatedBy = generatedBy,
            generatedByName = generatedByName,
            parameters = mapOf(
                "includePending" to true,
                "currency" to "SAR"
            )
        )
    }
}

