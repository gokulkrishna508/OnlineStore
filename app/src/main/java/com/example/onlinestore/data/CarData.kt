package com.example.onlinestore.data

data class CarData(
    var id: Int?=null,
    var name: Pair<String?, String?> = Pair(null,null),
    var carImage: String?=null,
    var gearType: Pair<String?,String?> = Pair(null,null),
    var doors: Int?=null,
    var seats: Int?=null,
    var rent: Int?=null,
    var fuelType: String?=null,
    var carDetails: Pair<String?,String?> = Pair(null,null),
    var isBlueTooth: Pair<String?,String?> = Pair(null,null),
    var isGps: Pair<String?,String?> = Pair(null,null),
    var bookingTotalPrice: Int?= null,
    var detailCarImages: MutableList<String?> ?=null,
    var viewType: Int?=null,
)

















/*// ... (existing code)

class AlarmReceiver(private val homeFragment: HomeFragment) : BroadcastReceiver() {
    // ... (rest of the class remains unchanged)

    override fun onReceive(context: Context?, intent: Intent?) {
        // ... (existing code)

        // Call the function in HomeFragment
        homeFragment.imageUrl?.let { homeFragment.startScheduledDownload(it) }
    }
}


class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // Handle the alarm trigger here
        val imageUrl = intent?.getStringExtra("imageUrl")
        imageUrl?.let {
            // Call the function to start the scheduled download
            startScheduledDownload(it)
        }
    }
}


private var scheduledDownloadTime: Long = 0

// ... (existing code)

private fun alertBox() {
  AlertDialog.Builder(context).setTitle("Download Image")
      .setPositiveButton("Download Now") { dialog, id ->
          imageUrl?.let { downloadImage(it) }
      }.setNeutralButton("Cancel") { dialog, id -> dialog.cancel() }
      .setNegativeButton("Schedule") { dialog, _ ->
          timePicker()
      }.show()
}

// ... (existing code)

private fun startScheduledDownload(time: String) {
  val scheduledTimeInMillis = calculateScheduledTimeInMillis(time)

  if (scheduledTimeInMillis > System.currentTimeMillis()) {
      scheduledDownloadTime = scheduledTimeInMillis
      scheduleDownloadAtTime(scheduledTimeInMillis)
      toast("Download scheduled successfully at $time")
  } else {
      toast("Invalid scheduled time")
  }
}

private fun calculateScheduledTimeInMillis(time: String): Long {
  // Convert the scheduled time to milliseconds
  // You may need to adjust this logic based on your time format
  val calendar = Calendar.getInstance()
  val parts = time.trim().split(":")
  if (parts.size == 2) {
      val hour = parts[0].toInt()
      val minute = parts[1].toInt()
      calendar.set(Calendar.HOUR_OF_DAY, hour)
      calendar.set(Calendar.MINUTE, minute)
      calendar.set(Calendar.SECOND, 0)
      return calendar.timeInMillis
  }
  return 0
}

private fun scheduleDownloadAtTime(timeInMillis: Long) {
  val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
  val intent = Intent(context, AlarmReceiver::class.java)
  val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

  // Set the alarm to trigger at the scheduled time
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      alarmManager.setExactAndAllowWhileIdle(
          AlarmManager.RTC_WAKEUP,
          timeInMillis,
          pendingIntent
      )
  } else {
      alarmManager.setExact(
          AlarmManager.RTC_WAKEUP,
          timeInMillis,
          pendingIntent
      )
  }
}

// ... (existing code)
*/

