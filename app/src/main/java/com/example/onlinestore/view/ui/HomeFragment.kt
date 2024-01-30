package com.example.onlinestore.view.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlertDialog
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
import java.util.Calendar


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: CarAdapter
    private val carViewModel by viewModels<CarViewModel>()
    private var currentPage = 1
    private var isLoading = false
    private val localizationDelegate = LocalizationDelegate()
    private var carData: CarData? = null
    var loadDataView: Int? = null
    private var msg: String? = ""
    private var lastMsg = ""

    var imageUrl: String? =null
    private val now = Calendar.getInstance()
    private var mHour = 0
    private var mMinute = 0
    private var scheduleTime: String?=null

    private var scheduledDownloadTime: Long = 0


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
        carViewModel.fetchData(currentPage)
        initViews()
        loadPage()

    }

    override fun onResume() {
        super.onResume()
        initViews()
    }


    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        loadPage()
        binding.rvCarCategory.smoothScrollToPosition(currentPage)
    }


    @SuppressLint("Range")
     fun downloadImage(url: String) {
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
                val cursor:     Cursor = downloadManager.query(query)
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
        var msg = ""
        msg = when (status) {
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        loadPage()
    }


    private fun loadPage() = binding.apply {

        viewLifecycleOwner.lifecycleScope.launch {

            carViewModel.apiResponseStateFlow.collect { response ->
                val meta = response?.optJSONObject("response")?.optJSONObject("result")
                    ?.optJSONObject("meta")
                val apiCurrentPage = meta?.optInt("current_page")
                val apiLastPage = meta?.optInt("last_page")

                rvCarCategory.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                        val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                        val totalItemCount = layoutManager.itemCount

                        if (!isLoading && lastVisibleItemPosition == totalItemCount - 1) {
                            if (apiCurrentPage != null) {
                                if (apiCurrentPage < (apiLastPage ?: 0)) {

                                    isLoading = true
                                    adapter.carList.add(CarData(viewType = LOADER_VIEW))
                                    adapter.notifyDataSetChanged()
                                    currentPage++

                                    lifecycleScope.launch {
                                        delay(2000L)
                                        carViewModel.fetchData(currentPage)
                                    }
                                }
                            }
                        }
                    }
                })
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged", "SuspiciousIndentation")
    private fun initViews() = binding.apply {
        adapter = CarAdapter { onClickCarData ->
            CarDetailFragment.carDataCompanionObject = onClickCarData
            BannerViewPagerAdapter.carDataCompanionObject = onClickCarData
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToCarDetailFragment())

        }
        carRecyclerView()
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
                }
                else alertBox()
        }
        createNotificationChannel()
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun carRecyclerView() = binding.apply {

        viewLifecycleOwner.lifecycleScope.launch {

            carViewModel.apiResponseStateFlow.collect { response ->

                if (currentPage >= 1 && adapter.carList.isNotEmpty()) {
                    adapter.carList.removeAt(adapter.carList.lastIndex)
                    adapter.notifyDataSetChanged()
                }

                val data = response?.optJSONObject("response")?.optJSONObject("result")
                    ?.optJSONArray("cars")


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
                        /*
                                                val detailCarImages: MutableList<String?> = mutableListOf(
                                                    getCarJsonObject?.optJSONArray("media")?.optString(0),
                                                    getCarJsonObject?.optJSONArray("media")?.optString(1),
                                                    getCarJsonObject?.optJSONArray("media")?.optString(2),
                                                    getCarJsonObject?.optJSONArray("media")?.optString(3),
                                                    getCarJsonObject?.optJSONArray("media")?.optString(4)
                                                )*/

                        loadDataView = DATA_VIEW

                        val getDetailImageArray = getCarJsonObject.optJSONArray("media")
                        val detailCarImages = mutableListOf<String?>()

                        if (getDetailImageArray != null) {
                            for (j in 0 until getDetailImageArray.length()) {
                                val image = getDetailImageArray.getString(j)
                                detailCarImages.add(image)
                            }
                        }

                        /*adapter.onItemPass={
                            it.hide()
                       }

                        if(isLoading){
                            loadDataView = LOADER_VIEW
                            adapter.onItemPass = {
                                it.visible()
                            }
                        }*/


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
//                            detailCarImages = Triple(detailCarImages.first,detailCarImages.second,detailCarImages.third),
                            detailCarImages = detailCarImages,
                            viewType = loadDataView
                        )
                        adapter.carList.addAll(listOf(carData))
                        isLoading = false
                    }

                    withContext(Dispatchers.Main) {
                        adapter.notifyDataSetChanged()
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
            .setPositiveButton("Download Now") { dialog, id ->
                imageUrl?.let { downloadImage(it) }
            }.setNeutralButton("Cancel") { dialog, id -> dialog.cancel() }
            .setNegativeButton("Schedule") { dialog, _ ->
                timePicker()
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
            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                AlertDialog.Builder(activity)
                    .setTitle("Permission required")
                    .setMessage("Permission required to save photos from the Web.")
                    .setPositiveButton("Accept") { dialog, _ ->
                        ActivityCompat.requestPermissions(
                            activity, arrayOf(permission),
                            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE)
                    }
                    .setNegativeButton("Deny") { dialog, _ -> dialog.cancel() }
                    .show()
            } else {
                ActivityCompat.requestPermissions(activity, arrayOf(permission), MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE)
            }
        } else {
            alertBox()
        }
    }

/*    private fun downloadDateTimePicker(){
        val calendarFragment = context?.let {
            DatePickerDialog(it,
                { view, year, monthOfYear, dayOfMonth ->
                    var date = (dayOfMonth.toString() + "/"
                            + (monthOfYear + 1) + "/" + year)
                }, currentYear, currentMonth, currentDay
            )
        }

        calendarFragment?.datePicker?.minDate=System.currentTimeMillis()
        calendarFragment?.show()
    }*/

    private fun timePicker() {
        mHour = now[Calendar.HOUR_OF_DAY]
        mMinute = now[Calendar.MINUTE]

        val timePickerDialog = TimePickerDialog(context,
            { view, hourOfDay, minute ->
                mHour = hourOfDay
                mMinute = minute
                 scheduleTime = (" $hourOfDay:$minute")

                scheduleTime?.let { startScheduledDownload(it) }

            }, mHour, mMinute, false
        )
        timePickerDialog.show()


    }


    @SuppressLint("ScheduleExactAlarm")
     fun startScheduledDownload(time : String){
        val scheduledTimeInMillis = calculateScheduledTimeInMillis(time)

        if (scheduledTimeInMillis > System.currentTimeMillis()) {
            scheduledDownloadTime = scheduledTimeInMillis
            scheduleDownloadAtTime(scheduledTimeInMillis)
            toast("Download scheduled successfully at $time")
            Log.d("@@download", "startScheduledDownload: $time")
        } else {
            toast("Invalid scheduled time")
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleDownloadAtTime(timeInMillis: Long) {
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        Log.e("@@scheduled", "$intent " )
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                timeInMillis,
                pendingIntent
            )
            Log.d("@@exactIn", "scheduleDownloadAtTime:>>>>>> ")
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                timeInMillis,
                pendingIntent
            )
            Log.d("@@exactIdle", "scheduleDownloadAtTime: >>>>")
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    fun startDownload(imageUrl: String?) {
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        imageUrl?.let { downloadImage(it) }
    }

    private fun calculateScheduledTimeInMillis(time: String): Long {
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

    private fun createNotificationChannel(){
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

fun Fragment.toast(message: String){
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}