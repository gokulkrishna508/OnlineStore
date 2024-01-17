package com.example.onlinestore.view.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.onlinestore.data.CarAdapter
import com.example.onlinestore.data.CarData
import com.example.onlinestore.databinding.FragmentHomeBinding
import com.example.onlinestore.view_model.CarViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: CarAdapter
    private val carViewModel by viewModels<CarViewModel>()
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
        initViews()

    }

    override fun onResume() {
        super.onResume()
        carViewModel.fetchData()
        initViews()
    }

    private fun initViews()=binding.apply{
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
                        carData = CarData(
                            id = getCarJsonObject.optInt("id"),
                            name = getCarJsonObject.optString("model_en"),
                            carImage = getCarJsonObject?.optString("cover_media"),
                            gearType = getCarJsonObject?.optString("transmission_en"),
                            doors = getCarJsonObject?.optInt("door_count"),
                            seats = getCarJsonObject?.optInt("seating_capacity"),
                            rent = getCarJsonObject?.optInt("booking_total_price"),
                            fuelType = getCarJsonObject?.optString("fuel_en"),
                            carDetails = getCarJsonObject?.optString("model_en"),
                            isBlueTooth = getCarJsonObject?.optJSONArray("facilities")?.optJSONObject(2)?.optString("name_en"),
                            isGps = getCarJsonObject?.optJSONArray("facilities")?.optJSONObject(1)?.optString("name_en")
                        )
                        adapter.carList= mutableListOf(carData)
                        adapter.notifyDataSetChanged()
                        Log.d("@@HomeCardata", "$carData")
                        Log.e("@@HomeCardata22", "${adapter.carList}")
                    }
                    withContext(Dispatchers.Main){
                        rvCarCategory.adapter = adapter
                    }

                }
            }
        }

    }


}