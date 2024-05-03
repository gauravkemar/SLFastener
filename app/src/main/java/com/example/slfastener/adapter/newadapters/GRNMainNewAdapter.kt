package com.example.slfastener.adapter.newadapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.slfastener.R
import com.example.slfastener.adapter.demoAdapter.CreateBatchesSingleList
import com.example.slfastener.model.PoLineItemSelectionModel
import com.google.android.material.card.MaterialCardView

class GRNMainNewAdapter (
    private val poLineItemParent: List<PoLineItemSelectionModel>,
    private val onInnerItemClick: (Int,PoLineItemSelectionModel) -> Unit,

    ) : RecyclerView.Adapter<GRNMainNewAdapter.ViewHolder>() {
    private var weightData: String? = null

    private val childAdapters = mutableMapOf<Int, CreateBatchesSingleList>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.grn_main_new_add, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var poLineItemModel: PoLineItemSelectionModel = poLineItemParent.get(position)

        holder.tvPoNo.setText(poLineItemModel.poNumber)
        holder.tvGdPoNo.setText(poLineItemModel.GDPONumber)
        holder.tvLineItemDesc.setText(poLineItemModel.itemName)
        holder.tvItemDesc.setText(poLineItemModel.itemDescription)
        holder.tvPuom.setText(poLineItemModel.pouom)
        holder.tvEpiryDt.setText(poLineItemModel.ExpiryDate)
        holder.tvQty.setText(poLineItemModel.ReceivedQty)
        holder.tvQuantityPrice.setText(poLineItemModel.poUnitPrice.toString())
        if (poLineItemModel.poQuantity.toString() != null) {
            holder.tvOpenQuantity.setText(poLineItemModel.poQuantity.toString())
        }
        holder.itemView.setOnClickListener {
            if(holder.cl2.visibility==View.VISIBLE)
            {
                holder.cl2.visibility=View.GONE
            }
            else{
                holder.cl2.visibility=View.VISIBLE
            }
        }
        holder.rcBatches.apply {
            if(poLineItemModel.batchInfoListModel!=null)
            {
                layoutManager = LinearLayoutManager(holder.rcBatches.context, RecyclerView.VERTICAL, false)
                adapter = CreateBatchesSingleList(poLineItemModel.batchInfoListModel!!) { position,innerItem ->
                    poLineItemModel.batchInfoListModel?.set(position, innerItem)
                    adapter!!.notifyItemChanged(position)
                    onInnerItemClick(position,poLineItemModel)
                }

            }
        }
        holder.rcBatches.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            val adapter = CreateBatchesSingleList(poLineItemModel.batchInfoListModel!!) { position, innerItem ->
                poLineItemModel.batchInfoListModel?.set(position, innerItem)
                notifyItemChanged(position)
                onInnerItemClick(position, poLineItemModel)
            }
            this.adapter = adapter
            childAdapters[position] = adapter
        }
        holder.btCreatebatches.setOnClickListener {
            onItemClickListener?.invoke(position,poLineItemParent[position])
        }
    }

    override fun getItemCount(): Int {
        if (poLineItemParent.size == 0) {
            //Toast.makeText(context,"List is empty", Toast.LENGTH_LONG).show()
        } else {
            return poLineItemParent.size
        }
        return poLineItemParent.size
    }

    fun updateWeightValue(weightData: String) {
        this.weightData = weightData

        Log.d("weightFromInner",weightData)
    }
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val tvPoNo: TextView = itemView.findViewById(R.id.tvPoNo)
        val tvGdPoNo: TextView = itemView.findViewById(R.id.tvGdPoNo)
        val tvLineItemDesc: TextView = itemView.findViewById(R.id.tvLineItemDesc)
        val tvItemDesc: TextView = itemView.findViewById(R.id.tvItemDesc)
        val tvPuom: TextView = itemView.findViewById(R.id.tvPuom)
        val tvQty: TextView = itemView.findViewById(R.id.tvQty)
        val tvEpiryDt: TextView = itemView.findViewById(R.id.tvEpiryDt)
        val tvOpenQuantity: TextView = itemView.findViewById(R.id.tvOpenQty)
        val btCreatebatches: MaterialCardView = itemView.findViewById(R.id.btCreatebatches)
        val tvQuantityPrice: TextView = itemView.findViewById(R.id.tvQuantityPrice)
        val cl2: ConstraintLayout = itemView.findViewById(R.id.cl2)
        val rcBatches: RecyclerView = itemView.findViewById(R.id.rcBatches)
    }

    private var onItemClickListener: ((Int,PoLineItemSelectionModel) -> Unit)? = null
    fun setOnItemCheckClickListener(listener: (Int,PoLineItemSelectionModel) -> Unit) {
        onItemClickListener = listener
    }

}