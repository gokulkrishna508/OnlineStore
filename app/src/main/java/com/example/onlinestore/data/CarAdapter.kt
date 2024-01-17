package com.example.onlinestore.data

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.onlinestore.R
import com.example.onlinestore.databinding.CellCarItemLayoutBinding

class CarAdapter(private val onItemClick: (item: CarData) -> Unit) : RecyclerView.Adapter<CarAdapter.ViewHolder>() {

    var carList: MutableList<CarData?> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarAdapter.ViewHolder {
       val view = CellCarItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarAdapter.ViewHolder, position: Int) {
       val item = carList[position]
        if (item != null) {
            holder.bind(item)
        }
    }

    override fun getItemCount(): Int {
        return carList.size
    }

    inner class ViewHolder( val itemLayoutBinding: CellCarItemLayoutBinding) : RecyclerView.ViewHolder(itemLayoutBinding.root) {
        fun bind(item: CarData) {
            itemLayoutBinding.apply {
                Glide.with(carImageView)
                    .load(item.carImage)
                    .placeholder(R.drawable.car_img2)
                    .into(carImageView)

                tvCarName.text = item.name
                tvGearType.text = item.gearType
                doorsValue.text = item.doors.toString()
                seatValue.text = item.seats.toString()
                amountPerDay.text = item.rent.toString()
                fuelType.text = item.fuelType.toString()
            }
        }
    }
}