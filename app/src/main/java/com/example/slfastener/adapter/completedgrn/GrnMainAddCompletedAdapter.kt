package com.example.slfastener.adapter.completedgrn

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.slfastener.R
import com.example.slfastener.helper.CustomArrayAdapter
import com.example.slfastener.model.offlinebatchsave.CustomPoLineItemSelectionModel

class GrnMainAddCompletedAdapter (
    private val context: Context,
    private val poLineItemParent: MutableList<CustomPoLineItemSelectionModel>,
    private val itemDescription:(itemDesc:String)->Unit,
    private val onItemCheck:(Int, CustomPoLineItemSelectionModel) -> Unit,
) : RecyclerView.Adapter<GrnMainAddCompletedAdapter.ViewHolder>() {

    lateinit var locationNameList: MutableList<String>
    var allLocationHashMap = HashMap<Int, String>()
    var selectedLocationID: Int = 0
    private var weightData: String? = null
    private var getAllLocationAdapter: CustomArrayAdapter? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.grn_main_po_line_item_complete, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var poLineItemModel: CustomPoLineItemSelectionModel = poLineItemParent.get(position)

        holder.tvSrNo.setText("${position+1}")
        holder.tvPoNo.setText(poLineItemModel.poNumber)
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

        setWareHouseLocation(holder,poLineItemModel)
        holder.tvSaveLineItem.setOnClickListener {
            poLineItemParent[position].locationId=selectedLocationID
            onItemCheck(position,poLineItemParent[position])
        }
        holder.tvItemDesc.setOnClickListener {
            itemDescription(poLineItemModel.itemDescription)
        }
        setGDPO(holder,poLineItemModel)

        if(poLineItemModel.isQCRequired)
        {
            holder.tvQc.setText("QC")
        }
        else{
            holder.tvQc.setText("")
        }

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

        val defaultLocationName = allLocation.getAllLocation.find { it.locationId == allLocation.locationId }?.locationName
        if (!defaultLocationName.isNullOrEmpty()) {
            holder.tvWareHouse.setText(defaultLocationName)
        }

    }

    private fun setGDPO(holder: ViewHolder, item: CustomPoLineItemSelectionModel) {
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

        val tvWareHouse: TextView = itemView.findViewById(R.id.tvWareHouse)
        val tvQc: TextView = itemView.findViewById(R.id.tvIsQc)
    }




    /*   private var onItemClickListener: ((Int,PoLineItemSelectionModelNewStore) -> Unit)? = null
       fun setOnItemCheckClickListener(listener: (Int,PoLineItemSelectionModelNewStore) -> Unit) {
           onItemClickListener = listener
       }*/

}