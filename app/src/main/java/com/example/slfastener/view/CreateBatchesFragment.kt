package com.example.slfastener.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.slfastener.R
import com.example.slfastener.databinding.FragmentCreateBatchesBinding

class CreateBatchesFragment : Fragment() {
    lateinit var binding:FragmentCreateBatchesBinding
  /*  private lateinit var usbCommunicationManager: UsbCommunicationManager
    lateinit var createBatchesList: ArrayList<BatchInfoListModel>
    lateinit var poLineItemSelectionModel: PoLineItemSelectionModel
    lateinit var  createBatchesListRecyclerList: ArrayList<BatchInfoListModel>
    private var createBatchesMainRcAdapter: CreateBatchesDemoAdapter? = null
    // Define a debounce period in milliseconds
    private val DEBOUNCE_PERIOD = 1000L

    // Variable to keep track of the last data update time
    private var lastUpdateTime: Long = 0
    private var previousData: String? = null


    var callback: OnCancelListener? = null
    interface OnCancelListener {
        fun onCancel()
    }
    private lateinit var createBatchesListMap: HashMap<String, ArrayList<BatchInfoListModel>>
    fun setPoModel(poModel: PoLineItemSelectionModel) {
        this.poLineItemSelectionModel = poModel
    }*/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_create_batches, container, false)
/*
        usbCommunicationManager = UsbCommunicationManager(requireActivity())
        createBatchesList = ArrayList()
        createBatchesListRecyclerList = ArrayList()
        createBatchesListMap = HashMap()
*/



      /*  createBatchesMainRcAdapter?.setOnItemAddClickListener {
            addNewExistingBatch(it)

        }*/
 /*       createBatchesMainRcAdapter?.setOnItemUpdatelickListener {
            updateBatches(it)
        }

        createBatchesMainRcAdapter?.setOnItemDeleteClickListener {
            deleteExistingBatch(it)
        }
*/
/*

        usbCommunicationManager.receivedData.observe(requireActivity()) { data ->
            val currentTime = System.currentTimeMillis()
            // Check if the time elapsed since the last update is greater than the debounce period
            if (currentTime - lastUpdateTime >= DEBOUNCE_PERIOD) {
                lastUpdateTime = currentTime
                // Check if the current data is different from the previous one
             */
/*   if (data != null && data != previousData) {
                    // Update the previous data value
                    previousData = data
                    createBatchesMainRcAdapter?.updateWeightValue(data)
                }*//*


                  if (data != null ) {
                    createBatchesMainRcAdapter?.updateWeightValue(data)
                }
                print("weightfromgrnadd: $data")
            }
        }
*/


   /*     setInfoValues(poLineItemSelectionModel)

        binding.mcvClearBatchBarcode.setOnClickListener {
            binding.edBatchNo.setText("")
        }



        binding.mcvAddBatches.setOnClickListener {
            addNewBatch(poLineItemSelectionModel)
        }

        binding.mcvCancel.setOnClickListener {
            callback?.onCancel()
        }*/
    /*    createBatchesMainRcAdapter?.setOnItemDeleteClickListener {


        }
*/
        return binding.root
    }
/*

    private fun deleteExistingBatch(poModel: BatchInfoListModel) {

        val batchBarcodeNo = poModel.batchBarcodeNo
        if (createBatchesListMap.containsKey(batchBarcodeNo)) {
            val batchList = createBatchesListMap[batchBarcodeNo]
            batchList?.let {
                val iterator = it.iterator()
                while (iterator.hasNext()) {
                    val batch = iterator.next()
                    if (batch.generatedBarcodeNo == poModel.generatedBarcodeNo) {
                        iterator.remove()
                        break
                    }
                }
                // If the list becomes empty after removal, no need to remove the key
            }
        }
        for (r in createBatchesList) {
            if (r.generatedBarcodeNo == poModel.generatedBarcodeNo) {
                createBatchesList.remove(poModel)
            }
        }
        addUpdateDeleteList(createBatchesList)


    }
    private fun addNewExistingBatch(poModel: BatchInfoListModel) {
        val existingBatch =
            createBatchesList.find { it.generatedBarcodeNo == poModel.generatedBarcodeNo }
        if (existingBatch != null) {
            existingBatch.apply {
                ReceivedQty = poModel.ReceivedQty
                ExpiryDate = poModel.ExpiryDate
                GDPONumber = poModel.GDPONumber
                itemCode = poModel.itemCode
                itemDescription = poModel.itemDescription
                itemName = poModel.itemName
                lineNumber = poModel.lineNumber
                materialType = poModel.materialType
                poId = poModel.poId
                poLineItemId = poModel.poLineItemId
                poNumber = poModel.poNumber
                poQuantity = poModel.poQuantity
                poUnitPrice = poModel.poUnitPrice
                posapLineItemNumber = poModel.posapLineItemNumber
                pouom = poModel.pouom
                batchBarcodeNo = poModel.batchBarcodeNo
            }


        }


        // Check if the batch barcode number already exists in the map
        if (createBatchesListMap.containsKey(poModel.batchBarcodeNo)) {
            // Update the existing item in the list with the changes from poModel
            createBatchesListMap[poModel.batchBarcodeNo]?.let { batchList ->
                val existingBatch =
                    batchList.find { it.generatedBarcodeNo == poModel.generatedBarcodeNo }
                existingBatch?.apply {
                    ReceivedQty = poModel.ReceivedQty
                    ExpiryDate = poModel.ExpiryDate
                    GDPONumber = poModel.GDPONumber
                    itemCode = poModel.itemCode
                    itemDescription = poModel.itemDescription
                    itemName = poModel.itemName
                    lineNumber = poModel.lineNumber
                    materialType = poModel.materialType
                    poId = poModel.poId
                    poLineItemId = poModel.poLineItemId
                    poNumber = poModel.poNumber
                    poQuantity = poModel.poQuantity
                    poUnitPrice = poModel.poUnitPrice
                    posapLineItemNumber = poModel.posapLineItemNumber
                    pouom = poModel.pouom
                    batchBarcodeNo = poModel.batchBarcodeNo
                }

            }
        }
        if (createBatchesListMap.containsKey(poModel.batchBarcodeNo)) {
            // Add the new item to the existing batch
            createBatchesListMap[poModel.batchBarcodeNo]?.add(
                createNewExisitingBatchInfo(poModel)
            )
        } else {
            // Create a new ArrayList and add the new item to it
            val newBatchList =
                arrayListOf(createNewExisitingBatchInfo(poModel))
            createBatchesListMap[poModel.batchBarcodeNo] = newBatchList
        }
        createBatchesList.add(createNewExisitingBatchInfo(poModel))

        addUpdateDeleteList(createBatchesList)
    }
    private fun createNewExisitingBatchInfo(
        poModel: BatchInfoListModel,
    ): BatchInfoListModel {

        val latestNumber = createBatchesList
            .filter { it.batchBarcodeNo == poModel.batchBarcodeNo }
            .flatMap {
                it.generatedBarcodeNo
                    .removePrefix("${poModel.batchBarcodeNo}-")
                    .split("-")
                    .lastOrNull()
                    ?.toIntOrNull()
                    ?.let { listOf(it) }
                    ?: emptyList()
            }
            .maxOrNull() ?: 0

        val generatedBarcodeNo = "${poModel.batchBarcodeNo}-${latestNumber + 1}"

        // Create and return the BatchInfoListModel
        return BatchInfoListModel(
            poModel.ExpiryDate,
            poModel.GDPONumber,
            "0.000",
            0,
            generatedBarcodeNo,
            poModel.itemCode,
            poModel.itemDescription,
            poModel.itemName,
            poModel.lineNumber,
            poModel.materialType,
            poModel.poId,
            poModel.poLineItemId,
            poModel.poNumber,
            poModel.poQuantity,
            poModel.poUnitPrice,
            poModel.posapLineItemNumber,
            poModel.pouom,
            poModel.batchBarcodeNo,
            false
        )
    }

    private fun updateBatches(poModel: BatchInfoListModel)
    {
        // Update or add the new item to createBatchesList
        val existingBatch =
            createBatchesList.find { it.generatedBarcodeNo == poModel.generatedBarcodeNo }
        if (existingBatch != null) {
            existingBatch.apply {
                ReceivedQty = poModel.ReceivedQty
                ExpiryDate = poModel.ExpiryDate
                GDPONumber = poModel.GDPONumber
                itemCode = poModel.itemCode
                itemDescription = poModel.itemDescription
                itemName = poModel.itemName
                lineNumber = poModel.lineNumber
                materialType = poModel.materialType
                poId = poModel.poId
                poLineItemId = poModel.poLineItemId
                poNumber = poModel.poNumber
                poQuantity = poModel.poQuantity
                poUnitPrice = poModel.poUnitPrice
                posapLineItemNumber = poModel.posapLineItemNumber
                pouom = poModel.pouom
                batchBarcodeNo = poModel.batchBarcodeNo
            }


        }


        // Check if the batch barcode number already exists in the map
        if (createBatchesListMap.containsKey(poModel.batchBarcodeNo)) {
            // Update the existing item in the list with the changes from poModel
            createBatchesListMap[poModel.batchBarcodeNo]?.let { batchList ->
                val existingBatch =
                    batchList.find { it.generatedBarcodeNo == poModel.generatedBarcodeNo }
                existingBatch?.apply {
                    ReceivedQty = poModel.ReceivedQty
                    ExpiryDate = poModel.ExpiryDate
                    GDPONumber = poModel.GDPONumber
                    itemCode = poModel.itemCode
                    itemDescription = poModel.itemDescription
                    itemName = poModel.itemName
                    lineNumber = poModel.lineNumber
                    materialType = poModel.materialType
                    poId = poModel.poId
                    poLineItemId = poModel.poLineItemId
                    poNumber = poModel.poNumber
                    poQuantity = poModel.poQuantity
                    poUnitPrice = poModel.poUnitPrice
                    posapLineItemNumber = poModel.posapLineItemNumber
                    pouom = poModel.pouom
                    batchBarcodeNo = poModel.batchBarcodeNo
                }

            }
        }
        addUpdateDeleteList(createBatchesList)
    }
    private fun addNewBatch(poModel: PoLineItemSelectionModel) {
        var edBatchNo = binding.edBatchNo.text.toString().trim()
        val hasNonZeroReceivedQuantity = createBatchesList.any { it.ReceivedQty.toDoubleOrNull() ?: 0.0 != 0.0 }

        if (edBatchNo.isNotEmpty()) {
            if (!hasNonZeroReceivedQuantity) {
                val newBatchList = arrayListOf(createBatchInfo(poModel, edBatchNo))
                createBatchesListMap[edBatchNo] = newBatchList
                createBatchesList.add(createBatchInfo(poModel, edBatchNo))
                addUpdateDeleteList(createBatchesList)
            }
            else{
                Toasty.warning(
                    requireActivity(),
                    "Please complete current Batch item!!",
                    Toasty.LENGTH_SHORT
                ).show()
            }


        } else {
            Toasty.warning(
                requireActivity(),
                "Please Enter Batch No.!!",
                Toasty.LENGTH_SHORT
            ).show()
        }

    }
    private fun createBatchInfo(
        poModel: PoLineItemSelectionModel,
        edBatchNo: String
    ): BatchInfoListModel {

        val latestNumber = createBatchesList
            .filter { it.batchBarcodeNo == edBatchNo }
            .flatMap {
                it.generatedBarcodeNo
                    .removePrefix("${edBatchNo}-")
                    .split("-")
                    .lastOrNull()
                    ?.toIntOrNull()
                    ?.let { listOf(it) }
                    ?: emptyList()
            }
            .maxOrNull() ?: 0

        // Increment the number part for the new entry
        val generatedBarcodeNo = "$edBatchNo-${latestNumber + 1}"
        return BatchInfoListModel(
            poModel.ExpiryDate,
            poModel.GDPONumber,
            "0.000",
            0,
            generatedBarcodeNo,
            poModel.itemCode,
            poModel.itemDescription,
            poModel.itemName,
            poModel.lineNumber,
            poModel.materialType,
            poModel.poId,
            poModel.poLineItemId,
            poModel.poNumber,
            poModel.poQuantity,
            poModel.poUnitPrice,
            poModel.posapLineItemNumber,
            poModel.pouom,
            edBatchNo,false
        )

    }

    private fun addUpdateDeleteList(createBatchesList: ArrayList<BatchInfoListModel>)
    {

    */
/*    Log.e("createBatchesList",createBatchesList.toString())
        createBatchesListRecyclerList.clear()
        createBatchesListRecyclerList.addAll(createBatchesList)
        createBatchesMainRcAdapter = CreateBatchesDemoAdapter()
        createBatchesMainRcAdapter?.updateList(createBatchesListRecyclerList, requireActivity())
        binding.rcBatchs.adapter = createBatchesMainRcAdapter
        binding.rcBatchs.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)*//*

    }

    private fun setInfoValues(poModel: PoLineItemSelectionModel) {
        binding.tvPoNoValue.setText(poModel.poNumber)
        binding.tvGdPoNoValue.setText(poModel.GDPONumber)
        binding.tvLineItemDescValue.setText(poModel.itemCode)
        binding.tvItemDescValue.setText(poModel.itemDescription)
        binding.tvPuomValue.setText(poModel.pouom)

        binding.tvPoQtyValue.setText(poModel.poQuantity.toString())
        binding.tvOpenQtyValue.setText(poModel.poQuantity.toString())


        binding.tvQtyValue.setText(poModel.ReceivedQty)
        binding.tvEpiryDtValue.setText(poModel.ExpiryDate)
        binding.tvBatchNoValue.setText("")
        binding.tvBarcodeNoInfoValue.setText("")
    }
*/





}