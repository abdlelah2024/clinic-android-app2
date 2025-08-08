package com.clinic.app.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.clinic.app.R
import com.clinic.app.presentation.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ClinicFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // التعامل مع الرسائل الواردة
        remoteMessage.notification?.let { notification ->
            showNotification(
                title = notification.title ?: "تطبيق العيادة",
                body = notification.body ?: "",
                data = remoteMessage.data
            )
        }

        // التعامل مع البيانات المخصصة
        if (remoteMessage.data.isNotEmpty()) {
            handleDataMessage(remoteMessage.data)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        
        // إرسال الرمز المميز الجديد إلى الخادم
        sendTokenToServer(token)
    }

    private fun showNotification(title: String, body: String, data: Map<String, String>) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "clinic_notifications"

        // إنشاء قناة الإشعارات للإصدارات الحديثة من Android
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "إشعارات العيادة",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "إشعارات المواعيد والرسائل"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // إنشاء Intent للانتقال إلى التطبيق عند النقر على الإشعار
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            // إضافة البيانات المخصصة إلى Intent
            data.forEach { (key, value) ->
                putExtra(key, value)
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // بناء الإشعار
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun handleDataMessage(data: Map<String, String>) {
        val messageType = data["type"]
        
        when (messageType) {
            "chat_message" -> {
                // التعامل مع رسائل الدردشة
                val senderId = data["senderId"]
                val message = data["message"]
                // يمكن إضافة منطق إضافي هنا
            }
            "appointment_reminder" -> {
                // التعامل مع تذكيرات المواعيد
                val appointmentId = data["appointmentId"]
                val patientName = data["patientName"]
                // يمكن إضافة منطق إضافي هنا
            }
            "appointment_update" -> {
                // التعامل مع تحديثات المواعيد
                val appointmentId = data["appointmentId"]
                val status = data["status"]
                // يمكن إضافة منطق إضافي هنا
            }
        }
    }

    private fun sendTokenToServer(token: String) {
        // إرسال الرمز المميز إلى الخادم لحفظه في قاعدة البيانات
        // يمكن استخدام Retrofit أو أي مكتبة HTTP أخرى
        // أو حفظه مباشرة في Firestore
    }
}

