# تطبيق العيادة - Android

تطبيق أندرويد أصلي لإدارة العيادات الطبية مع دعم المزامنة الفورية والدردشة.

## الميزات الرئيسية

### 🔍 البحث السريع
- البحث الفوري عن المرضى بالاسم أو رقم الهاتف
- إضافة موعد سريع للمرضى الموجودين
- إضافة مريض جديد مع موعد تلقائي في حالة عدم وجوده

### 👥 إدارة المرضى
- عرض قائمة شاملة بجميع المرضى
- إضافة وتعديل وحذف سجلات المرضى
- عرض تفاصيل المريض وتاريخه الطبي
- البحث والفلترة المتقدمة

### 📅 إدارة المواعيد
- جدولة وإدارة المواعيد
- تغيير حالة المواعيد (مجدول، مكتمل، ملغي، منتظر، عودة)
- عرض المواعيد حسب التاريخ والطبيب
- إشعارات المواعيد

### 💬 الدردشة الفورية
- دردشة فورية بين المستخدمين
- إشعارات الرسائل الجديدة
- حالة قراءة الرسائل
- دعم الرسائل النصية والملفات

### 🔄 المزامنة الفورية
- تحديث البيانات في الوقت الفعلي
- مزامنة تلقائية مع Firebase Firestore
- دعم العمل دون اتصال (قيد التطوير)

## البنية التقنية

### المعمارية
- **MVVM (Model-View-ViewModel)**: فصل الاهتمامات وسهولة الاختبار
- **Clean Architecture**: طبقات منفصلة (Domain, Data, Presentation)
- **Modular Design**: وحدات منطقية قابلة للتوسع

### التقنيات المستخدمة
- **Kotlin**: لغة البرمجة الأساسية
- **Jetpack Compose**: واجهة المستخدم الحديثة
- **Firebase Firestore**: قاعدة البيانات السحابية
- **Firebase Auth**: نظام المصادقة
- **Firebase Cloud Messaging**: الإشعارات الفورية
- **Hilt**: حقن التبعيات
- **Coroutines & Flow**: البرمجة غير المتزامنة
- **Navigation Component**: التنقل بين الشاشات

### الوحدات (Modules)
```
clinic-android-app/
├── app/                    # الوحدة الرئيسية (UI)
├── data/                   # طبقة البيانات
├── domain/                 # منطق الأعمال
└── common/                 # المرافق المشتركة
```

## إعداد المشروع

### المتطلبات
- Android Studio Arctic Fox أو أحدث
- JDK 11 أو أحدث
- Android SDK API 24+ (Android 7.0)

### خطوات التشغيل
1. استنساخ المشروع
2. فتح المشروع في Android Studio
3. إعداد Firebase:
   - إضافة ملف `google-services.json` إلى مجلد `app/`
   - تكوين Firebase Authentication و Firestore
4. بناء وتشغيل التطبيق

### إعداد Firebase
1. إنشاء مشروع جديد في Firebase Console
2. تفعيل Authentication (Email/Password)
3. إنشاء قاعدة بيانات Firestore
4. تكوين Firebase Cloud Messaging
5. تحديث قواعد الأمان في Firestore

## قواعد الأمان في Firestore

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // قواعد المرضى
    match /patients/{patientId} {
      allow read, write: if request.auth != null;
    }
    
    // قواعد المواعيد
    match /appointments/{appointmentId} {
      allow read, write: if request.auth != null;
    }
    
    // قواعد الدردشة
    match /chatRooms/{chatRoomId} {
      allow read, write: if request.auth != null 
        && request.auth.uid in resource.data.participants;
    }
    
    match /messages/{messageId} {
      allow read, write: if request.auth != null;
    }
  }
}
```

## الاختبار

### اختبارات الوحدة
```bash
./gradlew test
```

### اختبارات التكامل
```bash
./gradlew connectedAndroidTest
```

## النشر

### بناء APK للإنتاج
```bash
./gradlew assembleRelease
```

### بناء App Bundle
```bash
./gradlew bundleRelease
```

## المساهمة

1. Fork المشروع
2. إنشاء فرع للميزة الجديدة (`git checkout -b feature/AmazingFeature`)
3. Commit التغييرات (`git commit -m 'Add some AmazingFeature'`)
4. Push إلى الفرع (`git push origin feature/AmazingFeature`)
5. فتح Pull Request

## الترخيص

هذا المشروع مرخص تحت رخصة MIT - راجع ملف [LICENSE](LICENSE) للتفاصيل.

## الدعم

للحصول على الدعم، يرجى فتح issue في GitHub أو التواصل مع فريق التطوير.

---

تم تطوير هذا التطبيق باستخدام أحدث تقنيات Android وأفضل الممارسات في تطوير التطبيقات الطبية.

