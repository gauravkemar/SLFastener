package com.example.slfastener.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.slfastener.R
import com.example.slfastener.model.BatchInfoListModel
import com.example.slfastener.model.BatchParentListModel
import com.google.android.material.card.MaterialCardView
import kotlin.collections.ArrayList

class CreateBatchesMainRcAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var batchParentListModel = mutableListOf<BatchParentListModel>()
    private var context: Context?=null
    private var expandedList: MutableList<Boolean> = mutableListOf()

    fun setGrnMainList(
        batchParentListModel: ArrayList<BatchInfoListModel>,
        context: Context,
    ) {
        //this.batchParentListModel = batchParentListModel
        this.context=context
        expandedList = MutableList(batchParentListModel.size) { false }
        notifyDataSetChanged()
    }
    init {
        setHasStableIds(true) // To ensure stable item IDs
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            PARENT_VIEW_TYPE -> {
                val itemView = LayoutInflater.from(parent.context).inflate(R.layout.create_batches_parent_item, parent, false)
                ParentViewHolder(itemView)
            }
            CHILD_VIEW_TYPE -> {
                val itemView = LayoutInflater.from(parent.context).inflate(R.layout.create_batchs_item, parent, false)
                ChildViewHolder(itemView)
            }
            ADD_BUTTON_VIEW_TYPE -> {
                val itemView = LayoutInflater.from(parent.context).inflate(R.layout.add_child_batches_btn_item, parent, false)
                AddChildViewHolder(itemView)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }



    override fun onBindViewHolder(holder:  RecyclerView.ViewHolder, position: Int) {

        when (holder.itemViewType) {
            PARENT_VIEW_TYPE -> {
                val parentViewHolder = holder as ParentViewHolder
                parentViewHolder.bind(batchParentListModel[position])
                parentViewHolder.itemView.setOnClickListener {
                    toggleItemExpansion(position)
                }
            }
            CHILD_VIEW_TYPE -> {
                val childViewHolder = holder as ChildViewHolder
                childViewHolder.bind(batchParentListModel[position])
            }
            ADD_BUTTON_VIEW_TYPE -> {
                val addButtonViewHolder = holder as AddChildViewHolder
                addButtonViewHolder.mcvClearBatchBarcode.setOnClickListener {
                    // Handle "Add" button click
                }
            }
        }

    }
    private fun toggleItemExpansion(position: Int) {
        expandedList[position] = !expandedList[position]
        notifyItemChanged(position)
        notifyItemRangeInserted(position + 1, batchParentListModel[position].batchInfoListModel.size)
    }

    inner class ParentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(parentItem: BatchParentListModel) {

        }
    }

    inner class ChildViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(parentItem: BatchParentListModel) {

        }
    }

    inner class AddChildViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mcvClearBatchBarcode: MaterialCardView = itemView.findViewById(R.id.mcvClearBatchBarcode)
    }


    override fun getItemCount(): Int = batchParentListModel.size + 1 // Add 1 for the "Add" button

    override fun getItemViewType(position: Int): Int {
        return when {
            position < batchParentListModel.size -> PARENT_VIEW_TYPE
            position == batchParentListModel.size -> ADD_BUTTON_VIEW_TYPE
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
    override fun getItemId(position: Int): Long {
        // Ensure stable item IDs
        return if (position < batchParentListModel.size) {
            batchParentListModel[position].hashCode().toLong()
        } else {
            (position + 1).toLong() // For the "Add" button view
        }
    }

    companion object {
        private const val PARENT_VIEW_TYPE = 0
        private const val CHILD_VIEW_TYPE = 1
        private const val ADD_BUTTON_VIEW_TYPE = 2
    }

    private var onDataChangeListener: ((BatchParentListModel) -> Unit)? = null
    fun setOnDataChangeListenerListener(listener: (BatchParentListModel) -> Unit) {
        onDataChangeListener = listener
    }
}





/* override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       val view = LayoutInflater.from(parent.context).inflate(R.layout.create_batches_parent_item, parent, false)
       return ViewHolder(view)
   }
*/
/*   override fun getItemCount(): Int {
       if(batchParentListModel.size==0){
           //Toast.makeText(context,"List is empty", Toast.LENGTH_LONG).show()
       }else{
           return batchParentListModel.size
       }
       return batchParentListModel.size
   }*/



/*   fun bind(childItem: ChildData, listener: EditTextChangeListener) {
            // Bind data to child views

            // Example: Listen for EditText changes
            itemView.editText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    // Notify the activity or fragment about the EditText change
                    listener.onEditTextChanged(adapterPosition, s.toString())
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    // No implementation needed
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // No implementation needed
                }
            })
        }*/
/*   class ViewHolder (ItemView: View) : RecyclerView.ViewHolder(ItemView) {
       val tvBatchBarcodeNo: TextView = itemView.findViewById(R.id.tvBatchBarcodeNo)
       //val clBody: ConstraintLayout = itemView.findViewById(R.id.clBody)
       val clHeader: ConstraintLayout = itemView.findViewById(R.id.clHeader)
       val ivDownArrow: ImageView = itemView.findViewById(R.id.ivDownArrow)
       //val rcBatchesMain: RecyclerView = itemView.findViewById(R.id.rcBatchesMain)
   }
*/