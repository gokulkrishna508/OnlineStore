package com.example.onlinestore.view.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.onlinestore.R
import com.example.onlinestore.data.CarData
import com.example.onlinestore.data.repositories.BannerViewPagerAdapter
import com.example.onlinestore.databinding.FragmentCarDetailBinding

class CarDetailFragment : Fragment() {
    private lateinit var binding: FragmentCarDetailBinding
    private lateinit var viewPagerAdapter: BannerViewPagerAdapter
    private lateinit var viewPager: ViewPager2
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
        viewPager = binding.bannerViewPager
        viewPagerAdapter = BannerViewPagerAdapter{}
        viewPager.adapter = viewPagerAdapter
        bannerImageView()
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

    @SuppressLint("SuspiciousIndentation")
    fun bannerImageView()= binding.apply {

        var carData: CarData?=null
        carData= carDataCompanionObject?.detailCarImages?.let {
            CarData(
                detailCarImages = it
            )
        }

        Log.d("@@userViewPageImage", "$carData")

            viewPagerAdapter.bannerImageList = carData?.let { mutableListOf(it) }!!

            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                }

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                }

                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                }
            })
    }

}