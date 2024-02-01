package com.example.onlinestore.view.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.onlinestore.LocalizedApp
import com.example.onlinestore.data.CarAdapter
import com.example.onlinestore.data.CarAdapter.Companion.DATA_VIEW
import com.example.onlinestore.data.CarAdapter.Companion.LOADER_VIEW
import com.example.onlinestore.data.CarData
import com.example.onlinestore.data.repositories.AlarmReceiver
import com.example.onlinestore.data.repositories.BannerViewPagerAdapter
import com.example.onlinestore.databinding.FragmentHomeBinding
import com.example.onlinestore.view_model.CarViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: CarAdapter
    private val carViewModel by viewModels<CarViewModel>()
    private var isLoading = false
    private val localizationDelegate = LocalizationDelegate()
    private var carData: CarData? = null
    private var loadDataView: Int? = null
    private var msg: String? = ""
    private var lastMsg = ""
    private var imageUrl: String? = null
    private var scheduleTime: Long? = null
    private var counter = 0

    companion object {
        var positionInvoke: Int? = null
        private const val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LocalizedApp.localeLiveData.observe(viewLifecycleOwner, localizationDelegate)
        initViews()
    }

    @SuppressLint("Range")
    fun downloadImage(url: String) {
        val directory = File(Environment.DIRECTORY_PICTURES)

        if (!directory.exists()) { directory.mkdirs() }

        val downloadManager = requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
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

        Log.d("@@checkDownload", "downloadImage:>>>> ")
        val downloadId = downloadManager.enqueue(request)
        val query = DownloadManager.Query().setFilterById(downloadId)
        Thread {
            var downloading = true
            while (downloading) {
                val cursor: Cursor = downloadManager.query(query)
                cursor.moveToFirst()
                if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                    downloading = false
                }
                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                msg = statusMessage(url, directory, status)
                if (msg != lastMsg) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        toast(msg!!)
                    }
                    lastMsg = msg ?: ""
                }
                cursor.close()
            }
        }.start()
    }

    private fun statusMessage(url: String, directory: File, status: Int): String {
        val msg: String = when (status) {
            DownloadManager.STATUS_FAILED -> "Download has been failed, please try again"
            DownloadManager.STATUS_PAUSED -> "Paused"
            DownloadManager.STATUS_PENDING -> "Pending"
            DownloadManager.STATUS_RUNNING -> "Downloading..."
            DownloadManager.STATUS_SUCCESSFUL -> "Image downloaded successfully in $directory" + File.separator + url.substring(
                url.lastIndexOf("/") + 1
            )

            else -> "There's nothing to download"
        }
        return msg
    }


    @SuppressLint("NotifyDataSetChanged", "SuspiciousIndentation")
    private fun initViews() = binding.apply {

        adapter = CarAdapter { onClickCarData ->
            CarDetailFragment.carDataCompanionObject = onClickCarData
            BannerViewPagerAdapter.carDataCompanionObject = onClickCarData
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToCarDetailFragment())
        }
        observeData()
        rvCarCategory.adapter = adapter

        arabicIcon.setOnClickListener {
            LocalizedApp.updateLocale(LocalizedApp.LOCALE_AR)
            CarAdapter.companionObjectAdapter = "arabic"
            CarDetailFragment.companionObjectHomeScreen = "arabic"
            adapter.notifyDataSetChanged()
        }

        englishIcon.setOnClickListener {
            LocalizedApp.updateLocale(LocalizedApp.LOCALE_EN)
            CarAdapter.companionObjectAdapter = "English"
            CarDetailFragment.companionObjectHomeScreen = "English"
            adapter.notifyDataSetChanged()
        }

        adapter.onDownloadImage = { position ->
            imageUrl = position
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q || Build.VERSION.SDK_INT >= 29) {
                askPermissions()
            } else alertBox()
        }

        createNotificationChannel()

        rvCarCategory.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            @SuppressLint("NotifyDataSetChanged")
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount

                if (!isLoading && lastVisibleItemPosition == totalItemCount - 1) {
                    if(carViewModel.currentPage < carViewModel.lastPage){         //carViewModel.currentPage <= carViewModel.lastPage
                        isLoading = true
                        carViewModel.carList.add(CarData(viewType = LOADER_VIEW))
                        adapter.updateList(carViewModel.carList)
                        carViewModel.currentPage++
                        lifecycleScope.launch {
                            delay(2000L)
                            carViewModel.fetchData(carViewModel.currentPage)
                        }
                    }
                }
            }
        })
        carViewModel.fetchData(carViewModel.currentPage)
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun observeData() = binding.apply {

        viewLifecycleOwner.lifecycleScope.launch {

            carViewModel.apiResponseStateFlow.collect { response ->

                if (carViewModel.currentPage >= 1 && carViewModel.carList.isNotEmpty()) {

                    carViewModel.carList.removeAt(carViewModel.carList.lastIndex)
                    adapter.updateList(carViewModel.carList)

                }

                 val data = response?.optJSONObject("response")?.optJSONObject("result")?.optJSONArray("cars")
                 val meta = response?.optJSONObject("response")?.optJSONObject("result")?.optJSONObject("meta")
                 carViewModel.currentPage = meta?.optInt("current_page")?:1
                 carViewModel.lastPage = meta?.optInt("last_page")?:1

                if (data != null) {

                    for (i in 0 until data.length()) {

                        val getCarJsonObject = data.optJSONObject(i)

                        val brand: Pair<String, String> = Pair(
                            getCarJsonObject.optString("model_en"),
                            getCarJsonObject.optString("model_ar")
                        )
                        val model: Pair<String, String> = Pair(
                            getCarJsonObject.optString("model_en"),
                            getCarJsonObject.optString("model_ar")
                        )
                        val transmission: Pair<String, String> = Pair(
                            getCarJsonObject.optString("transmission_en"),
                            getCarJsonObject.optString("transmission_ar")
                        )
                        val blueTooth: Pair<String?, String?> = Pair(
                            getCarJsonObject?.optJSONArray("facilities")?.optJSONObject(2)
                                ?.optString("name_en"),
                            getCarJsonObject?.optJSONArray("facilities")?.optJSONObject(2)
                                ?.optString("name_ar")
                        )

                        val gps: Pair<String?, String?> = Pair(
                            getCarJsonObject?.optJSONArray("facilities")?.optJSONObject(1)
                                ?.optString("name_en"),
                            getCarJsonObject?.optJSONArray("facilities")?.optJSONObject(1)
                                ?.optString("name_ar")
                        )

                        loadDataView = DATA_VIEW

                        val getDetailImageArray = getCarJsonObject.optJSONArray("media")
                        val detailCarImages = mutableListOf<String?>()

                        if (getDetailImageArray != null) {
                            for (j in 0 until getDetailImageArray.length()) {
                                val image = getDetailImageArray.getString(j)
                                detailCarImages.add(image)
                            }
                        }

                        carData = CarData(
                            id = getCarJsonObject.optInt("id"),
                            name = Pair(brand.first, brand.second),
                            carImage = getCarJsonObject?.optString("cover_media"),
                            gearType = Pair(transmission.first, transmission.second),
                            doors = getCarJsonObject?.optInt("door_count"),
                            seats = getCarJsonObject?.optInt("seating_capacity"),
                            rent = getCarJsonObject?.optInt("amount_per_day"),
                            bookingTotalPrice = getCarJsonObject?.optInt("booking_total_price"),
                            fuelType = getCarJsonObject?.optString("fuel_en"),
                            carDetails = Pair(model.first, model.second),
                            isBlueTooth = Pair(blueTooth.first, blueTooth.second),
                            isGps = Pair(gps.first, gps.second),
                            detailCarImages = detailCarImages,
                            viewType = loadDataView
                        )
                         carViewModel.carList.addAll(listOf(carData))
                         isLoading = false
                    }
                    withContext(Dispatchers.Main) {
                        adapter.updateList(carViewModel.carList)
                    }
                }
            }
        }
    }

    private inner class LocalizationDelegate : Observer<String> {
        override fun onChanged(value: String) {
            val layoutDirection = LocalizedApp.getLayoutDirection(value)

            binding.apply {
                languageSwitch.layoutDirection = layoutDirection
                rvCarCategory.layoutDirection = layoutDirection
            }
        }
    }

    private fun alertBox() {
        AlertDialog.Builder(context).setTitle("Download Image")
            .setPositiveButton("Download Now") { _, _ ->
                imageUrl?.let { downloadImage(it) }
            }.setNeutralButton("Cancel") { dialog, _ -> dialog.cancel() }
            .setNegativeButton("Schedule") { _, _ ->
                dateTimePicker()
            }.show()

        /*        val downloadOption = arrayOf("Now","Schedule","Cancel")
                AlertDialog.Builder(context).setItems(downloadOption,DialogInterface.OnClickListener { dialogInterface, i ->
                    when(downloadOption){
                        arrayOf("Now") -> { toast("Downloading")}
                        arrayOf("Schedule") -> { toast("Scheduled..")}
                        arrayOf("Cancel") -> { dialogInterface.cancel()}
                    }
                }).show()*/
    }

    private fun askPermissions() {
        val activity = requireActivity()
        val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE

        if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                AlertDialog.Builder(activity)
                    .setTitle("Permission required")
                    .setMessage("Permission required to save photos from the Web.")
                    .setPositiveButton("Accept") { _, _ ->
                        ActivityCompat.requestPermissions(activity, arrayOf(permission),
                            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
                        )
                    }
                    .setNegativeButton("Deny") { dialog, _ -> dialog.cancel() }
                    .show()
            } else {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(permission),
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
                )
            }
        } else {
            alertBox()
        }
    }

    private fun dateTimePicker() {

        /*        val now = Calendar.getInstance()
        val currentHour = now[Calendar.HOUR_OF_DAY]
        val currentMinute = now[Calendar.MINUTE]
        mHour = currentHour
        mMinute = currentMinute

        val timePickerDialog = TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                mHour = hourOfDay
                mMinute = minute
                scheduleTime = "$hourOfDay:$minute"
                scheduleTime?.let { startScheduledDownload(it) }
            }, mHour, mMinute, false)
        //min time to picker
        timePickerDialog.updateTime(currentHour, currentMinute)
        timePickerDialog.show()*/

        val currentDateTime = Calendar.getInstance()
        val startYear = currentDateTime.get(Calendar.YEAR)
        val startMonth = currentDateTime.get(Calendar.MONTH)
        val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)
        val startHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
        val startMinute = currentDateTime.get(Calendar.MINUTE)

        DatePickerDialog(requireContext(), { _, year, month, day ->
            TimePickerDialog(requireContext(), { _, hour, minute ->
                val pickedDateTime = Calendar.getInstance()
                pickedDateTime.set(year, month, day, hour, minute)

                scheduleTime = pickedDateTime.timeInMillis
                scheduleTime?.let { startScheduledDownload(it) }

            }, startHour, startMinute, false).show()
        }, startYear, startMonth, startDay).show()
    }

    @SuppressLint("ScheduleExactAlarm")
    fun startScheduledDownload(time: Long) {
        val scheduledTimeInMillis = scheduleTime
        val standardDateFormat = convertLongToTime(time)

        if (scheduledTimeInMillis != null) {
            if (scheduledTimeInMillis > System.currentTimeMillis()) {
                Log.d("@@counterStart++", "startScheduledDownloadStart: $counter")

                val newCounter = counter+1

                for (i in 0 until newCounter) {
                    val intent = Intent(context, AlarmReceiver::class.java)
                    intent.putExtra("job_id", imageUrl)
                    scheduleDownloadAtTime(scheduledTimeInMillis, intent, newCounter)
                    toast("Download scheduled successfully at $standardDateFormat")
                }
                counter = newCounter
                Log.d("@@counter++", "startScheduledDownload: $counter")
                Log.d("@@download", "startScheduledDownload: $standardDateFormat")
            } else {
                toast("Invalid scheduled time")
            }
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    fun scheduleDownloadAtTime(timeInMillis: Long, intent: Intent, newCounter: Int) {
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = PendingIntent.getBroadcast(context, newCounter, intent, PendingIntent.FLAG_MUTABLE)

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


    /*private fun calculateScheduledTimeInMillis(time: Long): Long {
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
    }*/

    private fun createNotificationChannel() {
        val channelId = "download_Image_at_time"
        val channelName = "alarm_name"
        val notificationManager = context?.getSystemService(NotificationManager::class.java)
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager?.createNotificationChannel(channel)
    }

}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}

fun Fragment.toast(message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

@SuppressLint("SimpleDateFormat")
fun convertLongToTime(time: Long): String {
    val date = Date(time)
    val format = SimpleDateFormat("dd.MM.yyyy hh:mm a")
    return format.format(date)
}