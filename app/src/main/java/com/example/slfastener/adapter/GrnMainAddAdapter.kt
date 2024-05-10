package com.example.slfastener.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.slfastener.R
import com.example.slfastener.helper.CustomArrayAdapter
import com.example.slfastener.model.offlinebatchsave.PoLineItemSelectionModelNewStore

class GrnMainAddAdapter (
    private val context: Context,
    private val poLineItemParent: List<PoLineItemSelectionModelNewStore>,
    private val onItemCheck:(Int,PoLineItemSelectionModelNewStore) -> Unit

    ) : RecyclerView.Adapter<GrnMainAddAdapter.ViewHolder>() {

    lateinit var locationNameList: MutableList<String>
    val allLocationHashMap = HashMap<Int, String>()
    var selectedLocationID: Int = 0
    private var weightData: String? = null
    private var getAllLocationAdapter: CustomArrayAdapter? = null

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
        selectedLocationID=poLineItemModel.locationId


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
            holder.tvSaveLineItem.setImageResource(R.drawable.ic_edit_black)
        }
        setWareHouseLocation(holder,poLineItemModel)

        holder.tvSaveLineItem.setOnClickListener {
            if(poLineItemModel.currency.equals("INR"))
            {
                poLineItemParent[position].locationId=selectedLocationID
                onItemCheck(position,poLineItemParent[position])
            }
            else{
                var edGdpo=holder.edGDPO.text.toString().trim()
                if(edGdpo.isNotEmpty())
                {
                    poLineItemParent[position].locationId=selectedLocationID
                    onItemCheck(position,poLineItemParent[position])
                }
                else
                {
                    Toast.makeText(context,"Please enter GDPO number",Toast.LENGTH_SHORT).show()
                }
            }

        }
        setGDPO(holder,poLineItemModel)
        setWareHouseLocation(holder,poLineItemModel)
    }
    private fun setWareHouseLocation(
        holder: ViewHolder,
        allLocation: PoLineItemSelectionModelNewStore
    )
    {
        for (e in allLocation.getAllLocation) {
            allLocationHashMap[e.locationId] = e.locationName
            (locationNameList).add(e.locationName)
        }

        getAllLocationAdapter = CustomArrayAdapter(context, android.R.layout.simple_spinner_item, locationNameList)
        getAllLocationAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
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







    private fun setGDPO(holder: ViewHolder, item: PoLineItemSelectionModelNewStore) {
        if (item.currency.equals("INR"))
        {
            holder.edGDPO.visibility= View.GONE
        }
        else
        {
            holder.edGDPO.visibility= View.VISIBLE
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
        //val tvWareHouse: TextView = itemView.findViewById(R.id.tvWareHouse)
        val tvBatchCount: TextView = itemView.findViewById(R.id.tvBatchCount)
        val tvSaveLineItem: ImageButton = itemView.findViewById(R.id.tvSaveLineItem)
        val tvDeleteLineItem: ImageButton = itemView.findViewById(R.id.tvDeleteLineItem)
        val clWareHouse: ConstraintLayout = itemView.findViewById(R.id.clWareHouse)
        val tvWareHouse: Spinner = itemView.findViewById(R.id.tvWareHouse)
    }

 /*   private var onItemClickListener: ((Int,PoLineItemSelectionModelNewStore) -> Unit)? = null
    fun setOnItemCheckClickListener(listener: (Int,PoLineItemSelectionModelNewStore) -> Unit) {
        onItemClickListener = listener
    }*/

}