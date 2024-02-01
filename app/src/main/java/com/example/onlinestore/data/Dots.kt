package com.example.onlinestore.data

data class Dots(
    var id:Int = 0,
    var isSelected:Boolean = false
)

/*import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.io.File

const val NOTIFICATION_CHANNEL_ID = "file_notification_channel"
const val NOTIFICATION_ID = 1

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val filePath = intent?.getStringExtra("file_path")
        showNotification(context, filePath)
    }

    private fun showNotification(context: Context?, filePath: String?) {
        val notificationManager = NotificationManagerCompat.from(context!!)
        createNotificationChannel(context)

        val notificationIntent = Intent(context, YourMainActivity::class.java)
        notificationIntent.putExtra("file_path", filePath)
        val contentIntent = PendingIntent.getActivity(
            context, 0,
            notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("File Downloaded")
            .setContentText("Your file has been downloaded.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(contentIntent)
            .setAutoCancel(true)

        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "File Notification Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}

fun scheduleDownloadWithNotification(context: Context, timeInMillis: Long, filePath: String) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, AlarmReceiver::class.java)
    intent.putExtra("file_path", filePath)

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            timeInMillis,
            pendingIntent
        )
    } else {
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            timeInMillis,
            pendingIntent
        )
    }
}
*/