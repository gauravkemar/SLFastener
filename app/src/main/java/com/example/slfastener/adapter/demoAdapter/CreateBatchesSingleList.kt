package com.example.slfastener.adapter.demoAdapter

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.slfastener.R
import com.example.slfastener.interfaceclass.ItemClickListener
import com.example.slfastener.model.BatchInfoListModel
import com.google.android.material.card.MaterialCardView

class CreateBatchesSingleList(
    private val batches: MutableList<BatchInfoListModel>,
    private val onSave: (Int, BatchInfoListModel) -> Unit,


) :
    RecyclerView.Adapter<CreateBatchesSingleList.ViewHolder>() {
    private var weightData: String? = null
    private var focusedTextView: TextView? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.create_batchs_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemPosition = holder.layoutPosition
        val batchModel: BatchInfoListModel = batches?.get(itemPosition)!!
        holder.tvSrnNo.setText("${itemPosition + 1}")
        holder.tvBatchBarcodeNo.setText(batchModel.batchBarcodeNo)
        holder.edWeight.setText(batchModel.ReceivedQty)
        holder.tvBarcodeNo.setText(batchModel.generatedBarcodeNo)
        holder.edWeight.setText(batchModel.ReceivedQty)
        holder.mcvWeight.setOnClickListener {
            holder.edWeight.requestFocus()
        }
        holder.edWeight.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                focusedTextView = holder.edWeight
            }
        }
      /*  holder.ivAdd.setOnClickListener {
            itemClickListener?.onItemClick(
                itemPosition,
                BatchInfoListModel(
                    batchModel.ExpiryDate,
                    batchModel.GDPONumber,
                    holder.edWeight.getText().toString(),
                    0,
                    holder.tvBarcodeNo.text.toString().trim(),
                    batchModel.itemCode,
                    batchModel.itemDescription,
                    batchModel.itemName,
                    batchModel.lineNumber,
                    batchModel.materialType,
                    batchModel.poId,
                    batchModel.poLineItemId,
                    batchModel.poNumber,
                    batchModel.poQuantity,
                    batchModel.poUnitPrice,
                    batchModel.posapLineItemNumber,
                    batchModel.pouom,
                    batchModel.batchBarcodeNo,
                    true
                )
            )
            holder.edWeight.clearFocus()
        }*/

        updateView(holder, batchModel)
        holder.ivAdd.setOnClickListener {
            batchModel.ReceivedQty=holder.edWeight.getText().toString()
            batchModel.isUpdate = true
            updateView(holder, batchModel)
            onSave(position, batchModel)
            holder.edWeight.clearFocus()
        }
        holder.ivDelete.setOnClickListener {

            batches.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, batches.size) // To update the positions of the remaining items

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

        if (holder.edWeight.text.toString().trim() == "0.000") {
            holder.edWeight.requestFocus()
            holder.mcvWeight.visibility = View.GONE
        }

    }
    private fun updateView(holder: ViewHolder, batchInfoItem: BatchInfoListModel) {
        with(holder) {
            if (batchInfoItem.isUpdate) {
                clCardMain.setBackgroundColor(Color.LTGRAY)
            } else {
                clCardMain.setBackgroundColor(Color.TRANSPARENT)

            }
        }
    }

    fun updateWeightValue(weightData: String) {
        this.weightData = weightData
        focusedTextView?.setText(weightData)
        Log.d("weightFromInner",weightData)
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
        val tvBatchBarcodeNo: TextView = itemView.findViewById(R.id.tvBatchBarcodeNo)
        val edWeight: TextView = itemView.findViewById(R.id.edWeight)
        //val mcvGenerateBarcode: MaterialCardView = itemView.findViewById(R.id.mcvGenerateBarcode)
        val tvBarcodeNo: TextView = itemView.findViewById(R.id.tvBarcodeNo)
        val ivDelete: ImageView = itemView.findViewById(R.id.ivDelete)
        val ivAdd: ImageView = itemView.findViewById(R.id.ivAdd)
        val mcvWeight: MaterialCardView = itemView.findViewById(R.id.mcvWeight)
        val clCardMain: ConstraintLayout = itemView.findViewById(R.id.clCardMain)
       // val tvPrint: TextView = itemView.findViewById(R.id.tvPrint)
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