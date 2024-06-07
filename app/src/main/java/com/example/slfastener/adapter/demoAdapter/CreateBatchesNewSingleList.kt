package com.example.slfastener.adapter.demoAdapter

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
import com.example.slfastener.model.offlinebatchsave.GrnLineItemUnitStore
import com.google.android.material.card.MaterialCardView
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CreateBatchesNewSingleList(
    private val context: Context,
    private val batches: MutableList<GrnLineItemUnitStore>,
    private val onSave: (Int, GrnLineItemUnitStore) -> Unit,
    private val addItem: (GrnLineItemUnitStore) -> Unit,
    private val addMultiItem: (GrnLineItemUnitStore) -> Unit,
    private val onDelete: (Int, GrnLineItemUnitStore) -> Unit,
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

        if (grnLineItemUnit.recevedQty == "0.000" || grnLineItemUnit.recevedQty == "0") {
            holder.tvWeight.setText("")
            holder.edWeight.setText("")
        } else {
            holder.tvWeight.setText(grnLineItemUnit.recevedQty)
            holder.edWeight.setText(grnLineItemUnit.recevedQty)
        }

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
            if (conditionMatches) {
                Toast.makeText(context, "Please complete current transaction", Toast.LENGTH_SHORT)
                    .show()
            } else {
                if (grnLineItemUnit.isExpirable) {
                    if (holder.tvExpiryDate.text.toString() != "" && holder.tvExpiryDate.text.toString() != "null") {
                        addItem(
                            GrnLineItemUnitStore(
                                grnLineItemUnit.UOM,
                                grnLineItemUnit.mhType,
                                grnLineItemUnit.barcode,
                                holder.tvExpiryDate.text.toString(),
                                grnLineItemUnit.isExpirable,
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
                    } else {
                        Toast.makeText(context, "Please select Date!!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    addItem(
                        GrnLineItemUnitStore(
                            grnLineItemUnit.UOM,
                            grnLineItemUnit.mhType,
                            grnLineItemUnit.barcode,
                            grnLineItemUnit.expiryDate,
                            grnLineItemUnit.isExpirable,
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
            }

        }
        holder.ivMultiAdd.setOnClickListener {
            if (conditionMatches) {
                Toast.makeText(context, "Please complete current transaction", Toast.LENGTH_SHORT)
                    .show()
            }
            else {
                if (grnLineItemUnit.isExpirable) {
                    if (holder.tvExpiryDate.text.toString() != "") {
                        addMultiItem(
                            GrnLineItemUnitStore(
                                grnLineItemUnit.UOM,
                                grnLineItemUnit.mhType,
                                grnLineItemUnit.barcode,
                                grnLineItemUnit.expiryDate,
                                grnLineItemUnit.isExpirable,
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
                    } else {
                        Toast.makeText(context, "Please select Date!!", Toast.LENGTH_SHORT).show()
                    }
                }
                else {
                    addMultiItem(
                        GrnLineItemUnitStore(
                            grnLineItemUnit.UOM,
                            grnLineItemUnit.mhType,
                            grnLineItemUnit.barcode,
                            grnLineItemUnit.expiryDate,
                            grnLineItemUnit.isExpirable,
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
            }
        }
        updateView(holder, grnLineItemUnit)
        holder.ivSave.setOnClickListener {
            if (grnLineItemUnit.isExpirable) {
                if (holder.tvExpiryDate.text.toString() != "" && holder.tvExpiryDate.text.toString() != "null") {
                    if (grnLineItemUnit.UOM.equals("KGS")) {
                        grnLineItemUnit.recevedQty = holder.tvWeight.getText().toString()
                        grnLineItemUnit.expiryDate = holder.tvExpiryDate.getText().toString()
                        grnLineItemUnit.isUpdate = true
                        //updateView(holder, grnLineItemUnit)
                        //updateView(holder, grnLineItemUnit)
                        onSave(position, grnLineItemUnit)
                        holder.tvWeight.clearFocus()
                    } else {
                        grnLineItemUnit.recevedQty = holder.edWeight.getText().toString()
                        grnLineItemUnit.expiryDate = holder.tvExpiryDate.getText().toString()
                        grnLineItemUnit.isUpdate = true
                        //updateView(holder, grnLineItemUnit)
                        onSave(position, grnLineItemUnit)
                        holder.edWeight.clearFocus()
                    }
                } else {
                    Toast.makeText(context, "Please select Date!!", Toast.LENGTH_SHORT).show()
                }
            } else {
                if (grnLineItemUnit.UOM.equals("KGS")) {
                    if (holder.tvWeight.getText().toString() != "") {
                        grnLineItemUnit.recevedQty = holder.tvWeight.getText().toString()
                        grnLineItemUnit.isUpdate = true
                        //updateView(holder, grnLineItemUnit)
                        //updateView(holder, grnLineItemUnit)
                        onSave(position, grnLineItemUnit)
                        holder.tvWeight.clearFocus()
                    } else {
                        Toast.makeText(context, "Please Fill the QTY!!", Toast.LENGTH_SHORT)
                            .show()
                    }

                } else {
                    if (holder.edWeight.getText().toString() != "") {
                        grnLineItemUnit.recevedQty = holder.edWeight.getText().toString()
                        grnLineItemUnit.isUpdate = true
                        //updateView(holder, grnLineItemUnit)
                        onSave(position, grnLineItemUnit)
                        holder.edWeight.clearFocus()
                    } else {
                        Toast.makeText(context, "Please Fill the QTY!!", Toast.LENGTH_SHORT)
                            .show()
                    }

                }
            }

        }
        holder.ivDelete.setOnClickListener {
            onDelete(position, grnLineItemUnit)
        }

        if (grnLineItemUnit.UOM.equals("KGS")) {
            holder.ivAdd.visibility = View.GONE
            holder.tvWeight.visibility = View.VISIBLE
            holder.edWeight.visibility = View.GONE
        } else {
            if(grnLineItemUnit.mhType.lowercase().equals("serial"))
            {
                holder.ivAdd.visibility = View.GONE
            }
            else{
                holder.ivAdd.visibility = View.VISIBLE
            }
            holder.tvWeight.visibility = View.GONE
            holder.edWeight.visibility = View.VISIBLE
        }

        if (grnLineItemUnit.mhType.lowercase().equals("batch") && grnLineItemUnit.UOM.lowercase()
                .equals("pcs")
        ) {
            holder.ivMultiAdd.visibility = View.VISIBLE
        } else {
            holder.ivMultiAdd.visibility = View.GONE
        }


        if (holder.tvWeight.text.toString().trim() == "0.000" ||holder.tvWeight.text.toString().trim() == "" ) {
            holder.tvWeight.requestFocus()
            holder.mcvWeight.visibility = View.GONE
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

        holder.tvExpiryDate.setOnClickListener {
            showDatePickerDialog(holder)
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
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() // Optional: Set minimum date to current date
        datePickerDialog.show()
    }

}
