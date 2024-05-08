package com.example.slfastener.adapter.newadapters

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.slfastener.databinding.LineItemSelectionBinding
import com.example.slfastener.model.offlinebatchsave.PoLineItemSelectionModelNewStore

class SelectPoLineAdapter(
    private val items: MutableList<PoLineItemSelectionModelNewStore>,
    private val onItemCheckedChange: (PoLineItemSelectionModelNewStore) -> Unit,
    private val onGdpoAdded: (PoLineItemSelectionModelNewStore) -> Unit,
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

        setGDPO(holder,item)
        if(   holder.binding.edGDPONo.visibility== View.VISIBLE)
        {
            holder.binding.edGDPONo.addTextChangedListener(object : TextWatcher {

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    // Not needed, but required to implement
                }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // Your logic when text changes
                }

                override fun afterTextChanged(s: Editable?) {
                    updateTheGdpoNoForSamePoNumber(s,item)
                }
            })
        }
    }

    private fun updateTheGdpoNoForSamePoNumber(s: Editable?, item: PoLineItemSelectionModelNewStore) {
        onGdpoAdded(item)
    }

    private fun setGDPO(holder:ItemViewHolder, item: PoLineItemSelectionModelNewStore) {
        if (item.currency.equals("INR"))
        {
            holder.binding.edGDPONo.visibility= View.GONE
        }
        else
        {
            holder.binding.edGDPONo.visibility= View.VISIBLE
        }



    }

    override fun getItemCount() = items.size

}