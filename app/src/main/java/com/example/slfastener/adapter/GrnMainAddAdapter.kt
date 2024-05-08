package com.example.slfastener.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.slfastener.R
import com.example.slfastener.adapter.demoAdapter.CreateBatchesSingleList
import com.example.slfastener.model.PoLineItemSelectionModel
import com.example.slfastener.model.offlinebatchsave.PoLineItemSelectionModelNewStore
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class GrnMainAddAdapter (
    private val poLineItemParent: List<PoLineItemSelectionModelNewStore>,
    ) : RecyclerView.Adapter<GrnMainAddAdapter.ViewHolder>() {
    private var weightData: String? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.grn_add_item, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var poLineItemModel: PoLineItemSelectionModelNewStore = poLineItemParent.get(position)

        holder.tvSrNo.setText("${position+1}")
        holder.tvPoNo.setText(poLineItemModel.poNumber)
        holder.edGDPO.setText(poLineItemModel.GDPONumber.toString())
        holder.tvPoLineNo.setText(poLineItemModel.poLineNo.toString())
        holder.tvItemCode.setText(poLineItemModel.itemCode)
        holder.tvItemDesc.setText(poLineItemModel.itemDescription)
        holder.tvMhType.setText(poLineItemModel.mhType)
        holder.tvPoQty.setText(poLineItemModel.poqty.toString())
        holder.tvBalQty.setText(poLineItemModel.balQTY.toString())
        holder.tvRate.setText(poLineItemModel.unitPrice.toString()?:"")
        holder.tvGRNQty.setText(poLineItemModel.quantityReceived)
        holder.tvBatchCount.setText("${poLineItemModel.grnLineItemUnit?.size ?: 0}")
        holder.tvPuom.setText(poLineItemModel.pouom)


        //holder.tvEpiryDt.setText(poLineItemModel)

       /* holder.tvQuantityPrice.setText(poLineItemModel.poUnitPrice.toString())
        if (poLineItemModel.poQuantity.toString() != null) {
            holder.tvOpenQuantity.setText(poLineItemModel.poQuantity.toString())
        }*/
      /*  holder.itemView.setOnClickListener {
            if(holder.cl2.visibility==View.VISIBLE)
            {
                holder.cl2.visibility=View.GONE
            }
            else{
                holder.cl2.visibility=View.VISIBLE
            }
        }*/


        if(poLineItemModel.grnLineItemUnit!=null)
        {
            holder.tvDeleteLineItem.visibility=View.INVISIBLE
            holder.tvDeleteLineItem.setEnabled(false);
            holder.tvSaveLineItem.setImageResource(R.drawable.ic_save_black)
        }

        holder.tvSaveLineItem.setOnClickListener {
            onItemClickListener?.invoke(position,poLineItemParent[position])
        }
    }
    override fun getItemCount(): Int {
        if (poLineItemParent.size == 0) {
        } else {
            return poLineItemParent.size
        }
        return poLineItemParent.size
    }
    fun updateWeightValue(weightData: String) {
        this.weightData = weightData

        Log.d("weightFromInner",weightData)
    }
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val tvSrNo: TextView = itemView.findViewById(R.id.tvSrNo)
        val tvPoNo: TextView = itemView.findViewById(R.id.tvPoNo)
        val edGDPO: TextView = itemView.findViewById(R.id.edGDPO)
        val tvPoLineNo: TextView = itemView.findViewById(R.id.tvPoLineNo)
        val tvItemCode: TextView = itemView.findViewById(R.id.tvItemCode)
        val tvItemDesc: TextView = itemView.findViewById(R.id.tvItemDesc)
        val tvPuom: TextView = itemView.findViewById(R.id.tvPuom)
        val tvMhType: TextView = itemView.findViewById(R.id.tvMhType)
        val tvPoQty: TextView = itemView.findViewById(R.id.tvPoQty)
        val tvBalQty: TextView = itemView.findViewById(R.id.tvBalQty)
        val tvRate: TextView = itemView.findViewById(R.id.tvRate)
        val tvGRNQty: TextView = itemView.findViewById(R.id.tvGRNQty)
        val tvWareHouse: TextView = itemView.findViewById(R.id.tvWareHouse)
        val tvBatchCount: TextView = itemView.findViewById(R.id.tvBatchCount)
        val tvSaveLineItem: ImageButton = itemView.findViewById(R.id.tvSaveLineItem)
        val tvDeleteLineItem: ImageButton = itemView.findViewById(R.id.tvDeleteLineItem)
    }

    private var onItemClickListener: ((Int,PoLineItemSelectionModelNewStore) -> Unit)? = null
    fun setOnItemCheckClickListener(listener: (Int,PoLineItemSelectionModelNewStore) -> Unit) {
        onItemClickListener = listener
    }

}