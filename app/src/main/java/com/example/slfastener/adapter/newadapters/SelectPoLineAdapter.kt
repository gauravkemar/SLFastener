package com.example.slfastener.adapter.newadapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.slfastener.databinding.LineItemSelectionBinding
import com.example.slfastener.model.offlinebatchsave.PoLineItemSelectionModelNewStore

class SelectPoLineAdapter(
    private val items: MutableList<PoLineItemSelectionModelNewStore>,
    private val onItemCheckedChange: (PoLineItemSelectionModelNewStore) -> Unit
) : RecyclerView.Adapter<SelectPoLineAdapter.ItemViewHolder>() {

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
        holder.binding.cbLineItem.setOnCheckedChangeListener(null) // Clear before setting to avoid recursion
        holder.binding.cbLineItem.setOnCheckedChangeListener { _, isChecked ->
            item.isSelected = isChecked
            onItemCheckedChange(item)
            /*if (isChecked) {
                onItemCheckedChange((PoLineItemSelectionModel(
                    item.itemCode,item.itemDescription,
                    item.itemName,item.lineNumber,item.poId,item.poLineItemId,item.poQuantity,
                    item.poUnitPrice,item.posapLineItemNumber,item.pouom,item.poNumber,item.GDPONumber,
                    item.ExpiryDate,item.ReceivedQty,true,item.materialType)))
            } else {
                onItemCheckedChange((PoLineItemSelectionModel(
                    item.itemCode,item.itemDescription,
                    item.itemName,item.lineNumber,item.poId,item.poLineItemId,item.poQuantity,
                    item.poUnitPrice,item.posapLineItemNumber,item.pouom,item.poNumber,item.GDPONumber,
                    item.ExpiryDate,item.ReceivedQty,false,item.materialType)))
            }*/
        }
    }

    override fun getItemCount() = items.size

}