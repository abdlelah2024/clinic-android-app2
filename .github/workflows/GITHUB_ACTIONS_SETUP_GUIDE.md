# دليل إعداد GitHub Actions لتطبيق العيادة

هذا الدليل يوضح كيفية إعداد بيئة GitHub Actions لبناء وتوزيع تطبيق الأندرويد الخاص بالعيادة تلقائيًا. يتضمن ذلك إعداد مفاتيح التوقيع ومتغيرات البيئة الضرورية.

## 1. مفاتيح توقيع التطبيق (App Signing Keys)

لتوزيع تطبيق أندرويد، يجب توقيعه باستخدام مفتاح توقيع (Keystore). هذا يضمن أمان التطبيق ويسمح لنظام التشغيل بالتحقق من هويته وتحديثاته. لا يجب تخزين ملف Keystore مباشرة في مستودع GitHub لأسباب أمنية. بدلاً من ذلك، سنقوم بتشفيره وتخزينه كسر (Secret) في GitHub.

### 1.1. إنشاء مفتاح توقيع (إذا لم يكن لديك)

إذا لم يكن لديك مفتاح توقيع موجود، يمكنك إنشاؤه باستخدام الأمر `keytool`:

```bash
keytool -genkey -v -keystore my-upload-key.keystore -alias my-key-alias -keyalg RSA -keysize 2048 -validity 10000
```

-   `my-upload-key.keystore`: اسم ملف Keystore الذي سيتم إنشاؤه.
-   `my-key-alias`: اسم مستعار للمفتاح داخل Keystore.
-   `validity 10000`: صلاحية المفتاح لمدة 10000 يوم.

سيطلب منك هذا الأمر إدخال كلمة مرور لـ Keystore، وكلمة مرور للمفتاح، وبعض المعلومات الشخصية. احفظ هذه المعلومات جيدًا.

### 1.2. تشفير مفتاح التوقيع

لأسباب أمنية، يجب تشفير ملف `my-upload-key.keystore` قبل تخزينه كسر في GitHub. يمكنك استخدام OpenSSL لتشفير الملف:

```bash
openssl enc -aes-256-cbc -salt -in my-upload-key.keystore -out my-upload-key.keystore.enc -k "YOUR_ENCRYPTION_PASSWORD"
```

استبدل `"YOUR_ENCRYPTION_PASSWORD"` بكلمة مرور قوية خاصة بالتشفير. ستحتاج إلى هذه الكلمة لفك تشفير الملف لاحقًا في GitHub Actions.

### 1.3. إضافة مفتاح التوقيع المشفر كسر في GitHub

1.  اذهب إلى مستودع GitHub الخاص بك.
2.  انتقل إلى `Settings` -> `Secrets and variables` -> `Actions`.
3.  انقر على `New repository secret`.
4.  أنشئ سرًا جديدًا بالاسم `ENCRYPTED_KEYSTORE_FILE`.
5.  الصق محتوى ملف `my-upload-key.keystore.enc` (الذي تم تشفيره) في حقل `Value`. يمكنك الحصول على المحتوى باستخدام `cat my-upload-key.keystore.enc`.

## 2. متغيرات البيئة (Environment Variables)

سنحتاج إلى تخزين بعض المعلومات الحساسة الأخرى كمتغيرات بيئة في GitHub Secrets، مثل كلمات المرور الخاصة بـ Keystore و Firebase Token.

### 2.1. كلمات مرور Keystore

سنحتاج إلى كلمات المرور التي قمت بتعيينها عند إنشاء مفتاح التوقيع.

1.  انتقل إلى `Settings` -> `Secrets and variables` -> `Actions` في مستودع GitHub الخاص بك.
2.  أنشئ سرًا جديدًا بالاسم `KEYSTORE_PASSWORD` وضع فيه كلمة مرور Keystore.
3.  أنشئ سرًا جديدًا بالاسم `KEY_PASSWORD` وضع فيه كلمة مرور المفتاح (الاسم المستعار).
4.  أنشئ سرًا جديدًا بالاسم `KEYSTORE_ALIAS` وضع فيه الاسم المستعار للمفتاح (مثال: `my-key-alias`).
5.  أنشئ سرًا جديدًا بالاسم `ENCRYPTION_PASSWORD` وضع فيه كلمة المرور التي استخدمتها لتشفير ملف Keystore في الخطوة 1.2.

### 2.2. Firebase Token (لتوزيع Firebase App Distribution)

لتوزيع التطبيق تلقائيًا إلى Firebase App Distribution، ستحتاج إلى Firebase Token. هذا الرمز يسمح لـ GitHub Actions بالمصادقة مع Firebase.

1.  تأكد من تثبيت Firebase CLI على جهازك المحلي:
    ```bash
    npm install -g firebase-tools
    ```
2.  قم بتسجيل الدخول إلى Firebase CLI وقم بإنشاء رمز مميز (Token):
    ```bash
    firebase login:ci
    ```
    سيفتح هذا نافذة متصفح للمصادقة. بعد المصادقة، سيعرض لك Firebase CLI رمزًا مميزًا. انسخ هذا الرمز.
3.  انتقل إلى `Settings` -> `Secrets and variables` -> `Actions` في مستودع GitHub الخاص بك.
4.  أنشئ سرًا جديدًا بالاسم `FIREBASE_TOKEN` والصق الرمز المميز الذي حصلت عليه من Firebase CLI في حقل `Value`.

### 2.3. Firebase Project ID

سوف تحتاج إلى معرف مشروع Firebase الخاص بك لتحديد المشروع الذي سيتم التوزيع إليه.

1.  يمكنك العثور على معرف المشروع في Firebase Console، في إعدادات المشروع (Project settings).
2.  انتقل إلى `Settings` -> `Secrets and variables` -> `Actions` في مستودع GitHub الخاص بك.
3.  أنشئ سرًا جديدًا بالاسم `FIREBASE_PROJECT_ID` وضع فيه معرف مشروع Firebase الخاص بك.

## 3. إعداد ملف `gradle.properties` (للتوقيع)

للسماح لـ Gradle بالوصول إلى معلومات التوقيع من متغيرات البيئة (التي سيتم حقنها بواسطة GitHub Actions)، ستحتاج إلى تعديل ملف `gradle.properties` في جذر مشروع الأندرويد الخاص بك (إذا لم يكن موجودًا، قم بإنشائه):

```properties
# Signing properties for release builds
KEYSTORE_PATH=my-upload-key.keystore
KEYSTORE_PASSWORD=
KEY_ALIAS=
KEY_PASSWORD=
```

**ملاحظة**: لا تضع القيم الحقيقية هنا مباشرة. سيتم تجاوز هذه القيم بواسطة متغيرات البيئة في GitHub Actions. هذه مجرد أسماء للمتغيرات التي سيبحث عنها Gradle.

## 4. تحديث ملف `build.gradle` (على مستوى التطبيق)

تأكد من أن ملف `app/build.gradle` الخاص بك يحتوي على قسم التوقيع (signing config) الذي يستخدم هذه الخصائص:

```gradle
android {
    ...
    signingConfigs {
        release {
            storeFile file(System.getenv("KEYSTORE_PATH") ?: project.findProperty("KEYSTORE_PATH") as String)
            storePassword System.getenv("KEYSTORE_PASSWORD") ?: project.findProperty("KEYSTORE_PASSWORD") as String
            keyAlias System.getenv("KEYSTORE_ALIAS") ?: project.findProperty("KEYSTORE_ALIAS") as String
            keyPassword System.getenv("KEY_PASSWORD") ?: project.findProperty("KEY_PASSWORD") as String
        }
    }
    buildTypes {
        release {
            signingConfig signingConfigs.release
            ...
        }
    }
}
```

بهذه الإعدادات، سيكون مشروعك جاهزًا لاستخدام GitHub Actions للبناء والتوقيع والتوزيع تلقائيًا. في الخطوات التالية، سنقوم بإنشاء ملفات سير العمل (Workflow files) الفعلية.



## 5. سير عمل GitHub Actions للبناء والإصدار إلى GitHub Releases

لقد قمت بإنشاء ملف سير عمل GitHub Actions باسم `.github/workflows/github_release.yml` في مشروعك. هذا الملف مسؤول عن بناء تطبيق الأندرويد الخاص بك (ملف APK) وتوقيعه، ثم رفعه كإصدار (Release) على GitHub.

### 5.1. كيفية عمل سير العمل (`github_release.yml`)

يتم تشغيل هذا السير العمل في الحالات التالية:

-   **عند الدفع إلى الفرع `main`**: سيقوم ببناء ملف APK ورفعه كـ "artifact" (ملف مؤقت) يمكنك تنزيله من صفحة GitHub Actions.
-   **عند إنشاء علامة (Tag) تبدأ بـ `v` (مثل `v1.0.0`، `v1.0.1`)**: سيقوم ببناء ملف APK ورفعه كـ "إصدار" (Release) رسمي على صفحة GitHub Releases الخاصة بمستودعك. هذا هو الخيار المفضل لتوزيع الإصدارات النهائية.
-   **يدويًا (Workflow Dispatch)**: يمكنك تشغيل هذا السير العمل يدويًا من صفحة GitHub Actions في مستودعك.

### 5.2. الخطوات التي يقوم بها سير العمل:

1.  **Checkout code**: يقوم بسحب الكود المصدري لمشروعك من المستودع.
2.  **Set up JDK 17**: يقوم بإعداد بيئة Java Development Kit (JDK) 17، وهي ضرورية لبناء مشاريع الأندرويد.
3.  **Grant execute permission for gradlew**: يمنح صلاحيات التنفيذ لسكربت `gradlew`.
4.  **Decode Keystore**: يقوم بفك تشفير ملف Keystore الذي قمت بتخزينه كسر (`ENCRYPTED_KEYSTORE_FILE`) في GitHub Secrets. هذا الملف ضروري لتوقيع التطبيق.
5.  **Build Release APK**: يقوم ببناء ملف APK في وضع الإصدار (Release mode) باستخدام Gradle. يتم استخدام كلمات مرور Keystore والاسم المستعار للمفتاح من GitHub Secrets لتوقيع التطبيق.
6.  **Upload APK to GitHub Release**: إذا تم تشغيل سير العمل بواسطة علامة (Tag)، فسيقوم برفع ملف `app-release.apk` الذي تم بناؤه إلى صفحة GitHub Releases. سيتم إنشاء إصدار جديد بالاسم ورقم الإصدار الذي تحدده في العلامة.
7.  **Upload APK as artifact**: إذا تم تشغيل سير العمل بواسطة دفع (push) عادي إلى الفرع `main` (وليس علامة)، فسيتم رفع ملف APK كـ "artifact" يمكن تنزيله من صفحة GitHub Actions. هذا مفيد لاختبار الإصدارات قبل إصدارها رسميًا.

### 5.3. كيفية استخدام هذا السير العمل:

1.  **تأكد من إعداد GitHub Secrets**: كما هو موضح في القسم 2 من هذا الدليل، يجب عليك إعداد جميع الأسرار (Secrets) المتعلقة بـ Keystore في مستودع GitHub الخاص بك.
2.  **لإنشاء إصدار (Release) جديد**: قم بإنشاء علامة (Tag) جديدة في مستودع Git الخاص بك تبدأ بـ `v`، ثم ادفع هذه العلامة إلى GitHub. على سبيل المثال:
    ```bash
    git tag v1.0.0
    git push origin v1.0.0
    ```
    بمجرد دفع العلامة، سيتم تشغيل سير العمل تلقائيًا، وبمجرد اكتماله بنجاح، ستجد ملف APK الخاص بك متاحًا للتنزيل في صفحة GitHub Releases الخاصة بمستودعك.
3.  **للحصول على ملف APK من دفع عادي**: قم بإجراء تغييرات على الكود وادفعها إلى الفرع `main`:
    ```bash
    git push origin main
    ```
    سيتم تشغيل سير العمل، وبعد اكتماله، يمكنك الذهاب إلى صفحة GitHub Actions لمستودعك، والنقر على أحدث تشغيل لسير عمل `Android CI & GitHub Release`، ثم تنزيل ملف APK من قسم `Artifacts`.

**ملاحظة هامة**: تأكد من أن ملف `app/build.gradle` الخاص بك يحتوي على إعدادات التوقيع الصحيحة التي تستخدم متغيرات البيئة، كما هو موضح في القسم 4 من هذا الدليل. هذا يضمن أن GitHub Actions يمكنه توقيع التطبيق بنجاح.



## 6. سير عمل GitHub Actions للتوزيع إلى Firebase App Distribution

لقد قمت بإنشاء ملف سير عمل GitHub Actions آخر باسم `.github/workflows/firebase_app_distribution.yml` في مشروعك. هذا الملف مسؤول عن بناء تطبيق الأندرويد الخاص بك (ملف APK) وتوقيعه، ثم توزيعه تلقائيًا إلى Firebase App Distribution.

### 6.1. كيفية عمل سير العمل (`firebase_app_distribution.yml`)

يتم تشغيل هذا السير العمل في الحالات التالية:

-   **عند الدفع إلى الفرع `main`**: سيقوم ببناء ملف APK وتوزيعه على Firebase App Distribution.
-   **يدويًا (Workflow Dispatch)**: يمكنك تشغيل هذا السير العمل يدويًا من صفحة GitHub Actions في مستودعك.

### 6.2. الخطوات التي يقوم بها سير العمل:

1.  **Checkout code**: يقوم بسحب الكود المصدري لمشروعك من المستودع.
2.  **Set up JDK 17**: يقوم بإعداد بيئة Java Development Kit (JDK) 17.
3.  **Grant execute permission for gradlew**: يمنح صلاحيات التنفيذ لسكربت `gradlew`.
4.  **Decode Keystore**: يقوم بفك تشفير ملف Keystore لتوقيع التطبيق.
5.  **Build Release APK**: يقوم ببناء ملف APK في وضع الإصدار (Release mode) وتوقيعه.
6.  **Upload to Firebase App Distribution**: هذه هي الخطوة الرئيسية التي تستخدم `FirebaseExtended/action-hosting-deploy` لرفع ملف APK إلى Firebase App Distribution. تتطلب هذه الخطوة عدة متغيرات:
    -   `repoToken`: رمز GitHub المميز للوصول إلى المستودع.
    -   `firebaseServiceAccount`: حساب خدمة Firebase للمصادقة. (سنقوم بإعداده في القسم التالي).
    -   `projectId`: معرف مشروع Firebase الخاص بك.
    -   `apkPath`: المسار إلى ملف APK الذي تم بناؤه.
    -   `appId`: **معرف تطبيق Android الخاص بك من Firebase**. **يجب عليك استبدال `YOUR_FIREBASE_APP_ID` في ملف سير العمل بالقيمة الصحيحة.** يمكنك العثور على هذا المعرف في Firebase Console -> إعدادات المشروع -> تطبيقاتك -> معرف التطبيق.
    -   `groups`: (اختياري) قائمة بالمجموعات التي سيتم توزيع التطبيق عليها (مثل `testers`). يجب أن تكون هذه المجموعات قد تم إنشاؤها مسبقًا في Firebase App Distribution.
    -   `releaseNotes`: (اختياري) ملاحظات الإصدار التي ستظهر للمختبرين.

### 6.3. إعداد Firebase App Distribution

لتوزيع التطبيق إلى Firebase App Distribution، ستحتاج إلى إعداد حساب خدمة Firebase وتوفير معرف تطبيق Android الخاص بك.

#### 6.3.1. إنشاء حساب خدمة Firebase

1.  اذهب إلى [Firebase Console](https://console.firebase.google.com/).
2.  انتقل إلى `Project settings` -> `Service accounts`.
3.  انقر على `Generate new private key` لإنشاء ملف JSON يحتوي على مفتاح حساب الخدمة. قم بتنزيل هذا الملف.
4.  افتح ملف JSON الذي تم تنزيله وانسخ محتوياته بالكامل.
5.  في مستودع GitHub الخاص بك، انتقل إلى `Settings` -> `Secrets and variables` -> `Actions`.
6.  أنشئ سرًا جديدًا بالاسم `FIREBASE_SERVICE_ACCOUNT` والصق محتويات ملف JSON في حقل `Value`.

#### 6.3.2. الحصول على معرف تطبيق Android (App ID)

1.  في Firebase Console، اذهب إلى `Project settings`.
2.  في قسم `Your apps`، ابحث عن تطبيق Android الخاص بك.
3.  انسخ `App ID` الخاص بتطبيق Android (يبدأ عادة بـ `1:`).
4.  **الصق هذا المعرف في ملف `.github/workflows/firebase_app_distribution.yml` بدلاً من `YOUR_FIREBASE_APP_ID`**.

#### 6.3.3. إنشاء مجموعات المختبرين (اختياري)

إذا كنت ترغب في توزيع التطبيق على مجموعات محددة من المختبرين، فيمكنك إنشائها في Firebase App Distribution:

1.  في Firebase Console، اذهب إلى `Engage` -> `App Distribution`.
2.  انتقل إلى علامة التبويب `Testers & Groups`.
3.  أنشئ مجموعات جديدة وأضف المختبرين إليها.
4.  تأكد من أن أسماء المجموعات التي تستخدمها في متغير `groups` في سير عمل GitHub Actions تتطابق تمامًا مع الأسماء التي أنشأتها في Firebase.

### 6.4. كيفية استخدام هذا السير العمل:

1.  **تأكد من إعداد GitHub Secrets**: كما هو موضح في القسم 2 وهذا القسم من الدليل، يجب عليك إعداد جميع الأسرار الضرورية (`ENCRYPTED_KEYSTORE_FILE`, `KEYSTORE_PASSWORD`, `KEY_PASSWORD`, `KEYSTORE_ALIAS`, `ENCRYPTION_PASSWORD`, `FIREBASE_TOKEN`, `FIREBASE_PROJECT_ID`, `FIREBASE_SERVICE_ACCOUNT`).
2.  **تحديث `appId` في سير العمل**: **تأكد من استبدال `YOUR_FIREBASE_APP_ID` في ملف `.github/workflows/firebase_app_distribution.yml` بمعرف تطبيق Android الصحيح من Firebase.**
3.  **للتوزيع**: قم بإجراء تغييرات على الكود وادفعها إلى الفرع `main`:
    ```bash
    git push origin main
    ```
    سيتم تشغيل سير العمل تلقائيًا، وبعد اكتماله بنجاح، سيتم توزيع ملف APK إلى Firebase App Distribution، وسيتلقى المختبرون الذين أضفتهم إشعارًا بأن هناك إصدارًا جديدًا متاحًا للاختبار.
4.  **التشغيل اليدوي**: يمكنك أيضًا تشغيل هذا السير العمل يدويًا من صفحة GitHub Actions في مستودعك.

**ملاحظة هامة**: تأكد من أن ملف `app/build.gradle` الخاص بك يحتوي على إعدادات التوقيع الصحيحة التي تستخدم متغيرات البيئة، كما هو موضح في القسم 4 من هذا الدليل. هذا يضمن أن GitHub Actions يمكنه توقيع التطبيق بنجاح قبل توزيعه.



## 7. الخلاصة والخطوات التالية

لقد قمت الآن بإعداد مشروعك لاستخدام GitHub Actions للبناء التلقائي وتوزيع تطبيق الأندرويد الخاص بك إلى كل من GitHub Releases و Firebase App Distribution.

**الخطوات الرئيسية التي قمت بها:**

1.  **إعداد مفاتيح التوقيع**: قمت بإنشاء (أو استخدام) مفتاح توقيع، وتشفيره، وتخزينه كسر آمن في GitHub.
2.  **تكوين متغيرات البيئة**: قمت بتخزين كلمات مرور Keystore، و Firebase Token، و Firebase Project ID كأسرار في GitHub Secrets.
3.  **تحديث ملفات Gradle**: تأكدت من أن ملفات `gradle.properties` و `app/build.gradle` مهيأة بشكل صحيح لاستخدام متغيرات البيئة للتوقيع.
4.  **إنشاء سير عمل GitHub Releases**: قمت بإنشاء ملف سير عمل يقوم ببناء التطبيق ورفعه كإصدار على GitHub عند دفع علامة (Tag).
5.  **إنشاء سير عمل Firebase App Distribution**: قمت بإنشاء ملف سير عمل يقوم ببناء التطبيق وتوزيعه على Firebase App Distribution عند الدفع إلى الفرع `main`.

**ماذا تفعل الآن؟**

1.  **ادفع مشروعك إلى مستودع GitHub**: إذا لم تكن قد فعلت ذلك بالفعل، قم بدفع مشروع `clinic-android-app` بالكامل إلى مستودع GitHub الخاص بك.
2.  **أضف الأسرار (Secrets) إلى مستودع GitHub**: اتبع التعليمات في القسم 2 من هذا الدليل لإضافة جميع الأسرار المطلوبة إلى مستودع GitHub الخاص بك.
3.  **تأكد من `appId` في سير عمل Firebase App Distribution**: لا تنسَ استبدال `YOUR_FIREBASE_APP_ID` في ملف `.github/workflows/firebase_app_distribution.yml` بمعرف تطبيق Android الصحيح من Firebase.
4.  **ابدأ في استخدام سير العمل**: 
    -   لإنشاء إصدار على GitHub Releases، قم بإنشاء علامة (Tag) وادفعها إلى GitHub (مثال: `git tag v1.0.0 && git push origin v1.0.0`).
    -   لتوزيع التطبيق على Firebase App Distribution، قم بدفع التغييرات إلى الفرع `main` (مثال: `git push origin main`).

تهانينا! لقد أتممت الآن إعداد نظام CI/CD لتطبيق الأندرويد الخاص بك باستخدام GitHub Actions وخدمات Firebase. هذا سيجعل عملية البناء والتوزيع أسرع وأكثر كفاءة.

---

