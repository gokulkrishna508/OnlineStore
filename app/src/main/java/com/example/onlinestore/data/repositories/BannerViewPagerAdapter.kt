package com.example.onlinestore.data.repositories

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.onlinestore.R
import com.example.onlinestore.data.CarData
import com.example.onlinestore.databinding.CellViewPagerImageViewBinding

class BannerViewPagerAdapter(val onItemClick: ((item: CarData) -> Unit)) : RecyclerView.Adapter<BannerViewPagerAdapter.ViewHolder>() {

    var bannerImageList: MutableList<CarData> = mutableListOf()
    companion object {
        var carDataCompanionObject: CarData? = null
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewPagerAdapter.ViewHolder {
        val view = CellViewPagerImageViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: BannerViewPagerAdapter.ViewHolder, position: Int) {
        val item = bannerImageList[position]
        holder.bindBanner(item)
    }

    override fun getItemCount(): Int {
        return bannerImageList.size
    }

    inner class ViewHolder(val itemBinding: CellViewPagerImageViewBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindBanner(item: CarData) {
            itemBinding.apply {
                val detailImage = carDataCompanionObject?.detailCarImages
                Log.e("@@detailImage", "$detailImage " )
                Glide.with(bannerImageView).load(detailImage)
                    .placeholder(R.drawable.car_img2).into(bannerImageView)
            }
        }
    }

}