package com.example.onlinestore.view.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.onlinestore.data.CarAdapter
import com.example.onlinestore.data.CarData
import com.example.onlinestore.databinding.FragmentHomeBinding
import com.example.onlinestore.view_model.CarViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: CarAdapter
    private val carViewModel by viewModels<CarViewModel>()
    private var currentPage = 1
    private var isLoading = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadPage()
        carViewModel.fetchData(currentPage)
        initViews()
    }

    override fun onResume() {
        super.onResume()
    }

    private fun loadPage() = binding.apply {

        viewLifecycleOwner.lifecycleScope.launch {
            carViewModel.apiResponseStateFlow.collect { response ->
                val meta = response?.optJSONObject("response")?.optJSONObject("result")
                    ?.optJSONObject("meta")
                val apiCurrentPage = meta?.optInt("current_page")
                val apiLastPage = meta?.optInt("last_page")


                rvCarCategory.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                        val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                        val totalItemCount = layoutManager.itemCount

                        if (!isLoading && lastVisibleItemPosition == totalItemCount - 1) {
                            if (apiCurrentPage != null) {
                                if (apiCurrentPage <= apiLastPage!!) {
                                    isLoading = true
                                    currentPage++
                                    carViewModel.fetchData(currentPage)
                                }
                            }
                        }

                    }
                })
            }
        }
    }

    private fun initViews() = binding.apply {
        adapter = CarAdapter {}
        carRecyclerView()
    }


    private fun carRecyclerView() = binding.apply {
        var carData: CarData? = null

        viewLifecycleOwner.lifecycleScope.launch {


            carViewModel.apiResponseStateFlow.collect { response ->
                val data = response?.optJSONObject("response")?.optJSONObject("result")
                    ?.optJSONArray("cars")
                if (data != null) {

                    for (i in 0 until data.length()) {

                        val getCarJsonObject = data.optJSONObject(i)
                        val brand: Pair<String, String> = Pair(
                            getCarJsonObject.optString("branch_en"),
                            getCarJsonObject.optString("branch_ar")
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


                        carData = CarData(
                            id = getCarJsonObject.optInt("id"),
                            name = Pair(brand.first, brand.second),
                            carImage = getCarJsonObject?.optString("cover_media"),
                            gearType = Pair(transmission.first, transmission.second),
                            doors = getCarJsonObject?.optInt("door_count"),
                            seats = getCarJsonObject?.optInt("seating_capacity"),
                            rent = getCarJsonObject?.optInt("booking_total_price"),
                            fuelType = getCarJsonObject?.optString("fuel_en"),
                            carDetails = Pair(model.first, model.second),
                            isBlueTooth = Pair(blueTooth.first,blueTooth.second),
                            isGps = Pair(gps.first,gps.second)
                        )
                        adapter.carList.addAll(listOf(carData))
                        isLoading = false
                        adapter.notifyDataSetChanged()
                        Log.d("@@HomeCardata", "$carData")
                    }
                    withContext(Dispatchers.Main) {
                        rvCarCategory.adapter = adapter
                    }

                }
            }
        }

    }


}