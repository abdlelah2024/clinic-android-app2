package com.clinic.domain.model

enum class UserRole(val displayName: String, val permissions: List<Permission>) {
    ADMIN(
        displayName = "مدير النظام",
        permissions = Permission.values().toList()
    ),
    DOCTOR(
        displayName = "طبيب",
        permissions = listOf(
            Permission.VIEW_PATIENTS,
            Permission.ADD_PATIENT,
            Permission.EDIT_PATIENT,
            Permission.VIEW_APPOINTMENTS,
            Permission.ADD_APPOINTMENT,
            Permission.EDIT_APPOINTMENT,
            Permission.DELETE_APPOINTMENT,
            Permission.VIEW_MEDICAL_RECORDS,
            Permission.ADD_MEDICAL_RECORD,
            Permission.EDIT_MEDICAL_RECORD,
            Permission.CHAT,
            Permission.VIEW_REPORTS
        )
    ),
    NURSE(
        displayName = "ممرض/ة",
        permissions = listOf(
            Permission.VIEW_PATIENTS,
            Permission.ADD_PATIENT,
            Permission.EDIT_PATIENT,
            Permission.VIEW_APPOINTMENTS,
            Permission.ADD_APPOINTMENT,
            Permission.EDIT_APPOINTMENT,
            Permission.VIEW_MEDICAL_RECORDS,
            Permission.CHAT
        )
    ),
    RECEPTIONIST(
        displayName = "موظف استقبال",
        permissions = listOf(
            Permission.VIEW_PATIENTS,
            Permission.ADD_PATIENT,
            Permission.EDIT_PATIENT,
            Permission.VIEW_APPOINTMENTS,
            Permission.ADD_APPOINTMENT,
            Permission.EDIT_APPOINTMENT,
            Permission.DELETE_APPOINTMENT,
            Permission.CHAT
        )
    ),
    ACCOUNTANT(
        displayName = "محاسب",
        permissions = listOf(
            Permission.VIEW_PATIENTS,
            Permission.VIEW_APPOINTMENTS,
            Permission.VIEW_FINANCIAL_REPORTS,
            Permission.MANAGE_PAYMENTS,
            Permission.VIEW_REPORTS
        )
    )
}

enum class Permission(val displayName: String, val description: String) {
    // إدارة المرضى
    VIEW_PATIENTS("عرض المرضى", "عرض قائمة المرضى وتفاصيلهم"),
    ADD_PATIENT("إضافة مريض", "إضافة مرضى جدد"),
    EDIT_PATIENT("تعديل المريض", "تعديل بيانات المرضى"),
    DELETE_PATIENT("حذف المريض", "حذف سجلات المرضى"),
    
    // إدارة المواعيد
    VIEW_APPOINTMENTS("عرض المواعيد", "عرض قائمة المواعيد"),
    ADD_APPOINTMENT("إضافة موعد", "إضافة مواعيد جديدة"),
    EDIT_APPOINTMENT("تعديل الموعد", "تعديل المواعيد الموجودة"),
    DELETE_APPOINTMENT("حذف الموعد", "حذف المواعيد"),
    
    // إدارة السجلات الطبية
    VIEW_MEDICAL_RECORDS("عرض السجلات الطبية", "عرض السجلات الطبية للمرضى"),
    ADD_MEDICAL_RECORD("إضافة سجل طبي", "إضافة سجلات طبية جديدة"),
    EDIT_MEDICAL_RECORD("تعديل السجل الطبي", "تعديل السجلات الطبية"),
    DELETE_MEDICAL_RECORD("حذف السجل الطبي", "حذف السجلات الطبية"),
    
    // إدارة الأطباء
    VIEW_DOCTORS("عرض الأطباء", "عرض قائمة الأطباء"),
    ADD_DOCTOR("إضافة طبيب", "إضافة أطباء جدد"),
    EDIT_DOCTOR("تعديل الطبيب", "تعديل بيانات الأطباء"),
    DELETE_DOCTOR("حذف الطبيب", "حذف سجلات الأطباء"),
    
    // إدارة المستخدمين
    VIEW_USERS("عرض المستخدمين", "عرض قائمة المستخدمين"),
    ADD_USER("إضافة مستخدم", "إضافة مستخدمين جدد"),
    EDIT_USER("تعديل المستخدم", "تعديل بيانات المستخدمين"),
    DELETE_USER("حذف المستخدم", "حذف حسابات المستخدمين"),
    MANAGE_PERMISSIONS("إدارة الصلاحيات", "تعديل صلاحيات المستخدمين"),
    
    // التقارير
    VIEW_REPORTS("عرض التقارير", "عرض التقارير العامة"),
    VIEW_FINANCIAL_REPORTS("عرض التقارير المالية", "عرض التقارير المالية"),
    EXPORT_REPORTS("تصدير التقارير", "تصدير التقارير بصيغ مختلفة"),
    
    // المالية
    MANAGE_PAYMENTS("إدارة المدفوعات", "إدارة مدفوعات المرضى"),
    VIEW_FINANCIAL_DATA("عرض البيانات المالية", "عرض البيانات المالية"),
    
    // الدردشة
    CHAT("الدردشة", "استخدام نظام الدردشة الداخلي"),
    
    // سجلات التدقيق
    VIEW_AUDIT_LOG("عرض سجل التدقيق", "عرض سجلات التعديلات والأنشطة"),
    
    // إعدادات النظام
    SYSTEM_SETTINGS("إعدادات النظام", "تعديل إعدادات النظام العامة")
}

