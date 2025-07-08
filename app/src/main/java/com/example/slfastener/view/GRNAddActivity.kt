package com.example.slfastener.view

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialog
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
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
import com.example.slfastener.adapter.demoAdapter.CreateBatchesNewSingleListAdapter
import com.example.slfastener.adapter.newadapters.SelectPoLineAdapter
import com.example.slfastener.adapter.othercharges.OtherChargesAdapter
import com.example.slfastener.databinding.ActivityGrnaddBinding
import com.example.slfastener.databinding.CreateBatchesDialogBinding
import com.example.slfastener.databinding.CreateBatchesMultipleDialogBinding
import com.example.slfastener.databinding.CreateBatchesSingleDialogBinding
import com.example.slfastener.databinding.DescriptionInfoDialogBinding
import com.example.slfastener.databinding.OtherChargesDialogBinding
import com.example.slfastener.databinding.SelectLineItemDialogBinding
import com.example.slfastener.databinding.SelectPoIdsDialogBinding

import com.example.slfastener.helper.CustomKeyboard
import com.example.slfastener.helper.printer.USBPrinterHelper
import com.example.slfastener.helper.weighing.UsbCommunicationManager
import com.example.slfastener.model.GetActiveSuppliersDDLResponse
import com.example.slfastener.model.GetPOsAndLineItemsOnPOIdsResponse
import com.example.slfastener.model.GetSuppliersPOsDDLResponse
import com.example.slfastener.model.getalllocation.GetAllWareHouseLocationResponse
import com.example.slfastener.model.getalltax.GetAllTaxItem
import com.example.slfastener.model.grn.GRNSaveToDraftDefaultRequest
import com.example.slfastener.model.grn.ProcessGRNLineItemsResponse
import com.example.slfastener.model.grndraftdata.GetDraftGrnResponse
import com.example.slfastener.model.grndraftdata.GrnLineItemUnit
import com.example.slfastener.model.grnmain.SubmitGRNRequest
import com.example.slfastener.model.offlinebatchsave.CustomGrnLineItemUnit
import com.example.slfastener.model.offlinebatchsave.CustomPoLineItemSelectionModel
import com.example.slfastener.model.othercharges.GetOtherChargesItem
import com.example.slfastener.model.grnlineitemmain.GRNUnitLineItemsSaveRequest
import com.example.slfastener.viewmodel.GRNTransactionViewModel
import com.example.slfastener.viewmodel.GRNTransactionViewModelProviderFactory

import es.dmoral.toasty.Toasty
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class GRNAddActivity : AppCompatActivity(), USBPrinterHelper.PrinterStatusListener {
    //Default Set
    lateinit var binding: ActivityGrnaddBinding
    private lateinit var viewModel: GRNTransactionViewModel
    private lateinit var session: SessionManager
    private lateinit var progress: ProgressDialog
    private lateinit var userDetails: HashMap<String, Any?>
    var token: String = ""
    private var baseUrl: String = ""
    private var serverIpSharedPrefText: String? = null
    private var serverHttpPrefText: String? = null


    //Dialog Variables
    //Select Po Dialog
    var selectPoIdsDialog: Dialog? = null
    lateinit var selectPoIdsDialogBinding: SelectPoIdsDialogBinding
    lateinit var selectedPoFilteredList: MutableList<Int>

    //Line Item Dialog
    private lateinit var selectLineItemDialogBinding: SelectLineItemDialogBinding
    var selectLineItemDialog: Dialog? = null

    //create batches dialog
    lateinit var createBatchesDialogBinding: CreateBatchesDialogBinding
    lateinit var createBatchedDialog: AppCompatDialog

    //Add single batch dialog
    private lateinit var createBatchesSingleDialogBinding: CreateBatchesSingleDialogBinding
    var createBatchSingleDialog: Dialog? = null

    //Add multiple batches dialog
    private lateinit var createBatchesMultipleDialogBinding: CreateBatchesMultipleDialogBinding
    var createMultipleBatchesDialog: Dialog? = null

    // item description dialog
    private lateinit var itemDescriptionBinding: DescriptionInfoDialogBinding
    var itemDescriptionDialog: Dialog? = null


    //API Calling
    //Default Api Calls
    //getAllTax
    lateinit var getALlTax: MutableList<GetAllTaxItem>

    //GetALlLocations
    lateinit var getAllLocation: MutableList<GetAllWareHouseLocationResponse>

    //Transaction Api calls
    // Get Supplier List
    val supplierMap = HashMap<String, String>()
    var selectedSupplierBpCode: String = ""
    lateinit var supplierSpinnerArray: MutableList<String>
    lateinit var getActiveSuppliersDDLResponse: MutableList<GetActiveSuppliersDDLResponse>
    private var supplierSpinnerAdapter: ArrayAdapter<String>? = null

    //Get Po based on Supplier BPCode
    lateinit var getSuppliersPOsDDLResponse: MutableList<GetSuppliersPOsDDLResponse>

    //Generate GRN
    lateinit var grnSaveToDraftDefaultResponse: ProcessGRNLineItemsResponse

    //Get Po Line Items based on selected Po ids
    lateinit var getPOsAndLineItemsOnPOIdsResponse: MutableList<GetPOsAndLineItemsOnPOIdsResponse>
    lateinit var poLineItem: MutableList<CustomPoLineItemSelectionModel>
    lateinit var selectedPoLineItem: MutableList<CustomPoLineItemSelectionModel>
    var edInvoiceNo: String = ""
    private var selectPoLineAdapter: SelectPoLineAdapter? = null
    lateinit var createBatchesList: MutableList<CustomGrnLineItemUnit>
    var currentPoLineItemPosition = ""
    lateinit var currentSelectedPoModel: CustomPoLineItemSelectionModel
    private var grnMainItemAdapter: GrnMainAddAdapter? = null
    private var createBatchesMainRcAdapter: CreateBatchesNewSingleListAdapter? = null
    lateinit var selectedBatchForPrint: ArrayList<Int>
    private var printerPrnforGRN: String? = null
    var formattedPrnForPrinter = ""
    var isPrinterConnected = false
    var taxAmount = ""
    var totalUnits = 0
    var taxId = 0

    //get data from draft
    private var getDraftGrnResponse: GetDraftGrnResponse? = null

    //Other Charges
    lateinit var getOtherCharges: MutableList<GetOtherChargesItem>
    private var otherChargesAdapter: OtherChargesAdapter? = null
    lateinit var otherChargesDialogBinding: OtherChargesDialogBinding
    lateinit var otherChargesDialog: AppCompatDialog


    // Variable to keep track of the last data update time
    private var lastUpdateTime: Long = 0
    private var previousData: String? = null
    private var selectedBpId = ""
    private var selectedBpName = ""
    var selectedInvoiceDate: String = ""
    var selectedCurrency: String = ""
    var selectedKGRN: String = ""
    var balanceQty = ""
    var totalQty = ""
    var currentGrnID = 0
    var lineItemUnitId = 0
    var lineItemId = 0
    var grnId = 0
    var deleteLineItem = 0
    var multibarcodeMaintain = ""
    var deleteBatchUnitItemPosition = ""
    var deleteLineItemPosition = ""
    var currentBatchTobeDublicated: CustomGrnLineItemUnit? = null
    var responsesReceived = 0
    var numberOfTimesToDublicate = 0
    var barcodes = ArrayList<String>()
    var isGRNUpdate = false


    private lateinit var customKeyboard: CustomKeyboard
    private lateinit var usbPrinterHelper: USBPrinterHelper
    private var serverPrinterTypePrefText: String? = null
    private lateinit var usbCommunicationManager: UsbCommunicationManager
    private val DEBOUNCE_PERIOD = 1000L

    private val taxCodes: HashMap<String, Double> = HashMap<String, Double>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Bindings
        binding = DataBindingUtil.setContentView(this, R.layout.activity_grnadd)
        progress = ProgressDialog(this)
        progress.setMessage("Loading...")
        session = SessionManager(this)
        userDetails = session.getUserDetails()
        token = userDetails["jwtToken"].toString()
        serverIpSharedPrefText = userDetails!![Constants.KEY_SERVER_IP].toString()
        serverHttpPrefText = userDetails!![Constants.KEY_HTTP].toString()
        baseUrl = "$serverHttpPrefText://$serverIpSharedPrefText/service/api/"
        serverPrinterTypePrefText = userDetails[Constants.KEY_PRINTER_TYPE].toString()
        printerPrnforGRN = userDetails[Constants.KEY_GRN_PRN].toString()
        val SLFastenerRepository = SLFastenerRepository()
        val viewModelProviderFactory =
            GRNTransactionViewModelProviderFactory(application, SLFastenerRepository)
        viewModel =
            ViewModelProvider(this, viewModelProviderFactory)[GRNTransactionViewModel::class.java]


        //Dialog Inits
        //Select Po Ids Dialog Init
        selectPoIdsDialogBinding = SelectPoIdsDialogBinding.inflate(getLayoutInflater());
        selectPoIdsDialog = Dialog(this)
        binding.clSelectPoId.setOnClickListener {
            setSelectSupplierDialog()
        }

        //Line Item Dialog Inits
        poLineItem = ArrayList()
        selectLineItemDialogBinding = SelectLineItemDialogBinding.inflate(layoutInflater)
        selectLineItemDialog = AppCompatDialog(this).apply {
            setContentView(selectLineItemDialogBinding.root)
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
        selectPoLineAdapter = SelectPoLineAdapter(poLineItem, onItemCheckedChange = { item ->
            if (item.isSelected) {
                selectedPoLineItem.add(item)
                Log.d("selectedItemsitemitem", item.toString())
                Log.d("selectedItems", selectedPoLineItem.toString())
            } else {
                selectedPoLineItem.remove(item)
                Log.d("selectedItems", selectedPoLineItem.toString())
            }
        })
        selectLineItemDialogBinding.lineItemHeader.ivSelection.setOnClickListener {
            poLineItem.forEachIndexed { index, it ->
                it.isSelected = true
                selectedPoLineItem.add(it)
                selectPoLineAdapter!!.notifyItemChanged(index)
            }
        }
        selectLineItemDialogBinding.rcLineItem.adapter = selectPoLineAdapter
        selectLineItemDialogBinding.rcLineItem.layoutManager = LinearLayoutManager(this)
        selectLineItemDialogBinding.rcLineItem!!.setHasFixedSize(true)
        binding.mcvNewLineItem.setOnClickListener {
            setSelectLineItemDialog()
        }

        //create batches
        createBatchesDialogBinding = CreateBatchesDialogBinding.inflate(LayoutInflater.from(this));
        createBatchedDialog = AppCompatDialog(this).apply {
            setContentView(createBatchesDialogBinding.root)
            window?.apply {
                setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT
                )
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


                val layoutParams =
                    createBatchesDialogBinding.root.layoutParams as ViewGroup.MarginLayoutParams
                layoutParams.setMargins(10, 10, 10, 10)
                createBatchesDialogBinding.root.layoutParams = layoutParams
            }
        }
        createBatchedDialog!!.setCancelable(true)
        createBatchesDialogBinding.closeDialogueTopButton.setOnClickListener {
            totalUnits = 0
            createBatchesSingleDialogBinding.et1.setText("")
            createBatchedDialog!!.dismiss()
        }
        createBatchesDialogBinding.mcvCancel.setOnClickListener {
            totalUnits = 0
            createBatchesSingleDialogBinding.et1.setText("")
            createBatchedDialog!!.dismiss()

        }
        createBatchesDialogBinding.mcvClearBatchBarcode.setOnClickListener {
            createBatchesSingleDialogBinding.et1.setText("")
        }

        val keyboardLayout = layoutInflater.inflate(R.layout.custom_number_keyboard, null)
        customKeyboard = CustomKeyboard(this, keyboardLayout)
        createBatchesList = ArrayList()



        createBatchesMainRcAdapter =
            CreateBatchesNewSingleListAdapter(
                this@GRNAddActivity,
                createBatchesList,
                addItem = { newItem ->
                    if (createBatchesList.size < totalUnits) {
                        generateBarcodeForBatchesForExisitng()
                        addExisitngNewItem(
                            newItem.supplierBatchNo,
                            currentPoLineItemPosition.toInt(),
                            selectedPoLineItem.get(currentPoLineItemPosition.toInt())
                        )
                    } else {
                        Toasty.warning(
                            this@GRNAddActivity,
                            "Batches Should not exceed the total units..!"
                        ).show()
                    }

                },
                addMultiItem = { newItem ->
                    if (createBatchesList.size < totalUnits && (createBatchesList.size + numberOfTimesToDublicate <= totalUnits)) {
                        currentBatchTobeDublicated = newItem
                        addMultipleBatches(newItem)
                    } else {
                        Toasty.warning(
                            this@GRNAddActivity,
                            "Batches Should not exceed the total units..!"
                        ).show()
                    }
                },
                onSave = { position, updatedItem ->
                    val tempList = createBatchesList.toMutableList()
                    tempList[position] = updatedItem.copy()
                    if (selectedPoLineItem[currentPoLineItemPosition.toInt()].pouom.equals("KGS")) {
                        calculateWeightUpdate(tempList, position, updatedItem)
                    } else {
                        calculateForNumbers(tempList, position, updatedItem)
                    }
                },
                onDelete = { position, grnitem ->
                    if (grnitem.isUpdate == true) {
                        deleteBatchUnitItemPosition = position.toString()
                        lineItemUnitId = grnitem.lineItemUnitId
                        Log.e("Lineitemunitid", lineItemUnitId.toString())
                        deleteBatches(grnitem)
                    } else {
                        selectedPoLineItem[currentPoLineItemPosition.toInt()].grnLineItemUnit!!.removeAt(
                            position
                        )
                        grnMainItemAdapter!!.notifyItemRemoved(currentPoLineItemPosition.toInt())
                        grnMainItemAdapter!!.notifyItemRangeChanged(
                            currentPoLineItemPosition.toInt(),
                            selectedPoLineItem.size
                        )
                        createBatchesList.removeAt(position)
                        createBatchesMainRcAdapter!!.notifyItemRemoved(position)
                        createBatchesMainRcAdapter!!.notifyItemRangeChanged(
                            position,
                            createBatchesList.size
                        )
                    }
                },
                onPrint = { position, grnitem ->

                    if (isPrinterConnected == false) {
                        Toasty.error(
                            this,
                            "Printer Not Connected.!!",
                            Toasty.LENGTH_LONG
                        ).show()
                    } else if (serverPrinterTypePrefText!!.contains("USB")) {
                        printLabelForUSB(grnitem)
                    } else if (serverPrinterTypePrefText!!.contains("IP")) {
                        printLabelForGRN(grnitem)
                    } else {
                        Toasty.error(
                            this,
                            "Printer Not Set.!!",
                            Toasty.LENGTH_LONG
                        ).show()
                    }
                },
                onItemCheckedChange = { item ->
                    if (item.isChecked) {
                        selectedBatchForPrint.add(item.lineItemUnitId)
                    } else {
                        selectedBatchForPrint.remove(item.lineItemUnitId)
                    }
                },
                customKeyboard = customKeyboard,
            )
        createBatchesDialogBinding.rcBatchs.adapter = createBatchesMainRcAdapter
        createBatchesDialogBinding.rcBatchs.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        createBatchesDialogBinding.ivBatchesSelection.setOnClickListener {
            createBatchesList.forEachIndexed { index, it ->
                it.isChecked = true
                selectedBatchForPrint.add(it.lineItemUnitId)
                createBatchesMainRcAdapter!!.notifyItemChanged(index)
            }
        }
        createBatchesDialogBinding.ivPrintAll.setOnClickListener {
            if (isPrinterConnected == false) {
                Toasty.error(
                    this,
                    "Printer Not Connected.!!",
                    Toasty.LENGTH_LONG
                ).show()
            } else if (serverPrinterTypePrefText!!.contains("USB")) {

                printLabelUSBForBulk()
            } else if (serverPrinterTypePrefText!!.contains("IP")) {
                printLabelForBulk()

            } else {
                Toasty.error(
                    this,
                    "Printer Not Set.!!",
                    Toasty.LENGTH_LONG
                ).show()
            }

            Log.e("selectedBatchForPrint", selectedBatchForPrint.toString())
        }

        //item description dialog
        itemDescriptionBinding = DescriptionInfoDialogBinding.inflate(LayoutInflater.from(this))
        itemDescriptionDialog = Dialog(this)
        setItemDescriptionDialog()

        //create single batch
        createBatchesSingleDialogBinding =
            CreateBatchesSingleDialogBinding.inflate(LayoutInflater.from(this))
        createBatchSingleDialog = Dialog(this)
        setCreateBatchSingleDialog()

        //create multiple batch
        createBatchesMultipleDialogBinding =
            CreateBatchesMultipleDialogBinding.inflate(LayoutInflater.from(this))
        createMultipleBatchesDialog = Dialog(this)
        setCreateBatchesMultipleDialog()

        //other Charges Dialog
        getOtherCharges = ArrayList()
        otherChargesDialogBinding = OtherChargesDialogBinding.inflate(LayoutInflater.from(this))
        otherChargesDialog = AppCompatDialog(this).apply {
            setContentView(otherChargesDialogBinding.root)
            window?.apply {
                setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT
                )
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // Set transparent background
            }
        }
        otherChargesDialog!!.setCancelable(true)
        otherChargesDialogBinding.mcvCancel.setOnClickListener {
            otherChargesDialog.dismiss()
        }
        otherChargesDialogBinding.closeDialogButton.setOnClickListener {
            otherChargesDialog.dismiss()
        }

        binding.mcvViewOtherCharges.setOnClickListener {
            otherChargesDialog.show()
        }


        //DEFAULT APIS Calls
        val receivedIntent = intent
        grnId = receivedIntent.getIntExtra("GRNID", 0)

        if (grnId != 0) {
            currentGrnID = grnId
            callDefaultData()
            binding.clKGRNNo.visibility = View.VISIBLE
            binding.mcvAddGrn.visibility = View.GONE
            binding.mcvNewLineItem.visibility = View.VISIBLE
            binding.tvSpinnerSupplier.apply {
                isFocusable = false
                isFocusableInTouchMode = false
                isClickable = false

            }
        } else {
            binding.clKGRNNo.visibility = View.GONE
            binding.mcvAddGrn.visibility = View.VISIBLE
            binding.mcvNewLineItem.visibility = View.GONE
        }
        getAllLocations()
        getAllTax()
        getSupplierList()


        //getAllTax
        getALlTax = ArrayList()
        viewModel.getAllTaxResponse.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { resultResponse ->
                        try {
                            getALlTax = resultResponse
                            if (getALlTax.size > 0) {
                                getALlTax.forEach {
                                    taxCodes.put(it.taxCode, it.percentage)
                                }
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
                            "failed - \nError Message: $errorMessage"
                        ).show()
                        session.showToastAndHandleErrors(errorMessage, this@GRNAddActivity)
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }

        //Get All Locations
        getAllLocation = ArrayList()
        viewModel.getAllLocationsMutableResponse.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { resultResponse ->
                        try {
                            getAllLocation = resultResponse
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
                            "failed - \nError Message: $errorMessage"
                        ).show()
                        session.showToastAndHandleErrors(errorMessage, this@GRNAddActivity)
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }


        //TRANSACTION APIS
        //Get Active Supplier
        getActiveSuppliersDDLResponse = ArrayList()
        supplierSpinnerArray = ArrayList()
        viewModel.getActiveSuppliersDDLMutable.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    supplierMap.clear()
                    supplierSpinnerArray.clear()
                    getActiveSuppliersDDLResponse.clear()
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
                            "Error Message: $errorMessage"
                        ).show()
                        session.showToastAndHandleErrors(errorMessage, this@GRNAddActivity)
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }

        //get list of Po based on BP Code of supplier
        getSuppliersPOsDDLResponse = ArrayList()
        selectedPoFilteredList = ArrayList()
        viewModel.getSuppliersPosDDLLMutableResponse.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    getSuppliersPOsDDLResponse.clear()
                    hideProgressBar()
                    response.data?.let { resultResponse ->
                        try {
                            if (resultResponse.size > 0) {
                                for (r in resultResponse) {
                                    getSuppliersPOsDDLResponse.add(
                                        GetSuppliersPOsDDLResponse(
                                            r.code,
                                            r.isActive,
                                            r.text,
                                            r.value,
                                            false,
                                            false
                                        )
                                    )
                                }
                                binding.clSelectPoId.visibility = View.VISIBLE
                                if (grnId != 0) {
                                    selectePoDefaultRc()
                                } else {
                                    selectePoRc()
                                }
                            } else {
                                Toast.makeText(this, "List is Empty!!", Toast.LENGTH_SHORT).show()
                                binding.clSelectPoId.visibility = View.GONE
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
                    binding.clSelectPoId.visibility = View.GONE
                    response.message?.let { errorMessage ->
                        Toasty.error(
                            this@GRNAddActivity,
                            "Login failed - \nError Message: $errorMessage"
                        ).show()
                        session.showToastAndHandleErrors(errorMessage, this@GRNAddActivity)
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }

        //Generate GRN
        viewModel.processGRNMutableResponse.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { resultResponse ->
                        try {
                            if (resultResponse != null) {
                                grnSaveToDraftDefaultResponse = resultResponse
                                selectedKGRN = resultResponse.responseObject?.kgrnNumber ?: ""
                                currentGrnID = resultResponse.responseObject?.grnId ?: 0
                                binding.mcvAddGrn.visibility = View.GONE
                                binding.clKGRNNo.visibility = View.VISIBLE
                                binding.mcvNewLineItem.visibility = View.VISIBLE
                                binding.tvKGRNNo.setText(selectedKGRN.toString())

                                Log.e("processkgrn", grnSaveToDraftDefaultResponse.toString())
                                Log.e("processkgrn", selectedKGRN.toString())
                                getOtherCharges()

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
                        session.showToastAndHandleErrors(errorMessage, this@GRNAddActivity)
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }

        //Get Po Line items based on Selected Po Ids

        selectedPoLineItem = ArrayList()
        getPOsAndLineItemsOnPOIdsResponse = ArrayList()
        selectedBatchForPrint = ArrayList()
        viewModel.getPosLineItemsOnPoIdsMutableResponse.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { resultResponse ->
                        try {
                            edInvoiceNo = binding.edInvoiceNumber.text.toString().trim()
                            //edGdpoNo = binding.edGdpo.text.toString().trim()
                            if (resultResponse.size > 0) {
                                if (grnId != 0) {

                                    var findGrnLineItemSize =
                                        getDraftGrnResponse!!.grnTransaction.grnLineItems
                                    if (isGRNUpdate) {
                                        for (r in resultResponse) {
                                            for (e in r.poLineItems) {
                                                val additionalValue =
                                                    if (e.poUoM != "KGS") "0" else "0.000"
                                                val unitPrice = e.unitPrice

                                                val hasSamePosapline =
                                                    poLineItem.any { it.posapLineItemNumber == e.posapLineItemNumber }
                                                if (!hasSamePosapline) {
                                                    CustomPoLineItemSelectionModel(
                                                        e.isQCRequired,
                                                        e.isExpirable,
                                                        0,
                                                        e.balQty.toDouble(),
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
                                                        e.poQty,
                                                        e.posapLineItemNumber,
                                                        e.poUoM,
                                                        additionalValue,
                                                        false,
                                                        unitPrice,
                                                        getAllLocation,
                                                        e.locationId,
                                                        false,
                                                        getALlTax, 0, 0, 0.0, "0"
                                                    ).also {
                                                        poLineItem.add(it)
                                                    }
                                                }

                                                //Log.e("selectPolInepoLineItem", poLineItem.toString())
                                                //  Log.e("e.locationId", e.locationId.toString())
                                            }
                                        }
                                        selectPoLineAdapter!!.notifyDataSetChanged()
                                    } else if (findGrnLineItemSize.size == 0) {
                                        for (r in resultResponse) {
                                            for (e in r.poLineItems) {
                                                val additionalValue =
                                                    if (e.poUoM != "KGS") "0" else "0.000"
                                                val unitPrice = e.unitPrice
                                                val hasSamePosapline =
                                                    poLineItem.any { it.posapLineItemNumber == e.posapLineItemNumber }
                                                if (!hasSamePosapline) {
                                                    CustomPoLineItemSelectionModel(
                                                        e.isQCRequired,
                                                        e.isExpirable,
                                                        0,
                                                        e.balQty.toDouble(),
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
                                                        e.poQty,
                                                        e.posapLineItemNumber,
                                                        e.poUoM,
                                                        additionalValue,
                                                        false,

                                                        unitPrice,
                                                        getAllLocation,
                                                        e.locationId,
                                                        false,
                                                        getALlTax, 0, 0, 0.0, "0"
                                                    ).also {
                                                        poLineItem.add(it)
                                                    }
                                                }

                                                //Log.e("selectPolInepoLineItem", poLineItem.toString())
                                                //Log.e("e.locationId", e.locationId.toString())

                                            }
                                        }
                                        selectPoLineAdapter!!.notifyDataSetChanged()

                                    } else {
                                        getPOsAndLineItemsOnPOIdsResponse.addAll(resultResponse)
                                        var d = getDraftGrnResponse!!.grnTransaction
                                        var df = getDraftGrnResponse!!.grnTransaction.grnLineItems
                                        for (i in df) {
                                            var poID = i.posapLineItemNumber
                                            val convertedGrnLineItemUnits =
                                                i.grnLineItemUnit.map { grnLineItemUnit ->
                                                    grnLineItemUnit.uoM?.let {
                                                        CustomGrnLineItemUnit(
                                                            it,
                                                            i.mhType,
                                                            grnLineItemUnit.barcode,
                                                            grnLineItemUnit.expiryDate,
                                                            i.isExpirable,
                                                            grnLineItemUnit.kBatchNo,
                                                            false,
                                                            grnLineItemUnit.lineItemId,
                                                            grnLineItemUnit.lineItemUnitId,
                                                            grnLineItemUnit.qty.toString(),
                                                            grnLineItemUnit.supplierBatchNo,
                                                            true, i.totalUnit
                                                        )
                                                    }
                                                }.toMutableList()
                                            //Log.e("editcaseDefaultList", convertedGrnLineItemUnits.toString() + "//////${df.size}")
                                            //Log.e("editcaseDefaultListdf ", df.toString())
                                            val existingItem =
                                                selectedPoLineItem.find { it.posapLineItemNumber == poID }
                                            if (existingItem == null) {
                                                selectedPoLineItem.add(
                                                    CustomPoLineItemSelectionModel(
                                                        i.isQCRequired,
                                                        i.isExpirable,
                                                        i.lineItemId,
                                                        i.balQty,
                                                        d.currency,
                                                        convertedGrnLineItemUnits,
                                                        i.itemCode,
                                                        i.itemDescription,
                                                        i.itemName,
                                                        i.mhType,
                                                        i.poId,
                                                        i.poLineItemId,
                                                        i.poLineNo,
                                                        i.poNumber,
                                                        i.poQty,
                                                        i.posapLineItemNumber,
                                                        i.poUoM,
                                                        i.grnQty.toString(),
                                                        true,
                                                        i.unitPrice,
                                                        getAllLocation,
                                                        i.locationId,
                                                        true,
                                                        getALlTax,
                                                        i.taxId,
                                                        i.totalUnit,
                                                        i.discountAmount,
                                                        calculateLineItemTotal(
                                                            i.grnQty,
                                                            i.unitPrice,
                                                            i.discountAmount,
                                                            i.taxPercent
                                                        ).toString()
                                                    )
                                                )
                                            }
                                        }
                                        for (r in resultResponse) {
                                            for (e in r.poLineItems) {
                                                val additionalValue =
                                                    if (e.poUoM != "KGS") "0" else "0.000"
                                                val unitPrice =
                                                    e.unitPrice

                                                // Check if poNumber from df matches poNumber from resultResponse
                                                val poNumberMatches =
                                                    df.any { it.poNumber == r.poNumber && it.itemCode == e.itemCode }
                                                if (!poNumberMatches) {
                                                    CustomPoLineItemSelectionModel(
                                                        e.isQCRequired,
                                                        e.isExpirable,
                                                        e.poLineItemId,
                                                        e.balQty.toDouble(),
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
                                                        e.poQty,

                                                        e.posapLineItemNumber,
                                                        e.poUoM,
                                                        additionalValue,
                                                        false,

                                                        unitPrice,
                                                        getAllLocation,
                                                        e.locationId,
                                                        false,
                                                        getALlTax, 0, 0, 0.0, "0"
                                                    ).also { poLineItem.add(it) }
                                                    //Log.e("selectPolIne", poLineItem.toString())
                                                }
                                            }
                                        }
                                        grnMainItemAdapter!!.notifyDataSetChanged()
                                    }
                                } else {
                                    for (r in resultResponse) {
                                        for (e in r.poLineItems) {
                                            val additionalValue =
                                                if (e.poUoM != "KGS") "0" else "0.000"
                                            val unitPrice =
                                                e.unitPrice
                                            val hasSamePosapline =
                                                poLineItem.any { it.posapLineItemNumber == e.posapLineItemNumber }
                                            if (!hasSamePosapline) {
                                                CustomPoLineItemSelectionModel(
                                                    e.isQCRequired,
                                                    e.isExpirable,
                                                    0,
                                                    e.balQty.toDouble(),
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
                                                    e.poQty,
                                                    e.posapLineItemNumber,
                                                    e.poUoM,
                                                    additionalValue,
                                                    false,

                                                    unitPrice,
                                                    getAllLocation, e.locationId, false,
                                                    getALlTax, 0, 0, 0.0, "0"
                                                ).also {
                                                    poLineItem.add(it)
                                                }
                                            }

                                            //Log.e("selectPolInepoLineItem", poLineItem.toString())
                                            //Log.e("e.locationId", e.locationId.toString())


                                        }
                                    }
                                    selectPoLineAdapter!!.notifyDataSetChanged()
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
                        session.showToastAndHandleErrors(errorMessage, this@GRNAddActivity)
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }


        grnMainItemAdapter = GrnMainAddAdapter(
            this@GRNAddActivity,
            selectedPoLineItem,
            itemDescription = {
                setItemDescription(it)
            },
            onItemCheck = { position, poline ->
                createBatchesList.clear()
                if (poline?.grnLineItemUnit != null) {
                    lineItemId = poline.lineItemId
                    totalUnits = poline.totalUnit
                    taxId = poline.taxId
                    for (grnLineUnit in poline.grnLineItemUnit!!) {
                        if (grnLineUnit != null) {
                            createBatchesList.add(
                                CustomGrnLineItemUnit(
                                    grnLineUnit.UOM,
                                    poline.mhType,
                                    grnLineUnit.barcode,
                                    grnLineUnit.expiryDate,
                                    grnLineUnit.isExpirable,
                                    grnLineUnit.internalBatchNo,
                                    grnLineUnit.isChecked,
                                    grnLineUnit.lineItemId,
                                    grnLineUnit.lineItemUnitId,
                                    grnLineUnit.recevedQty,
                                    grnLineUnit.supplierBatchNo,
                                    grnLineUnit.isUpdate,
                                    grnLineUnit.totalUnits
                                )
                            )
                        }

                    }
                } else {
                    lineItemId = 0
                }
                lineItemUnitId = 0
                //poline.grnLineItemUnit?.let { createBatchesList.addAll(it) }
                currentPoLineItemPosition = position.toString()
                setCreateBatchSingleDialog(poline)
                createBatchesMainRcAdapter!!.notifyDataSetChanged()
                //  addNewBatch(position,poline)
            },
            onItemDelete = { position, grnitem ->
                if (grnitem.isUpdated == true) {
                    deleteLineItem = grnitem.lineItemId
                    deleteLineItemPosition = position.toString()
                    deleteLineItem(grnitem)
                } else {
                    poLineItem.forEachIndexed { index, poLineItemSelectionModelNewStore ->
                        if (grnitem.posapLineItemNumber == poLineItemSelectionModelNewStore.posapLineItemNumber) {
                            poLineItemSelectionModelNewStore.isSelected = false
                            selectPoLineAdapter?.notifyItemChanged(index)
                        }
                    }
                    selectedPoLineItem.removeAt(position)
                    grnMainItemAdapter!!.notifyItemRemoved(position)
                    grnMainItemAdapter!!.notifyItemRangeChanged(
                        position,
                        selectedPoLineItem.size
                    )

                }

            },
            onItemSave = { pos, mod ->
                //addSingleGrnLineUnitItemApiCall(mod(pos).grnLineItemUnit)
            },
            onDiscountAdded = { pos, mod ->
                Log.d(
                    "LineItemData",
                    "${pos}$//////LINEAMT${mod.lineAmount.toString()}/////TAXID////${mod.taxId}}"
                )
                /*  val quantity = mod.quantityReceived.toDouble()
                  val rate = mod.unitPrice
                  val discount = mod.discountAmount // Discount is a percentage
                  val taxPercent = getALlTax.find { it.taxId == mod.taxId }?.percentage ?: 0.0

                  // Find the index of the item you want to update (e.g., by lineItemId or pos)
                  val itemIndex = selectedPoLineItem.indexOfFirst { it.lineItemId == mod.lineItemId }

                  // If the item is found, update the lineAmount
                  if (itemIndex != -1) {
                      selectedPoLineItem[itemIndex].lineAmount = calculateLineItemTotal(quantity,rate,discount,taxPercent).toString()
                      grnMainItemAdapter?.notifyItemChanged(pos)

                  }
  */
            },
            customKeyboard = customKeyboard,
        )
        binding.rcGrnAdd!!.adapter = grnMainItemAdapter
        binding.rcGrnAdd.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        //get other charges based on poIds
        viewModel.getOtherChargesMutable.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { resultResponse ->
                        try {
                            if (resultResponse != null) {
                                try {
                                    //  getOtherCharges = resultResponse
                                    Log.e("taxCodes", taxCodes.toString())
                                    resultResponse.forEach {

                                        getOtherCharges.add(
                                            GetOtherChargesItem(
                                                it.expenseAmount,
                                                it.expenseCode,
                                                it.expenseName,
                                                it.expensesId,
                                                it.poId,
                                                it.poNumber,
                                                it.taxCode,
                                                calculateExpenseAmount(it.expenseAmount, it.taxCode)
                                            )
                                        )
                                    }
                                    otherChargesAdapter =
                                        OtherChargesAdapter(getOtherCharges, this@GRNAddActivity)
                                    otherChargesDialogBinding.rcOtherCharges.adapter =
                                        otherChargesAdapter
                                    otherChargesDialogBinding.rcOtherCharges.layoutManager =
                                        LinearLayoutManager(this)

                                    Log.e("taxCodes", getOtherCharges.toString())

                                    otherChargesDialogBinding.tvTotalAmount.setText("Total Expense Amount: ${getTotalExpenseSummary()}")
                                    binding.tvOtherCharges.setText(getTotalExpenseSummary())
                                } catch (e: Exception) {
                                    Log.e("taxCodes", e.toString())
                                    Toasty.warning(
                                        this@GRNAddActivity,
                                        "Something Went Wrong..!",
                                        Toasty.LENGTH_SHORT
                                    ).show()
                                }

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
                        session.showToastAndHandleErrors(errorMessage, this@GRNAddActivity)
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }

        //GRN Operations
        //generate barcode for single batch
        viewModel.getBarcodeValueWithPrefixMutableResponse.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    createBatchSingleDialog!!.dismiss()


                    response.data?.let { resultResponse ->
                        try {
                            Log.e("generatedBarcode", resultResponse.responseMessage.toString())
                            currentSelectedPoModel =
                                selectedPoLineItem[currentPoLineItemPosition.toInt()]
                            addBatchNewItem(
                                currentPoLineItemPosition.toInt(),
                                currentSelectedPoModel,
                                resultResponse.responseMessage
                            )
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
                        session.showToastAndHandleErrors(errorMessage, this@GRNAddActivity)
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }

        //process single grn line item with batch
        viewModel.processSingleGRNGRNItemBatchesMutableResponse.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { resultResponse ->
                        try {


                            if (lineItemId == 0) {
                                lineItemId =
                                    resultResponse.responseObject.grnLineItemUnit.get(0).lineItemId
                            }
                            if (resultResponse.responseObject.grnLineItemUnit.get(0).lineItemUnitId != null) {
                                lineItemUnitId =
                                    resultResponse.responseObject.grnLineItemUnit.get(0).lineItemUnitId
                            }
                            balanceQty = resultResponse.responseObject.retBalQty.toString()
                            val receivedGrnLineItem =
                                resultResponse.responseObject.grnLineItemUnit.getOrNull(0)

                            if (receivedGrnLineItem != null) {
                                val barcodeToMatch = receivedGrnLineItem.barcode
                                val updatedLineItemId = receivedGrnLineItem.lineItemId
                                val updatedLineItemUnitId = receivedGrnLineItem.lineItemUnitId
                                // Update GrnLineItemUnitStore objects based on barcode matching
                                selectedPoLineItem[currentPoLineItemPosition.toInt()].grnLineItemUnit!!.forEach { grnLineItem ->
                                    if (grnLineItem != null) {
                                        if (grnLineItem.barcode == barcodeToMatch) {
                                            grnLineItem.lineItemId = updatedLineItemId
                                            grnLineItem.lineItemUnitId = updatedLineItemUnitId
                                        }
                                    }

                                }

                                if (selectedPoLineItem[currentPoLineItemPosition.toInt()].lineItemId != lineItemId) {
                                    selectedPoLineItem[currentPoLineItemPosition.toInt()].lineItemId =
                                        lineItemId
                                }

                                createBatchesList.forEach { grnLineItem ->
                                    if (grnLineItem.barcode == barcodeToMatch) {
                                        grnLineItem.lineItemId = updatedLineItemId
                                        grnLineItem.lineItemUnitId = updatedLineItemUnitId
                                    }
                                }
                            }
                            taxAmount = resultResponse.responseObject.amount
                            setValuesForChargesTaxTotal()
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
                        session.showToastAndHandleErrors(errorMessage, this@GRNAddActivity)
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }

        //generate multiple barcode for multiple batches
        viewModel.getBarcodeForMultipleBatchesResponse.observe(this)
        { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    responsesReceived++
                    response.data?.let { resultResponse ->
                        try {
                            val newBarcode = resultResponse.responseMessage.toString()
                            Log.e("generatedBarcode $responsesReceived", newBarcode)
                            if (multibarcodeMaintain != newBarcode) {
                                multibarcodeMaintain = newBarcode
                                barcodes.add(newBarcode)
                                Log.e("barcodes", barcodes.toString())
                                if (currentBatchTobeDublicated!!.supplierBatchNo.isNotEmpty()) {
                                    currentSelectedPoModel =
                                        selectedPoLineItem[currentPoLineItemPosition.toInt()]
                                    val newBatch = createMultipleBatches(
                                        currentSelectedPoModel,
                                        currentBatchTobeDublicated!!,
                                        multibarcodeMaintain
                                    )
                                    if (currentPoLineItemPosition.toInt() != RecyclerView.NO_POSITION) {
                                        val model =
                                            selectedPoLineItem[currentPoLineItemPosition.toInt()]
                                        if (model.grnLineItemUnit == null) {
                                            model.grnLineItemUnit = mutableListOf()
                                        }
                                        model.grnLineItemUnit!!.add(newBatch)
                                        createBatchesList.add(newBatch)
                                        updateAfterMultipleEntries(
                                            currentBatchTobeDublicated!!,
                                            newBatch
                                        )
                                        Log.d(
                                            "edBatchNo.isNotEmpty()",
                                            selectedPoLineItem.toString()
                                        )
                                    } else {
                                        Log.d(
                                            "edBatchNoElse.isNotEmpty()",
                                            selectedPoLineItem.toString()
                                        )
                                    }
                                } else {
                                    Toast.makeText(
                                        this@GRNAddActivity,
                                        "Please enter batch no!!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {

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
                        session.showToastAndHandleErrors(errorMessage, this@GRNAddActivity)
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }

        //process  grn line item with  multiple batches at a time
        viewModel.processSingleGRNGRNItemBatchesForMultipleMutableResponse.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { resultResponse ->
                        try {
                            taxAmount = resultResponse.responseObject.amount
                            setValuesForChargesTaxTotal()
                            if (lineItemId == 0) {
                                lineItemId =
                                    resultResponse.responseObject.grnLineItemUnit.get(0).lineItemId
                            }
                            if (resultResponse.responseObject.grnLineItemUnit.get(0).lineItemUnitId != null) {

                                lineItemUnitId =
                                    resultResponse.responseObject.grnLineItemUnit.get(0).lineItemUnitId
                            }
                            balanceQty = resultResponse.responseObject.retBalQty.toString()
                            val receivedGrnLineItem =
                                resultResponse.responseObject.grnLineItemUnit.getOrNull(0)

                            if (receivedGrnLineItem != null) {
                                val barcodeToMatch = receivedGrnLineItem.barcode
                                val updatedLineItemId = receivedGrnLineItem.lineItemId
                                val updatedLineItemUnitId = receivedGrnLineItem.lineItemUnitId
                                // Update GrnLineItemUnitStore objects based on barcode matching
                                selectedPoLineItem[currentPoLineItemPosition.toInt()].grnLineItemUnit!!.forEach { grnLineItem ->
                                    if (grnLineItem != null) {
                                        if (grnLineItem.barcode == barcodeToMatch) {
                                            grnLineItem.lineItemId = updatedLineItemId
                                            grnLineItem.lineItemUnitId = updatedLineItemUnitId
                                        }
                                    }

                                }
                                createBatchesList.forEach { grnLineItem ->
                                    if (grnLineItem.barcode == barcodeToMatch) {
                                        grnLineItem.lineItemId = updatedLineItemId
                                        grnLineItem.lineItemUnitId = updatedLineItemUnitId
                                    }
                                }
                            }

                            if (responsesReceived != numberOfTimesToDublicate) {
                                getBarcodeForMultipleBatches()
                            } else {
                                currentBatchTobeDublicated = null
                                responsesReceived = 0
                                numberOfTimesToDublicate = 0
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
                    binding.clSelectPoId.visibility = View.GONE
                    response.message?.let { errorMessage ->
                        Toasty.error(
                            this@GRNAddActivity,
                            "Login failed - \nError Message: $errorMessage"
                        ).show()
                        session.showToastAndHandleErrors(errorMessage, this@GRNAddActivity)
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }

        //delete grn line item
        viewModel.deleteGRNLineUnitMutableResponse.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { resultResponse ->
                        try {
                            selectedPoLineItem.removeAt(deleteLineItemPosition.toInt())
                            grnMainItemAdapter!!.notifyItemRemoved(deleteLineItemPosition.toInt())
                            grnMainItemAdapter!!.notifyItemRangeChanged(
                                deleteLineItemPosition.toInt(),
                                selectedPoLineItem.size
                            )
                            lineItemId = 0
                            if (selectedPoLineItem.size == 0) {
                                if (grnId != 0) {
                                    isGRNUpdate = true
                                    callSelectedPoLineItems()
                                }
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
                            "failed - \nError Message: $errorMessage"
                        ).show()
                        session.showToastAndHandleErrors(errorMessage, this@GRNAddActivity)
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }

        //delete grn line item batch
        viewModel.deleteGRNLineItemsUnitMutableResponse.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { resultResponse ->
                        try {
                            selectedPoLineItem[currentPoLineItemPosition.toInt()].grnLineItemUnit?.removeAt(
                                deleteBatchUnitItemPosition.toInt()
                            )
                            createBatchesList.removeAt(deleteBatchUnitItemPosition.toInt())
                            lineItemUnitId = 0
                            balanceQty = resultResponse.responseObject.toString()
                            selectedPoLineItem[currentPoLineItemPosition.toInt()].balQTY =
                                balanceQty.toDouble()
                            createBatchesDialogBinding.tvBalanceQuantity.setText(
                                selectedPoLineItem[currentPoLineItemPosition.toInt()].balQTY.toString()
                            )
                            //var totalReceived = totalQty.toDouble() - balanceQty.toDouble()

                            var totalReceivedTotalFromList = 0.000
                            if (createBatchesList != null) {
                                totalReceivedTotalFromList =
                                    createBatchesList.sumByDouble { it.recevedQty.toDouble() }
                            }
                            val balQtyFormat = String.format("%.3f", totalReceivedTotalFromList)
                            selectedPoLineItem[currentPoLineItemPosition.toInt()].quantityReceived =
                                balQtyFormat
                            val sumOfReceivedQtyIncludingUpdatedItem =
                                createBatchesList.sumByDouble { it.recevedQty.toDouble() }
                            createBatchesDialogBinding.tvQtyValue.setText(
                                sumOfReceivedQtyIncludingUpdatedItem.toString()
                            )

                            createBatchesMainRcAdapter!!.notifyItemRemoved(
                                deleteBatchUnitItemPosition.toInt()
                            )
                            createBatchesMainRcAdapter!!.notifyItemRangeChanged(
                                deleteBatchUnitItemPosition.toInt(),
                                createBatchesList.size
                            )
                            grnMainItemAdapter?.notifyItemChanged(currentPoLineItemPosition.toInt())
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
                            "failed - \nError Message: $errorMessage"
                        ).show()
                        session.showToastAndHandleErrors(errorMessage, this@GRNAddActivity)
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }

        //print label for grn
        viewModel.printLabelForGRNMutableResponse.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { resultResponse ->
                    }
                }

                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { errorMessage ->
                        Toasty.error(
                            this@GRNAddActivity,
                            "failed - \nError Message: $errorMessage"
                        ).show()
                        session.showToastAndHandleErrors(errorMessage, this@GRNAddActivity)
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }

        //print label in bulk for grn
        viewModel.printLabelForGRNBulkMutableResponse.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { resultResponse ->

                    }
                }

                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { errorMessage ->
                        Toasty.error(
                            this@GRNAddActivity,
                            "failed - \nError Message: $errorMessage"
                        ).show()
                        session.showToastAndHandleErrors(errorMessage, this@GRNAddActivity)
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }

        //get data for print
        viewModel.getGRNProductDetailsOnUnitIdItemMutableResponse.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { dataMap ->
                        // Iterate through the map and handle fields dynamically

                        //Log.e("printer,string",bulkUsbPrintFlag.toString())
                        dataMap.forEachIndexed { index, item ->
                            item.forEach { (key, value) ->
                                formattedPrnForPrinter =
                                    printerPrnforGRN!!.replace(key, value.toString())
                            }
                            //    Log.e("printer,string", "${bulkUsbPrintFlag++}----------------===================$formattedPrnForPrinter")
                        }

                        //Log.e("printer,string",dataMap.toString())
                    }
                }

                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { errorMessage ->
                        Toasty.error(this@GRNAddActivity, "Error: $errorMessage").show()
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }

        //get draft GRN
        viewModel.getDraftGRNMutableResponse.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { resultResponse ->
                        try {
                            if (resultResponse != null) {
                                getDraftGrnResponse = resultResponse

                                binding.edInvoiceNumber.setText(getDraftGrnResponse!!.grnTransaction.invoiceNumber)

                                try {
                                    val inputFormat = SimpleDateFormat(
                                        "yyyy-MM-dd'T'HH:mm:ss",
                                        Locale.getDefault()
                                    )
                                    // Define the output format
                                    val outputFormat =
                                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                    val parsedDate =
                                        inputFormat.parse(getDraftGrnResponse!!.grnTransaction.invoiceDate)
                                    val formattedDate = outputFormat.format(parsedDate)
                                    binding.tvDate.setText(formattedDate)
                                    //binding.edOtherCharges.setText(getDraftGrnResponse!!.grnTransaction.otherCharges.toString())
                                    //taxAmount = getDraftGrnResponse?.grnTransaction?.grnLineItems?.sumOf { it.amount.toDouble() }?.toString() ?: "0"
                                    setValuesForChargesTaxTotal()

                                } catch (e: Exception) {
                                    Toasty.warning(
                                        this@GRNAddActivity,
                                        "Date format not good",
                                        Toasty.LENGTH_SHORT
                                    ).show()
                                }
                                selectedKGRN = getDraftGrnResponse!!.grnTransaction.kgrnNumber
                                binding.tvKGRNNo.setText(selectedKGRN.toString())
                                currentGrnID = getDraftGrnResponse!!.grnTransaction.grnId
                                getSupplierList()

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
                        session.showToastAndHandleErrors(errorMessage, this@GRNAddActivity)
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }


        //submit GRN
        viewModel.submitGRNMutableResponse.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { resultResponse ->
                        try {
                            Toast.makeText(
                                this,
                                resultResponse.responseMessage.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                            gotoMainPage()
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
                            "failed - \nError Message: $errorMessage"
                        ).show()
                        session.showToastAndHandleErrors(errorMessage, this@GRNAddActivity)
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }


        usbCommunicationManager = UsbCommunicationManager(this)
        usbPrinterHelper = USBPrinterHelper(this, this)



        usbCommunicationManager.receivedData.observe(this) { data ->
            val currentTime = System.currentTimeMillis()
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




        binding.clEnterInvDate.setOnClickListener {
            showDatePickerDialog()
        }

        binding.mcvSubmitGRN.setOnClickListener {
            submitGrn()
        }
        binding.mcvCancel.setOnClickListener {
            onBackPressed()
        }

        binding.logoutBtn.setOnClickListener {
            showLogoutDialog()
        }
        binding.mcvAddGrn.setOnClickListener {
            processGrn()
        }


    }

    private fun calculateLineItemTotal(
        quantity: Double,
        rate: Int?,
        discount: Double?,
        taxPercent: Double,
    ): Double {


        // Calculate the total amount before discount
        val totalAmount = quantity * rate!!

        // Calculate the discount amount (percentage of the total amount)
        val discountAmount = totalAmount * (discount!! / 100)

        // Calculate the amount after discount
        val amountAfterDiscount = totalAmount - discountAmount

        // Calculate the tax amount on the discounted amount
        val taxAmount = amountAfterDiscount * (taxPercent / 100)

        // Calculate the final line amount (after applying tax)
        val lineAmount = amountAfterDiscount + taxAmount

        return lineAmount


    }

    private fun calculateExpenseAmount(expenseAmount: Double, taxCode: String): String {
        val taxValue = taxCodes[taxCode] ?: 0.0
        val taxAmount = expenseAmount + (taxValue / expenseAmount * 100)
        return taxAmount.toString()
    }

    //default
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
                val formattedDate =
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectDt)
                selectedInvoiceDate = formattedDate.toString()
                //selectedDate = LocalDateTime.of(2024, 4, 20,0,0)
            }, year, month, dayOfMonth
        )
        // Set maximum date
        datePickerDialog.datePicker.maxDate =
            System.currentTimeMillis() // Optional: Set maximum date to current date
        datePickerDialog.show()
    }

    private fun gotoMainPage() {
        var intent = Intent(this, GRNMainActivity::class.java)
        startActivity(intent)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        usbPrinterHelper.unregisterReceiver()
    }

    private fun showProgressBar() {
        progress.show()
    }

    private fun hideProgressBar() {
        progress.cancel()
    }

    //All Dialogs Init
    private fun setSelectSupplierDialog() {
        selectPoIdsDialog!!.setContentView(selectPoIdsDialogBinding.root)
        selectPoIdsDialog!!.getWindow()?.setBackgroundDrawable(
            AppCompatResources.getDrawable(
                this@GRNAddActivity,
                android.R.color.transparent
            )
        )
        selectPoIdsDialog!!.getWindow()?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        selectPoIdsDialog!!.setCancelable(true)
        selectPoIdsDialogBinding.closeDialogueTopButton.setOnClickListener {
            selectPoIdsDialog!!.dismiss()
        }
        selectPoIdsDialog!!.show()
    }

    private fun selectePoRc() {
        selectPoIdsDialogBinding.rcPo.adapter = GRNSelectPoAdapter(getSuppliersPOsDDLResponse)
        selectPoIdsDialogBinding.rcPo.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        selectPoIdsDialogBinding.btnSubmit.setOnClickListener {
            callSelectedPoLineItems()
        }
    }

    private fun setSelectLineItemDialog() {
        selectLineItemDialog!!.show()

        selectLineItemDialogBinding.closeDialogueTopButton.setOnClickListener {
            selectLineItemDialog!!.dismiss()
        }
        selectLineItemDialogBinding.mcvPoLineItemSubmit.setOnClickListener {
            setPoLineItemList()
            grnMainItemAdapter!!.notifyDataSetChanged()
            selectLineItemDialog!!.dismiss()
        }
    }

    private fun setCreateBatchSingleDialog() {
        createBatchSingleDialog!!.setContentView(createBatchesSingleDialogBinding.root)
        createBatchSingleDialog!!.getWindow()?.setBackgroundDrawable(
            AppCompatResources.getDrawable(
                this@GRNAddActivity,
                android.R.color.transparent
            )
        )
        createBatchSingleDialog!!.getWindow()?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        createBatchSingleDialog!!.setCancelable(true)
        createBatchesSingleDialogBinding.closeDialogueTopButton.setOnClickListener {
            createBatchSingleDialog!!.dismiss()
        }
    }

    private fun setCreateBatchesMultipleDialog() {
        createMultipleBatchesDialog!!.setContentView(createBatchesMultipleDialogBinding.root)
        createMultipleBatchesDialog!!.getWindow()?.setBackgroundDrawable(
            AppCompatResources.getDrawable(
                this@GRNAddActivity,
                android.R.color.transparent
            )
        )
        createMultipleBatchesDialog!!.getWindow()?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        createMultipleBatchesDialog!!.setCancelable(true)
        createBatchesMultipleDialogBinding.closeDialogueTopButton.setOnClickListener {
            createMultipleBatchesDialog!!.dismiss()
        }
    }

    private fun setItemDescriptionDialog() {
        itemDescriptionDialog!!.setContentView(itemDescriptionBinding.root)
        itemDescriptionDialog!!.getWindow()?.setBackgroundDrawable(
            AppCompatResources.getDrawable(
                this@GRNAddActivity,
                android.R.color.transparent
            )
        )
        itemDescriptionDialog!!.getWindow()?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        itemDescriptionDialog!!.setCancelable(true)
        itemDescriptionBinding.closeDialogueTopButton.setOnClickListener {
            itemDescriptionDialog!!.dismiss()
        }
    }

    private fun setItemDescription(itemDesc: String) {
        itemDescriptionDialog?.show()
        itemDescriptionBinding.tvItemDescription.setText(itemDesc)
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

    private fun setCreateBatchSingleDialog(poModel: CustomPoLineItemSelectionModel) {
        setInfoValues(poModel)
        createBatchesDialogBinding.mcvAddBatches.setOnClickListener {
            createBatchesBasedOnTotalUnits()

        }
        createBatchesSingleDialogBinding.closeDialogueTopButton.setOnClickListener {
            createBatchesSingleDialogBinding.et1.setText("")
            createBatchSingleDialog!!.dismiss()
        }
        createBatchesSingleDialogBinding.btnSubmit.setOnClickListener {

            currentSelectedPoModel = poModel
            if (createBatchesList.size < totalUnits) {
                generateBarcodeForBatches()
            } else {
                Toasty.warning(this@GRNAddActivity, "Batches Should not exceed the total units..!")
                    .show()
            }

        }
        createBatchedDialog!!.show()
    }

    private fun createBatchesBasedOnTotalUnits() {
        if (totalUnits != 0) {
            if (createBatchesList.size < totalUnits) {
                createBatchSingleDialog!!.show()
            } else {
                Toast.makeText(this, "Batch size cannot exceed total units", Toast.LENGTH_SHORT)
                    .show()
            }

        } else {
            val edTotalUnits = createBatchesDialogBinding.edTotalUnitsOfBatch.text.toString().trim()
            if (edTotalUnits.isNotEmpty() && edTotalUnits != "0") {
                totalUnits = edTotalUnits.toInt()
                createBatchSingleDialog!!.show()
            } else {
                Toast.makeText(
                    this,
                    "Please enter a valid number greater than 0",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    private fun setInfoValues(poModel: CustomPoLineItemSelectionModel) {
        Log.e("poModelfrombatches", poModel.toString())
        createBatchesDialogBinding.tvPoNoValue.setText(poModel.poNumber)
        createBatchesDialogBinding.tvLineItemDescValue.setText(poModel.itemCode)
        createBatchesDialogBinding.tvItemDescValue.setText(poModel.itemDescription)
        createBatchesDialogBinding.tvPuomValue.setText(poModel.pouom)
        createBatchesDialogBinding.tvPoQtyValue.setText(poModel.poqty.toString())
        createBatchesDialogBinding.tvMhTypeValue.setText(poModel.mhType)
        createBatchesDialogBinding.tvBalanceQuantity.setText(poModel.balQTY.toString())
        createBatchesDialogBinding.edTotalUnitsOfBatch.setText(poModel.totalUnit.toString())

        if (totalUnits != 0) {
            createBatchesDialogBinding.mcvRcLineItem.visibility = View.VISIBLE
            createBatchesDialogBinding.tlTotalUnitsOfBatch.boxBackgroundColor =
                ContextCompat.getColor(this, R.color.enough_grey)
            createBatchesDialogBinding.edTotalUnitsOfBatch.apply {
                isFocusable = false
                isClickable = false
            }
        } else {
            createBatchesDialogBinding.mcvRcLineItem.visibility = View.INVISIBLE
        }

        if (
            (poModel.pouom!!.contains("Number", ignoreCase = true) ||
                    poModel.pouom.contains("PCS", ignoreCase = true))
            && poModel.mhType.contains("Batch", ignoreCase = true)
        ) {
            createBatchesDialogBinding.ivBatchesSelection.visibility = View.VISIBLE
        } else {
            createBatchesDialogBinding.ivBatchesSelection.visibility = View.GONE
        }

        if (poModel.isExpirable) {
            createBatchesDialogBinding.tvExpiryDate.visibility = View.VISIBLE
        } else {
            createBatchesDialogBinding.tvExpiryDate.visibility = View.GONE
        }


        if (poModel.mhType.equals("Serial")) {
            createBatchesDialogBinding.tvDialogueTitle.setText("Create Serial")
        } else {
            createBatchesDialogBinding.tvDialogueTitle.setText("Create Batches")
        }

        if (poModel.grnLineItemUnit != null) {
            var total = poModel.grnLineItemUnit!!.sumByDouble {
                it!!.recevedQty.toDouble()
            }
            createBatchesDialogBinding.tvQtyValue.setText(total.toString())
        } else {
            createBatchesDialogBinding.tvQtyValue.setText(poModel.quantityReceived)
        }
        balanceQty = poModel.balQTY.toString()
        totalQty = poModel.balQTY.toString()
    }


    //Default API Calls
    private fun getAllTax() {
        try {
            viewModel.getAllTax(this,token, baseUrl)
        } catch (e: Exception) {
            Toast.makeText(
                this,
                e.message.toString(),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun getAllLocations() {
        try {
            viewModel.getAllLocations(this,token, baseUrl)
        } catch (e: Exception) {
            Toast.makeText(
                this,
                e.message.toString(),
                Toast.LENGTH_SHORT
            ).show()
        }
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


    // Call Get Suppplier List
    private fun setSupplierSpinner() {
        val autoCompleteTextView = binding.tvSpinnerSupplier
        // Initialize ArrayAdapter and set it to the AutoCompleteTextView
        supplierSpinnerAdapter =
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, supplierSpinnerArray)
        autoCompleteTextView.setAdapter(supplierSpinnerAdapter)

        // Set threshold to 1 so that suggestions appear as soon as the user starts typing
        autoCompleteTextView.threshold = 1

        // If there is a default selection based on some condition
        if (grnId != 0) {
            val defaultBpName = getDraftGrnResponse?.grnTransaction?.bpName
            if (!defaultBpName.isNullOrEmpty()) {
                val defaultPosition = supplierSpinnerArray.indexOf(defaultBpName)
                if (defaultPosition != -1) {
                    autoCompleteTextView.setText(defaultBpName, false)
                    autoCompleteTextView.isEnabled = false
                    autoCompleteTextView.setTextColor(Color.BLACK)
                    callParentLocationApi(getDraftGrnResponse?.grnTransaction?.bpCode.toString())
                }
            }
        }

        // Set listener to handle item selection
        autoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            handleSupplierSelection(position)
        }

        // Show dropdown list when AutoCompleteTextView gains focus
        autoCompleteTextView.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                autoCompleteTextView.showDropDown()
            }
        }
    }

    private fun handleSupplierSelection(position: Int) {
        val selectedItem = supplierSpinnerArray[position]
        val selectedKey = supplierMap.entries.find { it.value == selectedItem }?.key
        if (selectedKey != null) {
            selectedSupplierBpCode = selectedKey
            val supplier =
                getActiveSuppliersDDLResponse.find { it.code == selectedSupplierBpCode }
            if (supplier != null) {
                selectedBpId = supplier.value.toString()
                selectedBpName = supplier.text.toString()
                callParentLocationApi(selectedKey)
            }
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

    //generate GRN
    private fun processGrn() {
        try {
            edInvoiceNo = binding.edInvoiceNumber.text.toString().trim()
            if (selectedInvoiceDate != "") {
                val listOfPoIds = selectedPoFilteredList.joinToString(separator = "|")
                if (listOfPoIds.isNotEmpty()) {
                    if (selectedCurrency != "" && selectedCurrency.equals("INR")) {
                        viewModel.processGRN(
                            this,token, baseUrl, GRNSaveToDraftDefaultRequest(
                                selectedSupplierBpCode,
                                selectedBpId.toInt(),
                                selectedBpName,
                                selectedCurrency,
                                currentGrnID,
                                "Draft",
                                selectedInvoiceDate!!,
                                edInvoiceNo,
                                listOfPoIds,
                                "Domestic",
                                ""
                            )
                        )
                    } else {
                        viewModel.processGRN(
                            this,token, baseUrl, GRNSaveToDraftDefaultRequest(
                                selectedSupplierBpCode,
                                selectedBpId.toInt(),
                                selectedBpName,
                                selectedCurrency,
                                currentGrnID,
                                "Draft",
                                selectedInvoiceDate!!,
                                edInvoiceNo,
                                listOfPoIds,
                                "Import",
                                "",
                            )
                        )
                    }
                } else {
                    Toasty.error(
                        this,
                        "Please select PO!!",
                        Toasty.LENGTH_LONG
                    ).show()
                }

            } else {
                Toasty.error(
                    this,
                    "Please select Invoice Date!!",
                    Toasty.LENGTH_LONG
                ).show()
            }
        } catch (e: Exception) {
            Toasty.error(
                this,
                e.message.toString() + "askjdhasjhdsa",
                Toasty.LENGTH_LONG
            ).show()
        }
    }

    //both default and GRNDraft Po Dialog
    private fun selectePoDefaultRc() {
        selectPoIdsDialogBinding.rcPo.adapter = GRNSelectPoAdapter(getSuppliersPOsDDLResponse)
        var getDraftGrnResponsepoid = getDraftGrnResponse!!.grnTransaction.poIds

        val poIdsList: MutableList<Int> = mutableListOf()
        val poIds = getDraftGrnResponsepoid.split("|")
        for (poIdStr in poIds) {
            val poId = poIdStr.toIntOrNull()
            if (poId != null) {
                poIdsList.add(poId)
            }
        }
        getSuppliersPOsDDLResponse.forEach { item ->
            // Check if the value of the item is in the poIds list
            if (poIdsList.contains(item.value)) {
                // If the value is present, set isChecked to true
                item.isUpdatable = true
                item.isChecked = true
            } else {
                // If the value is not present, set isChecked to false
                item.isChecked = false
            }
        }
        callSelectedPoLineItems()
        selectedPoFilteredList = getDraftGrnResponse!!.poIds.toMutableList()
        selectPoIdsDialogBinding.rcPo.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        selectPoIdsDialogBinding.btnSubmit.setOnClickListener {
            callSelectedPoLineItems()
        }
        getOtherCharges()

    }

    private fun callSelectedPoLineItems() {
        try {
            for (s in getSuppliersPOsDDLResponse) {
                if (s.isChecked) {
                    if (!selectedPoFilteredList.contains(s.value)) {
                        s.value?.let { selectedPoFilteredList.add(it) };
                    }
                }
            }
            if (selectedPoFilteredList.size > 0) {
                binding.tvCurrencyType.setText(getCommonCode().toString())
                selectedCurrency = getCommonCode().toString()
                /* if (selectedCurrency.equals("INR")) {
                     binding.grnAddHeader.edGDPO.visibility = View.GONE
                 } else {
                     binding.grnAddHeader.edGDPO.visibility = View.VISIBLE
                 }*/
                viewModel.getPosLineItemsOnPoIds(this,token, baseUrl, selectedPoFilteredList)
                selectPoIdsDialog!!.dismiss()
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

    private fun getCommonCode(): String? {
        val checkedItem = getSuppliersPOsDDLResponse.find { it.isChecked }
        return checkedItem?.code
    }


    //Po Line Items
    //create multiple batches
    private fun getBarcodeForMultipleBatches() {
        try {
            viewModel.getBarcodeForMultipleBatches(this,token, baseUrl, "G")
        } catch (e: Exception) {
            Toast.makeText(
                this,
                e.message.toString(),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun updateAfterMultipleEntries(
        newItem: CustomGrnLineItemUnit,
        newBatch: CustomGrnLineItemUnit,
    ) {
        val newBalanceQty = balanceQty.toDouble() - newItem.recevedQty.toDouble()
        val balQtyFormat = String.format("%.3f", newBalanceQty)
        selectedPoLineItem[currentPoLineItemPosition.toInt()].balQTY = balQtyFormat.toDouble()
        createBatchesDialogBinding.tvBalanceQuantity.setText(
            selectedPoLineItem[currentPoLineItemPosition.toInt()].balQTY.toString()
        )
        val sumOfReceivedQtyIncludingUpdatedItem =
            createBatchesList.sumByDouble { it.recevedQty.toDouble() }
        selectedPoLineItem[currentPoLineItemPosition.toInt()].quantityReceived =
            sumOfReceivedQtyIncludingUpdatedItem.toString()
        createBatchesDialogBinding.tvQtyValue.setText(
            sumOfReceivedQtyIncludingUpdatedItem.toString()
        )
        processSingleGRNGRNItemBatchesForMultiple(newBatch)
        createBatchesMainRcAdapter?.notifyItemInserted(createBatchesList.size - 1)
        grnMainItemAdapter?.notifyItemChanged(currentPoLineItemPosition.toInt())

    }

    private fun addMultipleBatches(
        newItem: CustomGrnLineItemUnit,

        ) {
        createMultipleBatchesDialog!!.show()
        createBatchesMultipleDialogBinding.btnSubmit.setOnClickListener {
            var edMultiTextNum =
                createBatchesMultipleDialogBinding.edMultipleBatch.text.toString().trim()
            submitMultipleBatches(newItem, edMultiTextNum.toInt())
        }

    }

    private fun submitMultipleBatches(newItem: CustomGrnLineItemUnit, edMultiTextNum: Int) {
        Log.e("thisNum", edMultiTextNum.toString())

        if (edMultiTextNum < 20) {

            var currentItemMultipleList = (newItem.recevedQty.toDouble() * edMultiTextNum)
            var totalQtyAfterMultipleEntry =
                createBatchesList.sumByDouble { it.recevedQty.toDouble() } + currentItemMultipleList
            if (currentItemMultipleList > balanceQty.toDouble()) {
                Toast.makeText(
                    this,
                    "Value must not exceed the Balance Qty.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val hasItemWithZeroReceivedQuantity = selectedPoLineItem.any {
                    it.grnLineItemUnit?.any { it?.recevedQty == "0.000" }
                        ?: false
                }

                if (hasItemWithZeroReceivedQuantity) {
                    Toasty.warning(
                        this@GRNAddActivity,
                        "Please complete current transaction!!"
                    ).show()
                    Log.d("edBatchNo.isNotEmpty()", "sad")
                } else {
                    createMultipleBatchesDialog!!.dismiss()
                    numberOfTimesToDublicate = edMultiTextNum
                    getBarcodeForMultipleBatches()

                }
            }
        } else {
            Toasty.error(
                this@GRNAddActivity,
                "Must not exceed 20 times"
            ).show()
        }
    }

    //generate barcodes

    private fun generateBarcodeForBatches() {
        try {
            viewModel.getBarcodeValueWithPrefix(this,token, baseUrl, "G")
        } catch (e: Exception) {
            Toasty.error(
                this,
                e.message.toString(),
                Toasty.LENGTH_LONG
            ).show()
        }

    }

    private fun generateBarcodeForBatchesForExisitng() {
        try {
            viewModel.getBarcodeValueWithPrefixForExisitng(this,token, baseUrl, "G")
        } catch (e: Exception) {
            Toasty.error(
                this,
                e.message.toString(),
                Toasty.LENGTH_LONG
            ).show()
        }

    }


    //print labels
    private fun printLabelForGRN(grnitem: CustomGrnLineItemUnit) {
        try {
            var grnLineUnitList = ArrayList<Int>()
            grnLineUnitList.add(grnitem.lineItemUnitId.toInt())
            viewModel.printLabelForGRN(this,token, baseUrl, grnLineUnitList)
        } catch (e: Exception) {
            Toast.makeText(
                this,
                e.message.toString(),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun printLabelForBulk() {
        try {
            viewModel.printLabelForGRNBulk(this,token, baseUrl, selectedBatchForPrint)
        } catch (e: Exception) {
            Toast.makeText(
                this,
                e.message.toString(),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun printLabelForUSB(grnitem: CustomGrnLineItemUnit) {
        try {
            var grnLineUnitList = ArrayList<Int>()
            grnLineUnitList.add(grnitem.lineItemUnitId.toInt())
            viewModel.getGRNProductDetailsOnUnitIdItem(this,token, baseUrl, grnLineUnitList)
        } catch (e: Exception) {
            Toast.makeText(
                this,
                e.message.toString(),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun printLabelUSBForBulk() {
        try {
            viewModel.getGRNProductDetailsOnUnitIdItem(this,token, baseUrl, selectedBatchForPrint)
        } catch (e: Exception) {
            Toast.makeText(
                this,
                e.message.toString(),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    //delete batch
    private fun deleteBatches(grnitem: CustomGrnLineItemUnit) {
        try {
            viewModel.deleteGRNLineItemsUnit(this,token, baseUrl, grnitem.lineItemUnitId)
        } catch (e: Exception) {
            Toast.makeText(
                this,
                e.message.toString(),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    //delete line item
    private fun deleteLineItem(poLineItem: CustomPoLineItemSelectionModel) {
        try {
            viewModel.deleteGRNLineUnit(this,token, baseUrl, poLineItem.lineItemId)
        } catch (e: Exception) {
            Toast.makeText(
                this,
                e.message.toString(),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setValuesForChargesTaxTotal() {
        // var edOtherCharges=binding.edOtherCharges.text.toString().trim()
        //binding.tvTotalAmount.setText("Total Amount : ${taxAmount.toDouble()+edOtherCharges.toDouble()}")
        binding.tvTaxAmount.setText("Tax Amount : $taxAmount")
    }

    private fun updateOtherCharges() {
        try {
            //var edOtherCharges=binding.edOtherCharges.text.toString()
            /*   if (edOtherCharges.isNotEmpty())
               {
                   viewModel.updateOtherCharges(token, baseUrl,grnId,edOtherCharges.toDouble())
               }
               else{
                   Toast.makeText(
                       this,
                       "Please fill the values..!",
                       Toast.LENGTH_SHORT
                   ).show()
               }*/

        } catch (e: Exception) {
            Toast.makeText(
                this,
                e.message.toString(),
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    ///can default addSame for numbers as 0.000
    private fun calculateWeightUpdate(
        tempList: MutableList<CustomGrnLineItemUnit>,
        position: Int,
        updatedItem: CustomGrnLineItemUnit,
    ) {

        if (updatedItem.recevedQty != "0.000" && updatedItem.recevedQty != "") {
            val sumOfReceivedQtyIncludingUpdatedItem =
                tempList.sumByDouble { it.recevedQty.toDouble() }
            /*Log.e(
                "receivedQty",
                "sum" + sumOfReceivedQtyIncludingUpdatedItem.toString() + "  ////   ${tempList.toString()}" + "  ${balanceQty.toDouble()}  "
            )*/
            if (sumOfReceivedQtyIncludingUpdatedItem > totalQty.toDouble()) {
                Toast.makeText(
                    this,
                    "Value must not exceed the Balance Qty.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val previousReceivedQty =
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].grnLineItemUnit!![position]?.recevedQty?.toDouble()
                val updatedReceivedQty = updatedItem.recevedQty.toDouble()
                if (updatedReceivedQty != previousReceivedQty) { // Only proceed if received quantity has changed
                    val receivedQtyDifference = updatedReceivedQty - previousReceivedQty!!
                    if (receivedQtyDifference > 0) { // Received quantity increased
                        if (receivedQtyDifference > balanceQty.toDouble()) {
                            // Show error message, quantity must not exceed the balance quantity
                        } else {
                            // Subtract the difference from the balance quantity
                            val newBalanceQty = balanceQty.toDouble() - receivedQtyDifference
                            val balQtyFormat = String.format("%.3f", newBalanceQty)

                            selectedPoLineItem[currentPoLineItemPosition.toInt()].balQTY =
                                balQtyFormat.toDouble()
                            selectedPoLineItem[currentPoLineItemPosition.toInt()].isUpdated = true

                            // Update received quantity for the item being modified
                            selectedPoLineItem[currentPoLineItemPosition.toInt()].grnLineItemUnit!![position] =
                                updatedItem.copy()
                            createBatchesDialogBinding.tvBalanceQuantity.setText(
                                selectedPoLineItem[currentPoLineItemPosition.toInt()].balQTY.toString()
                            )
                            createBatchesList[position] = updatedItem.copy()
                            // Update total received quantity for the PO line item
                            val sumOfReceivedQtyIncludingUpdatedItem =
                                createBatchesList.sumByDouble { it.recevedQty.toDouble() }
                            selectedPoLineItem[currentPoLineItemPosition.toInt()].quantityReceived =
                                sumOfReceivedQtyIncludingUpdatedItem.toString()
                            createBatchesDialogBinding.tvQtyValue.setText(
                                sumOfReceivedQtyIncludingUpdatedItem.toString()
                            )
                            addSingleGrnLineUnitItemApiCall(updatedItem)
                            createBatchesMainRcAdapter!!.notifyItemChanged(position)
                            grnMainItemAdapter!!.notifyItemChanged(currentPoLineItemPosition.toInt())
                        }
                    } else { // Received quantity decreased
                        // Add the difference to the balance quantity
                        val newBalanceQty = balanceQty.toDouble() + (-receivedQtyDifference)
                        val balQtyFormat = String.format("%.3f", newBalanceQty)

                        selectedPoLineItem[currentPoLineItemPosition.toInt()].balQTY =
                            balQtyFormat.toDouble()
                        // Update received quantity for the item being modified
                        selectedPoLineItem[currentPoLineItemPosition.toInt()].grnLineItemUnit!![position] =
                            updatedItem.copy()
                        createBatchesDialogBinding.tvBalanceQuantity.setText(
                            selectedPoLineItem[currentPoLineItemPosition.toInt()].balQTY.toString()
                        )
                        balanceQty = balQtyFormat
                        createBatchesList[position] = updatedItem.copy()
                        // Update total received quantity for the PO line item
                        val sumOfReceivedQtyIncludingUpdatedItem =
                            createBatchesList.sumByDouble { it.recevedQty.toDouble() }
                        selectedPoLineItem[currentPoLineItemPosition.toInt()].quantityReceived =
                            sumOfReceivedQtyIncludingUpdatedItem.toString()
                        createBatchesDialogBinding.tvQtyValue.setText(
                            sumOfReceivedQtyIncludingUpdatedItem.toString()
                        )
                        addSingleGrnLineUnitItemApiCall(updatedItem)
                        createBatchesMainRcAdapter!!.notifyItemChanged(position)
                        grnMainItemAdapter!!.notifyItemChanged(currentPoLineItemPosition.toInt())
                    }
                } else {
                    val previousExpiryDate =
                        selectedPoLineItem[currentPoLineItemPosition.toInt()].grnLineItemUnit!![position]?.expiryDate
                    if (!updatedItem?.expiryDate!!.equals(previousExpiryDate)) {
                        selectedPoLineItem[currentPoLineItemPosition.toInt()].grnLineItemUnit!![position]?.expiryDate =
                            updatedItem?.expiryDate!!
                        createBatchesList[position] = updatedItem.copy()
                        addSingleGrnLineUnitItemApiCall(updatedItem)
                        createBatchesMainRcAdapter!!.notifyItemChanged(position)
                        grnMainItemAdapter!!.notifyItemChanged(currentPoLineItemPosition.toInt())
                    }
                }
            }
        } else {
            Toast.makeText(
                this,
                "Value must not be 0.",
                Toast.LENGTH_SHORT
            ).show()
        }


    }


    private fun calculateForNumbers(
        tempList: MutableList<CustomGrnLineItemUnit>,
        position: Int,
        updatedItem: CustomGrnLineItemUnit,
    ) {
        Log.e("updatedItem", updatedItem.toString())
        if (updatedItem.recevedQty != "0" && updatedItem.recevedQty != "") {
            val sumOfReceivedQtyIncludingUpdatedItem =
                tempList.sumByDouble { it.recevedQty.toDouble() }
            /*Log.e(
                "receivedQty",
                "sum" + sumOfReceivedQtyIncludingUpdatedItem.toString() + "  ////   ${tempList.toString()}" + "  ${balanceQty.toDouble()}  "
            )*/
            if (sumOfReceivedQtyIncludingUpdatedItem > totalQty.toDouble()) {
                Toast.makeText(
                    this,
                    "Value must not exceed the Balance Qty.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val previousReceivedQty =
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].grnLineItemUnit!![position]?.recevedQty?.toDouble()
                val updatedReceivedQty = updatedItem.recevedQty.toDouble()
                if (updatedReceivedQty != previousReceivedQty) { // Only proceed if received quantity has changed
                    val receivedQtyDifference = updatedReceivedQty - previousReceivedQty!!
                    if (receivedQtyDifference > 0) { // Received quantity increased
                        if (receivedQtyDifference > balanceQty.toDouble()) {
                            // Show error message, quantity must not exceed the balance quantity
                        } else {
                            // Subtract the difference from the balance quantity
                            val newBalanceQty = balanceQty.toDouble() - receivedQtyDifference
                            val balQtyFormat = String.format("%.3f", newBalanceQty)

                            selectedPoLineItem[currentPoLineItemPosition.toInt()].balQTY =
                                balQtyFormat.toDouble()
                            selectedPoLineItem[currentPoLineItemPosition.toInt()].isUpdated = true

                            // Update received quantity for the item being modified
                            selectedPoLineItem[currentPoLineItemPosition.toInt()].grnLineItemUnit!![position] =
                                updatedItem.copy()
                            createBatchesDialogBinding.tvBalanceQuantity.setText(
                                selectedPoLineItem[currentPoLineItemPosition.toInt()].balQTY.toString()
                            )
                            createBatchesList[position] = updatedItem.copy()
                            // Update total received quantity for the PO line item
                            val sumOfReceivedQtyIncludingUpdatedItem =
                                createBatchesList.sumByDouble { it.recevedQty.toDouble() }
                            selectedPoLineItem[currentPoLineItemPosition.toInt()].quantityReceived =
                                sumOfReceivedQtyIncludingUpdatedItem.toString()
                            createBatchesDialogBinding.tvQtyValue.setText(
                                sumOfReceivedQtyIncludingUpdatedItem.toString()
                            )
                            addSingleGrnLineUnitItemApiCall(updatedItem)
                            createBatchesMainRcAdapter!!.notifyItemChanged(position)
                            grnMainItemAdapter!!.notifyItemChanged(currentPoLineItemPosition.toInt())
                        }
                    } else { // Received quantity decreased
                        // Add the difference to the balance quantity
                        val newBalanceQty = balanceQty.toDouble() + (-receivedQtyDifference)
                        val balQtyFormat = String.format("%.3f", newBalanceQty)

                        selectedPoLineItem[currentPoLineItemPosition.toInt()].balQTY =
                            balQtyFormat.toDouble()
                        // Update received quantity for the item being modified
                        selectedPoLineItem[currentPoLineItemPosition.toInt()].grnLineItemUnit!![position] =
                            updatedItem.copy()
                        createBatchesDialogBinding.tvBalanceQuantity.setText(
                            selectedPoLineItem[currentPoLineItemPosition.toInt()].balQTY.toString()
                        )
                        balanceQty = balQtyFormat
                        createBatchesList[position] = updatedItem.copy()
                        // Update total received quantity for the PO line item
                        val sumOfReceivedQtyIncludingUpdatedItem =
                            createBatchesList.sumByDouble { it.recevedQty.toDouble() }
                        selectedPoLineItem[currentPoLineItemPosition.toInt()].quantityReceived =
                            sumOfReceivedQtyIncludingUpdatedItem.toString()
                        createBatchesDialogBinding.tvQtyValue.setText(
                            sumOfReceivedQtyIncludingUpdatedItem.toString()
                        )
                        addSingleGrnLineUnitItemApiCall(updatedItem)
                        createBatchesMainRcAdapter!!.notifyItemChanged(position)
                        grnMainItemAdapter!!.notifyItemChanged(currentPoLineItemPosition.toInt())
                    }
                } else {
                    val previousExpiryDate =
                        selectedPoLineItem[currentPoLineItemPosition.toInt()].grnLineItemUnit!![position]?.expiryDate
                    if (!updatedItem?.expiryDate!!.equals(previousExpiryDate)) {
                        selectedPoLineItem[currentPoLineItemPosition.toInt()].grnLineItemUnit!![position]?.expiryDate =
                            updatedItem?.expiryDate!!
                        createBatchesList[position] = updatedItem.copy()
                        addSingleGrnLineUnitItemApiCall(updatedItem)
                        createBatchesMainRcAdapter!!.notifyItemChanged(position)
                        grnMainItemAdapter!!.notifyItemChanged(currentPoLineItemPosition.toInt())
                    }
                }
            }
        } else {
            Toast.makeText(
                this,
                "Value must not be 0.",
                Toast.LENGTH_SHORT
            ).show()
        }


    }


    //other charges
    fun getTotalExpenseSummary(): String {
        // Calculate the total expense amount
        val totalAmount = getOtherCharges.sumOf { it.calculatedExpenseAmount.toDouble() }

        // Return a formatted string with the total amount
        return "%.2f".format(totalAmount)
    }

    private fun getOtherCharges() {
        try {

            viewModel.getOtherCharges(this,token, baseUrl, createQueryMap(selectedPoFilteredList))


        } catch (e: Exception) {
            Toasty.error(
                this,
                e.message.toString(),
                Toasty.LENGTH_LONG
            ).show()
        }
    }

    fun createQueryMap(poIds: List<Int>): Map<String, Int> {
        val queryMap = mutableMapOf<String, Int>()
        poIds.forEachIndexed { index, value ->
            queryMap["PoIds[$index]"] = value
        }
        return queryMap
    }


    private fun setPoLineItemList() {
        /*grnMainItemAdapter?.setOnItemCheckClickListener { position, poline ->
            createBatchesList.clear()
            if (poline?.grnLineItemUnit != null) {
                lineItemId = poline.grnLineItemUnit?.firstOrNull()?.lineItemId!!
            } else {
                lineItemId = 0
            }
            lineItemUnitId = 0
            poline.grnLineItemUnit?.let { createBatchesList.addAll(it) }
            currentPoLineItemPosition = position.toString()
            setCreateBatchesDialog(poline)
            createBatchesMainRcAdapter!!.notifyDataSetChanged()
            //  addNewBatch(position,poline)
        }*/

    }

    ////Batches activity


    private fun addSingleGrnLineUnitItemApiCall(u: CustomGrnLineItemUnit) {
        try {
            viewModel.processSingleGRNGRNItemBatches(
                this,token, baseUrl,
                //weight not proper
                GRNUnitLineItemsSaveRequest(
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].balQTY,
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].currency ?: "",
                    null,
                    currentGrnID.toInt(),
                    listOf(
                        GrnLineItemUnit(
                            barcode = u.barcode,
                            expiryDate = u.expiryDate.toString(),
                            isQCRequired = false,
                            kBatchNo = u.internalBatchNo,
                            lineItemId = u.lineItemId,
                            lineItemUnitId = u.lineItemUnitId,
                            qcStatus = "",
                            qty = u.recevedQty.toDouble(), supplierBatchNo = u.supplierBatchNo,
                            uoM = u.UOM.toString()
                        )
                        /*   GrnLineItemUnit(
                               u.barcode,
                               "", createdDate,
                               u.lineItemId,
                               u.lineItemUnitId,
                               u.UOM,

                               false,
                               u.internalBatchNo,
                               false,
                               false, 0,
                               u.recevedQty.toDouble(),
                               u.supplierBatchNo,
                               u.UOM,
                               "", u.expiryDate.toString(), u.isExpirable
                           )*/
                    ),
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].quantityReceived.toDouble(),
                    false, "",
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].itemCode,
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].itemDescription,
                    "",
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].itemName,
                    lineItemId,
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].locationId,
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].mhType,
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].poId,
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].poLineItemId,
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].poLineNo,
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].poNumber,
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].poqty,
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].posapLineItemNumber,
                    selectedPoLineItem[currentPoLineItemPosition.toInt()]?.pouom ?: "", totalUnits,
                    selectedPoLineItem[currentPoLineItemPosition.toInt()]?.unitPrice ?: 0, "",
                    taxId, 100
                )
            )
        } catch (e: Exception) {
            Toast.makeText(this@GRNAddActivity, "${e.message}", Toast.LENGTH_SHORT)
                .show()
        }

    }

    private fun updateLineItem(u: CustomGrnLineItemUnit) {
        try {
            viewModel.processSingleGRNGRNItemBatches(
                this,token, baseUrl,
                //weight not proper
                GRNUnitLineItemsSaveRequest(
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].balQTY,
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].currency ?: "",
                    null,
                    currentGrnID.toInt(),
                    listOf(
                        GrnLineItemUnit(
                            barcode = u.barcode,
                            expiryDate = u.expiryDate.toString(),
                            isQCRequired = false,
                            kBatchNo = u.internalBatchNo,
                            lineItemId = u.lineItemId,
                            lineItemUnitId = u.lineItemUnitId,
                            qcStatus = "",
                            qty = u.recevedQty.toDouble(), supplierBatchNo = u.supplierBatchNo,
                            uoM = u.UOM.toString()
                        )
                        /*   GrnLineItemUnit(
                               u.barcode,
                               "", createdDate,
                               u.lineItemId,
                               u.lineItemUnitId,
                               u.UOM,

                               false,
                               u.internalBatchNo,
                               false,
                               false, 0,
                               u.recevedQty.toDouble(),
                               u.supplierBatchNo,
                               u.UOM,
                               "", u.expiryDate.toString(), u.isExpirable
                           )*/
                    ),
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].quantityReceived.toDouble(),
                    false, "",
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].itemCode,
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].itemDescription,
                    "",
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].itemName,
                    lineItemId,
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].locationId,
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].mhType,
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].poId,
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].poLineItemId,
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].poLineNo,
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].poNumber,
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].poqty,
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].posapLineItemNumber,
                    selectedPoLineItem[currentPoLineItemPosition.toInt()]?.pouom ?: "", totalUnits,
                    selectedPoLineItem[currentPoLineItemPosition.toInt()]?.unitPrice ?: 0, "",
                    taxId, 100
                )
            )
        } catch (e: Exception) {
            Toast.makeText(this@GRNAddActivity, "${e.message}", Toast.LENGTH_SHORT)
                .show()
        }

    }

    private fun processSingleGRNGRNItemBatchesForMultiple(u: CustomGrnLineItemUnit) {
        try {
            viewModel.processSingleGRNGRNItemBatchesForMultiple(
                this,token, baseUrl,
                GRNUnitLineItemsSaveRequest(
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].balQTY,
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].currency ?: "",
                    null,
                    currentGrnID.toInt(),
                    listOf(
                        GrnLineItemUnit(
                            barcode = u.barcode,
                            expiryDate = u.expiryDate.toString(),
                            isQCRequired = false,
                            kBatchNo = u.internalBatchNo,
                            lineItemId = u.lineItemId,
                            lineItemUnitId = u.lineItemUnitId,
                            qcStatus = "",
                            qty = u.recevedQty.toDouble(), supplierBatchNo = u.supplierBatchNo,
                            uoM = u.UOM.toString()
                        )
                        /*   GrnLineItemUnit(
                               u.barcode,
                               "", createdDate,
                               u.lineItemId,
                               u.lineItemUnitId,
                               u.UOM,

                               false,
                               u.internalBatchNo,
                               false,
                               false, 0,
                               u.recevedQty.toDouble(),
                               u.supplierBatchNo,
                               u.UOM,
                               "", u.expiryDate.toString(), u.isExpirable
                           )*/
                    ),
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].quantityReceived.toDouble(),
                    false, "",
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].itemCode,
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].itemDescription,
                    "",
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].itemName,
                    lineItemId,
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].locationId,
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].mhType,
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].poId,
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].poLineItemId,
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].poLineNo,
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].poNumber,
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].poqty,
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].posapLineItemNumber,
                    selectedPoLineItem[currentPoLineItemPosition.toInt()]?.pouom ?: "", totalUnits,
                    selectedPoLineItem[currentPoLineItemPosition.toInt()]?.unitPrice ?: 0, "",
                    taxId, 100
                )
            )
        } catch (e: Exception) {
            Toast.makeText(this@GRNAddActivity, "${e.message}", Toast.LENGTH_SHORT)
                .show()
        }

    }

    private fun addBatchNewItem(
        position: Int,
        poModel: CustomPoLineItemSelectionModel,
        newBarcode: String,
    ) {
        val edBatchNo = createBatchesSingleDialogBinding.et1.text.toString().trim()
        if (edBatchNo.isNotEmpty()) {

            val hasItemWithZeroReceivedQuantity = poModel.grnLineItemUnit?.any {
                it!!.recevedQty == "0" || it!!.recevedQty == "0.000"
            } ?: false
            Log.d("hasItemWithZeroReceivedQuantity", poModel.toString())
            if (poModel.mhType.equals("Serial")) {
                if (hasItemWithZeroReceivedQuantity) {
                    Toasty.warning(this@GRNAddActivity, "Please complete current transaction!!")
                        .show()
                    Log.d("edBatchNo.isNotEmpty()", "sad")
                } else {
                    if (poModel.grnLineItemUnit != null) {
                        val batchExists =
                            poModel.grnLineItemUnit!!.any { it!!.supplierBatchNo == edBatchNo }
                        if (!batchExists) {
                            if (newBarcode != "") {
                                val newBatch = createBatchInfo(poModel, edBatchNo, newBarcode)
                                if (position != RecyclerView.NO_POSITION) {
                                    val model = selectedPoLineItem[position]
                                    // Initialize batchInfoListModel if it's null
                                    if (model.grnLineItemUnit == null) {
                                        model.grnLineItemUnit = mutableListOf()
                                    }
                                    // Add new batch to batchInfoListModel
                                    model.grnLineItemUnit!!.add(newBatch)
                                    createBatchesList.add(
                                        createBatchInfo(
                                            poModel,
                                            edBatchNo,
                                            newBarcode
                                        )
                                    )
                                    createBatchesMainRcAdapter?.notifyItemInserted(createBatchesList.size - 1)
                                    grnMainItemAdapter?.notifyItemChanged(position)
                                    Log.d("edBatchNo.isNotEmpty()", selectedPoLineItem.toString())
                                } else {

                                }
                            } else {
                                Toast.makeText(
                                    this@GRNAddActivity,
                                    "Batch No is Empty!!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                this@GRNAddActivity,
                                "Batch already exists!!",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    } else {
                        if (newBarcode != "") {
                            val newBatch = createBatchInfo(poModel, edBatchNo, newBarcode)
                            if (position != RecyclerView.NO_POSITION) {
                                val model = selectedPoLineItem[position]
                                // Initialize batchInfoListModel if it's null
                                if (model.grnLineItemUnit == null) {
                                    model.grnLineItemUnit = mutableListOf()
                                }
                                // Add new batch to batchInfoListModel
                                model.grnLineItemUnit!!.add(newBatch)
                                createBatchesList.add(
                                    createBatchInfo(
                                        poModel,
                                        edBatchNo,
                                        newBarcode
                                    )
                                )
                                createBatchesMainRcAdapter?.notifyItemInserted(createBatchesList.size - 1)
                                grnMainItemAdapter?.notifyItemChanged(position)
                                Log.d("edBatchNo.isNotEmpty()", selectedPoLineItem.toString())
                            } else {

                            }
                        } else {
                            Toast.makeText(
                                this@GRNAddActivity,
                                "Batch No is Empty!!",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
                }
            } else {
                if (hasItemWithZeroReceivedQuantity) {
                    Toasty.warning(this@GRNAddActivity, "Please complete current transaction!!")
                        .show()
                    Log.d("edBatchNo.isNotEmpty()", "sad")
                } else {
                    if (newBarcode != "") {
                        val newBatch = createBatchInfo(poModel, edBatchNo, newBarcode)
                        if (position != RecyclerView.NO_POSITION) {
                            val model = selectedPoLineItem[position]
                            if (model.grnLineItemUnit == null) {
                                model.grnLineItemUnit = mutableListOf()
                            }
                            model.grnLineItemUnit!!.add(newBatch)
                            createBatchesList.add(createBatchInfo(poModel, edBatchNo, newBarcode))
                            createBatchesMainRcAdapter?.notifyItemInserted(createBatchesList.size - 1)
                            grnMainItemAdapter?.notifyItemChanged(position)
                            Log.d("edBatchNo.isNotEmpty()", selectedPoLineItem.toString())
                        } else {

                        }
                    } else {
                        Toast.makeText(this@GRNAddActivity, "Barcode is null!!", Toast.LENGTH_SHORT)
                            .show()
                    }
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
        poModel: CustomPoLineItemSelectionModel,
    ) {
        viewModel.getBarcodeValueWithPrefixForExisitngMutableResponse.observe(this)
        { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { resultResponse ->
                        try {
                            val newBarcode = resultResponse.responseMessage.toString()
                            Log.e("generatedBarcode", newBarcode)
                            if (edBatchNo.isNotEmpty()) {
                                val hasItemWithZeroReceivedQuantity = selectedPoLineItem.any {
                                    it.grnLineItemUnit?.any { it!!.recevedQty.toString() == "0.000" || it!!.recevedQty == "0" }
                                        ?: false
                                }
                                if (hasItemWithZeroReceivedQuantity) {
                                    Toasty.warning(
                                        this@GRNAddActivity,
                                        "Please complete current transaction!!"
                                    ).show()
                                    Log.d("edBatchNo.isNotEmpty()", "sad")
                                } else {
                                    val newBatch = createBatchInfo(poModel, edBatchNo, newBarcode)
                                    if (position != RecyclerView.NO_POSITION) {

                                        val barcodeExistsInParent =
                                            selectedPoLineItem[currentPoLineItemPosition.toInt()].grnLineItemUnit!!.any { it!!.barcode == newBarcode }

                                        if (!barcodeExistsInParent) {
                                            val model = selectedPoLineItem[position]
                                            // Initialize batchInfoListModel if it's null
                                            if (model.grnLineItemUnit == null) {
                                                model.grnLineItemUnit = mutableListOf()
                                            }
                                            // Add new batch to batchInfoListModel
                                            model.grnLineItemUnit!!.add(newBatch)
                                            model.totalUnit = totalUnits
                                            grnMainItemAdapter?.notifyItemChanged(
                                                currentPoLineItemPosition.toInt()
                                            )
                                        }
                                        val barcodeExistsInBatches =
                                            createBatchesList!!.any { it.barcode == newBarcode }
                                        if (!barcodeExistsInBatches) {
                                            createBatchesList.add(
                                                createBatchInfo(
                                                    poModel,
                                                    edBatchNo,
                                                    newBarcode
                                                )
                                            )
                                            createBatchesMainRcAdapter?.notifyItemInserted(
                                                createBatchesList.size - 1
                                            )
                                        }
                                        Log.d(
                                            "edBatchNo.isNotEmpty()",
                                            selectedPoLineItem.toString()
                                        )
                                    } else {

                                    }
                                }
                            } else {
                                Toast.makeText(
                                    this@GRNAddActivity,
                                    "Please enter batch no!!",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
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
                    binding.clSelectPoId.visibility = View.GONE
                    response.message?.let { errorMessage ->
                        Toasty.error(
                            this@GRNAddActivity,
                            "Login failed - \nError Message: $errorMessage"
                        ).show()
                        session.showToastAndHandleErrors(errorMessage, this@GRNAddActivity)
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }
    }

    private fun createBatchInfo(
        poModel: CustomPoLineItemSelectionModel,
        edBatchNo: String,
        newBarcode: String,
    ): CustomGrnLineItemUnit {

        val additionalValue = if (poModel.pouom != "KGS") "0" else "0.000"
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
        return CustomGrnLineItemUnit(
            poModel.pouom,
            poModel.mhType,
            newBarcode,
            "",
            poModel.isExpirable,
            "$edBatchNo/$selectedKGRN",
            false,
            lineItemId, 0,
            additionalValue, edBatchNo, false, totalUnits
        )

    }

    private fun createMultipleBatches(
        poModel: CustomPoLineItemSelectionModel,
        edBatchNo: CustomGrnLineItemUnit,
        newBarcode: String,
    ): CustomGrnLineItemUnit {

        return CustomGrnLineItemUnit(
            poModel.pouom,
            poModel.mhType,
            newBarcode,
            edBatchNo.expiryDate.toString(),
            poModel.isExpirable,
            "${edBatchNo.supplierBatchNo}/$selectedKGRN",
            false,
            lineItemId, 0,
            edBatchNo.recevedQty, edBatchNo.supplierBatchNo, true, totalUnits
        )

    }


    private fun logout() {
        session.logoutUser()
        startActivity(Intent(this@GRNAddActivity, LoginActivity::class.java))
        finish()
    }

    //edit case update the values
    private fun callDefaultData() {
        if (grnId != 0) {
            getDraftGrn()
        }

    }

    private fun getDraftGrn() {
        try {
            viewModel.getDraftGRN(this,token, baseUrl, currentGrnID!!.toInt())
        } catch (e: Exception) {
            Toasty.error(
                this@GRNAddActivity,
                "Failed - \nError Message: $e"
            ).show()
        }
    }


    private fun submitGrn() {
        try {
            viewModel.submitGRN(this,token!!, baseUrl, SubmitGRNRequest(currentGrnID!!.toInt()))
        } catch (e: Exception) {
            Toasty.error(
                this@GRNAddActivity,
                "failed - \nError Message: $e"
            ).show()
        }
    }


    override fun onPrinterStatusChanged(isConnected: Boolean) {
        runOnUiThread {
            if (isConnected) {
                isPrinterConnected = true
                binding.ivWeightIndicator.visibility = View.VISIBLE
                Toast.makeText(this, "Printer connected", Toast.LENGTH_SHORT).show()
            } else {
                isPrinterConnected = false
                binding.ivWeightIndicator.visibility = View.GONE
                Toast.makeText(this, "Printer disconnected", Toast.LENGTH_SHORT).show()
            }
        }
    }


}


/*   fun convertToGrams(amount: Double, unit: String): Double {
       return when (unit.toLowerCase()) {
           "gram" -> amount
           "kg" -> amount * 1000
           else -> throw IllegalArgumentException("Unknown unit: $unit")
       }
   }

   fun convertGramsToKilograms(grams: Int): String {
       val kilograms = grams / 1000.0
       return String.format("%.3f", kilograms)
   }

   fun parseValue(value: String): Pair<Double, String> {
       val (amountStr, unit) = value.split(" ")
       var amount = amountStr.toDouble()
       if (amount < 1 && amountStr.length > 2) {
           amount *= 1000 // Convert to grams
       }
       return Pair(amount, unit)
   }
*/


