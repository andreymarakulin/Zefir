package com.andmar.zefir

import android.R.attr.description
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService

const val NEW_MESSAGE_ID = "newMessageNotification"
class NotificationClient(
    val context: Context
) {

    fun showNewMessageNotification(chatName: String) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
                ) {
                return
            }
        }

        var builder = NotificationCompat.Builder(context, NEW_MESSAGE_ID)
            .setSmallIcon(R.drawable.send)
            .setContentTitle("New message!")
            .setContentText("Новые уведомления в $chatName")
            .setPriority(NotificationCompat.PRIORITY_MAX)

        with(NotificationManagerCompat.from(context)) {
            notify(1, builder.build())
        }
    }


    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "New message"
            val descriptionText = "New message notification"
            val importance = IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NEW_MESSAGE_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system.
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}