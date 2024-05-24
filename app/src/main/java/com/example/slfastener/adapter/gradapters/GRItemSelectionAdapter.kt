package com.example.slfastener.adapter.gradapters

import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.slfastener.databinding.LineItemSelectionBinding
import com.example.slfastener.databinding.SelectGrItemBinding
import com.example.slfastener.model.goodsreceipt.GetAllItemMasterResponse
import com.example.slfastener.model.goodsreceipt.GetAllItemMasterSelection
import com.example.slfastener.model.offlinebatchsave.PoLineItemSelectionModelNewStore

class GRItemSelectionAdapter  (
    private val items: MutableList<GetAllItemMasterSelection>,
    private val onItemCheckedChange: (GetAllItemMasterSelection) -> Unit,
) : RecyclerView.Adapter<GRItemSelectionAdapter.ItemViewHolder>() {

    class ItemViewHolder(val binding: SelectGrItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = SelectGrItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]

        holder.binding.tvColumnOne.setText(item.code)
        holder.binding.tvColumnTwo.setText(item.name)
        holder.binding.tvColumnThree.setText(item.description)
        holder.binding.tvColumnFour.setText(item.uom)
        holder.binding.tvColumnFive.setText(item.mhType)
        holder.binding.cbLineItem.isChecked = item.isSelected
        holder.binding.cbLineItem.setOnCheckedChangeListener(null) // Clear before setting to avoid recursion
        holder.binding.cbLineItem.setOnCheckedChangeListener { _, isChecked ->
            item.isSelected = isChecked
            onItemCheckedChange(item)
        }

    }
    override fun getItemCount() = items.size

}