package com.example.slfastener.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.slfastener.R
import com.example.slfastener.model.GetSuppliersPOsDDLResponse
import com.example.slfastener.model.offlinebatchsave.GrnLineItemUnitStore


class GRNSelectPoAdapter (private val originalList: MutableList<GetSuppliersPOsDDLResponse>) :
    RecyclerView.Adapter<GRNSelectPoAdapter.ViewHolder>() {

    private var dataList: MutableList<GetSuppliersPOsDDLResponse> = originalList.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.supplier_po_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemPosition = holder.layoutPosition
        val grnModel: GetSuppliersPOsDDLResponse = dataList[itemPosition]

        holder.tvPo.text = grnModel.text
        holder.tvCode.text = grnModel.code
        holder.cbPo.isChecked = grnModel.isChecked
        // Set a tag to prevent checkbox from firing off on bind
        holder.cbPo.tag = position
        var grnIsUpdatabble=dataList.any{ it.isUpdatable }
        if(grnIsUpdatabble)
        {
            holder.cbPo.isEnabled=false
        }
        else{
            holder.cbPo.setOnCheckedChangeListener { _, isChecked ->
                val pos = holder.adapterPosition
                if (pos != RecyclerView.NO_POSITION && holder.cbPo.tag == pos) {
                    grnModel.isChecked = isChecked
                    if (isChecked) {
                        grnModel.code?.let { filterListByCode(it) }
                    } else {

                        if (dataList.none { it.isChecked }) {
                            resetFilter()
                        }
                    }
                }
            }

        }


    }

    override fun getItemCount(): Int = dataList.size

    private fun filterListByCode(code: String) {
        dataList = originalList.filter { it.code == code }.toMutableList()
        notifyDataSetChanged()
    }
    private fun resetFilter() {
        dataList = originalList.toMutableList()
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cbPo: CheckBox = itemView.findViewById(R.id.cbPo)
        val tvPo: TextView = itemView.findViewById(R.id.tvPo)
        val tvCode: TextView = itemView.findViewById(R.id.tvCode)
    }
}









/*
class GRNSelectPoAdapter(
    private var grnMainModel: MutableList<GetSuppliersPOsDDLResponse>,
    private val onItemClickListener: ( Int,GetSuppliersPOsDDLResponse) -> Unit,
    private val onItemUncheckClickListener: ( Int,GetSuppliersPOsDDLResponse) -> Unit
) : RecyclerView.Adapter<GRNSelectPoAdapter.ViewHolder>() {
    private var selectedCode: String? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.supplier_po_list_item, parent, false)
        return ViewHolder(view)
    }


    fun setSelectedCode(code: String?) {
        selectedCode = code
        notifyDataSetChanged()
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemPosition = holder.layoutPosition
        val grnModel: GetSuppliersPOsDDLResponse = grnMainModel[itemPosition]

        holder.tvPo.text = grnModel.text
        holder.cbPo.isChecked = grnModel.isChecked

        if (selectedCode == null || selectedCode == grnModel.code) {
            holder.tvPo.text = grnModel.text
            holder.cbPo.isChecked = grnModel.isChecked

            holder.cbPo.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    onItemClickListener?.invoke(position, grnModel)
                    Log.d("fromclick", grnModel.toString())
                } else {
                    onItemUncheckClickListener?.invoke(position, grnModel)
                    Log.d("fromclick", grnModel.toString())
                }
            }
        } else {
            // Hide the item if the selected code does not match
            holder.itemView.visibility = View.GONE
           // holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
        }
    }

    override fun getItemCount(): Int = grnMainModel.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cbPo: CheckBox = itemView.findViewById(R.id.cbPo)
        val tvPo: TextView = itemView.findViewById(R.id.tvPo)
    }
}

*/



/*
class GRNSelectPoAdapter : RecyclerView.Adapter<GRNSelectPoAdapter.ViewHolder>() {

    private var grnMainModel = mutableListOf<GetSuppliersPOsDDLResponse>()
    private var context: Context?=null
    fun setGrnMainList(
        grnMainModel: MutableList<GetSuppliersPOsDDLResponse>,
        context: Context,
    ) {
        this.grnMainModel = grnMainModel
        this.context=context
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.supplier_po_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var grnModel: GetSuppliersPOsDDLResponse =grnMainModel.get(position)
        holder.tvPo.setText(grnModel.text)
        holder.cbPo.setOnCheckedChangeListener(null) // Reset listener to avoid unwanted calls
        holder.cbPo.isChecked = false// Set the initial state of the CheckBox

        holder.cbPo.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                onItemClickListener?.invoke(grnMainModel[position])
                Log.d("fromclick", grnMainModel[position].toString())
            } else {
                onItemUncheckClickListener?.invoke(grnMainModel[position])
                Log.d("fromclick", grnMainModel[position].toString())
            }
        }


        */
/*holder.cbPo.setOnCheckedChangeListener { buttonView, isChecked ->
            holder.cbPo.isChecked = isChecked
            onItemClickListener?.let {
                if (isChecked) {
                    onItemClickListener?.let {
                        it(grnMainModel[position])
                        Log.d("fromclick",it(grnMainModel[position]).toString())
                    }
                } else {
                    onItemClickListener?.let {
                        it(grnMainModel[position])
                        Log.d("fromclick",it(grnMainModel[position]).toString())
                    }
                }
            }
        }*//*


    }

    override fun getItemCount(): Int {
        if(grnMainModel.size==0){
            //Toast.makeText(context,"List is empty", Toast.LENGTH_LONG).show()
        }else{
            return grnMainModel.size
        }
        return grnMainModel.size
    }
    class ViewHolder (ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val cbPo: CheckBox = itemView.findViewById(R.id.cbPo)
        val tvPo: TextView = itemView.findViewById(R.id.tvPo)


    }
    private var onItemClickListener: ((GetSuppliersPOsDDLResponse) -> Unit)? = null
    private var onItemUncheckClickListener: ((GetSuppliersPOsDDLResponse) -> Unit)? = null


    fun setOnItemCheckClickListener(listener: (GetSuppliersPOsDDLResponse) -> Unit) {
        onItemClickListener = listener
    }
    fun setOnItemUncheckClickListener(listener: (GetSuppliersPOsDDLResponse) -> Unit) {
        onItemUncheckClickListener = listener
    }
}*/
