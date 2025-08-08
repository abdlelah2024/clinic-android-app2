package com.clinic.domain.model

data class AuditLog(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val userRole: String = "",
    val action: AuditAction = AuditAction.VIEW,
    val entityType: EntityType = EntityType.PATIENT,
    val entityId: String = "",
    val entityName: String = "",
    val oldValues: Map<String, Any> = emptyMap(),
    val newValues: Map<String, Any> = emptyMap(),
    val description: String = "",
    val ipAddress: String = "",
    val deviceInfo: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val success: Boolean = true,
    val errorMessage: String = ""
)

enum class AuditAction(val displayName: String, val description: String) {
    // عمليات القراءة
    VIEW("عرض", "عرض البيانات"),
    SEARCH("بحث", "البحث في البيانات"),
    EXPORT("تصدير", "تصدير البيانات"),
    
    // عمليات الكتابة
    CREATE("إنشاء", "إنشاء سجل جديد"),
    UPDATE("تحديث", "تحديث سجل موجود"),
    DELETE("حذف", "حذف سجل"),
    
    // عمليات المصادقة
    LOGIN("تسجيل دخول", "تسجيل دخول المستخدم"),
    LOGOUT("تسجيل خروج", "تسجيل خروج المستخدم"),
    LOGIN_FAILED("فشل تسجيل الدخول", "محاولة تسجيل دخول فاشلة"),
    PASSWORD_CHANGE("تغيير كلمة المرور", "تغيير كلمة مرور المستخدم"),
    
    // عمليات الصلاحيات
    PERMISSION_GRANTED("منح صلاحية", "منح صلاحية للمستخدم"),
    PERMISSION_REVOKED("سحب صلاحية", "سحب صلاحية من المستخدم"),
    ROLE_CHANGED("تغيير الدور", "تغيير دور المستخدم"),
    
    // عمليات النظام
    SYSTEM_BACKUP("نسخ احتياطي", "إنشاء نسخة احتياطية"),
    SYSTEM_RESTORE("استعادة", "استعادة من نسخة احتياطية"),
    SYSTEM_MAINTENANCE("صيانة النظام", "عمليات صيانة النظام"),
    
    // عمليات خاصة
    BULK_OPERATION("عملية مجمعة", "عملية على عدة سجلات"),
    DATA_IMPORT("استيراد بيانات", "استيراد بيانات خارجية"),
    REPORT_GENERATED("إنشاء تقرير", "إنشاء تقرير"),
    
    // عمليات الدردشة
    MESSAGE_SENT("إرسال رسالة", "إرسال رسالة في الدردشة"),
    MESSAGE_DELETED("حذف رسالة", "حذف رسالة من الدردشة")
}

enum class EntityType(val displayName: String) {
    PATIENT("مريض"),
    APPOINTMENT("موعد"),
    DOCTOR("طبيب"),
    USER("مستخدم"),
    MEDICAL_RECORD("سجل طبي"),
    CHAT_MESSAGE("رسالة دردشة"),
    CHAT_ROOM("غرفة دردشة"),
    REPORT("تقرير"),
    PAYMENT("دفعة"),
    SYSTEM("النظام")
}

// فئة مساعدة لإنشاء سجلات التدقيق
object AuditLogBuilder {
    fun create(
        userId: String,
        userName: String,
        userRole: String,
        action: AuditAction,
        entityType: EntityType,
        entityId: String,
        entityName: String = "",
        description: String = "",
        oldValues: Map<String, Any> = emptyMap(),
        newValues: Map<String, Any> = emptyMap(),
        success: Boolean = true,
        errorMessage: String = ""
    ): AuditLog {
        return AuditLog(
            userId = userId,
            userName = userName,
            userRole = userRole,
            action = action,
            entityType = entityType,
            entityId = entityId,
            entityName = entityName,
            description = description.ifEmpty { 
                "${action.displayName} ${entityType.displayName}: $entityName"
            },
            oldValues = oldValues,
            newValues = newValues,
            timestamp = System.currentTimeMillis(),
            success = success,
            errorMessage = errorMessage
        )
    }
    
    fun createLoginLog(
        userId: String,
        userName: String,
        userRole: String,
        success: Boolean,
        ipAddress: String = "",
        deviceInfo: String = "",
        errorMessage: String = ""
    ): AuditLog {
        return AuditLog(
            userId = userId,
            userName = userName,
            userRole = userRole,
            action = if (success) AuditAction.LOGIN else AuditAction.LOGIN_FAILED,
            entityType = EntityType.USER,
            entityId = userId,
            entityName = userName,
            description = if (success) "تسجيل دخول ناجح" else "فشل في تسجيل الدخول",
            ipAddress = ipAddress,
            deviceInfo = deviceInfo,
            timestamp = System.currentTimeMillis(),
            success = success,
            errorMessage = errorMessage
        )
    }
}

