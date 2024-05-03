package com.example.slfastener.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.slfastener.R
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

        holder.tvSrNo.setText("${position+1}")
        holder.tvKGRNNO.setText(grnMainModel.kgrnNumber)
        holder.tvKGRNDate.setText(grnMainModel.kgrnDate)
        holder.tvInvoiceNo.setText(grnMainModel.invoiceNumber)
        holder.tvInvoiceDate.setText(grnMainModel.invoiceDate)
        holder.tvBPCode.setText(grnMainModel.bpCode)
        holder.tvBPCodeName.setText(grnMainModel.bpName)
        holder.tvType.setText(grnMainModel.transactionType)
        holder.tvCurrency.setText(grnMainModel.currency)
        holder.tvStatus.setText(grnMainModel.grnStatus)

        holder.ivAdd.setOnClickListener {

        }
        holder.ivEdit.setOnClickListener {

        }
        holder.ivDelete.setOnClickListener {

        }
        holder.ivPrint.setOnClickListener {

        }



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
        val tvKGRNDate: TextView = itemView.findViewById(R.id.tvKGRNDate)
        val tvInvoiceNo: TextView = itemView.findViewById(R.id.tvInvoiceNo)
        val tvInvoiceDate: TextView = itemView.findViewById(R.id.tvInvoiceDate)
        val tvBPCode: TextView = itemView.findViewById(R.id.tvBPCode)
        val tvBPCodeName: TextView = itemView.findViewById(R.id.tvBPCodeName)
        val tvType: TextView = itemView.findViewById(R.id.tvType)
        val tvCurrency: TextView = itemView.findViewById(R.id.tvCurrency)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val ivAdd: ImageButton = itemView.findViewById(R.id.ivAdd)
        val ivEdit: ImageButton = itemView.findViewById(R.id.ivEdit)
        val ivDelete: ImageButton = itemView.findViewById(R.id.ivDelete)
        val ivPrint: ImageButton = itemView.findViewById(R.id.ivPrint)

    }

}