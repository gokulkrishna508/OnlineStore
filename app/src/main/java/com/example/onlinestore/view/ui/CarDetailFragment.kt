package com.example.onlinestore.view.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.onlinestore.R
import com.example.onlinestore.data.CarData
import com.example.onlinestore.data.repositories.BannerViewPagerAdapter
import com.example.onlinestore.databinding.FragmentCarDetailBinding


class CarDetailFragment : Fragment() {
    private lateinit var binding: FragmentCarDetailBinding
    private lateinit var viewPagerAdapter: BannerViewPagerAdapter
    private lateinit var viewPager: ViewPager2
    private var dotsCount = 0
    companion object {
        var carDataCompanionObject: CarData? = null
        var companionObjectHomeScreen: String? = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCarDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager = binding.bannerViewPager
        viewPagerAdapter = BannerViewPagerAdapter {}
        viewPager.adapter = viewPagerAdapter
        bannerImageView()
        initViews()
    }

    private fun initViews() = binding.apply {

        if (companionObjectHomeScreen == "arabic") {
            tvCarName.text = carDataCompanionObject?.name?.second
            tvCarDetails.text = carDataCompanionObject?.carDetails?.second
            doorValue.text = carDataCompanionObject?.doors?.toString()
            seatValue.text = carDataCompanionObject?.seats?.toString()
            gpsValue.text = carDataCompanionObject?.isGps?.second
            blueToothValue.text = carDataCompanionObject?.isBlueTooth?.second
            pricePerDayValue.text = carDataCompanionObject?.rent?.toString()
            totalPaymentValue.text = carDataCompanionObject?.bookingTotalPrice?.toString()

        } else {
            tvCarName.text = carDataCompanionObject?.name?.first
            tvCarDetails.text = carDataCompanionObject?.carDetails?.first
            doorValue.text = carDataCompanionObject?.doors?.toString()
            seatValue.text = carDataCompanionObject?.seats?.toString()
            gpsValue.text = carDataCompanionObject?.isGps?.first
            blueToothValue.text = carDataCompanionObject?.isBlueTooth?.first
            pricePerDayValue.text = carDataCompanionObject?.rent?.toString()
            totalPaymentValue.text = carDataCompanionObject?.bookingTotalPrice?.toString()
        }

        carDetailFavouriteIcon.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    @SuppressLint("SuspiciousIndentation")
    fun bannerImageView() = binding.apply {

        val carData: CarData? = carDataCompanionObject?.detailCarImages?.let { CarData(detailCarImages = it) }
        Log.d("@@userViewPageImage", "${carData}")
        viewPagerAdapter.bannerImageList =carData?.detailCarImages?.map { it.toString() }?.toMutableList()?: mutableListOf()

        bannerViewPager.adapter= viewPagerAdapter
        bannerViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        Log.e("@@size", "${viewPagerAdapter.bannerImageList!!.size}")

        dotsCount =viewPagerAdapter.itemCount

        createDotIndicator(dotsCount)

        bannerViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateDotIndicator(position)

            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
            }
        })
    }

    private fun createDotIndicator(count: Int)= binding.apply{
        for (i in 0 until count){
            val dot = ImageView(context)
            dot.setImageResource(R.drawable.circle_indicator)
            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            params.setMargins(8, 0, 8, 0)
            sliderDots.addView(dot,params)
        }
    }

    private fun updateDotIndicator(position: Int) = binding.apply {
        for (i in 0 until dotsCount) {
            val dot = sliderDots.getChildAt(i) as ImageView

            if(i==position){
                dot.setImageResource(R.drawable.circle_selected_indicator)
            }

            if(i!=position){
                dot.setImageResource(R.drawable.circle_indicator)
            }

        }

    }
}