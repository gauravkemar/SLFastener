package com.example.slfastener.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.slfastener.R
import com.example.slfastener.model.GetSuppliersPOsDDLResponse
import com.example.slfastener.model.grn.GrnMainAddListResponse

class GRNSelectPoAdapter : RecyclerView.Adapter<GRNSelectPoAdapter.ViewHolder>() {

    private var grnMainModel = mutableListOf<GetSuppliersPOsDDLResponse>()
    private var context: Context?=null
    fun setGrnMainList(
        grnMainModel: ArrayList<GetSuppliersPOsDDLResponse>,
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
        }*/

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
}