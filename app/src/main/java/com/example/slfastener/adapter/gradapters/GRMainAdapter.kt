package com.example.slfastener.adapter.gradapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.slfastener.R
import com.example.slfastener.model.goodsreceipt.GetAllGRResponse
import com.example.slfastener.model.grnmain.GetFilteredGRNResponse
import com.example.slfastener.model.offlinebatchsave.GrnLineItemUnitStore
import java.text.SimpleDateFormat
import java.util.ArrayList

class GRMainAdapter(private val editItem: (Int) -> Unit) :
    RecyclerView.Adapter<GRMainAdapter.ViewHolder>() {

    private var grnMainModel = mutableListOf<GetAllGRResponse>()
    private var context: Context? = null
    fun setGrnMainList(
        grnMainModel: ArrayList<GetAllGRResponse>,
        context: Context,
    ) {
        this.grnMainModel = grnMainModel
        this.context = context
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.gr_main_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var grnMainModel: GetAllGRResponse = grnMainModel.get(position)

        holder.tvSrNo.setText("${position + 1}")
        holder.tvKGRNNO.setText(grnMainModel.kgrNumber)

        if (grnMainModel.kgrDate != null) {
            holder.tvKGRNDate.setText(convertDateFormatKgrn(grnMainModel.kgrDate))
        }

        holder.tvRemark.setText(grnMainModel.remark)
        holder.tvBusinessPartner.setText(grnMainModel.bpName)
        holder.tvBPCode.setText(grnMainModel.bpCode)


        holder.ivAdd.setOnClickListener {

        }
        holder.ivEdit.setOnClickListener {
            editItem(grnMainModel.grId)
        }
        holder.ivDelete.setOnClickListener {

        }
        holder.ivPrint.setOnClickListener {

        }

    }

    fun convertDateFormatKgrn(inputDate: String): String {

        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS")
        val date = inputFormat.parse(inputDate)
        val outputFormat = SimpleDateFormat("dd-MM-yyyy")
        return outputFormat.format(date)
    }

    fun convertDateFormatInvoice(inputDate: String): String {

        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val date = inputFormat.parse(inputDate)
        val outputFormat = SimpleDateFormat("dd-MM-yyyy")
        return outputFormat.format(date)
    }

    override fun getItemCount(): Int {
        if (grnMainModel.size == 0) {
            //Toast.makeText(context,"List is empty", Toast.LENGTH_LONG).show()
        } else {
            return grnMainModel.size
        }
        return grnMainModel.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val tvSrNo: TextView = itemView.findViewById(R.id.tvSrNo)
        val tvKGRNNO: TextView = itemView.findViewById(R.id.tvKGRNNO)
        val tvKGRNDate: TextView = itemView.findViewById(R.id.tvKGRNDate)

        val tvRemark: TextView = itemView.findViewById(R.id.tvRemark)

        val tvBusinessPartner: TextView = itemView.findViewById(R.id.tvBusinessPartner)
        val tvBPCode: TextView = itemView.findViewById(R.id.tvBPCode)
        val ivAdd: ImageButton = itemView.findViewById(R.id.ivAdd)
        val ivEdit: ImageButton = itemView.findViewById(R.id.ivEdit)
        val ivDelete: ImageButton = itemView.findViewById(R.id.ivDelete)
        val ivPrint: ImageButton = itemView.findViewById(R.id.ivPrint)

    }

}