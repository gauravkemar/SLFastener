package com.example.slfastener.adapter.demoAdapter

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.slfastener.R
import com.example.slfastener.helper.CustomKeyboard
import com.example.slfastener.model.offlinebatchsave.GrnLineItemUnitStore
import com.google.android.material.card.MaterialCardView

class CreateBatchesNewSingleList(
    private val context: Context,
    private val batches: MutableList<GrnLineItemUnitStore>,
    private val onSave: (Int, GrnLineItemUnitStore) -> Unit,
    private val addItem: (GrnLineItemUnitStore) -> Unit,
    private val addMultiItem: (GrnLineItemUnitStore) -> Unit,
    private val onDelete: (Int,GrnLineItemUnitStore) -> Unit,
    private var customKeyboard: CustomKeyboard? = null
) :
    RecyclerView.Adapter<CreateBatchesNewSingleList.ViewHolder>() {
    private var weightData: String? = null
    private var focusedTextView: TextView? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.create_batchs_item, parent, false)
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

        val conditionMatches = batches.any { it.recevedQty == "0.000" || it.recevedQty == "0" }
        holder.mcvWeight.setOnClickListener {
            if (conditionMatches) {
                Toast.makeText(context, "Please complete current transaction", Toast.LENGTH_SHORT)
                    .show()
            } else {
                if (grnLineItemUnit.UOM.equals("KGS")) {
                    holder.tvWeight.requestFocus()
                } else {
                    holder.edWeight.isFocusable = true
                    holder.edWeight.isEnabled = true
                }
            }

        }
        holder.tvWeight.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                focusedTextView = holder.tvWeight
            }
        }
        holder.edWeight.showSoftInputOnFocus = false
        holder.edWeight.setOnClickListener {
            // Show custom keyboard when edWeight EditText is clicked
            customKeyboard!!.setTargetEditText(holder.edWeight)
            customKeyboard!!.showAt(holder.itemView)
        }

        holder.ivAdd.setOnClickListener {
            addItem(
                GrnLineItemUnitStore(
                    grnLineItemUnit.UOM,
                    grnLineItemUnit.mhType,
                    grnLineItemUnit.barcode,
                    grnLineItemUnit.expiryDate,
                    grnLineItemUnit.internalBatchNo,
                    grnLineItemUnit.isChecked,
                    grnLineItemUnit.lineItemId,
                    grnLineItemUnit.lineItemUnitId,
                    holder.edWeight.text.toString(),
                    grnLineItemUnit.supplierBatchNo,
                    false
                )
            )
            holder.edWeight.clearFocus()
        }
        holder.ivMultiAdd.setOnClickListener {
            addItem(
                GrnLineItemUnitStore(
                    grnLineItemUnit.UOM,
                    grnLineItemUnit.mhType,
                    grnLineItemUnit.barcode,
                    grnLineItemUnit.expiryDate,
                    grnLineItemUnit.internalBatchNo,
                    grnLineItemUnit.isChecked,
                    grnLineItemUnit.lineItemId,
                    grnLineItemUnit.lineItemUnitId,
                    holder.edWeight.text.toString(),
                    grnLineItemUnit.supplierBatchNo,
                    false
                )
            )
            holder.edWeight.clearFocus()
        }

        updateView(holder, grnLineItemUnit)
        holder.ivSave.setOnClickListener {
            if (grnLineItemUnit.UOM.equals("KGS")) {
                grnLineItemUnit.recevedQty = holder.tvWeight.getText().toString()
                grnLineItemUnit.isUpdate = true
                //updateView(holder, grnLineItemUnit)
                //updateView(holder, grnLineItemUnit)
                onSave(position, grnLineItemUnit)
                holder.tvWeight.clearFocus()
            } else {
                grnLineItemUnit.recevedQty = holder.edWeight.getText().toString()
                grnLineItemUnit.isUpdate = true
                //updateView(holder, grnLineItemUnit)
                onSave(position, grnLineItemUnit)
                holder.edWeight.clearFocus()
            }

        }
        holder.ivDelete.setOnClickListener {
            onDelete(position,grnLineItemUnit)
            batches.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(
                position,
                batches.size
            ) // To update the positions of the remaining items
        }

        if (grnLineItemUnit.UOM.equals("KGS")) {
            holder.ivAdd.visibility = View.GONE
            holder.tvWeight.visibility = View.VISIBLE
            holder.edWeight.visibility = View.GONE
        } else {
            holder.ivAdd.visibility = View.VISIBLE
            holder.tvWeight.visibility = View.GONE
            holder.edWeight.visibility = View.VISIBLE
        }

        if(grnLineItemUnit.mhType.lowercase().equals("batches") && grnLineItemUnit.UOM.lowercase().equals("number"))
        {
            holder.ivMultiAdd.visibility = View.VISIBLE
        }
        else{
            holder.ivMultiAdd.visibility = View.GONE
        }

        /* if (batchModel.isUpdate) {
             Log.d("MyTag", "Setting visibility and colors")
             holder.ivAdd.setImageResource(R.drawable.ic_save_black)
             holder.mcvWeight.visibility = View.VISIBLE
             holder.clCardMain.setBackgroundColor(Color.RED)

            *//* holder.clCardMain.setBackgroundColor(
                ContextCompat.getColor(
                    context!!,
                    R.color.header_bg
                )
            )
            holder.mcvGenerateBarcode.setCardBackgroundColor(
                ContextCompat.getColor(
                    context!!,
                    R.color.light_grey
                )
            )*//*
        } else {
            Log.d("MyTag", "Setting visibility and colors False $list")
        }*/

        if (holder.tvWeight.text.toString().trim() == "0.000") {
            holder.tvWeight.requestFocus()
            holder.mcvWeight.visibility = View.GONE
        }


    }

    private fun updateView(holder: ViewHolder, batchInfoItem: GrnLineItemUnitStore) {
        with(holder) {
            if (batchInfoItem.isUpdate) {
                edWeight.setBackgroundColor(Color.LTGRAY)
                tvWeight.setBackgroundColor(Color.LTGRAY)
                holder.edWeight.isFocusable = false
                holder.edWeight.isEnabled = false
                holder.mcvWeight.visibility = View.VISIBLE
            } else {
                edWeight.setBackgroundColor(Color.TRANSPARENT)
                tvWeight.setBackgroundColor(Color.TRANSPARENT)
                holder.edWeight.isFocusable = true
                holder.edWeight.isEnabled = true
                holder.mcvWeight.visibility = View.GONE
            }
        }
    }

    fun updateWeightValue(weightData: String) {
        this.weightData = weightData
        focusedTextView?.setText(weightData)
        Log.d("weightFromInnerCreateBatchesNewSingleList", weightData)
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


/*    private fun listsAreEqual(
        list1: List<BatchInfoListModel>,
        list2: List<BatchInfoListModel>
    ): Boolean {
        if (list1.size != list2.size) {
            Log.e("MyTag", "notifyDataSetChanged()")
            return false
        }
        for (i in list1.indices) {
            if (list1[i] != list2[i]) {
                return false
            }
        }
        return true
    }*/