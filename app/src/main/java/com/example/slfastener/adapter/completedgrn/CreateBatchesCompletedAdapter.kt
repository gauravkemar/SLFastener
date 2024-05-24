package com.example.slfastener.adapter.completedgrn

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.slfastener.R
import com.example.slfastener.adapter.demoAdapter.CreateBatchesNewSingleList
import com.example.slfastener.helper.CustomKeyboard
import com.example.slfastener.model.offlinebatchsave.GrnLineItemUnitStore
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CreateBatchesCompletedAdapter(
    private val context: Context,
    private val batches: MutableList<GrnLineItemUnitStore>,
    private val onSave: (Int, GrnLineItemUnitStore) -> Unit,
) :
    RecyclerView.Adapter<CreateBatchesCompletedAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.completed_batches_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemPosition = holder.layoutPosition
        val grnLineItemUnit: GrnLineItemUnitStore = batches?.get(itemPosition)!!
        holder.tvSrnNo.setText("${itemPosition + 1}")
        holder.tvBatchNo.setText(grnLineItemUnit.supplierBatchNo)
        holder.tvInternalBatchNo.setText(grnLineItemUnit.internalBatchNo)
        holder.tvUOM.setText(grnLineItemUnit.UOM)
        holder.tvBarcodeLableValue.setText(grnLineItemUnit.barcode)
        holder.tvWeight.setText(grnLineItemUnit.recevedQty)
        holder.edWeight.setText(grnLineItemUnit.recevedQty)

        updateView(holder, grnLineItemUnit)
        holder.ivSave.setOnClickListener {

        }

        if (grnLineItemUnit.UOM.equals("KGS")) {
            holder.tvWeight.visibility = View.VISIBLE
            holder.edWeight.visibility = View.GONE
        } else {

            holder.tvWeight.visibility = View.GONE
            holder.edWeight.visibility = View.VISIBLE
        }

        if (grnLineItemUnit.isExpirable) {
            holder.tvExpiryDate.visibility = View.VISIBLE
            holder.tvExpiryDate.setText(grnLineItemUnit.expiryDate.toString())
        } else {
            holder.tvExpiryDate.visibility = View.GONE
        }
    }

    private fun updateView(holder: ViewHolder, batchInfoItem: GrnLineItemUnitStore) {
        with(holder) {
            if (batchInfoItem.isUpdate) {
                edWeight.setBackgroundColor(Color.LTGRAY)
                tvWeight.setBackgroundColor(Color.LTGRAY)
                holder.edWeight.isFocusable = false
                holder.edWeight.isEnabled = false
            } else {
                edWeight.setBackgroundColor(Color.TRANSPARENT)
                tvWeight.setBackgroundColor(Color.TRANSPARENT)
                holder.edWeight.isFocusable = true
                holder.edWeight.isEnabled = true

            }
        }
    }



    override fun getItemCount(): Int {
        if (batches.size == 0) {
            //Toast.makeText(context,"List is empty", Toast.LENGTH_LONG).show()
        } else {
            return batches.size
        }
        return batches.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val tvSrnNo: TextView = itemView.findViewById(R.id.tvSrnNo)
        val tvBatchNo: TextView = itemView.findViewById(R.id.tvBatchNo)
        val tvInternalBatchNo: TextView = itemView.findViewById(R.id.tvInternalBatchNo)
        val tvWeight: TextView = itemView.findViewById(R.id.tvWeight)
        val edWeight: EditText = itemView.findViewById(R.id.edWeight)
        val tvUOM: TextView = itemView.findViewById(R.id.tvUOM)
        val tvBarcodeLableValue: TextView = itemView.findViewById(R.id.tvBarcodeLableValue)
        val tvExpiryDate: TextView = itemView.findViewById(R.id.tvExpiryDate)
        val ivAdd: ImageButton = itemView.findViewById(R.id.ivAdd)
        val ivSave: ImageButton = itemView.findViewById(R.id.ivSave)
        val ivDelete: ImageButton = itemView.findViewById(R.id.ivDelete)
        val ivMultiAdd: ImageButton = itemView.findViewById(R.id.ivMultiAdd)
        val clCardMain: ConstraintLayout = itemView.findViewById(R.id.clCardMain)
        val mcvWeight: MaterialCardView = itemView.findViewById(R.id.mcvWeight)


    }


}