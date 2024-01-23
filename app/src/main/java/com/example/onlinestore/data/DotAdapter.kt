package com.example.onlinestore.data

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.onlinestore.databinding.CellLayoutCircleIndicatorBinding

class DotAdapter(): RecyclerView.Adapter<DotAdapter.ViewHolder>() {

     var circleIndicatorList: MutableList<CarData> = mutableListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DotAdapter.ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: DotAdapter.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        return circleIndicatorList.size
    }

    class ViewHolder(val itemBinding: CellLayoutCircleIndicatorBinding): RecyclerView.ViewHolder(itemBinding.root) {


    }
}