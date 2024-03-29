package com.example.slfastener.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.slfastener.R
import com.example.slfastener.model.GetPOsAndLineItemsOnPOIdsResponse
import com.example.slfastener.model.PoLineItem
import com.example.slfastener.model.PoLineItemSelectionModel

class GrnMainAddAdapter :RecyclerView.Adapter<GrnMainAddAdapter.ViewHolder>() {

    private var poLineItemMainModel = mutableListOf<PoLineItemSelectionModel>()
    private var context: Context?=null
    fun setGrnMainList(
        poLineItemMainModel: ArrayList<PoLineItemSelectionModel>,
        context: Context,
    ) {
        this.poLineItemMainModel =poLineItemMainModel
        this.context=context
        notifyDataSetChanged()
    }

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
        holder.tvOpenQuantity.setText(poLineItemModel.poQuantity)


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
        val tvBatchNo: TextView = itemView.findViewById(R.id.tvBatchNo)
        val tvOpenQuantity: TextView = itemView.findViewById(R.id.tvasdasd)
        //val tvBarcodeNo: TextView = itemView.findViewById(R.id.tvBarcodeNo)

    }

}