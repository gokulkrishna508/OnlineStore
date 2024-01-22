package com.example.onlinestore.data

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.onlinestore.R
import com.example.onlinestore.databinding.CellCarItemLayoutBinding
import com.example.onlinestore.databinding.CellProgressBarLayoutBinding

class CarAdapter(private val onItemClick: (item: CarData) -> Unit) :
    RecyclerView.Adapter<CarAdapter.ViewHolder>() {
    var carList: MutableList<CarData?> = mutableListOf()
    var isLoading = false
    companion object {
        var companionObjectAdapter: String? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarAdapter.ViewHolder {
        val view = CellCarItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val progressView = CellProgressBarLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return if(viewType==1){
             ViewHolder(view)
        }else{
            ViewHolder(view)
        }

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

    inner class ViewHolder(private val itemLayoutBinding: CellCarItemLayoutBinding) :
        RecyclerView.ViewHolder(itemLayoutBinding.root) {
        fun bind(item: CarData) {
            itemLayoutBinding.apply {
                Glide.with(carImageView)
                    .load(item.carImage)
                    .placeholder(R.drawable.car_img2)
                    .into(carImageView)

                if (companionObjectAdapter == "arabic") {
                    tvCarName.text = item.name.second
                    tvGearType.text = item.gearType.second
                    doorsValue.text = item.doors.toString()
                    seatValue.text = item.seats.toString()
                    amountPerDay.text = item.rent.toString()
                    fuelType.text = item.fuelType.toString()

                } else {
                    tvCarName.text = item.name.first
                    tvGearType.text = item.gearType.first
                    doorsValue.text = item.doors.toString()
                    seatValue.text = item.seats.toString()
                    amountPerDay.text = item.rent.toString()
                    fuelType.text = item.fuelType.toString()
                }


                root.setOnClickListener {
                    onItemClick.invoke(
                        CarData(
                            id = item.id,
                            name = item.name,
                            gearType = item.gearType,
                            doors = item.doors,
                            seats = item.seats,
                            carDetails = item.carDetails,
                            fuelType = item.fuelType,
                            rent = item.rent,
                            isBlueTooth = item.isBlueTooth,
                            isGps = item.gearType,
                            bookingTotalPrice = item.bookingTotalPrice,
                            detailCarImages = item.detailCarImages,
                            carImage = item.carImage
                        )
                    )
                }
            }
        }
    }

    inner class progressViewHolder(val progressItemLayoutBinding: CellProgressBarLayoutBinding) : RecyclerView.ViewHolder(progressItemLayoutBinding.root) {
        fun progressBind(item: CarData){
            progressItemLayoutBinding.apply {
                progressBar.progress= item.progressBar!!
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(carList[position]){
            is CarData -> 0
            else -> 1

        }

    }

}
