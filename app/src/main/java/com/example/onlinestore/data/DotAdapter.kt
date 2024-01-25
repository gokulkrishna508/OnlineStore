package com.example.onlinestore.data

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.onlinestore.R
import com.example.onlinestore.databinding.CellLayoutCircleIndicatorBinding

class DotAdapter(): RecyclerView.Adapter<DotAdapter.ViewHolder>(){
    var dotList: MutableList<Dots> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DotAdapter.ViewHolder {
        val view = CellLayoutCircleIndicatorBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: DotAdapter.ViewHolder, position: Int) {
        val item = dotList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return dotList.size
    }

    class ViewHolder(val itemBinding: CellLayoutCircleIndicatorBinding): RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(item: Dots){

            if(!item.isSelected){
                itemBinding.circleIndicator.setImageResource(R.drawable.circle_indicator)
            }

            if (item.isSelected){
                itemBinding.circleIndicator.setImageResource(R.drawable.circle_selected_indicator)
            }
        }
    }

}