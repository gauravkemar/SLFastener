package com.example.slfastener.adapter.gradapters.completedGr

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.slfastener.R
import com.example.slfastener.helper.CustomArrayAdapter
import com.example.slfastener.model.goodsreceipt.GetAllItemMasterSelection

class GRMainAddCompletedAdapter (
    private val context: Context,
    private val poLineItemParent: MutableList<GetAllItemMasterSelection>,
    private val itemDescription:(itemDesc:String)->Unit,
    private val onItemCheck:(Int, GetAllItemMasterSelection) -> Unit,
) : RecyclerView.Adapter<GRMainAddCompletedAdapter.ViewHolder>() {

    lateinit var locationNameList: MutableList<String>
    var allLocationHashMap = HashMap<Int, String>()
    var selectedLocationID: Int = 0
    var defaultLocationCode:String=""

    private var getAllLocationAdapter: CustomArrayAdapter? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.completed_gr_main_list_item, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var poLineItemModel: GetAllItemMasterSelection = poLineItemParent.get(position)
        holder.tvSrNo.setText("${position+1}")
        holder.tvItemCode.setText(poLineItemModel.code)
        holder.tvItemDesc.setText(poLineItemModel.description)
        holder.tvMhType.setText(poLineItemModel.mhType)
        holder.tvItemName.setText(poLineItemModel.name)
        holder.tvBalQty.setText(poLineItemModel.balQty.toString())
        holder.tvBatchCount.setText("${poLineItemModel.grLineItemUnit?.size ?: 0}")
        holder.tvPuom.setText(poLineItemModel.uom)
        defaultLocationCode=poLineItemModel.defaultLocationCode
        holder.tvItemDesc.setOnClickListener {
            itemDescription(poLineItemModel.description)
        }

        holder.tvSaveLineItem.setOnClickListener {
            poLineItemParent[position].LocationId=selectedLocationID
            onItemCheck(position,poLineItemParent[position])
        }
        setWareHouseLocation(holder,poLineItemModel)
        holder.tvWareHouse.isEnabled=false

    }
    private fun setWareHouseLocation(
        holder: ViewHolder,
        allLocation: GetAllItemMasterSelection
    )
    {
        locationNameList = mutableListOf()
        allLocationHashMap= HashMap()
        for (e in allLocation.getAllLocation) {
            allLocationHashMap[e.locationId] = e.locationName
            (locationNameList).add(e.locationName)
        }
        getAllLocationAdapter = CustomArrayAdapter(context, android.R.layout.simple_spinner_item, locationNameList)
        getAllLocationAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        holder.tvWareHouse.adapter = getAllLocationAdapter

        val defaultLocationName = allLocation.getAllLocation.find { it.locationCode == allLocation.defaultLocationCode }?.locationName
        if (!defaultLocationName.isNullOrEmpty()) {
            val defaultPosition = locationNameList.indexOf(defaultLocationName)
            holder.tvWareHouse.setSelection(defaultPosition)
            selectedLocationID= allLocation.getAllLocation.find { it.locationCode == allLocation.defaultLocationCode }?.locationId!!.toInt()
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

    override fun getItemCount(): Int {
        if (poLineItemParent.size == 0) {
        } else {
            return poLineItemParent.size
        }
        return poLineItemParent.size
    }
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val tvSrNo: TextView = itemView.findViewById(R.id.tvSrNo)
        val tvItemName: TextView = itemView.findViewById(R.id.tvItemName)
        val tvItemCode: TextView = itemView.findViewById(R.id.tvItemCode)
        val tvItemDesc: TextView = itemView.findViewById(R.id.tvItemDesc)
        val tvPuom: TextView = itemView.findViewById(R.id.tvPuom)
        val tvMhType: TextView = itemView.findViewById(R.id.tvMhType)
        val tvBalQty: TextView = itemView.findViewById(R.id.tvBalQty)
        val tvBatchCount: TextView = itemView.findViewById(R.id.tvBatchCount)
        val tvSaveLineItem: ImageButton = itemView.findViewById(R.id.tvSaveLineItem)
        val tvDeleteLineItem: ImageButton = itemView.findViewById(R.id.tvDeleteLineItem)
        val tvWareHouse: Spinner = itemView.findViewById(R.id.tvWareHouse)
        val tvQc: TextView = itemView.findViewById(R.id.tvQc)

    }





}