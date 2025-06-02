package com.example.slfastener.adapter

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.slfastener.R
import com.example.slfastener.helper.CustomArrayAdapter
import com.example.slfastener.helper.CustomKeyboard
import com.example.slfastener.model.offlinebatchsave.CustomPoLineItemSelectionModel
import com.google.android.material.card.MaterialCardView

class GrnMainAddAdapter(
    private val context: Context,
    private val poLineItemParent: MutableList<CustomPoLineItemSelectionModel>,
    private val itemDescription: (itemDesc: String) -> Unit,
    private val onItemCheck: (Int, CustomPoLineItemSelectionModel) -> Unit,
    private val onItemDelete: (Int, CustomPoLineItemSelectionModel) -> Unit,
    private val onItemSave: (Int, CustomPoLineItemSelectionModel) -> Unit,
    private val onDiscountAdded: (Int, CustomPoLineItemSelectionModel) -> Unit,
    private val customKeyboard: CustomKeyboard,

    ) : RecyclerView.Adapter<GrnMainAddAdapter.ViewHolder>() {

    lateinit var locationNameList: MutableList<String>
    var allLocationHashMap = HashMap<Int, String>()
    var selectedLocationID: Int = 0

    lateinit var taxNames: MutableList<String>
    var allTaxMap = HashMap<Int, String>()
    var selectedTax: Int = 0
    private var getAllTaxAdapter: CustomArrayAdapter? = null

    private var weightData: String? = null
    private var getAllLocationAdapter: CustomArrayAdapter? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.grn_added_line_item_rc_layout, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemPosition = holder.layoutPosition
        var poLineItemModel: CustomPoLineItemSelectionModel = poLineItemParent.get(itemPosition)

        holder.tvSrNo.setText("${itemPosition+1}")
        holder.tvPoNo.setText(poLineItemModel.poNumber)

        holder.tvPoLineNo.setText(poLineItemModel.poLineNo.toString())
        holder.tvItemCode.setText(poLineItemModel.itemCode)
        holder.tvItemDesc.setText(poLineItemModel.itemDescription)
        holder.tvMhType.setText(poLineItemModel.mhType)
        holder.tvPoQty.setText(poLineItemModel.poqty.toString())
        holder.tvBalQty.setText(poLineItemModel.balQTY.toString())
        holder.tvRate.setText(poLineItemModel.unitPrice.toString()?:"")
        holder.tvGRNQty.setText(poLineItemModel.quantityReceived)
        holder.edDiscount.setText((poLineItemModel.discountAmount ?:"0").toString())
        //holder.edTotalUnit.setText(poLineItemModel.totalUnit.toString())
       // holder.edAmount.setText(poLineItemModel.amount)
        holder.tvBatchCount.setText("${poLineItemModel.grnLineItemUnit?.size ?: 0}/${poLineItemModel.totalUnit}")
        holder.tvPuom.setText(poLineItemModel.pouom)
        selectedLocationID=poLineItemModel.locationId
        holder.tvLineAmount.setText(poLineItemModel.lineAmount.toString())
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
        holder.edDiscount.showSoftInputOnFocus = false
        holder.edDiscount.setOnClickListener {
            customKeyboard?.setTargetEditText(holder.edDiscount)
            customKeyboard?.showAt(holder.itemView)
        }
        holder.edDiscount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val input = s.toString().toDoubleOrNull() ?: 0.0
                if (input >= 100) {
                    holder.edDiscount.error = "Discount cannot be 100%"
                    holder.edDiscount.removeTextChangedListener(this)
                    holder.edDiscount.setText("99")
                    holder.edDiscount.setSelection(holder.edDiscount.text.length)
                    holder.edDiscount.addTextChangedListener(this)
                } else {

                        poLineItemModel.discountAmount = input
                        setData(itemPosition,holder,poLineItemModel,poLineItemParent)


                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        holder.tvItemDesc.setOnClickListener {
            itemDescription(poLineItemModel.itemDescription)
        }
        holder.mcvSaveLineItem.setOnClickListener {
            onItemSave(itemPosition,poLineItemModel)
        }

        holder.mcvDeleteLineItem.setOnClickListener {
            onItemDelete(itemPosition,poLineItemModel)
            /*  poLineItemParent.removeAt(position)
              notifyItemRemoved(position)
              notifyItemRangeChanged(
                  position,
                  poLineItemParent.size
              ) */
        }

        if(poLineItemModel.grnLineItemUnit!=null )
        {
            if((poLineItemModel.grnLineItemUnit!!.size > 0))
            {
                holder.mcvDeleteLineItem.visibility=View.GONE
                holder.mcvDeleteLineItem.setEnabled(false);
                holder.ivEditLineItem.setImageResource(R.drawable.ic_edit_black)
            }
            else{
                holder.mcvDeleteLineItem.setEnabled(true);
                holder.mcvDeleteLineItem.visibility=View.VISIBLE
                holder.ivEditLineItem.setImageResource(R.drawable.ic_add_blue)
            }
        }
        else{
            holder.mcvDeleteLineItem.setEnabled(true);
            holder.mcvDeleteLineItem.visibility=View.VISIBLE
            holder.ivEditLineItem.setImageResource(R.drawable.ic_add_blue)
        }

        holder.mcvEditLineItem.setOnClickListener {

                poLineItemParent[itemPosition].locationId=selectedLocationID
               // poLineItemParent[position].amount=holder.edAmount.text.toString()
                poLineItemParent[itemPosition].taxId=selectedTax
               // poLineItemParent[position].totalUnit = holder.edTotalUnit.text.toString().toIntOrNull() ?: 0
                onItemCheck(itemPosition,poLineItemParent[itemPosition])

        }



        setWareHouseLocation(holder,poLineItemModel)
       // setTax( holder,poLineItemModel,itemPosition)

        taxNames = mutableListOf()
        allTaxMap= HashMap()
        for (e in poLineItemModel.getAllTax!!) {
            allTaxMap[e.taxId] = e.taxName
            (taxNames).add( e.taxName)
        }
        getAllTaxAdapter = CustomArrayAdapter(context, R.layout.spinner_layout, taxNames)
        getAllTaxAdapter!!.setDropDownViewResource(R.layout.spinner_layout)
        holder.tvTaxSpinner.adapter = getAllTaxAdapter

        val defaultLocationName = poLineItemModel.getAllTax!!.find { it.taxId == poLineItemModel.taxId }?.taxName
        if (!defaultLocationName.isNullOrEmpty()) {
            val defaultPosition = taxNames.indexOf(defaultLocationName)
            holder.tvTaxSpinner.setSelection(defaultPosition)
            if (defaultPosition != -1) {
                holder.tvTaxSpinner.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            adapterView: AdapterView<*>?,
                            view: View?,
                            i: Int,
                            l: Long
                        ) {
                            val selectedItem = adapterView?.selectedItem.toString()
                            val selectedKey: Int? = allTaxMap.entries.find { it.value == selectedItem }?.key
                            selectedTax = selectedKey!!
                            poLineItemParent[itemPosition].taxId=selectedTax
                            val getTaxPercent = poLineItemModel.getAllTax!!.find { it.taxId ==  selectedTax }?.percentage
                            holder.tvTaxPercent.text=getTaxPercent.toString()
                            setData(itemPosition,holder,poLineItemModel,poLineItemParent)
                        }
                        override fun onNothingSelected(adapterView: AdapterView<*>?) {}
                    }
            }
        }


    }
    private fun setData(
        itemPosition: Int,
        holder: ViewHolder,
        poLineItemModel: CustomPoLineItemSelectionModel,
        poLineItemParent: MutableList<CustomPoLineItemSelectionModel>
    )
    {
        val quantity = poLineItemModel.quantityReceived.toDouble()
        val rate = poLineItemModel.unitPrice
        val discount = poLineItemModel.discountAmount // Discount is a percentage
        val taxPercent = poLineItemModel.getAllTax?.find { it.taxId == poLineItemModel.taxId }?.percentage ?: 0.0

        // Find the index of the item you want to update (e.g., by lineItemId or pos)
        val itemIndex = poLineItemParent.indexOfFirst { it.lineItemId == poLineItemModel.lineItemId }

        // If the item is found, update the lineAmount
        if (itemIndex != -1) {
            val formattedAmount = String.format("%.2f", kotlin.math.abs(calculateLineItemTotal(quantity,rate,discount,taxPercent)))
            holder.tvLineAmount.text =formattedAmount
            poLineItemParent[itemIndex].lineAmount = calculateLineItemTotal(quantity,rate,discount,taxPercent).toString()
           //notifyItemChanged(itemPosition)

        }
    }
    private fun calculateLineItemTotal(
        quantity: Double,
        rate: Int?,
        discount: Double?,
        taxPercent: Double
    ):Double {


        // Calculate the total amount before discount
        val totalAmount = quantity * rate!!

        // Calculate the discount amount (percentage of the total amount)
        val discountAmount = totalAmount * (discount!! / 100)

        // Calculate the amount after discount
        val amountAfterDiscount = totalAmount - discountAmount

        // Calculate the tax amount on the discounted amount
        val taxAmount = amountAfterDiscount * (taxPercent / 100)

        // Calculate the final line amount (after applying tax)
        val lineAmount = amountAfterDiscount + taxAmount
        Log.e("CALCULATIONDATA","totalAmount-${totalAmount}//DISCOUNT-${discountAmount}//AMOUNTAFTERDISCOUNT-${amountAfterDiscount}//TAXAMT-${taxAmount}//LINEAMT-${lineAmount}")

        return lineAmount


    }
    private fun setWareHouseLocation(
        holder: ViewHolder,
        allLocation: CustomPoLineItemSelectionModel
    )
    {
        locationNameList = mutableListOf()
        allLocationHashMap= HashMap()
        for (e in allLocation.getAllLocation) {
            allLocationHashMap[e.locationId] = e.locationName
            (locationNameList).add(e.locationName)
        }
        getAllLocationAdapter = CustomArrayAdapter(context, R.layout.spinner_layout, locationNameList)
        getAllLocationAdapter!!.setDropDownViewResource(R.layout.spinner_layout)
        holder.tvWareHouse.adapter = getAllLocationAdapter

        val defaultLocationName = allLocation.getAllLocation.find { it.locationId == allLocation.locationId }?.locationName
        if (!defaultLocationName.isNullOrEmpty()) {
            val defaultPosition = locationNameList.indexOf(defaultLocationName)
            holder.tvWareHouse.setSelection(defaultPosition)
            if (defaultPosition != -1) {
                holder.tvWareHouse.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            adapterView: AdapterView<*>?,
                            view: View?,
                            i: Int,
                            l: Long
                        ) {
                            val selectedItem = adapterView?.selectedItem.toString()
                            val selectedKey: Int? = allLocationHashMap.entries.find { it.value == selectedItem }?.key
                            selectedLocationID = selectedKey!!
                        }
                        override fun onNothingSelected(adapterView: AdapterView<*>?) {}
                    }
            }
        }
    }
    private fun setTax(
        holder: ViewHolder,
        allTax: CustomPoLineItemSelectionModel,
        position: Int
    )
    {
        taxNames = mutableListOf()
        allTaxMap= HashMap()
        for (e in allTax.getAllTax!!) {
            allTaxMap[e.taxId] = e.taxName
            (taxNames).add( e.taxName)
        }
        getAllTaxAdapter = CustomArrayAdapter(context, R.layout.spinner_layout, taxNames)
        getAllTaxAdapter!!.setDropDownViewResource(R.layout.spinner_layout)
        holder.tvTaxSpinner.adapter = getAllTaxAdapter

        val defaultLocationName = allTax.getAllTax!!.find { it.taxId == allTax.taxId }?.taxName
        if (!defaultLocationName.isNullOrEmpty()) {
            val defaultPosition = taxNames.indexOf(defaultLocationName)
            holder.tvTaxSpinner.setSelection(defaultPosition)
            if (defaultPosition != -1) {
                holder.tvTaxSpinner.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            adapterView: AdapterView<*>?,
                            view: View?,
                            i: Int,
                            l: Long
                        ) {
                            val selectedItem = adapterView?.selectedItem.toString()
                            val selectedKey: Int? = allTaxMap.entries.find { it.value == selectedItem }?.key
                            selectedTax = selectedKey!!
                            val getTaxPercent = allTax.getAllTax!!.find { it.taxId ==  selectedTax }?.percentage
                            holder.tvTaxPercent.text=getTaxPercent.toString()
                            setData(position,holder,allTax,poLineItemParent)
                        }
                        override fun onNothingSelected(adapterView: AdapterView<*>?) {}
                    }
            }
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
        this.weightData = weightData

        Log.d("weightFromInner",weightData)
    }
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val tvSrNo: TextView = itemView.findViewById(R.id.tvSrNo)
        val tvPoNo: TextView = itemView.findViewById(R.id.tvPoNo)

        val tvPoLineNo: TextView = itemView.findViewById(R.id.tvPoLineNo)
        val tvItemCode: TextView = itemView.findViewById(R.id.tvItemCode)
        val tvItemDesc: TextView = itemView.findViewById(R.id.tvItemDesc)
        val tvPuom: TextView = itemView.findViewById(R.id.tvPuom)
        val tvMhType: TextView = itemView.findViewById(R.id.tvMhType)
        val tvPoQty: TextView = itemView.findViewById(R.id.tvPoQty)
        val tvBalQty: TextView = itemView.findViewById(R.id.tvBalQty)
        val tvRate: TextView = itemView.findViewById(R.id.tvRate)
        val tvGRNQty: TextView = itemView.findViewById(R.id.tvGRNQty)
        //val tvWareHouse: TextView = itemView.findViewById(R.id.tvWareHouse)
        val tvBatchCount: TextView = itemView.findViewById(R.id.tvBatchCount)
        val mcvSaveLineItem: MaterialCardView = itemView.findViewById(R.id.mcvSaveLineItem)
        val mcvEditLineItem: MaterialCardView = itemView.findViewById(R.id.mcvEditLineItem)
        val mcvDeleteLineItem: MaterialCardView = itemView.findViewById(R.id.mcvDeleteLineItem)
        val ivEditLineItem: ImageView = itemView.findViewById(R.id.ivEditLineItem)
        val clWareHouse: ConstraintLayout = itemView.findViewById(R.id.clWareHouse)
        val tvWareHouse: Spinner = itemView.findViewById(R.id.tvWareHouse)
        val tvTaxSpinner: Spinner = itemView.findViewById(R.id.tvTaxSpinner)
  /*      val edAmount: EditText = itemView.findViewById(R.id.tvTaxPercent)
        val edTotalUnit: EditText = itemView.findViewById(R.id.tvLineAmount)    */
        val tvTaxPercent: TextView = itemView.findViewById(R.id.tvTaxPercent)
        val tvLineAmount: TextView = itemView.findViewById(R.id.tvLineAmount)
        val tvQc: TextView = itemView.findViewById(R.id.tvIsQc)
        val edDiscount: EditText = itemView.findViewById(R.id.edDiscount)

    }




    /*   private var onItemClickListener: ((Int,PoLineItemSelectionModelNewStore) -> Unit)? = null
       fun setOnItemCheckClickListener(listener: (Int,PoLineItemSelectionModelNewStore) -> Unit) {
           onItemClickListener = listener
       }*/

}