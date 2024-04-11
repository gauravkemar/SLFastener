package com.example.slfastener.adapter

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
import com.google.android.material.card.MaterialCardView

class CreateBatchesNewAdapter (
    private val batchInfoListModel: MutableList<BatchInfoListModel>,
    private val context: Context,
    /*  private val addItem: (BatchInfoListModel) -> Unit,*/
    private val updateItem: (Int, String, Boolean) -> Unit
) : RecyclerView.Adapter<CreateBatchesNewAdapter.ViewHolder>() {
    private var focusedTextView: TextView? = null
    private var weightData: String? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.create_batchs_item, parent, false)
        return  ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemPosition = holder.layoutPosition
        val batchModel = batchInfoListModel[position]
        holder.tvSrnNo.setText("${position + 1}")
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

        if (batchModel.isUpdate) {
            Log.d("MyTag", "Setting visibility and colors")
            holder.ivAdd.setImageResource(R.drawable.ic_save_black)
            holder.mcvWeight.visibility = View.VISIBLE
            holder.clCardMain.setBackgroundColor(
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
            )
        } else {
            Log.d("MyTag", "Setting visibility and colors False $batchInfoListModel")
        }
        holder.ivAdd.setOnClickListener {
            updateItem(position, holder.edWeight.getText().toString(), true)
            holder.edWeight.clearFocus()
        }

        /*      holder.ivAdd.setOnClickListener {
                  if (holder.ivAdd.drawable.constantState?.equals(
                          ContextCompat.getDrawable(
                              context!!,
                              R.drawable.ic_save_black
                          )?.constantState
                      ) == false) {
                      val baseUsername = batchModel.batchBarcodeNo
                      val highestNumber =
                          batchInfoListModel.mapNotNull { it.generatedBarcodeNo.substringAfterLast("-").toIntOrNull() }.maxOrNull()
                              ?: 0
                      // Add new item with default values
                      val newItem =    BatchInfoListModel(
                          batchModel.ExpiryDate,
                          batchModel.GDPONumber,
                          holder.edWeight.getText().toString(),
                          0,
                          "$baseUsername-${highestNumber + 1}",
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
                          false
                      )
                      // Update current item's username
                      updateItem(position, holder.edWeight.getText().toString(), true)
                     // addItem(newItem)

                  } else {
                      updateItem(position, holder.edWeight.getText().toString(), true)
                  }


              }*/
        holder.ivDelete.setOnClickListener {
            onItemDeleteClickListener?.invoke(batchInfoListModel[position])
            deleteItemAtPosition(position)
            Log.e("positionfromboth","$position adapter--${batchInfoListModel.size}")
        }

        /* holder.ivDelete.setOnClickListener {
             if (batchInfoListModel.size > 1) { // Prevent removal if only one item remains
                 batchInfoListModel.removeAt(position)
                 notifyItemRemoved(position)
             }
         }*/
         if (holder.edWeight.text.toString().trim() == "0.000") {
             holder.edWeight.requestFocus()
             //focusedTextView = holder.edWeight
             holder.mcvWeight.visibility = View.GONE
         }

    }


    /* fun updateWeightValue(weightData: String) {
         this.weightData = weightData
         focusedTextView?.setText(weightData)
     }*/
    fun deleteItemAtPosition(position: Int) {
        if (position < 0 || position >= batchInfoListModel.size) {
            return
        }
        batchInfoListModel.removeAt(position)
        notifyItemRemoved(position)
    }

    /*  fun removeItem(position: Int) {
          if (position in 0 until dockList.size) {
              dockList.removeAt(position)
              notifyItemRemoved(position)
          }
      }*/

    override fun getItemCount(): Int {
        if (batchInfoListModel.size == 0) {
            //Toast.makeText(context,"List is empty", Toast.LENGTH_LONG).show()
        } else {
            return batchInfoListModel.size
        }
        return batchInfoListModel.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val tvSrnNo: TextView = itemView.findViewById(R.id.tvSrnNo)
        val tvBatchBarcodeNo: TextView = itemView.findViewById(R.id.tvBatchBarcodeNo)
        val edWeight: TextView = itemView.findViewById(R.id.edWeight)
        val mcvGenerateBarcode: MaterialCardView = itemView.findViewById(R.id.mcvGenerateBarcode)
        val tvBarcodeNo: TextView = itemView.findViewById(R.id.tvBarcodeNo)
        val ivDelete: ImageView = itemView.findViewById(R.id.ivDelete)
        val ivAdd: ImageView = itemView.findViewById(R.id.ivAdd)
        val mcvWeight: MaterialCardView = itemView.findViewById(R.id.mcvWeight)
        val clCardMain: ConstraintLayout = itemView.findViewById(R.id.clCardMain)
        val tvPrint: TextView = itemView.findViewById(R.id.tvPrint)


    }
    private var onItemDeleteClickListener: ((BatchInfoListModel) -> Unit)? = null

    fun setOnItemDeleteClickListener(listener: (BatchInfoListModel) -> Unit) {
        onItemDeleteClickListener = listener
    }
}