package com.example.onlinestore.view.ui

import android.annotation.SuppressLint
import android.icu.text.Transliterator.Position
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.onlinestore.data.repositories.BannerViewPagerAdapter
import com.example.onlinestore.databinding.FragmentHomeBinding
import com.example.onlinestore.view_model.CarViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: CarAdapter
    private val carViewModel by viewModels<CarViewModel>()
    private var currentPage = 1
    private var isLoading = false
    private val localizationDelegate = LocalizationDelegate()
    private var carData: CarData? = null
    var loadDataView: Int?= null

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

    @SuppressLint("NotifyDataSetChanged")
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
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun carRecyclerView() = binding.apply {

        viewLifecycleOwner.lifecycleScope.launch {

            carViewModel.apiResponseStateFlow.collect { response ->

                    if(currentPage >= 1 && adapter.carList.isNotEmpty()){
                        adapter.carList.removeAt(adapter.carList.lastIndex)
                        adapter.notifyDataSetChanged()
                    }

                val data = response?.optJSONObject("response")?.optJSONObject("result")?.optJSONArray("cars")


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

                        val detailCarImages: Triple<String?, String?, String?> = Triple(
                            getCarJsonObject?.optJSONArray("media")?.optString(1),
                            getCarJsonObject?.optJSONArray("media")?.optString(2),
                            getCarJsonObject?.optJSONArray("media")?.optString(3)
                        )

                       loadDataView = DATA_VIEW
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
                            detailCarImages = Triple(detailCarImages.first,detailCarImages.second,detailCarImages.third),
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
}

fun View.visible(){
    this.visibility = View.VISIBLE
}

fun View.hide(){
    this.visibility = View.GONE
}