package com.example.onlinestore.data.repositories

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.onlinestore.R
import java.io.File


const val NOTIFICATION_ID = 1
//const val PICK_FILE_RESULT_CODE=0
class AlarmReceiver: BroadcastReceiver() {

    companion object {
        var URLTobeDownload: String? = null
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("MutableImplicitPendingIntent", "LaunchActivityFromNotification", "SuspiciousIndentation")
    override fun onReceive(context: Context?, intent: Intent?) {

        val imageId = intent?.getStringExtra("job_id")
        val channelId = "download_Image_at_time"

        context?.let { getContext ->
            val notificationManager = getContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

//            val toLaunch = Intent()
//            val intents= Intent(Intent.ACTION_VIEW)
//            intents.setType("*/*")
//            val pendingIntent = PendingIntent.getBroadcast(context, 0, toLaunch, PendingIntent.FLAG_MUTABLE)
//            context.startActivity(intents)
            val intents = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.journaldev.com/")) // need to change the click event
            val pendingIntent = PendingIntent.getActivity(context, 0, intents, PendingIntent.FLAG_MUTABLE)

            val builder = NotificationCompat.Builder(getContext, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Image Downloaded")
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)

            notificationManager.notify(NOTIFICATION_ID, builder.build())
        }
        startDownload(imageId,context)
    }

    @SuppressLint("ScheduleExactAlarm")
    fun startDownload(imageUrl: String?,context: Context?) {
        imageUrl?.let { downloadImage(it,context) }
    }

    @SuppressLint("Range")
    fun downloadImage(url: String,context: Context?) {

        val directory = File(Environment.DIRECTORY_PICTURES)
        if (!directory.exists()) { directory.mkdirs() }
        val downloadManager = context?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadUri = Uri.parse(url)

        val request = DownloadManager.Request(downloadUri).apply {
            setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle(url.substring(url.lastIndexOf("/") + 1))
                .setDescription("")
                .setDestinationInExternalPublicDir(
                    directory.toString(),
                    url.substring(url.lastIndexOf("/") + 1)
                )
        }

        val downloadId = downloadManager.enqueue(request)
        val query = DownloadManager.Query().setFilterById(downloadId)
        Thread{
            var downloading = true
            while (downloading) {
                val cursor: Cursor = downloadManager.query(query)
                cursor.moveToFirst()
                if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                    downloading = false
                }
                cursor.close()
            }
        }.start()
    }
}
