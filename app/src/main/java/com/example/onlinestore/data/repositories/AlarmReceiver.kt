package com.example.onlinestore.data.repositories

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleOwner
import com.bumptech.glide.manager.Lifecycle
import com.example.onlinestore.R
import kotlinx.coroutines.Dispatchers
import java.io.File

class AlarmReceiver: BroadcastReceiver() {

    companion object {
        var URLTobeDownload: String? = null
    }
    override fun onReceive(context: Context?, intent: Intent?) {

        val imageId = intent?.getStringExtra("job_id")
        Log.e("@@job_id","<<<<<< $imageId")
        val channelId = "download_Image_at_time"
        context?.let { getContext ->
            val notificationManager = getContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val builder = NotificationCompat.Builder(getContext, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Download Alarm")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                //.setContentIntent(pendingIntent)
            notificationManager.notify(1, builder.build())
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