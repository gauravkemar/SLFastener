package com.example.slfastener.adapter.completedgrn

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.slfastener.databinding.LineItemSelectionBinding
import com.example.slfastener.model.offlinebatchsave.CustomPoLineItemSelectionModel

class SelectPoLineCompletedAdapter(
    private val items: MutableList<CustomPoLineItemSelectionModel>,
) : RecyclerView.Adapter<SelectPoLineCompletedAdapter.ItemViewHolder>() {

    class ItemViewHolder(val binding: LineItemSelectionBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = LineItemSelectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]

        holder.binding.tvColumnOne.setText(item.posapLineItemNumber)
        holder.binding.tvColumnTwo.setText(item.itemCode)
        holder.binding.tvColumnThree.setText(item.itemName)
        holder.binding.tvColumnFour.setText(item.itemDescription)
        if(item.poqty.toString()!=null)
        {
            holder.binding.tvColumnFive.setText(item.poqty.toString())
        }
        holder.binding.tvColumnSix.setText(item.pouom)

        holder.binding.cbLineItem.isChecked = item.isSelected
        holder.binding.cbLineItem.isEnabled=false


    }


    override fun getItemCount() = items.size

}