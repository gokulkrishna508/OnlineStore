package com.example.onlinestore.data.repositories

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.lifecycleScope
import com.example.onlinestore.R
import com.example.onlinestore.view.ui.HomeFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmReceiver: BroadcastReceiver() {
    companion object{
        var URLTobeDownload: String? = null
    }
    override fun onReceive(context: Context?, intent: Intent?) {
        val homeFragment: HomeFragment = HomeFragment()

        val viewer = Intent(context, HomeFragment::class.java)
        intent?.flags =  Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(context,0,viewer, PendingIntent.FLAG_IMMUTABLE)
        val channelId = "download_Image_at_time"
        Log.e("@@run", "onReceive: >>>>, $URLTobeDownload " )

        context?.let { getContext ->
            val notificationManager = getContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val builder = NotificationCompat.Builder(getContext, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Download Alarm")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
            Log.e("@@run2", "onReceive: >>>>2 " )

            URLTobeDownload?.let { homeFragment.startDownload(it) }
            notificationManager.notify(1, builder.build())
        }

    }
}