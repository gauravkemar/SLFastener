package com.example.slfastener.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.slfastener.R
import com.example.slfastener.model.GetPOsAndLineItemsOnPOIdsResponse
import com.example.slfastener.model.GetSuppliersPOsDDLResponse
import com.example.slfastener.model.PoLineItem
import com.example.slfastener.model.PoLineItemSelectionModel
import com.example.slfastener.view.CreateBatchesActivity
import com.google.android.material.card.MaterialCardView

class GrnMainAddAdapter :RecyclerView.Adapter<GrnMainAddAdapter.ViewHolder>() {

    private var poLineItemMainModel = mutableListOf<PoLineItemSelectionModel>()
    private var context: Context?=null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.grn_add_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var poLineItemModel: PoLineItemSelectionModel =poLineItemMainModel.get(position)

        holder.tvPoNo.setText(poLineItemModel.poNumber)
        holder.tvGdPoNo.setText(poLineItemModel.GDPONumber)
        holder.tvLineItemDesc.setText(poLineItemModel.itemName)
        holder.tvItemDesc.setText(poLineItemModel.itemDescription)
        holder.tvPuom.setText(poLineItemModel.pouom)
        holder.tvEpiryDt.setText(poLineItemModel.ExpiryDate)
        holder.tvQty.setText(poLineItemModel.ReceivedQty)
        holder.tvQuantityPrice.setText(poLineItemModel.poUnitPrice.toString())
        if(poLineItemModel.poQuantity.toString()!=null)
        {
            holder.tvOpenQuantity.setText( poLineItemModel.poQuantity.toString())
        }

        holder.btCreatebatches.setOnClickListener {
            onItemClickListener?.invoke(poLineItemMainModel[position])
        }


    }
    private fun listsAreEqual(list1: List<PoLineItemSelectionModel>, list2: List<PoLineItemSelectionModel>): Boolean {
        if (list1.size != list2.size) {
            return false
        }
        for (i in list1.indices) {
            if (list1[i] != list2[i]) {
                return false
            }
        }
        return true
    }
    fun updateList(newList: ArrayList<PoLineItemSelectionModel>,  context: Context,) {
        if (!listsAreEqual(poLineItemMainModel, newList)) {
            poLineItemMainModel = newList
            this.context=context
            notifyDataSetChanged()
        }
    }
    override fun getItemCount(): Int {
        if(poLineItemMainModel.size==0){
            //Toast.makeText(context,"List is empty", Toast.LENGTH_LONG).show()
        }else{
            return poLineItemMainModel.size
        }
        return poLineItemMainModel.size
    }
    class ViewHolder (ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val tvPoNo: TextView = itemView.findViewById(R.id.tvPoNo)
        val tvGdPoNo: TextView = itemView.findViewById(R.id.tvGdPoNo)
        val tvLineItemDesc: TextView = itemView.findViewById(R.id.tvLineItemDesc)
        val tvItemDesc: TextView = itemView.findViewById(R.id.tvItemDesc)

        val tvPuom: TextView = itemView.findViewById(R.id.tvPuom)
        val tvQty: TextView = itemView.findViewById(R.id.tvQty)
        val tvEpiryDt: TextView = itemView.findViewById(R.id.tvEpiryDt)
        val tvOpenQuantity: TextView = itemView.findViewById(R.id.tvOpenQty)
        val btCreatebatches: MaterialCardView = itemView.findViewById(R.id.btCreatebatches)
        val tvQuantityPrice: TextView = itemView.findViewById(R.id.tvQuantityPrice)

    }
    private var onItemClickListener: ((PoLineItemSelectionModel) -> Unit)? = null


    fun setOnItemCheckClickListener(listener: (PoLineItemSelectionModel) -> Unit) {
        onItemClickListener = listener
    }

}