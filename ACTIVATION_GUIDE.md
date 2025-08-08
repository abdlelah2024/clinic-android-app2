# دليل تفعيل الميزات والصلاحيات في تطبيق العيادة

هذا الدليل يوضح الخطوات اللازمة لتفعيل جميع الميزات والصلاحيات التي تم تطويرها في تطبيق العيادة.

## 1. إعداد Firebase (أساسي)

تطبيق العيادة يعتمد بشكل كبير على Firebase لخدمات قاعدة البيانات (Firestore)، المصادقة (Authentication)، والإشعارات (FCM).

### 1.1. ملف `google-services.json`

يجب أن يكون لديك ملف `google-services.json` صحيح من مشروع Firebase الخاص بك. هذا الملف يحتوي على جميع معلومات الاتصال بمشروع Firebase.

**الخطوات:**
1.  اذهب إلى [Firebase Console](https://console.firebase.google.com/).
2.  اختر مشروعك (أو أنشئ مشروعًا جديدًا).
3.  أضف تطبيق Android إلى مشروعك (إذا لم تكن قد فعلت ذلك).
4.  اتبع التعليمات لتسجيل تطبيقك، وعندما يُطلب منك، قم بتنزيل ملف `google-services.json`.
5.  ضع هذا الملف في مجلد `app/` داخل مشروع Android Studio الخاص بك:
    ```
    clinic-android-app/
    └── app/
        └── google-services.json  <-- هنا يجب وضع الملف
    ```

### 1.2. تفعيل خدمات Firebase

تأكد من تفعيل الخدمات التالية في مشروع Firebase الخاص بك:
-   **Firestore Database**: لقاعدة البيانات في الوقت الفعلي.
-   **Authentication**: لإدارة المستخدمين وتسجيل الدخول (خاصة "البريد الإلكتروني/كلمة المرور").
-   **Cloud Messaging (FCM)**: لإرسال الإشعارات الفورية (مطلوب للدردشة وتذكيرات المواعيد).

### 1.3. قواعد أمان Firestore

لضمان عمل الصلاحيات بشكل صحيح وحماية بياناتك، يجب تطبيق قواعد الأمان التالية في Firestore. هذه القواعد تضمن أن المستخدمين المصادق عليهم فقط يمكنهم قراءة وكتابة البيانات، وأن الأدوار المختلفة لديها وصول مناسب.

**الخطوات:**
1.  في Firebase Console، اذهب إلى `Firestore Database` -> `Rules`.
2.  استبدل القواعد الموجودة بالقواعد التالية:

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
        
        // قواعد الأطباء
        match /doctors/{doctorId} {
          allow read: if request.auth != null;
          allow write: if request.auth != null && getUserRole(request.auth.uid) == 'ADMIN';
        }
        
        // قواعد المستخدمين (لإدارة المستخدمين والصلاحيات)
        match /users/{userId} {
          allow read: if request.auth != null;
          allow write: if request.auth != null && (request.auth.uid == userId || getUserRole(request.auth.uid) == 'ADMIN');
        }
        
        // قواعد سجلات التدقيق
        match /auditLogs/{auditLogId} {
          allow read: if request.auth != null && getUserRole(request.auth.uid) == 'ADMIN';
          allow write: if request.auth != null; // السماح بالكتابة من أي مستخدم مصادق عليه لتسجيل الأنشطة
        }
        
        // قواعد التقارير
        match /reports/{reportId} {
          allow read: if request.auth != null;
          allow write: if request.auth != null && getUserRole(request.auth.uid) == 'ADMIN';
        }
        
        // قواعد الدردشة
        match /chatRooms/{chatRoomId} {
          allow read, write: if request.auth != null 
            && request.auth.uid in resource.data.participants;
        }
        
        match /messages/{messageId} {
          allow read, write: if request.auth != null;
        }
        
        // دالة مساعدة للحصول على دور المستخدم
        function getUserRole(userId) {
          return get(/databases/$(database)/documents/users/$(userId)).data.role;
        }
      }
    }
    ```
    **ملاحظة**: هذه القواعد هي مثال. قد تحتاج إلى تعديلها لتناسب متطلبات الأمان الدقيقة لتطبيقك. على سبيل المثال، يمكنك إضافة شروط للتحقق من صلاحيات محددة بدلاً من مجرد الدور.

## 2. تفعيل الميزات في الكود

تم تصميم التطبيق ليكون وحدات (Modular) قدر الإمكان، ومعظم الميزات تعمل تلقائيًا بمجرد إعداد Firebase بشكل صحيح.

### 2.1. إدارة الأطباء والمستخدمين والتقارير وسجل التدقيق

تم دمج هذه الميزات في بنية التطبيق (Domain, Data, Presentation) وتم ربطها بـ Firebase Firestore. الشاشات والـ ViewModels والمستودعات (Repositories) الخاصة بها جاهزة للاستخدام.

-   **الأطباء**: يمكنك الوصول إلى شاشة الأطباء من خلال شريط التنقل السفلي. ستتمكن من إضافة، تعديل، تفعيل/إلغاء تفعيل الأطباء.
-   **المستخدمون والصلاحيات**: شاشة المستخدمين تتيح لك إدارة المستخدمين وتغيير أدوارهم. الصلاحيات يتم تطبيقها على مستوى الكود (في الـ ViewModels والـ Use Cases) وعلى مستوى قواعد أمان Firebase. يجب أن يكون المستخدم الذي يقوم بهذه العمليات لديه دور `ADMIN`.
-   **سجل التدقيق (Audit Log)**: يتم تسجيل الأنشطة تلقائيًا في Firestore. يمكنك عرض هذه السجلات من شاشة سجل التدقيق (تحتاج إلى صلاحية `VIEW_AUDIT_LOG`).
-   **التقارير**: شاشة التقارير تتيح لك إنشاء وتصدير التقارير. سيتم إنشاء التقارير بناءً على البيانات الموجودة في Firestore.

### 2.2. المزامنة الفورية والدردشة

-   **المزامنة الفورية**: يتم تحقيقها باستخدام `Flow` في Kotlin و `addSnapshotListener` من Firebase Firestore. هذا يعني أن أي تغييرات في قاعدة البيانات ستنعكس تلقائيًا في واجهة المستخدم دون الحاجة لتحديث يدوي.
-   **الدردشة**: تعمل الدردشة الفورية من خلال Firestore و Firebase Cloud Messaging (FCM). تأكد من أن خدمة `ClinicFirebaseMessagingService` مسجلة بشكل صحيح في `AndroidManifest.xml` وأن جهازك لديه اتصال بالإنترنت لتلقي الإشعارات.

### 2.3. إدارة الصلاحيات في التطبيق (على مستوى الكود)

في طبقة `domain`، تم تعريف الأدوار والصلاحيات. في طبقة `presentation` (الـ ViewModels)، يمكنك استخدام `UserRepository` للتحقق من صلاحيات المستخدم الحالي قبل السماح بإجراء عمليات معينة.

**مثال (في ViewModel أو Use Case):**
```kotlin
import com.clinic.domain.model.Permission
import com.clinic.domain.repository.UserRepository

// ... داخل ViewModel أو Use Case

suspend fun someActionThatRequiresPermission() {
    val currentUser = userRepository.getCurrentUser()
    if (currentUser != null && userRepository.hasPermission(currentUser.id, Permission.ADD_PATIENT)) {
        // قم بالإجراء
    } else {
        // أظهر رسالة خطأ: لا توجد صلاحية
    }
}
```

## 3. بناء وتشغيل التطبيق

بعد وضع ملف `google-services.json` وتحديث قواعد أمان Firebase، يمكنك بناء وتشغيل التطبيق في Android Studio.

1.  افتح المشروع في Android Studio.
2.  انتظر حتى يقوم Gradle بمزامنة المشروع.
3.  قم بتوصيل جهاز Android أو استخدم محاكي.
4.  انقر على زر `Run` (المثلث الأخضر) لتثبيت التطبيق وتشغيله على جهازك.

## 4. اختبار الميزات

راجع ملف `TESTING_GUIDE.md` للحصول على سيناريوهات اختبار مفصلة لجميع الميزات، بما في ذلك البحث السريع، إدارة المرضى، المواعيد، الأطباء، المستخدمين، سجل التدقيق، والتقارير.

---

