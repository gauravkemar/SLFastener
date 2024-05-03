package com.example.slfastener.view

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.DatePicker
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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
import com.example.slfastener.adapter.GrnMainAddAdapter
import com.example.slfastener.adapter.demoAdapter.CreateBatchesNewSingleList
import com.example.slfastener.adapter.newadapters.SelectPoLineAdapter
import com.example.slfastener.databinding.ActivityGrnaddBinding
import com.example.slfastener.databinding.CreateBatchesDialogBinding
import com.example.slfastener.databinding.SelectLineItemDialogBinding
import com.example.slfastener.databinding.SelectSupplierPoLineItemBinding
import com.example.slfastener.helper.CustomArrayAdapter
import com.example.slfastener.helper.CustomKeyboard
import com.example.slfastener.helper.UsbCommunicationManager
import com.example.slfastener.model.GetActiveSuppliersDDLResponse
import com.example.slfastener.model.GetPOsAndLineItemsOnPOIdsResponse
import com.example.slfastener.model.GetSuppliersPOsDDLResponse
import com.example.slfastener.model.grn.GRNSaveToDraftDefaultRequest
import com.example.slfastener.model.grn.GRNSaveToDraftDefaultResponse
import com.example.slfastener.model.grn.ProcessGRNLineItemsResponse
import com.example.slfastener.model.offlinebatchsave.GrnLineItemUnitStore
import com.example.slfastener.model.offlinebatchsave.PoLineItemSelectionModelNewStore
import com.example.slfastener.viewmodel.GRNTransactionViewModel
import com.example.slfastener.viewmodel.GRNTransactionViewModelProviderFactory

import es.dmoral.toasty.Toasty
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

class GRNAddActivity : AppCompatActivity() {
    lateinit var binding: ActivityGrnaddBinding
    lateinit var spinnerItems: MutableList<String>
    private var selectedReasonSpinnerString: String? = ""
    private lateinit var viewModel: GRNTransactionViewModel
    private var grnMainItemAdapter: GrnMainAddAdapter? = null

    //private var grnSelectPoAdapter: GRNSelectPoAdapter? = null
    private var createBatchesMainRcAdapter: CreateBatchesNewSingleList? = null

    lateinit var getSuppliersPOsDDLResponse: MutableList<GetSuppliersPOsDDLResponse>
    lateinit var getPOsAndLineItemsOnPOIdsResponse: MutableList<GetPOsAndLineItemsOnPOIdsResponse>
    lateinit var poLineItem: MutableList<PoLineItemSelectionModelNewStore>
    lateinit var selectedPoLineItem: MutableList<PoLineItemSelectionModelNewStore>
    private lateinit var session: SessionManager
    private lateinit var progress: ProgressDialog
    lateinit var selectPoBinding: SelectSupplierPoLineItemBinding
    lateinit var supplierSpinnerArray: MutableList<String>
    lateinit var selectedPoFilteredList: MutableList<Int>

    val supplierMap = HashMap<String, String>()
    var selectedSupplierCode: String = ""

    private var supplierSpinnerAdapter: CustomArrayAdapter? = null
    private var isIntialSelectSupplier = true
    var selectPoDialog: Dialog? = null

    private lateinit var selectLineItemBinding: SelectLineItemDialogBinding
    var selectLineItemDialog: Dialog? = null

    lateinit var createBatchesDialogBinding: CreateBatchesDialogBinding
    lateinit var grnSaveToDraftDefaultResponse: ProcessGRNLineItemsResponse
    lateinit var createBatchedDialog: AppCompatDialog
    lateinit var createBatchesList: MutableList<GrnLineItemUnitStore>
    lateinit var getActiveSuppliersDDLResponse: MutableList<GetActiveSuppliersDDLResponse>
    var currentPoLineItemPosition = ""

    //private lateinit var createBatchesListMap: HashMap<String, ArrayList<BatchInfoListModel>>

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


    private var selectedBpId = ""
    var selectedDate:String = ""
    var edInvoiceNo: String = ""
    var currentGeneratedBarcode: String = ""
    var selectedCurrency: String = ""
    var selectedKGRN: String = ""

    private lateinit var customKeyboard: CustomKeyboard
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_grnadd)
        session = SessionManager(this)
        selectPoBinding = SelectSupplierPoLineItemBinding.inflate(getLayoutInflater());
        selectLineItemBinding = SelectLineItemDialogBinding.inflate(LayoutInflater.from(this))
        selectPoDialog = Dialog(this)
        selectLineItemDialog = AppCompatDialog(this).apply {
            setContentView(selectLineItemBinding.root)
            window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
            getWindow()?.setBackgroundDrawable(
                AppCompatResources.getDrawable(
                    this@GRNAddActivity,
                    android.R.color.transparent
                )
            )
        }
        progress = ProgressDialog(this)
        progress.setMessage("Loading...")
        usbCommunicationManager = UsbCommunicationManager(this)
        supplierSpinnerArray = ArrayList()
        selectedPoFilteredList = ArrayList()
        getActiveSuppliersDDLResponse = ArrayList()
        getSuppliersPOsDDLResponse = ArrayList()
        createBatchesList = ArrayList()
        //createBatchesListMap = HashMap()
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
        getSupplierList()
        binding.mcvAddGrn.setOnClickListener {
            processGrn()
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
        ///get list of suppliers PO
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
        ///get list of suppliers PO
        viewModel.getSuppliersPosDDLLMutableResponse.observe(this) { response ->
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
        ///get list of po line item
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
                            //edGdpoNo = binding.edGdpo.text.toString().trim()
                            if (resultResponse.size > 0) {
                                getPOsAndLineItemsOnPOIdsResponse.addAll(resultResponse)
                                for (r in resultResponse) {
                                    for (e in r.poLineItems) {
                                        val unitPrice =
                                            e.unitPrice ?: "" // Provide a default value here
                                        PoLineItemSelectionModelNewStore(
                                            e.balQTY,
                                            r.poCurrency,
                                            null,
                                            e.itemCode,
                                            e.itemDescription,
                                            e.itemName,
                                            e.mhType,
                                            e.poId,
                                            e.poLineItemId,
                                            e.poLineNo,
                                            r.poNumber,
                                            e.poqty,
                                            e.posapLineItemNumber,
                                            e.pouom,
                                            "",
                                            false,
                                            "",
                                            unitPrice
                                        ).also { poLineItem.add(it) }
                                        Log.e("selectPolIne", poLineItem.toString())

                                        /* poLineItem.add(
                                             PoLineItemSelectionModel(
                                                 e.itemCode,
                                                 e.itemDescription,
                                                 e.itemName,
                                                 e.poLineNo,
                                                 e.poId,
                                                 e.poLineItemId,
                                                 e.poqty,
                                                 e.unitPrice,
                                                 e.posapLineItemNumber,
                                                 e.pouom,
                                                 r.poNumber,
                                                 "",
                                                 "",
                                                 receivedQty,
                                                 false,
                                                 e.mhType, null
                                             )
                                         )*/
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
        ///get grn response
        viewModel.processGRNMutableResponse.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { resultResponse ->
                        try {
                            if (resultResponse != null) {
                                grnSaveToDraftDefaultResponse = resultResponse
                                selectedKGRN = resultResponse.responseObject.kgrnNumber
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

        ///generate barcode
        viewModel.getBarcodeValueWithPrefixMutableResponse.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    currentGeneratedBarcode = ""
                    response.data?.let { resultResponse ->
                        try {
                            currentGeneratedBarcode = resultResponse.responseMessage.toString()
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

        binding.clEnterInvDate.setOnClickListener {
            showDatePickerDialog()
        }
        ////batches dialog
        createBatchesDialogBinding = CreateBatchesDialogBinding.inflate(LayoutInflater.from(this));
        createBatchedDialog = AppCompatDialog(this).apply {
            setContentView(createBatchesDialogBinding.root)
            window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
        }
        createBatchedDialog!!.setCancelable(true)
        createBatchesDialogBinding.closeDialogueTopButton.setOnClickListener {
            createBatchedDialog!!.dismiss()
        }
        createBatchesDialogBinding.mcvClearBatchBarcode.setOnClickListener {
            createBatchesDialogBinding.edBatchNo.setText("")
        }

        val keyboardLayout = layoutInflater.inflate(R.layout.custom_number_keyboard, null)
        customKeyboard = CustomKeyboard(this, keyboardLayout)

        // createBatchesMainRcAdapter = CreateBatchesSingleList(createBatchesList)
        /* createBatchesMainRcAdapter = CreateBatchesNewSingleList(
             createBatchesList,

             ) {
         }*/
        createBatchesMainRcAdapter =
            CreateBatchesNewSingleList(createBatchesList,
                addItem = { newItem ->
                    addExisitngNewItem(
                        newItem.supplierBatchNo,
                        currentPoLineItemPosition.toInt(),
                        selectedPoLineItem.get(currentPoLineItemPosition.toInt())
                    )
                }, onSave = { position, updatedItem ->
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].grnLineItemUnit!![position] =
                        updatedItem.copy()
                    createBatchesList[position] = updatedItem.copy()
                    createBatchesMainRcAdapter!!.notifyItemChanged(position)
                }, onDelete = { position ->
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].grnLineItemUnit!!.removeAt(
                        position
                    )
                },
                customKeyboard = customKeyboard,
                grnAddActivity = this@GRNAddActivity
            )

        createBatchesDialogBinding.rcBatchs.adapter = createBatchesMainRcAdapter
        createBatchesDialogBinding.rcBatchs.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
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
                print("usbCommunicationManager: $data")
            }
        }

    }

    private fun selectePoRc() {
        /*  grnSelectPoAdapter = GRNSelectPoAdapter()
          grnSelectPoAdapter?.setGrnMainList(getSuppliersPOsDDLResponse, this@GRNAddActivity)


          selectPoBinding.rcPo.layoutManager =
              LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
          grnSelectPoAdapter!!.setOnItemCheckClickListener {
              //submitInvoiceCheckedList.add(product);
              //Log.d("thisChecked","checked "+submitInvoiceCheckedList.toString());
              selectedCurrency = it.code
              binding.tvCurrencyType.setText(selectedCurrency)
              var selectedPoList: ArrayList<Int>
              selectedPoList = ArrayList()
              for (poCode in selectedPoFilteredList) {
                  selectedPoList.add(poCode)
                  Log.e("onitemclick", poCode.toString())
              }
              if (!selectedPoList.contains(it.value)) {
                  selectedPoFilteredList.add(it.value)
              }
              updateSelectedList(it)
          }
          grnSelectPoAdapter!!.setOnItemUncheckClickListener {
              val iterator: MutableIterator<Int> = selectedPoFilteredList.iterator()
              while (iterator.hasNext()) {
                  val item: Int = iterator.next()
                  if (item == it.value) {
                      iterator.remove()
                  }
              }
              updateSelectedList(it)
              Log.d("selectedPoFilteredListghj", selectedPoFilteredList.toString())
          }*/


        selectPoBinding.rcPo.adapter = GRNSelectPoAdapter(getSuppliersPOsDDLResponse)
        /*onItemClickListener = { position,it ->
            selectedCurrency = it.code
            binding.tvCurrencyType.setText(selectedCurrency)
            var selectedPoList: ArrayList<Int>
            selectedPoList = ArrayList()
            for (poCode in selectedPoFilteredList) {
                selectedPoList.add(poCode)
                Log.e("onitemclick", poCode.toString())
            }
            if (!selectedPoList.contains(it.value)) {
                selectedPoFilteredList.add(it.value)
            }
            getSuppliersPOsDDLResponse[position] = it.copy()


             updateSelectedList(it)
        }, onItemUncheckClickListener = { position,it ->
            val iterator: MutableIterator<Int> = selectedPoFilteredList.iterator()
            while (iterator.hasNext()) {
                val item: Int = iterator.next()
                if (item == it.value) {
                    iterator.remove()
                }
            }
            getSuppliersPOsDDLResponse[position] = it.copy()
            updateSelectedList(it)
        }*/

        selectPoBinding.rcPo.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        selectPoBinding.btnSubmit.setOnClickListener {
                callSelectedPoLineItems()
        }

        binding.mcvNewLineItem.setOnClickListener {
            setSelectLineItemDialog()
        }
    }
    private fun getCommonCode(): String? {
        val checkedItem = getSuppliersPOsDDLResponse.find { it.isChecked }
        return checkedItem?.code
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
                //selectedDate = "$selectedDay-${selectedMonth + 1}-$selectedYear"
                val dateFormat = SimpleDateFormat("dd-M-yyyy", Locale.getDefault())
                val selectDt = dateFormat.parse("$selectedDay-${selectedMonth + 1}-$selectedYear")
                val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectDt)
                selectedDate=formattedDate.toString()
                //selectedDate = LocalDateTime.of(2024, 4, 20,0,0)
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
                            selectedBpId = getActiveSuppliersDDLResponse.find { it.code == selectedSupplierCode }!!.value.toString()
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
    private fun setSelectLineItemDialog() {

        setLineItemRc()
        selectLineItemBinding.closeDialogueTopButton.setOnClickListener {
            selectLineItemDialog!!.dismiss()
        }
        selectLineItemBinding.mcvSubmit.setOnClickListener {
            setPoLineItemList()
            selectLineItemDialog!!.dismiss()
        }
        selectLineItemDialog!!.show()
    }

    private fun setLineItemRc() {
        selectLineItemBinding.rcLineItem.adapter = SelectPoLineAdapter(poLineItem) { item ->
            if (item.isSelected) {
                selectedPoLineItem.add(item)
                Log.d("selectedItems", selectedPoLineItem.toString())
            } else {
                selectedPoLineItem.remove(item)
                Log.d("selectedItems", selectedPoLineItem.toString())
            }
        }
        selectLineItemBinding.rcLineItem.layoutManager = LinearLayoutManager(this)
        selectLineItemBinding.rcLineItem!!.setHasFixedSize(true)
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

    private fun callSelectedPoLineItems() {
        try {
            for (s in getSuppliersPOsDDLResponse) {
                if (s.isChecked) {
                    selectedPoFilteredList.add(s.value)
                }
            }

            if (selectedPoFilteredList.size > 0) {
                binding.tvCurrencyType.setText(getCommonCode().toString())
                selectedCurrency=getCommonCode().toString()
                viewModel.getPosLineItemsOnPoIds(token, Constants.BASE_URL, selectedPoFilteredList)
                selectPoDialog!!.dismiss()
            } else {

                Toasty.warning(
                    this@GRNAddActivity,
                    "Please Select Po from list!!",
                    Toasty.LENGTH_SHORT
                ).show()
            }
        } catch (e: Exception) {
            Toasty.error(
                this,
                e.message.toString(),
                Toasty.LENGTH_LONG
            ).show()
        }
    }

    private fun processGrn() {
        try {
            edInvoiceNo = binding.edInvoiceNumber.text.toString().trim()
            /* for (r in getActiveSuppliersDDLResponse) {
                *//* if (selectedSupplierCode != "" && selectedSupplierCode == r.code) {
                    grnSaveToDraftDefaultRequest = GRNSaveToDraftDefaultRequest(
                        r.code, r.value,
                        r.text, "",
                        0, "Draft",
                        "2024-04-06", "$edInvoiceNo",
                        selectedPoFilteredList.toString(), "Domestic"
                    )
                }*//*
            }*/

            ///if not inr =import else domestic
         /*   Log.e("currency",selectedSupplierCode.toString())
            Log.e("currency",GRNSaveToDraftDefaultRequest(
                selectedSupplierCode,
                selectedBpId.toInt(),
                null, "Draft", selectedDate!!, edInvoiceNo, "Domestic", selectedCurrency
            ).toString())*/
            if(selectedDate!="")
            {
                if (selectedCurrency != "" && selectedCurrency.equals("INR") )
                {
                    viewModel.processGRN(
                        token, Constants.BASE_URL, GRNSaveToDraftDefaultRequest(
                            selectedSupplierCode,
                            selectedBpId.toInt(),
                            null, "Draft", selectedDate!!, edInvoiceNo, "Domestic", selectedCurrency
                        )
                    )
                }
                else {

                    viewModel.processGRN(
                        token, Constants.BASE_URL, GRNSaveToDraftDefaultRequest(
                            selectedSupplierCode,
                            selectedBpId.toInt(),
                            null, "Draft", selectedDate!!, edInvoiceNo, "Import", selectedCurrency
                        )
                    )
                }
            }
            else
            {
                Toasty.error(
                    this,
                    "Please select Invoice Date!!",
                    Toasty.LENGTH_LONG
                ).show()
            }


        } catch (e: Exception) {
            Toasty.error(
                this,
                e.message.toString()+"askjdhasjhdsa",
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

    private fun getSupplierList() {
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

    private fun setPoLineItemList() {
        /* grnMainItemAdapter = GrnMainAddAdapter()
         grnMainItemAdapter?.updateList(selectedPoLineItem, this@GRNAddActivity)
         binding.rcGrnAdd!!.adapter = grnMainItemAdapter
         binding.rcGrnAdd.layoutManager =
             LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

         grnMainItemAdapter?.setOnItemCheckClickListener {
             setCreateBatchesDialog(it)
             *//*   binding.clContainer.visibility=View.GONE
               binding.clContainer2.visibility=View.VISIBLE
               transacton(it)*//*
        }*/
        grnMainItemAdapter = GrnMainAddAdapter(selectedPoLineItem)
        binding.rcGrnAdd!!.adapter = grnMainItemAdapter
        binding.rcGrnAdd.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        grnMainItemAdapter?.setOnItemCheckClickListener { position, poline ->
            createBatchesList.clear()
            poline.grnLineItemUnit?.let { createBatchesList.addAll(it) }
            currentPoLineItemPosition = position.toString()
            setCreateBatchesDialog(position, poline)
            //  addNewBatch(position,poline)
        }
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
        selectPoBinding.closeDialogueTopButton.setOnClickListener {
            selectPoDialog!!.dismiss()
        }
        selectPoDialog!!.show()
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
    private fun setCreateBatchesDialog(position: Int, poModel: PoLineItemSelectionModelNewStore) {
        setInfoValues(poModel)
        createBatchesDialogBinding.mcvAddBatches.setOnClickListener {
            addBatchNewItem(position, poModel)
        }

        createBatchedDialog!!.show()
    }

    private fun setInfoValues(poModel: PoLineItemSelectionModelNewStore) {
        createBatchesDialogBinding.grnAddHeader.tvPoNoValue.setText(poModel.poNumber)
        createBatchesDialogBinding.grnAddHeader.tvGdPoNoValue.setText(poModel.GDPONumber)
        createBatchesDialogBinding.grnAddHeader.tvLineItemDescValue.setText(poModel.itemCode)
        createBatchesDialogBinding.grnAddHeader.tvItemDescValue.setText(poModel.itemDescription)
        createBatchesDialogBinding.grnAddHeader.tvPuomValue.setText(poModel.pouom)
        createBatchesDialogBinding.grnAddHeader.tvPoQtyValue.setText(poModel.poqty.toString())
        createBatchesDialogBinding.grnAddHeader.tvQtyValue.setText(poModel.quantityReceived)
        createBatchesDialogBinding.grnAddHeader.tvMhTypeValue.setText(poModel.mhType)
        createBatchesDialogBinding.grnAddHeader.tvBalanceQuantity.setText(poModel.balQTY.toString())
    }

    /*  private fun addNewBatch(poModel: PoLineItemSelectionModel) {
          var edBatchNo = createBatchesDialogBinding.edBatchNo.text.toString().trim()
          if (edBatchNo.isNotEmpty()) {
              val hasItemWithZeroReceivedQuantity = createBatchesList.any { it.ReceivedQty == "0.000" }
              if(hasItemWithZeroReceivedQuantity)
              {
                  Toasty.warning(this@GRNAddActivity,"Please complete current transaction!!").show()
              }
              else
              {
                  val newBatchList = arrayListOf(createBatchInfo(poModel, edBatchNo))
                 // createBatchesListMap[edBatchNo] = newBatchList
                  createBatchesList.add(createBatchInfo(poModel, edBatchNo))
                  Log.e("createBatchesListAddNewBAtch",createBatchesList.toString())
                  createBatchesMainRcAdapter?.notifyItemInserted(createBatchesList.size-1)
              }
          }
          else
          {
              Toast.makeText(this@GRNAddActivity,"Please enter batch no!!",Toast.LENGTH_SHORT).show()
          }
      }*/
    private fun addBatchNewItem(position: Int, poModel: PoLineItemSelectionModelNewStore) {
        val edBatchNo = createBatchesDialogBinding.edBatchNo.text.toString().trim()
        if (edBatchNo.isNotEmpty()) {
            val hasItemWithZeroReceivedQuantity = selectedPoLineItem.any {
                it.grnLineItemUnit?.any { it.recevedQty.toString() == "0.000" } ?: false
            }
            if (hasItemWithZeroReceivedQuantity) {
                Toasty.warning(this@GRNAddActivity, "Please complete current transaction!!").show()
                Log.d("edBatchNo.isNotEmpty()", "sad")
            } else {
                val newBatch = createBatchInfo(poModel, edBatchNo)
                if (position != RecyclerView.NO_POSITION) {
                    val model = selectedPoLineItem[position]
                    // Initialize batchInfoListModel if it's null
                    if (model.grnLineItemUnit == null) {
                        model.grnLineItemUnit = mutableListOf()
                    }
                    // Add new batch to batchInfoListModel
                    model.grnLineItemUnit!!.add(newBatch)
                    createBatchesList.add(createBatchInfo(poModel, edBatchNo))
                    createBatchesMainRcAdapter?.notifyItemInserted(createBatchesList.size - 1)
                    grnMainItemAdapter?.notifyItemChanged(position)
                    Log.d("edBatchNo.isNotEmpty()", selectedPoLineItem.toString())
                }
            }
        } else {
            Toast.makeText(this@GRNAddActivity, "Please enter batch no!!", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun addExisitngNewItem(
        edBatchNo: String,
        position: Int,
        poModel: PoLineItemSelectionModelNewStore
    ) {

        if (edBatchNo.isNotEmpty()) {
            val hasItemWithZeroReceivedQuantity = selectedPoLineItem.any {
                it.grnLineItemUnit?.any { it.recevedQty.toString() == "0.000" } ?: false
            }
            if (hasItemWithZeroReceivedQuantity) {
                Toasty.warning(this@GRNAddActivity, "Please complete current transaction!!").show()
                Log.d("edBatchNo.isNotEmpty()", "sad")
            } else {
                val newBatch = createBatchInfo(poModel, edBatchNo)
                if (position != RecyclerView.NO_POSITION) {
                    val model = selectedPoLineItem[position]
                    // Initialize batchInfoListModel if it's null
                    if (model.grnLineItemUnit == null) {
                        model.grnLineItemUnit = mutableListOf()
                    }
                    // Add new batch to batchInfoListModel
                    model.grnLineItemUnit!!.add(newBatch)
                    createBatchesList.add(createBatchInfo(poModel, edBatchNo))
                    createBatchesMainRcAdapter?.notifyItemInserted(createBatchesList.size - 1)
                    grnMainItemAdapter?.notifyItemChanged(position)
                    Log.d("edBatchNo.isNotEmpty()", selectedPoLineItem.toString())
                }
            }
        } else {
            Toast.makeText(this@GRNAddActivity, "Please enter batch no!!", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun createBatchInfo(
        poModel: PoLineItemSelectionModelNewStore,
        edBatchNo: String
    ): GrnLineItemUnitStore {
        // Find the latest number associated with the batch prefix

        /*   val latestNumber = createBatchesList
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
               .maxOrNull() ?: 0*/
        // Increment the number part for the new entry
        //val generatedBarcodeNo = "$edBatchNo-${latestNumber + 1}"

        // Create and return the BatchInfoListModel
        return GrnLineItemUnitStore(
            poModel.pouom,
            currentGeneratedBarcode,
            "",
            "$edBatchNo/$selectedKGRN",
            false,
            0, 0,
            "0.000", edBatchNo, false
        )


        /*BatchInfoListModel(
                poModel.ex,
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
            )*/

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