package com.example.slfastener.adapter.completedgrn

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.slfastener.R
import com.example.slfastener.model.offlinebatchsave.CustomGrnLineItemUnit
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.Locale

class CreateBatchesCompletedAdapter(
    private val context: Context,
    private val batches: MutableList<CustomGrnLineItemUnit>,
    private val onSave: (Int, CustomGrnLineItemUnit) -> Unit,

    private val onItemCheckedChange: (CustomGrnLineItemUnit) -> Unit,
) :
    RecyclerView.Adapter<CreateBatchesCompletedAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.completed_batches_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemPosition = holder.layoutPosition
        val grnLineItemUnit: CustomGrnLineItemUnit = batches?.get(itemPosition)!!
        holder.tvSrnNo.setText("${itemPosition + 1}")
        holder.tvBatchNo.setText(grnLineItemUnit.supplierBatchNo)
        holder.tvInternalBatchNo.setText(grnLineItemUnit.internalBatchNo)
        holder.tvUOM.setText(grnLineItemUnit.UOM)
        holder.tvBarcodeLableValue.setText(grnLineItemUnit.barcode)
        holder.tvWeight.setText(grnLineItemUnit.recevedQty)
        holder.edWeight.setText(grnLineItemUnit.recevedQty)
        updateView(holder, grnLineItemUnit)
        setMultiSelect(holder, grnLineItemUnit)

        holder.ivDelete.setOnClickListener {
            onSave(position,grnLineItemUnit)
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


            Log.e("inputDateelse","grnLineItemUnit.expiryDate.toString()")
            if (grnLineItemUnit.expiryDate.toString() != "" && grnLineItemUnit.expiryDate.toString() !="null") {
                val inputDate = grnLineItemUnit.expiryDate.toString()
                val inputFormat: SimpleDateFormat
                val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())


                if (inputDate.length == "yyyy-MM-dd'T'HH:mm:ss".length) {
                    Log.e("inputDate.length ",inputDate.length.toString())
                    inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                    val parsedDate = inputFormat.parse(inputDate)
                    val formattedDate = outputFormat.format(parsedDate)
                    holder.tvExpiryDate.setText(formattedDate)
                } else {
                    Log.e("inputDateelse",inputDate.length.toString())
                    inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val parsedDate = inputFormat.parse(inputDate)
                    val formattedDate = outputFormat.format(parsedDate)
                    holder.tvExpiryDate.setText(formattedDate)
                }
            } else {
                Log.e("inputDateelse","outerelse")
                holder.tvExpiryDate.setText("")
            }

        } else {
            holder.tvExpiryDate.visibility = View.GONE
        }



    }

    private fun setMultiSelect(
        holder: ViewHolder,
        grnLineItemUnit: CustomGrnLineItemUnit
    )
    {
        if(grnLineItemUnit.isUpdate)
        {
            if(
                (grnLineItemUnit.UOM!!.contains("Number",ignoreCase = true) ||
                        grnLineItemUnit.UOM .contains("PCS",ignoreCase = true) )
                && grnLineItemUnit.mhType.contains("Batch",ignoreCase = true))
            {
                holder.cbBatchesLineUnitItem.visibility=View.VISIBLE
                holder.cbBatchesLineUnitItem.isChecked = grnLineItemUnit.isChecked
                holder.cbBatchesLineUnitItem.setOnCheckedChangeListener(null) // Clear before setting to avoid recursion
                holder.cbBatchesLineUnitItem.setOnCheckedChangeListener { _, isChecked ->
                    grnLineItemUnit.isChecked = isChecked
                    onItemCheckedChange(grnLineItemUnit)
                }
            }
            else{
                holder.cbBatchesLineUnitItem.visibility=View.GONE
            }

        }
    }
    private fun updateView(holder: ViewHolder, batchInfoItem: CustomGrnLineItemUnit) {
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
        val cbBatchesLineUnitItem: CheckBox = itemView.findViewById(R.id.cbBatchesLineUnitItem)

    }


}