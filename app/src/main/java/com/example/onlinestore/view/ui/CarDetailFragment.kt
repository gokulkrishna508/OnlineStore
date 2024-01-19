package com.example.onlinestore.view.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.onlinestore.data.CarData
import com.example.onlinestore.databinding.FragmentCarDetailBinding

class CarDetailFragment : Fragment() {
    private lateinit var binding: FragmentCarDetailBinding
    companion object{
        var carDataCompanionObject: CarData?= null
        var companionObjectHomeScreen: String?=null
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCarDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() =binding.apply{

        if (companionObjectHomeScreen=="arabic") {
            tvCarName.text = carDataCompanionObject?.name?.second
            tvCarDetails.text = carDataCompanionObject?.carDetails?.second
            doorValue.text = carDataCompanionObject?.doors?.toString()
            seatValue.text = carDataCompanionObject?.seats?.toString()
            gpsValue.text = carDataCompanionObject?.isGps?.second
            blueToothValue.text = carDataCompanionObject?.isBlueTooth?.second
            pricePerDayValue.text = carDataCompanionObject?.rent?.toString()
            totalPaymentValue.text = carDataCompanionObject?.bookingTotalPrice?.toString()
        }else{
            tvCarName.text = carDataCompanionObject?.name?.first
            tvCarDetails.text = carDataCompanionObject?.carDetails?.first
            doorValue.text = carDataCompanionObject?.doors?.toString()
            seatValue.text = carDataCompanionObject?.seats?.toString()
            gpsValue.text = carDataCompanionObject?.isGps?.first
            blueToothValue.text = carDataCompanionObject?.isBlueTooth?.first
            pricePerDayValue.text = carDataCompanionObject?.rent?.toString()
            totalPaymentValue.text = carDataCompanionObject?.bookingTotalPrice?.toString()
        }
    }
}