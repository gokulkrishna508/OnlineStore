package com.example.onlinestore.data.repositories

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.onlinestore.R
import com.example.onlinestore.data.CarData
import com.example.onlinestore.databinding.CellViewPagerImageViewBinding
import com.example.onlinestore.view.ui.HomeFragment

class BannerViewPagerAdapter(val onItemClick: ((item: CarData) -> Unit)) : RecyclerView.Adapter<BannerViewPagerAdapter.ViewHolder>() {

    var bannerImageList: MutableList<String>? = mutableListOf()
    companion object {
        var carDataCompanionObject: CarData? = null
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewPagerAdapter.ViewHolder {
        val view = CellViewPagerImageViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: BannerViewPagerAdapter.ViewHolder, position: Int) {
        val item = bannerImageList?.get(position)?:""
        holder.bindBanner(item)
        HomeFragment.positionInvoke=position
    }

    override fun getItemCount(): Int {
        return bannerImageList?.size?:0
    }

    inner class ViewHolder(private val itemBinding: CellViewPagerImageViewBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindBanner(item: String) {
            itemBinding.apply {
                val detailImage = carDataCompanionObject?.detailCarImages
                Log.e("@@detailImage", "$detailImage" )
                Glide.with(bannerImageView).load(item)
                    .placeholder(R.drawable.car_img2).into(bannerImageView)
            }
        }
    }

}