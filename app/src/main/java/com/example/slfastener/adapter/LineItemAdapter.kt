package com.example.slfastener.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.slfastener.R
import com.example.slfastener.model.PoLineItem
import com.example.slfastener.model.PoLineItemSelectionModel

class LineItemAdapter  : RecyclerView.Adapter<LineItemAdapter.ViewHolder>() {

    private var poLineItemMainModel = mutableListOf<PoLineItemSelectionModel>()
    private var context: Context?=null
    fun setGrnMainList(
        poLineItemMainModel: ArrayList<PoLineItemSelectionModel>,
        context: Context,
    ) {
        this.poLineItemMainModel =poLineItemMainModel
        this.context=context
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.line_item_selection, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var poLineItemModel: PoLineItemSelectionModel =poLineItemMainModel.get(position)

        holder.tvColumnOne.setText(poLineItemModel.posapLineItemNumber)
        holder.tvColumnTwo.setText(poLineItemModel.itemCode)
        holder.tvColumnThree.setText(poLineItemModel.itemName)
        holder.tvColumnFour.setText(poLineItemModel.itemDescription)
        //holder.tvColumnFive.setText(poLineItemModel.poQuantity)
        holder.tvColumnSix.setText(poLineItemModel.pouom)

        holder.cbPcbLineItem.setOnCheckedChangeListener(null) // Reset listener to avoid unwanted calls
        holder.cbPcbLineItem.isChecked = false// Set the initial state of the CheckBox

        holder.cbPcbLineItem.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                onItemClickListener?.invoke(poLineItemMainModel[position])
                Log.d("fromclick", poLineItemMainModel[position].toString())
            } else {
                onItemUncheckClickListener?.invoke(poLineItemMainModel[position])
                Log.d("fromclick", poLineItemMainModel[position].toString())
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
        if(poLineItemMainModel.size==0){
            //Toast.makeText(context,"List is empty", Toast.LENGTH_LONG).show()
        }else{
            return poLineItemMainModel.size
        }
        return poLineItemMainModel.size
    }
    class ViewHolder (ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val cbPcbLineItem: CheckBox = itemView.findViewById(R.id.cbLineItem)
        val tvColumnOne: TextView = itemView.findViewById(R.id.tvColumnOne)
        val tvColumnTwo: TextView = itemView.findViewById(R.id.tvColumnTwo)
        val tvColumnThree: TextView = itemView.findViewById(R.id.tvColumnThree)
        val tvColumnFour: TextView = itemView.findViewById(R.id.tvColumnFour)
        val tvColumnFive: TextView = itemView.findViewById(R.id.tvColumnFive)
        val tvColumnSix: TextView = itemView.findViewById(R.id.tvColumnSix)


    }
    private var onItemClickListener: ((PoLineItemSelectionModel) -> Unit)? = null
    private var onItemUncheckClickListener: ((PoLineItemSelectionModel) -> Unit)? = null


    fun setOnItemCheckClickListener(listener: (PoLineItemSelectionModel) -> Unit) {
        onItemClickListener = listener
    }
    fun setOnItemUncheckClickListener(listener: (PoLineItemSelectionModel) -> Unit) {
        onItemUncheckClickListener = listener
    }
}