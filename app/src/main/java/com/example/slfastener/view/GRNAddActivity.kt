package com.example.slfastener.view

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.DatePicker
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.demorfidapp.helper.Constants
import com.example.demorfidapp.helper.Resource
import com.example.demorfidapp.helper.SessionManager
import com.example.demorfidapp.repository.SLFastenerRepository
import com.example.slfastener.R
import com.example.slfastener.adapter.CreateBatchesSingleList
import com.example.slfastener.adapter.GRNSelectPoAdapter
import com.example.slfastener.adapter.GrnMainAddAdapter
import com.example.slfastener.adapter.LineItemAdapter
import com.example.slfastener.databinding.ActivityGrnaddBinding
import com.example.slfastener.databinding.CreateBatchesDialogBinding
import com.example.slfastener.databinding.SelectLineItemDialogBinding
import com.example.slfastener.databinding.SelectSupplierPoLineItemBinding
import com.example.slfastener.helper.CustomArrayAdapter
import com.example.slfastener.helper.UsbCommunicationManager
import com.example.slfastener.model.BatchInfoListModel
import com.example.slfastener.model.GetPOsAndLineItemsOnPOIdsResponse
import com.example.slfastener.model.GetSuppliersPOsDDLResponse
import com.example.slfastener.model.PoLineItemSelectionModel
import com.example.slfastener.viewmodel.GRNTransactionViewModel
import com.example.slfastener.viewmodel.GRNTransactionViewModelProviderFactory
import es.dmoral.toasty.Toasty
import java.util.Calendar

class GRNAddActivity : AppCompatActivity(),CreateBatchesFragment.OnCancelListener {
    lateinit var binding: ActivityGrnaddBinding
    lateinit var spinnerItems: MutableList<String>
    private var selectedReasonSpinnerString: String? = ""
    private lateinit var viewModel: GRNTransactionViewModel
    private var grnMainItemAdapter: GrnMainAddAdapter? = null
    private var grnSelectPoAdapter: GRNSelectPoAdapter? = null
    private var createBatchesMainRcAdapter: CreateBatchesSingleList? = null
    private var lineItemAdapter: LineItemAdapter? = null
    lateinit var grnMainResponse: ArrayList<GrnMainAddListResponse>
    lateinit var getSuppliersPOsDDLResponse: ArrayList<GetSuppliersPOsDDLResponse>
    lateinit var getPOsAndLineItemsOnPOIdsResponse: ArrayList<GetPOsAndLineItemsOnPOIdsResponse>
    lateinit var poLineItem: ArrayList<PoLineItemSelectionModel>
    lateinit var selectedPoLineItem: ArrayList<PoLineItemSelectionModel>
    private lateinit var session: SessionManager
    private lateinit var progress: ProgressDialog
    lateinit var selectPoBinding: SelectSupplierPoLineItemBinding
    lateinit var supplierSpinnerArray: ArrayList<String>
    lateinit var selectedPoFilteredList: ArrayList<Int>
    val supplierMap = HashMap<String, String>()
    var selectedSupplierCode: String = ""

    private var supplierSpinnerAdapter: CustomArrayAdapter? = null
    private var isIntialSelectSupplier = true
    var selectPoDialog: Dialog? = null


    lateinit var selectLineItemBinding: SelectLineItemDialogBinding
    var selectLineItemDialog: Dialog? = null

    lateinit var createBatchesDialogBinding: CreateBatchesDialogBinding
    var createBatchedDialog: Dialog? = null
    lateinit var createBatchesList: ArrayList<BatchInfoListModel>

    private lateinit var createBatchesListMap: HashMap<String, ArrayList<BatchInfoListModel>>


    var selectedDate: String = ""
    var edInvoiceNo: String = ""
    var edGdpoNo: String = ""
    var receivedQty: String = ""
    private lateinit var userDetails: HashMap<String, Any?>
    var token: String = ""
    private lateinit var usbCommunicationManager: UsbCommunicationManager

    // Define a debounce period in milliseconds
    private val DEBOUNCE_PERIOD = 1000L

    // Variable to keep track of the last data update time
    private var lastUpdateTime: Long = 0
    private var previousData: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_grnadd)
        session = SessionManager(this)
        selectPoBinding = SelectSupplierPoLineItemBinding.inflate(getLayoutInflater());
        selectLineItemBinding = SelectLineItemDialogBinding.inflate(getLayoutInflater());
        createBatchesDialogBinding = CreateBatchesDialogBinding.inflate(getLayoutInflater());
        selectPoDialog = Dialog(this)
        selectLineItemDialog = Dialog(this)
        createBatchedDialog = Dialog(this)
        progress = ProgressDialog(this)
        usbCommunicationManager = UsbCommunicationManager(this)

        progress.setMessage("Loading...")
        supplierSpinnerArray = ArrayList()
        selectedPoFilteredList = ArrayList()
        getSuppliersPOsDDLResponse = ArrayList()
        createBatchesList = ArrayList()
        createBatchesListMap = HashMap()
        poLineItem = ArrayList()
        selectedPoLineItem = ArrayList()
        getPOsAndLineItemsOnPOIdsResponse = ArrayList()
        userDetails = session.getUserDetails()
        token = userDetails["jwtToken"].toString()

        val SLFastenerRepository = SLFastenerRepository()
        val viewModelProviderFactory =
            GRNTransactionViewModelProviderFactory(application, SLFastenerRepository)
        viewModel =
            ViewModelProvider(this, viewModelProviderFactory)[GRNTransactionViewModel::class.java]

        getApplicationVersionDetails()


        grnMainResponse = ArrayList()

        binding.mcvAddGrn.setOnClickListener {
            if (selectedPoFilteredList.size > 0) {
                callSelectedPoLineItems(selectedPoFilteredList)
            } else {
                Toasty.warning(
                    this@GRNAddActivity,
                    "Please Select Po from list!!",
                    Toasty.LENGTH_SHORT
                ).show()
            }
        }

        binding.mcvSubmit.setOnClickListener {
            var intent = Intent(this, GRNMainActivity::class.java)
            startActivity(intent)
        }
        binding.mcvCancel.setOnClickListener {
            onBackPressed()
        }

        binding.clSelectPo.setOnClickListener {
            setSelectSupplierDialog()
        }
        binding.logoutBtn.setOnClickListener {
            showLogoutDialog()
        }


        viewModel.getActiveSuppliersDDLMutable.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    supplierMap.clear()
                    supplierSpinnerArray.clear()
                    (supplierSpinnerArray).add("Select Supplier")
                    hideProgressBar()
                    response.data?.let { resultResponse ->
                        try {
                            if (resultResponse.size > 0) {
                                for (e in resultResponse) {
                                    supplierMap[e.code] = e.text
                                    (supplierSpinnerArray).add(e.text)
                                }
                                setSupplierSpinner()
                                supplierSpinnerAdapter?.notifyDataSetChanged()
                            }

                        } catch (e: Exception) {
                            Toasty.warning(
                                this@GRNAddActivity,
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
                            this@GRNAddActivity,
                            "Login failed - \nError Message: $errorMessage"
                        ).show()
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }

        viewModel.getSuppliersPosDDLLMutableResponse.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { resultResponse ->
                        try {
                            if (resultResponse.size > 0) {
                                getSuppliersPOsDDLResponse.addAll(resultResponse)
                                binding.clSelectPo.visibility = View.VISIBLE
                                selectePoRc(getSuppliersPOsDDLResponse)

                            } else {
                                Toast.makeText(this, "List is Empty!!", Toast.LENGTH_SHORT).show()
                                binding.clSelectPo.visibility = View.GONE
                            }


                        } catch (e: Exception) {
                            Toasty.warning(
                                this@GRNAddActivity,
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
                            this@GRNAddActivity,
                            "Login failed - \nError Message: $errorMessage"
                        ).show()
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }


        viewModel.getPosLineItemsOnPoIdsMutableResponse.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    supplierMap.clear()
                    supplierSpinnerArray.clear()
                    (supplierSpinnerArray).add("Select Supplier")
                    hideProgressBar()
                    response.data?.let { resultResponse ->
                        try {
                            edInvoiceNo = binding.edInvoiceNumber.text.toString().trim()
                            edGdpoNo = binding.edGdpo.text.toString().trim()


                            if (resultResponse.size > 0) {
                                getPOsAndLineItemsOnPOIdsResponse.addAll(resultResponse)

                                for (r in resultResponse) {

                                    for (e in r.poLineItems) {
                                        poLineItem.add(
                                            PoLineItemSelectionModel(
                                                e.itemCode,
                                                e.itemDescription,
                                                e.itemName,
                                                e.lineNumber,
                                                e.poId,
                                                e.poLineItemId,
                                                e.poQuantity,
                                                e.poUnitPrice,
                                                e.posapLineItemNumber,
                                                e.pouom,
                                                r.poNumber,
                                                edGdpoNo,
                                                "",
                                                receivedQty,
                                                false,
                                                e.materialType
                                            )
                                        )
                                    }

                                }


                            } else {
                                Toast.makeText(this, "List is Empty!!", Toast.LENGTH_SHORT).show()
                            }


                        } catch (e: Exception) {
                            Toasty.warning(
                                this@GRNAddActivity,
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
                            this@GRNAddActivity,
                            "Login failed - \nError Message: $errorMessage"
                        ).show()
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }
        binding.clEnterInvDate.setOnClickListener {
            showDatePickerDialog()
        }
    }
    override fun onCancel() {
        // Hide the current fragment
        supportFragmentManager.popBackStack()
        // You can also handle showing the previous view here if needed
        binding.clContainer2.visibility = View.GONE
        binding.clContainer.visibility = View.VISIBLE
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


    private fun selectePoRc(productList: ArrayList<GetSuppliersPOsDDLResponse>) {
        grnSelectPoAdapter = GRNSelectPoAdapter()
        grnSelectPoAdapter?.setGrnMainList(productList, this@GRNAddActivity)
        selectPoBinding.rcPo.adapter = grnSelectPoAdapter

        selectPoBinding.rcPo.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        grnSelectPoAdapter!!.setOnItemCheckClickListener {
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
        }

        selectPoBinding.btnSubmit.setOnClickListener {
            if (selectedPoFilteredList.size > 0) {
                callSelectedPoLineItems(selectedPoFilteredList)
            } else {
                Toasty.warning(
                    this@GRNAddActivity,
                    "Please Select Po from list!!",
                    Toasty.LENGTH_SHORT
                ).show()
            }
        }
        binding.mcvNewLineItem.setOnClickListener {
            setSelectLineItemDialog()

        }

    }

    private fun callParentLocationApi(selectedKey: String) {
        try {
            viewModel.getSuppliersPosDDLL(token, Constants.BASE_URL, selectedKey)
        } catch (e: Exception) {
            Toasty.error(
                this,
                e.message.toString(),
                Toasty.LENGTH_LONG
            ).show()
        }
    }

    private fun callSelectedPoLineItems(poCode: ArrayList<Int>) {
        try {

            viewModel.getPosLineItemsOnPoIds(token, Constants.BASE_URL, poCode)
        } catch (e: Exception) {
            Toasty.error(
                this,
                e.message.toString(),
                Toasty.LENGTH_LONG
            ).show()
        }
    }

    private fun showProgressBar() {
        progress.show()
    }

    private fun hideProgressBar() {
        progress.cancel()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun getApplicationVersionDetails() {
        try {
            viewModel.getActiveSuppliersDDL(token, Constants.BASE_URL)
        } catch (e: Exception) {
            Toasty.error(
                this,
                e.message.toString(),
                Toasty.LENGTH_LONG
            ).show()
        }
    }

    private fun setPoLineItemList(poLineItemSelected: ArrayList<PoLineItemSelectionModel>) {

        grnMainItemAdapter = GrnMainAddAdapter()
        grnMainItemAdapter?.updateList(poLineItemSelected, this@GRNAddActivity)
        binding.rcGrnAdd!!.adapter = grnMainItemAdapter
        binding.rcGrnAdd.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        grnMainItemAdapter?.setOnItemCheckClickListener {
            //setCreateBatchesDialog(it)

            binding.clContainer.visibility=View.GONE
            binding.clContainer2.visibility=View.VISIBLE
            transacton(it)
        }
    }
    private fun transacton(poLineItemSelectionModel: PoLineItemSelectionModel)
    {
   /*     val fragment = CreateBatchesFragment()
        fragment.setPoModel(poLineItemSelectionModel)
        supportFragmentManager.beginTransaction()
            .replace(R.id.clContainer2, fragment)
            .commit()
*/
        val fragment = CreateBatchesFragment()
        fragment.setPoModel(poLineItemSelectionModel)
        // Set the cancel listener for the fragment
        fragment.callback = this
        supportFragmentManager.beginTransaction()
            .replace(R.id.clContainer2, fragment)
            .addToBackStack(null) // Optional: Add fragment to back stack
            .commit()
    }
    private fun setSelectSupplierDialog() {
        selectPoDialog!!.setContentView(selectPoBinding.root)
        selectPoDialog!!.getWindow()?.setBackgroundDrawable(
            AppCompatResources.getDrawable(
                this@GRNAddActivity,
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

    private fun setSelectLineItemDialog() {
        selectLineItemDialog!!.setContentView(selectLineItemBinding.root)
        selectLineItemDialog!!.getWindow()?.setBackgroundDrawable(
            AppCompatResources.getDrawable(
                this@GRNAddActivity,
                android.R.color.transparent
            )
        )
        selectLineItemDialog!!.getWindow()?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        selectLineItemDialog!!.setCancelable(true)


        setLineItemRc()
        selectLineItemBinding.closeDialogueTopButton.setOnClickListener {
            selectLineItemDialog!!.dismiss()
        }

        selectLineItemBinding.mcvSubmit.setOnClickListener {
            selectLineItemDialog!!.dismiss()
        }
        selectLineItemDialog!!.show()
    }

    private fun setLineItemRc() {

        runOnUiThread {
            lineItemAdapter = LineItemAdapter()
            lineItemAdapter?.updateList(poLineItem, this@GRNAddActivity)
            selectLineItemBinding.rcLineItem.adapter = lineItemAdapter
            selectLineItemBinding.rcLineItem.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            lineItemAdapter!!.setOnItemCheckClickListener {
                val selectedPoList = HashMap<String, String>()
                for (poCode in selectedPoLineItem) {
                    selectedPoList[poCode.itemCode] = poCode.poNumber
                    Log.e("onitemclick", selectedPoList.toString())
                }
                val itemCodeMatch =
                    selectedPoList.containsKey(it.itemCode) && selectedPoList[it.itemCode] == it.poNumber
                if (!itemCodeMatch) {
                    selectedPoLineItem.add(it)
                }
                /*if (!selectedPoList .contains(it.itemCode) && ) {
                    selectedPoLineItem.add(it)
                }*/

                for (selectedPo in selectedPoLineItem) {
                    val itemToUpdate =
                        poLineItem.find { it.itemCode == selectedPo.itemCode && it.poNumber == selectedPo.poNumber }
                    itemToUpdate?.isSelected = true
                    itemToUpdate?.let {
                        val index = poLineItem.indexOf(it)
                        if (index != -1) {
                            poLineItem[index] = it
                        }
                    }
                }
                setPoLineItemList(selectedPoLineItem)


            }
            lineItemAdapter!!.setOnItemUncheckClickListener {
                /*val iterator: MutableIterator<PoLineItemSelectionModel> =
                    selectedPoLineItem.iterator()
                while (iterator.hasNext()) {
                    val item: PoLineItemSelectionModel = iterator.next()
                    val itemToUpdate =
                        poLineItem.find { it.itemCode == item.itemCode && it.poNumber == item.poNumber }
                    itemToUpdate?.isSelected = false
                    itemToUpdate?.let {
                        val index = poLineItem.indexOf(it)
                        if (index != -1) {
                            poLineItem[index] = it
                        }
                    }
                    if (item.itemCode == it.itemCode && item.poNumber == it.poNumber) {
                        iterator.remove()
                    }

                }
                setPoLineItemList(selectedPoLineItem)*/

                val iterator = selectedPoLineItem.iterator()
                while (iterator.hasNext()) {
                    val item = iterator.next()
                    val foundItem =
                        poLineItem.find { it.itemCode == item.itemCode && it.poNumber == item.poNumber }
                    if (foundItem != null) {
                        foundItem.isSelected = false
                        iterator.remove() // Remove only the first found item
                        break // Break the loop after removing the item
                    }
                }

                // Update poLineItem list
                poLineItem.forEachIndexed { index, item ->
                    if (selectedPoLineItem.any { it.itemCode == item.itemCode && it.poNumber == item.poNumber }) {
                        item.isSelected = true
                        poLineItem[index] = item // Update the item in the list
                    }
                }

                setPoLineItemList(selectedPoLineItem)


                Log.d("selectedPoFilteredListghj", selectedPoLineItem.toString())
            }

        }


    }

    private fun showLogoutDialog() {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Logout")
            .setMessage("Are you sure you want to log out?")
            .setPositiveButton("Yes") { dialog, which ->
                logout()
            }
            .setNegativeButton("Cancel") { dialog, which ->

                dialog.dismiss()
            }
            .setCancelable(false)

        val dialog = builder.create()
        dialog.show()
    }

    ////Batches activity
    private fun setCreateBatchesDialog(poModel: PoLineItemSelectionModel) {
        createBatchedDialog!!.setContentView(createBatchesDialogBinding.root)
        createBatchedDialog!!.getWindow()?.setBackgroundDrawable(
            AppCompatResources.getDrawable(
                this@GRNAddActivity,
                android.R.color.transparent
            )
        )
        createBatchedDialog!!.getWindow()?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )

        //createBatchedDialog!!.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)


        createBatchedDialog!!.setCancelable(true)


        setBatchesRc()
        setInfoValues(poModel)
        createBatchesDialogBinding.closeDialogueTopButton.setOnClickListener {
            createBatchedDialog!!.dismiss()
        }
        createBatchesDialogBinding.mcvClearBatchBarcode.setOnClickListener {
            createBatchesDialogBinding.edBatchNo.setText("")
        }



        createBatchesDialogBinding.mcvAddBatches.setOnClickListener {

            addNewBatch(poModel)

            //createBatchesList.add(newItem)
            //adapter.notifyItemInserted(itemList.size - 1)
        }




        createBatchedDialog!!.show()
    }

    private fun setInfoValues(poModel: PoLineItemSelectionModel) {
        createBatchesDialogBinding.tvPoNoValue.setText(poModel.poNumber)
        createBatchesDialogBinding.tvGdPoNoValue.setText(poModel.GDPONumber)
        createBatchesDialogBinding.tvLineItemDescValue.setText(poModel.itemCode)
        createBatchesDialogBinding.tvItemDescValue.setText(poModel.itemDescription)
        createBatchesDialogBinding.tvPuomValue.setText(poModel.pouom)

        createBatchesDialogBinding.tvPoQtyValue.setText(poModel.poQuantity.toString())
        createBatchesDialogBinding.tvOpenQtyValue.setText(poModel.poQuantity.toString())


        createBatchesDialogBinding.tvQtyValue.setText(poModel.ReceivedQty)
        createBatchesDialogBinding.tvEpiryDtValue.setText(poModel.ExpiryDate)
        createBatchesDialogBinding.tvBatchNoValue.setText("")
        createBatchesDialogBinding.tvBarcodeNoInfoValue.setText("")
    }

    /*    private fun addNewBatch(poModel: PoLineItemSelectionModel) {
            var edBatchNo = createBatchesDialogBinding.edBatchNo.text.toString().trim()

            if (edBatchNo.isNotEmpty()) {
                // Check if the batch barcode number already exists in the map
                if (createBatchesListMap.containsKey(edBatchNo)) {
                    // Add the new item to the existing batch
                    createBatchesListMap[edBatchNo]?.add(createBatchInfo(poModel, edBatchNo))
                } else {
                    // Create a new ArrayList and add the new item to it
                    val newBatchList = arrayListOf(createBatchInfo(poModel, edBatchNo))
                    createBatchesListMap[edBatchNo] = newBatchList
                }
            } else {
                Toasty.warning(
                    this@GRNAddActivity,
                    "Please Enter Batch No.!!",
                    Toasty.LENGTH_SHORT
                ).show()
            }

            // Add the new item to createBatchesList (if required)
            createBatchesList.add(createBatchInfo(poModel, edBatchNo))
            Log.e("onitemclickFromAddBatch", createBatchesList.toString())
            createBatchesMainRcAdapter?.notifyItemInserted(createBatchesList.size - 1)
            createBatchesMainRcAdapter?.updateList(createBatchesList, this)

        }*/
    private fun addNewBatch(poModel: PoLineItemSelectionModel) {
        var edBatchNo = createBatchesDialogBinding.edBatchNo.text.toString().trim()

        if (edBatchNo.isNotEmpty()) {
            // Check if the batch barcode number already exists in the map
            if (createBatchesListMap.containsKey(edBatchNo)) {
                Toasty.warning(
                    this@GRNAddActivity,
                    "Batch No. $edBatchNo already exists!",
                    Toasty.LENGTH_SHORT
                ).show()
            } else {
                // Create a new ArrayList and add the new item to it
                val newBatchList = arrayListOf(createBatchInfo(poModel, edBatchNo))
                createBatchesListMap[edBatchNo] = newBatchList

                // Add the new item to createBatchesList
                createBatchesList.add(createBatchInfo(poModel, edBatchNo))
                //createBatchesMainRcAdapter?.updateList(createBatchesList, this)
                createBatchesMainRcAdapter?.notifyItemInserted(createBatchesList.size - 1)
                for (i in 0 until createBatchesList.size - 1) {
                    createBatchesMainRcAdapter?.notifyItemChanged(i)
                }

            }
        } else {
            Toasty.warning(
                this@GRNAddActivity,
                "Please Enter Batch No.!!",
                Toasty.LENGTH_SHORT
            ).show()
        }

    }

    private fun createBatchInfo(
        poModel: PoLineItemSelectionModel,
        edBatchNo: String
    ): BatchInfoListModel {
        // Find the latest number associated with the batch prefix


        val latestNumber = createBatchesList.filter { it.generatedBarcodeNo.startsWith(edBatchNo) }
            .mapNotNull { it.generatedBarcodeNo.removePrefix(edBatchNo).toIntOrNull() }
            .maxOrNull() ?: 0

        // Increment the number part for the new entry
        val generatedBarcodeNo = "$edBatchNo-${latestNumber + 1}"

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
            edBatchNo,false
        )

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
                createNewExisitingBatchInfo(
                    poModel
                )
            )
        } else {
            // Create a new ArrayList and add the new item to it
            val newBatchList =
                arrayListOf(createNewExisitingBatchInfo(poModel))
            createBatchesListMap[poModel.batchBarcodeNo] = newBatchList
        }
        createBatchesList.add(createNewExisitingBatchInfo(poModel))
        Log.e("AfterChange", createBatchesListMap.toString())
        Log.e("AfterChange", createBatchesList.toString())
        //Log.e("onitemclickFromAddNewExisitngBatch", createBatchesList.toString())
        // Notify adapter about the insertion
       // createBatchesMainRcAdapter?.updateList(createBatchesList, this)
        createBatchesMainRcAdapter?.notifyItemInserted(createBatchesList.size - 1)

        for (i in 0 until createBatchesList.size - 1) {
            createBatchesMainRcAdapter?.notifyItemChanged(i)
        }


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
        Log.e("AfterChange", createBatchesList.toString())

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
     /*   createBatchesMainRcAdapter?.updateList(createBatchesList, this)
        for (i in 0 until createBatchesList.size - 1) {
            createBatchesMainRcAdapter?.notifyItemChanged(i)
        }*/

    }

    private fun createNewExisitingBatchInfo(
        poModel: BatchInfoListModel,
    ): BatchInfoListModel {

        /*   val latestNumber = poModel.generatedBarcodeNo.removePrefix("${poModel.batchBarcodeNo}-")
               .toIntOrNull() ?: 0
   */
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


    private fun deleteExistingBatch(poModel: BatchInfoListModel) {

        val batchBarcodeNo = poModel.batchBarcodeNo
        /*if (createBatchesListMap.containsKey(batchBarcodeNo)) {
            val batchList = createBatchesListMap[batchBarcodeNo]
            batchList?.let {
                // Find and remove the specific BatchInfoListModel object from the list
                val iterator = it.iterator()
                while (iterator.hasNext()) {
                    val batch = iterator.next()
                    if (batch.generatedBarcodeNo == poModel.generatedBarcodeNo) {
                        iterator.remove()
                        // If the list becomes empty after removal, remove the entry from the map
                        if (it.isEmpty()) {
                            createBatchesListMap.remove(batchBarcodeNo)
                        }
                        // Exit loop after removal
                        break
                    }
                }
            }
        }*/

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
            if (r.generatedBarcodeNo == batchBarcodeNo) {
                createBatchesList.remove(poModel)
            }
        }
        Log.e("onitemclickFromDeleteBatch", createBatchesList.toString())
        // Notify adapter about the removal

        for (i in 0 until createBatchesList.size - 1) {
            createBatchesMainRcAdapter?.notifyItemChanged(i)
        }


    }


    private fun setBatchesRc() {

        createBatchesMainRcAdapter = CreateBatchesSingleList()
        createBatchesMainRcAdapter?.updateList(createBatchesList, this@GRNAddActivity)
        createBatchesDialogBinding.rcBatchs.adapter = createBatchesMainRcAdapter
        createBatchesDialogBinding.rcBatchs.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        createBatchesMainRcAdapter?.setOnItemAddClickListener {
            addNewExistingBatch(it)
            Log.e("ONITEMCLICKExisting", it.toString())
        }
        createBatchesMainRcAdapter?.setOnItemUpdatelickListener {
            updateBatches(it)
        }

        createBatchesMainRcAdapter?.setOnItemDeleteClickListener {
            deleteExistingBatch(it)
        }

        usbCommunicationManager.receivedData.observe(this) { data ->
            if (data != null) {
                createBatchesMainRcAdapter?.updateWeightValue(data)
            }
            print("weightfromgrnadd" + data)
        }
        usbCommunicationManager.receivedData.observe(this) { data ->
            val currentTime = System.currentTimeMillis()
            // Check if the time elapsed since the last update is greater than the debounce period
            if (currentTime - lastUpdateTime >= DEBOUNCE_PERIOD) {
                lastUpdateTime = currentTime
                // Check if the current data is different from the previous one
                if (data != null && data != previousData) {
                    // Update the previous data value
                    previousData = data
                    createBatchesMainRcAdapter?.updateWeightValue(data)
                }
                print("weightfromgrnadd: $data")
            }
        }

    }


    private fun logout() {
        session.logoutUser()
        startActivity(Intent(this@GRNAddActivity, LoginActivity::class.java))
        finish()
    }
}


/*    private fun addNewExistingBatch(poModel: BatchInfoListModel) {
         // Create a BatchInfoListModel with receivedQty as ""
         val defaultArrayList = BatchInfoListModel(
             poModel.ExpiryDate,
             poModel.GDPONumber,
             "",
             0,
             "",
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
             poModel.batchBarcodeNo
         )

         // Update or add the new item to createBatchesList
         val existingBatch = createBatchesList.find { it.generatedBarcodeNo == poModel.generatedBarcodeNo }
         if (existingBatch != null) {
             existingBatch.apply {
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
             createBatchesListMap[poModel.batchBarcodeNo]?.add(defaultArrayList)
         } else {
             // Create a new ArrayList and add the new item to it
             val newBatchList = arrayListOf(defaultArrayList)
             createBatchesListMap[poModel.batchBarcodeNo] = newBatchList
         }
         createBatchesList.add(defaultArrayList)

         Log.e("onitemclickFromAddNewExisitngBatch", createBatchesList.toString())
         // Notify adapter about the insertion
         createBatchesMainRcAdapter?.notifyItemInserted(createBatchesList.size - 1)
         createBatchesMainRcAdapter?.updateList(createBatchesList, this)
     }*/

/* private fun setSupplierDropDown()
    {
        val languages = resources.getStringArray(R.array.leveler)
        spinnerItems = mutableListOf(*languages)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerItems)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.tvSpinnerSupplier.adapter = adapter

        binding.tvSpinnerSupplier.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {

                selectedReasonSpinnerString = spinnerItems[position]

            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }*/


/*  grnMainResponse.clear()
      grnMainResponse.addAll(
          listOf(
              GrnMainAddListResponse(
                  "4654798438",
                  "12:00:23",
                  "DGPO12345",
                  "1",
                  "ABC",
                  "Import", "", "PO1", "", "4"
              ),
              GrnMainAddListResponse(
                  "4654798797",
                  "12:00:23",
                  "DGPO12345",
                  "2",
                  "DEF",
                  "Import", "", "PO1", "", "2"
              ),
              GrnMainAddListResponse(
                  "4654798568",
                  "13:15:00",
                  "DGPO12345",
                  "3",
                  "GHJ",
                  "Import", "", "PO2", "", "1"
              ),
              GrnMainAddListResponse(
                  "4654798712",
                  "12:30:45",
                  "DGPO12345",
                  "4",
                  "UIO",
                  "Export", "", "PO2", "", "4"
              )
          )
      )*/


/* private fun createNewExisitingBatchInfo(
        poModel: BatchInfoListModel,
        edBatchNo: String
    ): BatchInfoListModel {
        // Find the latest number associated with the batch prefix
        val latestNumber = createBatchesList.filter {
            poModel.generatedBarcodeNo.startsWith("$edBatchNo-") // Check if the generatedBarcodeNo starts with the prefix followed by a hyphen
        }.mapNotNull {
            poModel.generatedBarcodeNo.removePrefix("$edBatchNo-").toIntOrNull() // Extract the numeric part after the hyphen
        }.maxOrNull() ?: 0
        Log.e("onitemclicklatestNumberExisiting", latestNumber.toString())
        // Increment the number part for the new entry
        val generatedBarcodeNo = "$edBatchNo-${latestNumber + 1}"
        Log.e(
            "onitemclicklatestNumberExisiting", BatchInfoListModel(
                poModel.ExpiryDate,
                poModel.GDPONumber,
                "",
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
                edBatchNo
            ).toString()
        )
        // Create and return the BatchInfoListModel
        return BatchInfoListModel(
            poModel.ExpiryDate,
            poModel.GDPONumber,
            "",
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
            edBatchNo
        )

    }*/