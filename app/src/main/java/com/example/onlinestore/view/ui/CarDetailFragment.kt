package com.example.onlinestore.view.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.onlinestore.LocalizedApp
import com.example.onlinestore.R
import com.example.onlinestore.data.CarData
import com.example.onlinestore.data.DotAdapter
import com.example.onlinestore.data.Dots
import com.example.onlinestore.data.repositories.BannerViewPagerAdapter
import com.example.onlinestore.databinding.FragmentCarDetailBinding
import java.net.URL
import java.util.Locale


class CarDetailFragment : Fragment() {
    private lateinit var binding: FragmentCarDetailBinding
    private lateinit var viewPagerAdapter: BannerViewPagerAdapter
    private lateinit var viewPager: ViewPager2
    private var dotsCount = 0
    private var dotAdapter: DotAdapter = DotAdapter()
    private val localizationDelegate = LocalizationDelegate()
    var flag = false

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
        binding.apply {
            LocalizedApp.localeLiveData.observe(viewLifecycleOwner,localizationDelegate)
            viewPager = bannerViewPager
            viewPagerAdapter = BannerViewPagerAdapter {}
            viewPager.adapter = viewPagerAdapter
            initViews()
            bannerImageView()
        }
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
            LocalizedApp.updateLocale(LocalizedApp.LOCALE_AR)

        } else {
            tvCarName.text = carDataCompanionObject?.name?.first
            tvCarDetails.text = carDataCompanionObject?.carDetails?.first
            doorValue.text = carDataCompanionObject?.doors?.toString()
            seatValue.text = carDataCompanionObject?.seats?.toString()
            gpsValue.text = carDataCompanionObject?.isGps?.first
            blueToothValue.text = carDataCompanionObject?.isBlueTooth?.first
            pricePerDayValue.text = carDataCompanionObject?.rent?.toString()
            totalPaymentValue.text = carDataCompanionObject?.bookingTotalPrice?.toString()
            LocalizedApp.updateLocale(LocalizedApp.LOCALE_EN)
        }

        backArrowButton.setOnClickListener {
            findNavController().navigateUp()
        }

        zoomButton.setOnClickListener {
            findNavController().navigate(CarDetailFragmentDirections.actionCarDetailFragmentToZoomImageFragment())
        }

        carDetailFavouriteIcon.setOnClickListener {
            flag = !flag
            if(flag){
                context?.let { it1 -> ContextCompat.getColor(it1, R.color.text_red) }
                    ?.let { it2 -> carDetailFavouriteIcon.setColorFilter(it2, android.graphics.PorterDuff.Mode.MULTIPLY) }
            }
            else {
                context?.let { it1 -> ContextCompat.getColor(it1, R.color.box_border_grey) }
                    ?.let { it2 -> carDetailFavouriteIcon.setColorFilter(it2, android.graphics.PorterDuff.Mode.MULTIPLY) }
            }
        }
    }

    @SuppressLint("SuspiciousIndentation", "NotifyDataSetChanged")
    fun bannerImageView() = binding.apply {

        val carData: CarData? = carDataCompanionObject?.detailCarImages?.let { CarData(detailCarImages = it) }
        Log.d("@@userViewPageImage", "$carData")
        viewPagerAdapter.bannerImageList = carData?.detailCarImages?.map { it.toString() }?.toMutableList()?: mutableListOf()

        bannerViewPager.adapter= viewPagerAdapter
        bannerViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        dotsCount = viewPagerAdapter.itemCount

        if(dotAdapter.dotList.isEmpty()){
            for (i in 0 until dotsCount){
                dotAdapter.dotList.addAll(mutableListOf(Dots(i,false)))
            }

            val currentItem = bannerViewPager.currentItem
            dotAdapter.dotList[currentItem].isSelected = true
        }

//        createDotIndicator(dotsCount)

        bannerViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)

            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                dotAdapter.dotList.forEach { it.isSelected = false }
                dotAdapter.dotList[position].isSelected = true
                dotAdapter.notifyDataSetChanged()

                if(dotAdapter.dotList[position].isSelected ){
                    val getCurrentViewImage = viewPagerAdapter.bannerImageList!![position]
                    Log.e("@@currentItem", getCurrentViewImage)
                    ZoomImageFragment.zoomImageCompanionObject=getCurrentViewImage
                }

//                updateDotIndicator(position)
            }


            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)

            }
        })

        rvViewPagerIcon.adapter = dotAdapter

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

    fun loadIndicator() =  binding.apply{
        detailSubClt.hide()
        progressBar.visible()
    }

    fun loadIndicatorHide() = binding.apply{
        detailSubClt.visible()
        progressBar.hide()
    }
    private inner class LocalizationDelegate : Observer<String> {
        override fun onChanged(value: String) {
            val layoutDirection = LocalizedApp.getLayoutDirection(value)

            binding.apply {
                carDetailsClt.layoutDirection = layoutDirection
            }

        }

    }


}