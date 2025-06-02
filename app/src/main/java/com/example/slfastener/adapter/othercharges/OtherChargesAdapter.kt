package com.example.slfastener.adapter.othercharges

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.slfastener.R
import com.example.slfastener.model.BatchInfoListModel
import com.example.slfastener.model.othercharges.GetOtherChargesItem
import com.google.android.material.card.MaterialCardView

class OtherChargesAdapter   (
    private val getOtherChargesItem:  MutableList<GetOtherChargesItem>,
    private val context: Context,

) : RecyclerView.Adapter<OtherChargesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.other_charges_item_layout, parent, false)
        return  ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemPosition = holder.layoutPosition
        val otherChargesMod = getOtherChargesItem[itemPosition]

        holder.poNumber.setText(otherChargesMod.poNumber)
        holder.expenseCode.setText(otherChargesMod.expenseCode)
        holder.expenseName.setText(otherChargesMod.expenseName)
        holder.taxCode.setText(otherChargesMod.taxCode)
/*        val taxValue = otherChargesMod.taxCodes?.get(otherChargesMod.taxCode) ?: 0.0
        var taxAmount=otherChargesMod.expenseAmount + (taxValue/otherChargesMod.expenseAmount * 100)
        getOtherChargesItem[itemPosition].calculatedExpenseAmount=taxAmount.toString()
        notifyItemChanged(itemPosition)*/
        holder.expenseAmount.setText(otherChargesMod.calculatedExpenseAmount)

    }



    override fun getItemCount(): Int {
        if (getOtherChargesItem.size == 0) {
            //Toast.makeText(context,"List is empty", Toast.LENGTH_LONG).show()
        } else {
            return getOtherChargesItem.size
        }
        return getOtherChargesItem.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val poNumber: TextView = itemView.findViewById(R.id.poNumber)
        val expenseCode: TextView = itemView.findViewById(R.id.expenseCode)
        val expenseName: TextView = itemView.findViewById(R.id.expenseName)
        val taxCode: TextView = itemView.findViewById(R.id.taxCode)
        val expenseAmount: TextView = itemView.findViewById(R.id.expenseAmount)
    }

}