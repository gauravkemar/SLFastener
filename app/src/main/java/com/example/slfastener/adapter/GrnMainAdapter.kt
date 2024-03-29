package com.example.slfastener.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.slfastener.R
import com.example.slfastener.model.grn.GrnMainListResponse
import com.example.slfastener.model.grnmain.GetFilteredGRNResponse
import java.util.ArrayList

class GrnMainAdapter : RecyclerView.Adapter<GrnMainAdapter.ViewHolder>() {

    private var grnMainModel = mutableListOf<GetFilteredGRNResponse>()
    private var context: Context?=null
    fun setGrnMainList(
        grnMainModel: ArrayList<GetFilteredGRNResponse>,
        context: Context,
    ) {
        this.grnMainModel = grnMainModel
        this.context=context
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.grn_main_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var grnMainModel:GetFilteredGRNResponse=grnMainModel.get(position)

        holder.tvSrNo.setText(position.toString())
        holder.tvKGRNNO.setText(grnMainModel.kgrnNumber)
        holder.tvBPCodeName.setText(grnMainModel.bpName)
        holder.tvType.setText(grnMainModel.transactionType)
        holder.tvInitiatedOn.setText(grnMainModel.createdDate)
        holder.tvStatus.setText(grnMainModel.grnStatus)

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
        val tvSrNo: TextView = itemView.findViewById(R.id.tvSrNo)
        val tvKGRNNO: TextView = itemView.findViewById(R.id.tvKGRNNO)
        val tvBPCodeName: TextView = itemView.findViewById(R.id.tvBPCodeName)
        val tvType: TextView = itemView.findViewById(R.id.tvType)
        val tvInitiatedOn: TextView = itemView.findViewById(R.id.tvInitiatedOn)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)

    }

}