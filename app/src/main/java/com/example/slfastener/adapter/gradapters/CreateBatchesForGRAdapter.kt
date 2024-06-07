package com.example.slfastener.adapter.gradapters

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
import com.example.slfastener.helper.CustomKeyboard
import com.example.slfastener.model.goodsreceipt.GRLineUnitItemSelection
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CreateBatchesForGRAdapter(
    private val context: Context,
    private val batches: MutableList<GRLineUnitItemSelection>,
    private val onSave: (Int, GRLineUnitItemSelection) -> Unit,
    private val addItem: (GRLineUnitItemSelection) -> Unit,
    private val addMultiItem: (GRLineUnitItemSelection) -> Unit,
    private val onDelete: (Int, GRLineUnitItemSelection) -> Unit,
    private var customKeyboard: CustomKeyboard? = null
) :
    RecyclerView.Adapter<CreateBatchesForGRAdapter.ViewHolder>() {
    private var weightData: String? = null
    private var focusedTextView: TextView? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.create_batchs_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemPosition = holder.layoutPosition
        val grLineItemUnit: GRLineUnitItemSelection = batches?.get(itemPosition)!!
        holder.tvSrnNo.setText("${itemPosition + 1}")
        holder.tvBatchNo.setText(grLineItemUnit.BatchNo)
        holder.tvInternalBatchNo.setText(grLineItemUnit.InternalBatchNo)
        holder.tvUOM.setText(grLineItemUnit.UOM)
        holder.tvBarcodeLableValue.setText(grLineItemUnit.Barcode)

        if (grLineItemUnit.Qty == "0.000" || grLineItemUnit.Qty == "0") {
            holder.tvWeight.setText("")
            holder.edWeight.setText("")
        } else {
            holder.tvWeight.setText(grLineItemUnit.Qty)
            holder.edWeight.setText(grLineItemUnit.Qty)
        }

        val conditionMatches = batches.any { it.Qty == "0.000" || it.Qty == "0" }

        holder.mcvWeight.setOnClickListener {
            if (conditionMatches) {
                Toast.makeText(context, "Please complete current transaction", Toast.LENGTH_SHORT)
                    .show()
            } else {
                if (grLineItemUnit.UOM.equals("KGS")) {
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
            if (conditionMatches) {
                Toast.makeText(context, "Please complete current transaction", Toast.LENGTH_SHORT)
                    .show()
            } else {
                if (grLineItemUnit.isExpirable ) {
                    if (holder.tvExpiryDate.text.toString() != "" && holder.tvExpiryDate.text.toString() != "null") {
                        addItem(
                            GRLineUnitItemSelection(
                                grLineItemUnit.Barcode,
                                grLineItemUnit.BatchNo,
                                grLineItemUnit.isExpirable,
                                holder.tvExpiryDate.text.toString(),
                                grLineItemUnit.InternalBatchNo,
                                grLineItemUnit.IsEdit,
                                grLineItemUnit.Isdisabled,
                                grLineItemUnit.LineItemId,
                                grLineItemUnit.LineItemUnitId,
                                holder.edWeight.text.toString(),
                                grLineItemUnit.UOM,
                                grLineItemUnit.mhType,
                                false
                            )
                        )
                        holder.edWeight.clearFocus()
                    } else {
                        Toast.makeText(context, "Please select Date!!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    addItem(
                        GRLineUnitItemSelection(
                            grLineItemUnit.Barcode,
                            grLineItemUnit.BatchNo,
                            grLineItemUnit.isExpirable,
                            grLineItemUnit.ExpiryDate,
                            grLineItemUnit.InternalBatchNo,
                            grLineItemUnit.IsEdit,
                            grLineItemUnit.Isdisabled,
                            grLineItemUnit.LineItemId,
                            grLineItemUnit.LineItemUnitId,
                            holder.edWeight.text.toString(),
                            grLineItemUnit.UOM,
                            grLineItemUnit.mhType,
                            false
                        )
                    )
                    holder.edWeight.clearFocus()
                }
            }


        }
        holder.ivMultiAdd.setOnClickListener {
            if (conditionMatches) {
                Toast.makeText(context, "Please complete current transaction", Toast.LENGTH_SHORT)
                    .show()
            } else {
                if (grLineItemUnit.isExpirable) {
                    if (holder.tvExpiryDate.text.toString() != "") {
                        addMultiItem(
                            GRLineUnitItemSelection(
                                grLineItemUnit.Barcode,
                                grLineItemUnit.BatchNo,
                                grLineItemUnit.isExpirable,
                                grLineItemUnit.ExpiryDate,
                                grLineItemUnit.InternalBatchNo,
                                grLineItemUnit.IsEdit,
                                grLineItemUnit.Isdisabled,
                                grLineItemUnit.LineItemId,
                                grLineItemUnit.LineItemUnitId,
                                holder.edWeight.text.toString(),
                                grLineItemUnit.UOM,
                                grLineItemUnit.mhType,
                                false
                            )
                        )
                        holder.edWeight.clearFocus()
                    } else {
                        Toast.makeText(context, "Please select Date!!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    addMultiItem(
                        GRLineUnitItemSelection(
                        grLineItemUnit.Barcode,
                        grLineItemUnit.BatchNo,
                        grLineItemUnit.isExpirable,
                        "",
                        grLineItemUnit.InternalBatchNo,
                        grLineItemUnit.IsEdit,
                        grLineItemUnit.Isdisabled,
                        grLineItemUnit.LineItemId,
                        grLineItemUnit.LineItemUnitId,
                        holder.edWeight.text.toString(),
                        grLineItemUnit.UOM,
                        grLineItemUnit.mhType,
                        false
                    ))

                    holder.edWeight.clearFocus()
                }
            }
        }
        updateView(holder, grLineItemUnit)
        holder.ivSave.setOnClickListener {
            if (grLineItemUnit.isExpirable) {
                if (holder.tvExpiryDate.text.toString() != "" && holder.tvExpiryDate.text.toString() != "null") {
                    if (grLineItemUnit.UOM.equals("KGS")) {
                        grLineItemUnit.Qty = holder.tvWeight.getText().toString()
                        grLineItemUnit.ExpiryDate = holder.tvExpiryDate.getText().toString()
                        grLineItemUnit.isUpdate = true
                        //updateView(holder, grnLineItemUnit)
                        //updateView(holder, grnLineItemUnit)
                        onSave(position, grLineItemUnit)
                        holder.tvWeight.clearFocus()
                    } else {
                        grLineItemUnit.Qty = holder.edWeight.getText().toString()
                        grLineItemUnit.ExpiryDate = holder.tvExpiryDate.getText().toString()
                        grLineItemUnit.isUpdate = true
                        //updateView(holder, grnLineItemUnit)
                        onSave(position, grLineItemUnit)
                        holder.edWeight.clearFocus()
                    }
                } else {
                    Toast.makeText(context, "Please select Date!!", Toast.LENGTH_SHORT).show()
                }
            } else {
                if (grLineItemUnit.UOM.equals("KGS")) {
                    if (holder.tvWeight.getText().toString() != "") {
                        grLineItemUnit.Qty = holder.tvWeight.getText().toString()
                        grLineItemUnit.isUpdate = true
                        //updateView(holder, grnLineItemUnit)
                        //updateView(holder, grnLineItemUnit)
                        onSave(position, grLineItemUnit)
                        holder.tvWeight.clearFocus()
                    } else {
                        Toast.makeText(context, "Please Fill the QTY!!", Toast.LENGTH_SHORT)
                            .show()
                    }

                } else {
                    if (holder.edWeight.getText().toString() != "") {
                        grLineItemUnit.Qty = holder.edWeight.getText().toString()
                        grLineItemUnit.isUpdate = true
                        //updateView(holder, grnLineItemUnit)
                        onSave(position, grLineItemUnit)
                        holder.edWeight.clearFocus()
                    } else {
                        Toast.makeText(context, "Please Fill the QTY!!", Toast.LENGTH_SHORT)
                            .show()
                    }

                }
            }

        }
        holder.ivDelete.setOnClickListener {
            onDelete(position, grLineItemUnit)
            /* batches.removeAt(position)
             notifyItemRemoved(position)
             notifyItemRangeChanged(
                 position,
                 batches.size
             ) // To update the positions of the remaining items*/
        }

        if (grLineItemUnit.UOM.equals("KGS")) {
            holder.ivAdd.visibility = View.GONE
            holder.tvWeight.visibility = View.VISIBLE
            holder.edWeight.visibility = View.GONE
        } else {
            if (grLineItemUnit.mhType.lowercase().equals("serial")) {
                holder.ivAdd.visibility = View.GONE
            } else {
                holder.ivAdd.visibility = View.VISIBLE
            }
            holder.tvWeight.visibility = View.GONE
            holder.edWeight.visibility = View.VISIBLE
        }

        if (grLineItemUnit.mhType.lowercase().equals("batch") && grLineItemUnit.UOM.lowercase()
                .equals("pcs")
        ) {
            holder.ivMultiAdd.visibility = View.VISIBLE
        } else {
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

        if (holder.tvWeight.text.toString().trim() == "0.000" ||holder.tvWeight.text.toString().trim() == "" ) {
            holder.tvWeight.requestFocus()
            holder.mcvWeight.visibility = View.GONE

        }

        if (grLineItemUnit.isExpirable) {
            holder.tvExpiryDate.visibility = View.VISIBLE

            /*if (grnLineItemUnit.expiryDate.toString() == "") {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val parsedDate = inputFormat.parse(grnLineItemUnit.expiryDate.toString())
                val formattedDate = outputFormat.format(parsedDate)
                holder.tvExpiryDate.setText(formattedDate)
            }else if()

            else {
                holder.tvExpiryDate.setText("")
            }*/

            Log.e("inputDateelse", "grnLineItemUnit.expiryDate.toString()")
            if (grLineItemUnit.ExpiryDate.toString() != "" && grLineItemUnit.ExpiryDate.toString() != "null") {
                val inputDate = grLineItemUnit.ExpiryDate.toString()
                val inputFormat: SimpleDateFormat
                val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())


                if (inputDate.length == "yyyy-MM-dd'T'HH:mm:ss".length) {
                    Log.e("inputDate.length ", inputDate.length.toString())
                    inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                    val parsedDate = inputFormat.parse(inputDate)
                    val formattedDate = outputFormat.format(parsedDate)
                    holder.tvExpiryDate.setText(formattedDate)
                } else {
                    Log.e("inputDateelse", inputDate.length.toString())
                    inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val parsedDate = inputFormat.parse(inputDate)
                    val formattedDate = outputFormat.format(parsedDate)
                    holder.tvExpiryDate.setText(formattedDate)
                }
            } else {
                Log.e("inputDateelse", "outerelse")
                holder.tvExpiryDate.setText("")
            }

        } else {
            holder.tvExpiryDate.visibility = View.GONE
        }

        holder.tvExpiryDate.setOnClickListener {
            showDatePickerDialog(holder)
        }
    }

    private fun updateView(holder: ViewHolder, batchInfoItem: GRLineUnitItemSelection) {
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

    private fun showDatePickerDialog(holder: ViewHolder) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(
            context,
            DatePickerDialog.OnDateSetListener { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                // Do something with the selected date
                val selectedDateCal = Calendar.getInstance()
                selectedDateCal.set(selectedYear, selectedMonth, selectedDay)
                // You can handle the selected date here, for example, set it to a TextView
                //holder.tvExpiryDate.setText("$selectedDay/${selectedMonth + 1}/$selectedYear")
                //selectedDate = "$selectedDay-${selectedMonth + 1}-$selectedYear"
                val dateFormat = SimpleDateFormat("dd-M-yyyy", Locale.getDefault())
                val selectDt = dateFormat.parse("$selectedDay-${selectedMonth + 1}-$selectedYear")
                val formattedDate =
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectDt)
                holder.tvExpiryDate.setText(formattedDate.toString())
                //selectedDate = LocalDateTime.of(2024, 4, 20,0,0)
            }, year, month, dayOfMonth
        )
        // Set maximum date
        datePickerDialog.datePicker.minDate =
            System.currentTimeMillis() // Optional: Set minimum date to current date
        datePickerDialog.show()
    }

}

