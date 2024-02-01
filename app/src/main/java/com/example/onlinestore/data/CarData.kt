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












/* @SuppressLint("ScheduleExactAlarm")
  fun startScheduledDownload(time: Long) {
      val scheduledTimeInMillis = scheduleTime
      val standardDateFormat = convertLongToTime(time)

      if (scheduledTimeInMillis != null) {
          if (scheduledTimeInMillis > System.currentTimeMillis()) {
              scheduledDownloadTime = scheduledTimeInMillis
              counter++
              Log.d("@@counterStart++", "startScheduledDownloadStart: $counter") // again downloading the downloaded images which means for loop counting again
              for (i in 0 until counter) {
                  val intent = Intent(context, AlarmReceiver::class.java)
                  intent.putExtra("job_id", imageUrl)

                  scheduleDownloadAtTime(scheduledTimeInMillis, intent)
                  toast("Download scheduled successfully at $standardDateFormat")
              }

              Log.d("@@counter++", "startScheduledDownload: $counter")
              Log.d("@@download", "startScheduledDownload: $standardDateFormat")
          } else {
              toast("Invalid scheduled time")
          }
      }
  }

  @SuppressLint("ScheduleExactAlarm")
  fun scheduleDownloadAtTime(timeInMillis: Long, intent: Intent) {
      val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager

      for (i in 0 until counter) {
          val pendingIntent = PendingIntent.getBroadcast(
              context,
              i,
              intent,
              PendingIntent.FLAG_MUTABLE
          )

          if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
              alarmManager.setExact(
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
  }*/

