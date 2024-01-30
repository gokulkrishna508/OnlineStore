package com.example.onlinestore.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.onlinestore.R
import com.example.onlinestore.data.repositories.AlarmReceiver
import com.example.onlinestore.databinding.CellCarItemLayoutBinding
import com.example.onlinestore.databinding.CellProgressBarLayoutBinding

class CarAdapter(private val onItemClick: (item: CarData) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var carList: MutableList<CarData?> = mutableListOf()
    var onItemPass: ((item2: View)-> Unit)?= null
    var onDownloadImage: ((position: String)->Unit)?=null
//    var isLoading = false
//    var selectedItemPosition: Int = RecyclerView.NO_POSITION

    companion object {
        var companionObjectAdapter: String? = null
        const val DATA_VIEW = 0
        const val LOADER_VIEW = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = CellCarItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val progressView = CellProgressBarLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return when(viewType){
            DATA_VIEW -> DataViewHolder(view)
            LOADER_VIEW -> ProgressViewHolder(progressView)

            else->throw IllegalArgumentException("Invalid Item Type")
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = carList[position]
        return when(item?.viewType){
           DATA_VIEW -> (holder as DataViewHolder).bind(item)
           LOADER_VIEW -> (holder as ProgressViewHolder).progressBind()

           else -> throw IllegalArgumentException("Invalid Data View")
       }
    }

    override fun getItemCount(): Int {
        return carList.size
    }

     inner class DataViewHolder(private val itemLayoutBinding: CellCarItemLayoutBinding) :
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


                cellCarDataClt.setOnClickListener {
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
                            carImage = item.carImage,
                            viewType = item.viewType
                        )
                    )
                }

                downloadButton.setOnClickListener {
                    item.carImage?.let { onDownloadImage?.invoke( it) }
                    AlarmReceiver.URLTobeDownload = item.carImage
                }
            }
        }
    }

    inner class ProgressViewHolder(private val progressItemLayoutBinding: CellProgressBarLayoutBinding) : RecyclerView.ViewHolder(progressItemLayoutBinding.root) {
        fun progressBind(){
            progressItemLayoutBinding.apply {
                onItemPass?.invoke(progressBar)
                progressBar.progress
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        return carList[position]?.viewType!!
    }

}
