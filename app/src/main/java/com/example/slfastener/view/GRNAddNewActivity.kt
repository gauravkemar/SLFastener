package com.example.slfastener.view

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.DatePicker
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialog
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.demorfidapp.helper.Constants
import com.example.demorfidapp.helper.Resource
import com.example.demorfidapp.helper.SessionManager
import com.example.demorfidapp.repository.SLFastenerRepository
import com.example.slfastener.R
import com.example.slfastener.adapter.GRNSelectPoAdapter
import com.example.slfastener.adapter.newadapters.GRNMainNewAdapter
import com.example.slfastener.databinding.ActivityGrnaddNew2Binding
import com.example.slfastener.databinding.CreateNewBatchItemDialogBinding

import com.example.slfastener.databinding.SelectLineItemDialogBinding
import com.example.slfastener.databinding.SelectPoIdsDialogBinding
import com.example.slfastener.helper.CustomArrayAdapter
import com.example.slfastener.helper.weighing.UsbCommunicationManager
import com.example.slfastener.model.BatchInfoListModel
import com.example.slfastener.model.GetActiveSuppliersDDLResponse
import com.example.slfastener.model.GetPOsAndLineItemsOnPOIdsResponse
import com.example.slfastener.model.GetSuppliersPOsDDLResponse
import com.example.slfastener.model.PoLineItemSelectionModel
import com.example.slfastener.viewmodel.GRNTransactionViewModel
import com.example.slfastener.viewmodel.GRNTransactionViewModelProviderFactory
import es.dmoral.toasty.Toasty
import java.util.Calendar

class GRNAddNewActivity : AppCompatActivity() {
    lateinit var binding: ActivityGrnaddNew2Binding
    private lateinit var viewModel: GRNTransactionViewModel
    private lateinit var session: SessionManager
    var token: String = ""
    var selectedDate: String = ""
    var edInvoiceNo: String = ""
    var receivedQty: String = ""
    private lateinit var userDetails: HashMap<String, Any?>
    lateinit var selectedPoFilteredList: MutableList<Int>

    val supplierMap = HashMap<String, String>()
    var selectedSupplierCode: String = ""
    lateinit var getActiveSuppliersDDLResponse: MutableList<GetActiveSuppliersDDLResponse>
    lateinit var supplierSpinnerArray: MutableList<String>
    private var isIntialSelectSupplier = true
    private var supplierSpinnerAdapter: CustomArrayAdapter? = null
    private lateinit var progress: ProgressDialog
    lateinit var getSuppliersPOsDDLResponse: MutableList<GetSuppliersPOsDDLResponse>
    private var grnSelectPoAdapter: GRNSelectPoAdapter? = null
    private var selectPoLineAdapter: GRNMainNewAdapter? = null
    lateinit var selectPoBinding: SelectPoIdsDialogBinding
    lateinit var poLineItem: MutableList<PoLineItemSelectionModel>
    lateinit var getPOsAndLineItemsOnPOIdsResponse: MutableList<GetPOsAndLineItemsOnPOIdsResponse>

    lateinit var selectedPoLineItem: MutableList<PoLineItemSelectionModel>
    var selectPoDialog: Dialog? = null

    private lateinit var selectLineItemBinding: SelectLineItemDialogBinding
    private lateinit var selectLineItemDialog: AppCompatDialog


    private lateinit var createNewBatchesItemDialogBinding: CreateNewBatchItemDialogBinding
    private lateinit var createNewBatches: AppCompatDialog

    private lateinit var usbCommunicationManager: UsbCommunicationManager

    private val DEBOUNCE_PERIOD = 1000L
    private var lastUpdateTime: Long = 0
    private var previousData: String? = null
    private var baseUrl: String =""
    private var serverIpSharedPrefText: String? = null
    private var serverHttpPrefText: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_grnadd_new2)
        session = SessionManager(this)
        selectPoBinding = SelectPoIdsDialogBinding.inflate(getLayoutInflater());
        val SLFastenerRepository = SLFastenerRepository()
        val viewModelProviderFactory =
            GRNTransactionViewModelProviderFactory(application, SLFastenerRepository)
        viewModel =
            ViewModelProvider(this, viewModelProviderFactory)[GRNTransactionViewModel::class.java]
        selectPoDialog = Dialog(this)
        progress = ProgressDialog(this)
        progress.setMessage("Loading...")
        userDetails = session.getUserDetails()
        token = userDetails["jwtToken"].toString()
        serverIpSharedPrefText = userDetails!![Constants.KEY_SERVER_IP].toString()
        serverHttpPrefText = userDetails!![Constants.KEY_HTTP].toString()
        baseUrl = "$serverHttpPrefText://$serverIpSharedPrefText/service/api/"

        getSupplierList()

        selectedPoFilteredList = ArrayList()
        getActiveSuppliersDDLResponse = ArrayList()
        supplierSpinnerArray = ArrayList()
        getSuppliersPOsDDLResponse = ArrayList()
        selectedPoLineItem = ArrayList()
        poLineItem = ArrayList()
        getPOsAndLineItemsOnPOIdsResponse = ArrayList()

        ////po line item selection dialog
        selectLineItemBinding = SelectLineItemDialogBinding.inflate(LayoutInflater.from(this))

        // Create the dialog
        selectLineItemDialog = AppCompatDialog(this).apply {
            setContentView(selectLineItemBinding.root)
            window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
            getWindow()?.setBackgroundDrawable(
                AppCompatResources.getDrawable(
                    this@GRNAddNewActivity,
                    android.R.color.transparent
                )
            )
        }

        createNewBatchesItemDialogBinding  = CreateNewBatchItemDialogBinding.inflate(LayoutInflater.from(this))

        createNewBatches = AppCompatDialog(this).apply {
            setContentView(createNewBatchesItemDialogBinding.root)
            window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
        }
        createNewBatchesItemDialogBinding.closeDialogueTopButton.setOnClickListener {
            createNewBatches.dismiss()
        }
        selectLineItemDialog!!.setCancelable(true)
        binding.clEnterInvDate.setOnClickListener {
            showDatePickerDialog()
        }
        binding.clSelectPo.setOnClickListener {
            setSelectSupplierDialog()
        }
        binding.mcvAddGrn.setOnClickListener {
            if (selectedPoFilteredList.size > 0) {
                //processGrn(selectedPoFilteredList)
                callSelectedPoLineItems(selectedPoFilteredList)
            } else {
                Toasty.warning(
                    this@GRNAddNewActivity,
                    "Please Select Po from list!!",
                    Toasty.LENGTH_SHORT
                ).show()
            }
        }
        viewModel.getActiveSuppliersDDLMutable.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    supplierMap.clear()
                    supplierSpinnerArray.clear()
                    getActiveSuppliersDDLResponse.clear()
                    (supplierSpinnerArray).add("Select Supplier")
                    hideProgressBar()
                    response.data?.let { resultResponse ->
                        try {
                            if (resultResponse.size > 0) {
                                for (e in resultResponse) {
                                    supplierMap[e.code] = e.text
                                    (supplierSpinnerArray).add(e.text)
                                }
                                getActiveSuppliersDDLResponse.addAll(resultResponse)
                                setSupplierSpinner()
                                supplierSpinnerAdapter?.notifyDataSetChanged()
                            }
                        } catch (e: Exception) {
                            Toasty.warning(
                                this@GRNAddNewActivity,
                                e.printStackTrace().toString(),
                                Toasty.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { errorMessage ->
                        Toasty.error(
                            this@GRNAddNewActivity,
                            "Login failed - \nError Message: $errorMessage"
                        ).show()
                        session.showToastAndHandleErrors(errorMessage, this@GRNAddNewActivity)
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }
        /*   viewModel.getSuppliersPosDDLLMutableResponse.observe(this) { response ->
               when (response) {
                   is Resource.Success -> {
                       hideProgressBar()
                       response.data?.let { resultResponse ->
                           try {
                               if (resultResponse.size > 0) {
                                   getSuppliersPOsDDLResponse.addAll(resultResponse)
                                   binding.clSelectPo.visibility = View.VISIBLE
                                   selectePoRc()
                               } else {
                                   Toast.makeText(this, "List is Empty!!", Toast.LENGTH_SHORT).show()
                                   binding.clSelectPo.visibility = View.GONE
                               }


                           } catch (e: Exception) {
                               Toasty.warning(
                                   this@GRNAddNewActivity,
                                   e.printStackTrace().toString(),
                                   Toasty.LENGTH_SHORT
                               ).show()
                           }

                       }
                   }

                   is Resource.Error -> {
                       hideProgressBar()
                       binding.clSelectPo.visibility = View.GONE
                       response.message?.let { errorMessage ->
                           Toasty.error(
                               this@GRNAddNewActivity,
                               "Login failed - \nError Message: $errorMessage"
                           ).show()
                       }
                   }

                   is Resource.Loading -> {
                       showProgressBar()
                   }
               }
           }*/
        viewModel.getPosLineItemsOnPoIdsMutableResponse.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    poLineItem.clear()
                    getPOsAndLineItemsOnPOIdsResponse.clear()
                    hideProgressBar()
                    response.data?.let { resultResponse ->
                        try {
                            edInvoiceNo = binding.edInvoiceNumber.text.toString().trim()
                            //edGdpoNo = binding.edGdpo.text.toString().trim()
                            if (resultResponse.size > 0) {
                                getPOsAndLineItemsOnPOIdsResponse.addAll(resultResponse)
                                for (r in resultResponse) {
                                    for (e in r.poLineItems) {
                                        poLineItem.add(
                                            PoLineItemSelectionModel(
                                                e.itemCode,
                                                e.itemDescription,
                                                e.itemName,
                                                e.poLineNo,
                                                e.poId,
                                                e.poLineItemId,
                                                e.poQty,
                                                e.unitPrice,
                                                e.posapLineItemNumber,
                                                e.poUoM,
                                                r.poNumber,
                                                "",
                                                "",
                                                receivedQty,
                                                false,
                                                e.mhType, null
                                            )
                                        )
                                    }
                                }

                            } else {
                                Toast.makeText(this, "List is Empty!!", Toast.LENGTH_SHORT).show()
                            }


                        } catch (e: Exception) {
                            Toasty.warning(
                                this@GRNAddNewActivity,
                                e.printStackTrace().toString(),
                                Toasty.LENGTH_SHORT
                            ).show()
                        }

                    }
                }

                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { errorMessage ->
                        Toasty.error(
                            this@GRNAddNewActivity,
                            "Login failed - \nError Message: $errorMessage"
                        ).show()
                        session.showToastAndHandleErrors(errorMessage, this@GRNAddNewActivity)
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }
        usbCommunicationManager = UsbCommunicationManager(this)
        usbCommunicationManager.receivedData.observe(this) { data ->
            print("weightfromgrnadd: $data")
            val currentTime = System.currentTimeMillis()
            // Check if the time elapsed since the last update is greater than the debounce period
            if (currentTime - lastUpdateTime >= DEBOUNCE_PERIOD) {
                lastUpdateTime = currentTime
                // Check if the current data is different from the previous one
                /* if (data != null && data != previousData) {
                     // Update the previous data value
                     previousData = data
                     selectPoLineAdapter?.updateWeightValue(data)
                 }
                 print("weightfromgrnadd: $data")*/
            }
        }
    }

    private fun setSelectSupplierDialog() {
        selectPoDialog!!.setContentView(selectPoBinding.root)
        selectPoDialog!!.getWindow()?.setBackgroundDrawable(
            AppCompatResources.getDrawable(
                this@GRNAddNewActivity,
                android.R.color.transparent
            )
        )
        selectPoDialog!!.getWindow()?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        selectPoDialog!!.setCancelable(true)
        selectPoBinding.btnSubmit.setOnClickListener {
            selectPoDialog!!.dismiss()
        }

        selectPoBinding.closeDialogueTopButton.setOnClickListener {
            selectPoDialog!!.dismiss()
        }
        selectPoDialog!!.show()
    }
    private fun setPoLineItemList() {
        selectPoLineAdapter = GRNMainNewAdapter(selectedPoLineItem) { position, onInnerItemClick ->
            selectedPoLineItem[position] = onInnerItemClick
            selectPoLineAdapter?.notifyItemChanged(position)
        }

        binding.rcGrnAdd!!.adapter = selectPoLineAdapter
        binding.rcGrnAdd.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        selectPoLineAdapter?.setOnItemCheckClickListener {position,poline->
            createNewBatches!!.show()
            addNewBatch(position,poline)
        }
    }

    private fun selectePoRc() {
        // grnSelectPoAdapter = GRNSelectPoAdapter()
        // grnSelectPoAdapter?.setGrnMainList(getSuppliersPOsDDLResponse, this@GRNAddNewActivity)
        selectPoBinding.rcPo.adapter = grnSelectPoAdapter

        selectPoBinding.rcPo.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        /* grnSelectPoAdapter!!.setOnItemCheckClickListener {
             //submitInvoiceCheckedList.add(product);
             //Log.d("thisChecked","checked "+submitInvoiceCheckedList.toString());
             var selectedPoList: ArrayList<Int>
             selectedPoList = ArrayList()
             for (poCode in selectedPoFilteredList) {
                 selectedPoList.add(poCode)
                 Log.e("onitemclick", poCode.toString())
             }
             if (!selectedPoList.contains(it.value)) {
                 selectedPoFilteredList.add(it.value)
             }

         }
         grnSelectPoAdapter!!.setOnItemUncheckClickListener {
             val iterator: MutableIterator<Int> = selectedPoFilteredList.iterator()
             while (iterator.hasNext()) {
                 val item: Int = iterator.next()
                 if (item == it.value) {
                     iterator.remove()
                 }
             }
             Log.d("selectedPoFilteredListghj", selectedPoFilteredList.toString())
         }*/

        selectPoBinding.btnSubmit.setOnClickListener {
            if (selectedPoFilteredList.size > 0) {
                callSelectedPoLineItems(selectedPoFilteredList)
            } else {
                Toasty.warning(
                    this@GRNAddNewActivity,
                    "Please Select Po from list!!",
                    Toasty.LENGTH_SHORT
                ).show()
            }
        }
        binding.mcvNewLineItem.setOnClickListener {
            setSelectLineItemDialog()
        }

    }

    /*    private fun addNewBatch(position: Int, poModel: PoLineItemSelectionModel) {


            createNewBatches!!.show()

            var edBatchNo = createNewBatchesItemDialogBinding.edBatchNo.text.toString().trim()
            if (edBatchNo.isNotEmpty()) {
                for(s in selectedPoLineItem)
                {
                    val hasItemWithZeroReceivedQuantity = s.batchInfoListModel?.any { it.ReceivedQty == "0.000" }
                    if(hasItemWithZeroReceivedQuantity == true)
                    {
                        Toasty.warning(this@GRNAddNewActivity,"Please complete current transaction!!").show()
                    }
                    else
                    {
                        selectedPoLineItem.add(createBatchInfo(poModel, edBatchNo))
                        Log.e("createBatchesListAddNewBAtch",createBatchesList.toString())
                        selectPoLineAdapter?.notifyItemInserted(selectedPoLineItem.size-1)
                    }
                }



            }
            else
            {
                Toast.makeText(this@GRNAddNewActivity,"Please enter batch no!!",Toast.LENGTH_SHORT).show()
            }
        }*/

    private fun addNewBatch(position: Int, poModel: PoLineItemSelectionModel) {

        createNewBatchesItemDialogBinding.mcvSubmit.setOnClickListener {
            addBatchNewItem(position,poModel)
        }


    }
    /* private fun addBatchNewItem(position: Int, poModel: PoLineItemSelectionModel)
     {
         val edBatchNo = createNewBatchesItemDialogBinding.edBatchNo.text.toString().trim()
         if (edBatchNo.isNotEmpty()) {
             val hasItemWithZeroReceivedQuantity = selectedPoLineItem.any { it.batchInfoListModel?.any { it.ReceivedQty == "0.000" } ?: false }
             if (hasItemWithZeroReceivedQuantity) {
                 Toasty.warning(this@GRNAddNewActivity, "Please complete current transaction!!").show()
                 Log.d("edBatchNo.isNotEmpty()","sad")
             } else {
                 val newBatch = createBatchInfo(poModel, edBatchNo)
                 Log.d("edBatchNo.newBatch()",newBatch.toString())
                 if (position != RecyclerView.NO_POSITION) {
                     //selectedPoLineItem[position].batchInfoListModel?.add(newBatch.batchInfoListModel!![0])
                     selectedPoLineItem[position].batchInfoListModel?.add(newBatch)

                     Log.d("edBatchNo.isNotEmpty()",selectedPoLineItem.toString())
                     selectPoLineAdapter?.notifyItemChanged(position)
                     createNewBatches.dismiss()
                 }
             }
         } else {
             Toast.makeText(this@GRNAddNewActivity, "Please enter batch no!!", Toast.LENGTH_SHORT).show()
         }
     }*/
    private fun addBatchNewItem(position: Int, poModel: PoLineItemSelectionModel) {
        val edBatchNo = createNewBatchesItemDialogBinding.edBatchNo.text.toString().trim()
        if (edBatchNo.isNotEmpty()) {
            val hasItemWithZeroReceivedQuantity = selectedPoLineItem.any { it.batchInfoListModel?.any { it.ReceivedQty == "0.000" } ?: false }
            if (hasItemWithZeroReceivedQuantity) {
                Toasty.warning(this@GRNAddNewActivity, "Please complete current transaction!!").show()
                Log.d("edBatchNo.isNotEmpty()", "sad")
            } else {
                val newBatch = createBatchInfo(poModel, edBatchNo)
                if (position != RecyclerView.NO_POSITION) {
                    val model = selectedPoLineItem[position]
                    // Initialize batchInfoListModel if it's null
                    if (model.batchInfoListModel == null) {
                        model.batchInfoListModel = mutableListOf()
                    }
                    // Add new batch to batchInfoListModel
                    model.batchInfoListModel!!.add(newBatch)

                    selectPoLineAdapter?.notifyItemChanged(position)
                    Log.d("edBatchNo.isNotEmpty()", selectedPoLineItem.toString())
                    createNewBatches.dismiss()
                }
            }
        } else {
            Toast.makeText(this@GRNAddNewActivity, "Please enter batch no!!", Toast.LENGTH_SHORT).show()
        }
    }
    private fun createBatchInfo(
        poModel: PoLineItemSelectionModel,
        edBatchNo: String
    ): BatchInfoListModel {
        // Find the latest number associated with the batch prefix
        val latestNumber = poModel.batchInfoListModel?.filter { it.batchBarcodeNo == edBatchNo }
            ?.flatMap { it.generatedBarcodeNo?.removePrefix("${edBatchNo}-")?.split("-")?.lastOrNull()?.toIntOrNull()?.let { listOf(it) } ?: emptyList() }
            ?.maxOrNull() ?: 0

        // Increment the number part for the new entry
        val generatedBarcodeNo = "$edBatchNo-${latestNumber + 1}"

        // Create the new batch
        val newBatch = BatchInfoListModel(
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
            edBatchNo, false
        )
        /*        // Add the new batch to the existing batch list
                val newBatchList = ArrayList<BatchInfoListModel>()
                poModel.batchInfoListModel?.let { newBatchList.addAll(it) }
                newBatchList.add(newBatch)

                // Update the batch list inside the poModel
                val updatedPoModel = poModel.copy(batchInfoListModel = newBatchList)*/

        return newBatch
    }

    /*  private fun createBatchInfo(
        poModel: PoLineItemSelectionModel,
        edBatchNo: String
    ): PoLineItemSelectionModel {
        // Find the latest number associated with the batch prefix


        for(s in selectedPoLineItem)
        {
            val latestNumber = s.batchInfoListModel?.filter { it.batchBarcodeNo == edBatchNo }?.flatMap {
                    it.generatedBarcodeNo
                        .removePrefix("${edBatchNo}-")
                        .split("-")
                        .lastOrNull()
                        ?.toIntOrNull()
                        ?.let { listOf(it) }
                        ?: emptyList()
                }
                ?.maxOrNull() ?: 0
            // Increment the number part for the new entry
            val generatedBarcodeNo = "$edBatchNo-${latestNumber + 1}"

            // Create and return the BatchInfoListModel
            return PoLineItemSelectionModel(poModel,BatchInfoListModel(
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
                edBatchNo, false
            ))
        }


    }
*/



    /*    private fun addNewBatch(position: Int, poModel: PoLineItemSelectionModel) {
            createNewBatches!!.show()
            val edBatchNo = createNewBatchesItemDialogBinding.edBatchNo.text.toString().trim()
            if (edBatchNo.isNotEmpty()) {
                val hasItemWithZeroReceivedQuantity = selectedPoLineItem.any { it.batchInfoListModel?.any { it.ReceivedQty == "0.000" } ?: false }
                if (hasItemWithZeroReceivedQuantity) {
                    Toasty.warning(this@GRNAddNewActivity, "Please complete current transaction!!").show()
                } else {
                    val newBatch = createBatchInfo(poModel, edBatchNo)
                    val index = selectedPoLineItem.indexOfFirst { it.poLineItemId == poModel.poLineItemId }
                    if (index != -1) {
                        selectedPoLineItem[index].batchInfoListModel?.add(newBatch.batchInfoListModel!![0])
                        selectPoLineAdapter?.notifyItemChanged(index)
                    }
                }
            }
            else
            {
                Toast.makeText(this@GRNAddNewActivity, "Please enter batch no!!", Toast.LENGTH_SHORT).show()
            }
        }*/

    private fun setSupplierSpinner() {

        //supplierSpinnerAdapter = ArrayAdapter<String>(this, R.layout.spinner_layout, supplierSpinnerArray)
        supplierSpinnerAdapter =
            CustomArrayAdapter(this, android.R.layout.simple_spinner_item, supplierSpinnerArray)
        supplierSpinnerAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)


        binding.tvSpinnerSupplier.adapter = supplierSpinnerAdapter
        binding.tvSpinnerSupplier.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>?,
                    view: View?,
                    i: Int,
                    l: Long
                ) {
                    val selectedItem = adapterView?.selectedItem.toString()
                    // val selectedItemPosi = adapterView?.selectedItemPosition

                    if (!isIntialSelectSupplier) {
                        if (supplierSpinnerArray[i] != "Select Supplier") {
                            val selectedKey: String? =
                                supplierMap.entries.find { it.value == selectedItem }?.key
                            selectedSupplierCode = selectedKey!!
                            // callParentLocationApi(selectedKey)
                            callParentLocationApi(selectedKey)

                        } else {
                            selectedSupplierCode = ""
                        }
                    }
                    isIntialSelectSupplier = false

                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {}
            }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                // Do something with the selected date
                val selectedDateCal = Calendar.getInstance()
                selectedDateCal.set(selectedYear, selectedMonth, selectedDay)
                // You can handle the selected date here, for example, set it to a TextView
                binding.tvDate.text = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            }, year, month, dayOfMonth
        )

        // Set maximum date
        datePickerDialog.datePicker.maxDate =
            System.currentTimeMillis() // Optional: Set maximum date to current date
        datePickerDialog.show()
    }

    private fun getSupplierList() {
        try {
            viewModel.getActiveSuppliersDDL(this,token, baseUrl)
        } catch (e: Exception) {
            Toasty.error(
                this,
                e.message.toString(),
                Toasty.LENGTH_LONG
            ).show()
        }
    }

    private fun callSelectedPoLineItems(poCode: MutableList<Int>) {
        try {
            viewModel.getPosLineItemsOnPoIds(this,token, baseUrl, poCode)

        } catch (e: Exception) {
            Toasty.error(
                this,
                e.message.toString(),
                Toasty.LENGTH_LONG
            ).show()
        }
    }

    private fun callParentLocationApi(selectedKey: String) {
        try {
            viewModel.getSuppliersPosDDLL(this,token, baseUrl, selectedKey)
        } catch (e: Exception) {
            Toasty.error(
                this,
                e.message.toString(),
                Toasty.LENGTH_LONG
            ).show()
        }
    }

    private fun setSelectLineItemDialog() {

        setLineItemRc()
        selectLineItemBinding.closeDialogueTopButton.setOnClickListener {
            selectLineItemDialog!!.dismiss()
        }

        selectLineItemBinding.mcvPoLineItemSubmit.setOnClickListener {
            setPoLineItemList()
            selectLineItemDialog!!.dismiss()
        }
        selectLineItemDialog!!.show()
    }

    private fun setLineItemRc() {
        /*  selectLineItemBinding.rcLineItem.adapter = SelectPoLineAdapter(poLineItem) { item ->
              if (item.isSelected) {
                  selectedPoLineItem.add(item)
                  Log.d("selectedItems", selectedPoLineItem.toString())
              } else {
                  selectedPoLineItem.remove(item)
                  Log.d("selectedItems", selectedPoLineItem.toString())
              }
          }*/
        selectLineItemBinding.rcLineItem.layoutManager = LinearLayoutManager(this)
        selectLineItemBinding.rcLineItem!!.setHasFixedSize(true)
    }


    private fun showProgressBar() {
        progress.show()
    }

    private fun hideProgressBar() {
        progress.cancel()
    }

    private fun logout() {
        session.logoutUser()
        startActivity(Intent(this@GRNAddNewActivity, LoginActivity::class.java))
        finish()
    }
}