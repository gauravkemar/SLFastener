package com.example.slfastener.view

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialog
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.demorfidapp.helper.Constants
import com.example.demorfidapp.helper.Resource
import com.example.demorfidapp.helper.SessionManager
import com.example.demorfidapp.repository.SLFastenerRepository
import com.example.slfastener.R
import com.example.slfastener.adapter.completedgrn.CreateBatchesCompletedAdapter
import com.example.slfastener.adapter.completedgrn.GRNSelectPoCompletedAdapter
import com.example.slfastener.adapter.completedgrn.GrnMainAddCompletedAdapter
import com.example.slfastener.databinding.ActivityCompletedGrnactivityBinding
import com.example.slfastener.databinding.CompletedBatchesDialogBinding
import com.example.slfastener.databinding.DescriptionInfoDialogBinding
import com.example.slfastener.databinding.SelectPoIdsDialogBinding


import com.example.slfastener.helper.CustomArrayAdapter
import com.example.slfastener.helper.printer.USBPrinterHelper

import com.example.slfastener.model.GetActiveSuppliersDDLResponse
import com.example.slfastener.model.GetPOsAndLineItemsOnPOIdsResponse
import com.example.slfastener.model.GetSuppliersPOsDDLResponse
import com.example.slfastener.model.getalllocation.GetAllWareHouseLocationResponse
import com.example.slfastener.model.grndraftdata.GetDraftGrnResponse
import com.example.slfastener.model.offlinebatchsave.CustomGrnLineItemUnit
import com.example.slfastener.model.offlinebatchsave.CustomPoLineItemSelectionModel
import com.example.slfastener.viewmodel.GRNTransactionViewModel
import com.example.slfastener.viewmodel.GRNTransactionViewModelProviderFactory
import es.dmoral.toasty.Toasty
import java.text.SimpleDateFormat
import java.util.Locale

class CompletedGRNActivity : AppCompatActivity(), USBPrinterHelper.PrinterStatusListener   {
    lateinit var binding: ActivityCompletedGrnactivityBinding
    private lateinit var viewModel: GRNTransactionViewModel
    private var grnMainItemAdapter: GrnMainAddCompletedAdapter? = null
    private var createBatchesMainRcAdapter: CreateBatchesCompletedAdapter? = null
    lateinit var getSuppliersPOsDDLResponse: MutableList<GetSuppliersPOsDDLResponse>
    lateinit var getPOsAndLineItemsOnPOIdsResponse: MutableList<GetPOsAndLineItemsOnPOIdsResponse>
    lateinit var poLineItem: MutableList<CustomPoLineItemSelectionModel>
    lateinit var selectedPoLineItem: MutableList<CustomPoLineItemSelectionModel>
    private lateinit var session: SessionManager
    private lateinit var progress: ProgressDialog
    lateinit var selectPoBinding: SelectPoIdsDialogBinding
    lateinit var supplierSpinnerArray: MutableList<String>
    lateinit var selectedPoFilteredList: MutableList<Int>
    val supplierMap = HashMap<String, String>()
    var selectedSupplierCode: String = ""

    var token: String = ""
    private var supplierSpinnerAdapter: CustomArrayAdapter? = null
    private var isIntialSelectSupplier = true
    var selectPoDialog: Dialog? = null
    lateinit var createBatchesDialogBinding: CompletedBatchesDialogBinding
    lateinit var createBatchedDialog: AppCompatDialog
    lateinit var createBatchesList: MutableList<CustomGrnLineItemUnit>
    lateinit var getActiveSuppliersDDLResponse: MutableList<GetActiveSuppliersDDLResponse>
    lateinit var getAllLocation: MutableList<GetAllWareHouseLocationResponse>
    var currentPoLineItemPosition = ""
    private lateinit var userDetails: HashMap<String, Any?>
    private var selectedBpId = ""
    var edInvoiceNo: String = ""
    var selectedCurrency: String = ""
    var selectedKGRN: String = ""
    var balanceQty = ""
    var totalQty = ""
    var currentGrnID = 0
    var lineItemUnitId = 0
    var lineItemId = 0
    var grnId = 0

    private var baseUrl: String = ""
    private var serverIpSharedPrefText: String? = null
    private var serverHttpPrefText: String? = null

    ///get data from draft
    private var getDraftGrnResponse: GetDraftGrnResponse? = null

    private lateinit var itemDescriptionBinding: DescriptionInfoDialogBinding
    var itemDescriptionDialog: Dialog? = null

    lateinit var selectedBatchForPrint: ArrayList<Int>

    private var serverPrinterTypePrefText: String? = null
    private var printerPrnforGRN: String? = null
    var formattedPrnForPrinter=""

    private lateinit var usbPrinterHelper: USBPrinterHelper
    var bulkUsbPrintFlag=0

    var isPrinterConnected=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_completed_grnactivity)

        session = SessionManager(this)
        selectPoBinding = SelectPoIdsDialogBinding.inflate(getLayoutInflater());
        selectPoDialog = Dialog(this)
        progress = ProgressDialog(this)
        progress.setMessage("Loading...")
        supplierSpinnerArray = ArrayList()
        selectedPoFilteredList = ArrayList()
        getActiveSuppliersDDLResponse = ArrayList()
        getSuppliersPOsDDLResponse = ArrayList()
        createBatchesList = ArrayList()
        poLineItem = ArrayList()
        selectedPoLineItem = ArrayList()
        getAllLocation = ArrayList()
        getPOsAndLineItemsOnPOIdsResponse = ArrayList()
        userDetails = session.getUserDetails()
        token = userDetails["jwtToken"].toString()
        serverIpSharedPrefText = userDetails!![Constants.KEY_SERVER_IP].toString()
        serverHttpPrefText = userDetails!![Constants.KEY_HTTP].toString()

        serverPrinterTypePrefText = userDetails[Constants.KEY_PRINTER_TYPE].toString()
        printerPrnforGRN = userDetails[Constants.KEY_GRN_PRN].toString()

        baseUrl = "$serverHttpPrefText://$serverIpSharedPrefText/service/api/"
        itemDescriptionBinding = DescriptionInfoDialogBinding.inflate(LayoutInflater.from(this))
        itemDescriptionDialog = Dialog(this)
        selectedBatchForPrint = ArrayList()

        usbPrinterHelper = USBPrinterHelper(this, this)
        val receivedIntent = intent
        grnId = receivedIntent.getIntExtra("GRNID", 0)

        val SLFastenerRepository = SLFastenerRepository()
        val viewModelProviderFactory =
            GRNTransactionViewModelProviderFactory(application, SLFastenerRepository)
        viewModel =
            ViewModelProvider(this, viewModelProviderFactory)[GRNTransactionViewModel::class.java]
        getAllLocations()
        setItemDescriptionDialog()
        if (grnId != 0) {
            callDefaultData()
            binding.clKGRNNo.visibility = View.VISIBLE
            binding.mcvAddGrn.visibility = View.GONE
        } else {
            binding.clKGRNNo.visibility = View.GONE
            binding.mcvAddGrn.visibility = View.VISIBLE
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
        grnMainItemAdapter = GrnMainAddCompletedAdapter(
            this@CompletedGRNActivity,
            selectedPoLineItem,
            itemDescription = {
                setItemDescription(it)
            },
            onItemCheck = { position, poline ->
                createBatchesList.clear()
                if (poline?.grnLineItemUnit != null) {
                    lineItemId = poline.lineItemId
                    for (grnLineUnit in poline.grnLineItemUnit!!) {
                        if(grnLineUnit!=null)
                        {
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

                if (selectedCurrency.equals("INR")) {
                    binding.grnAddHeader.edGDPO.visibility = View.GONE
                } else {
                    binding.grnAddHeader.edGDPO.visibility = View.VISIBLE

                }

                //poline.grnLineItemUnit?.let { createBatchesList.addAll(it) }
                currentPoLineItemPosition = position.toString()
                setCreateBatchesDialog(poline)
                createBatchesMainRcAdapter!!.notifyDataSetChanged()
                //  addNewBatch(position,poline)
            },
        )
        binding.rcGrnAdd!!.adapter = grnMainItemAdapter
        binding.rcGrnAdd.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        ///get list of suppliers PO
        viewModel.getActiveSuppliersDDLMutable.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    supplierMap.clear()
                    supplierSpinnerArray.clear()
                    getActiveSuppliersDDLResponse.clear()
                    (supplierSpinnerArray).add("Select Supplier")

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
                                this@CompletedGRNActivity,
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
                            this@CompletedGRNActivity,
                            "Login failed - \nError Message: $errorMessage"
                        ).show()
                        session.showToastAndHandleErrors(errorMessage, this@CompletedGRNActivity)
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
                                binding.clSelectPo.visibility = View.VISIBLE
                                if (grnId != 0) {
                                    selectePoDefaultRc()
                                }
                            } else {
                                Toast.makeText(this, "List is Empty!!", Toast.LENGTH_SHORT).show()
                                binding.clSelectPo.visibility = View.GONE
                            }
                        } catch (e: Exception) {
                            Toasty.warning(
                                this@CompletedGRNActivity,
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
                            this@CompletedGRNActivity,
                            "Login failed - \nError Message: $errorMessage"
                        ).show()
                        session.showToastAndHandleErrors(errorMessage, this@CompletedGRNActivity)
                    }
                }

                is Resource.Loading -> {
                }
            }
        }
        ///get list of po line item
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
                                                        true,i.totalUnit
                                                    )
                                                }
                                            }.toMutableList()
                                        Log.e(
                                            "editcaseDefaultList",
                                            convertedGrnLineItemUnits.toString() + "//////${df.size}"
                                        )
                                        Log.e("editcaseDefaultListdf ", df.toString())
                                        val existingItem =
                                            selectedPoLineItem.find { it.posapLineItemNumber == poID }
                                        if (existingItem == null) {
                                            selectedPoLineItem.add(
                                                CustomPoLineItemSelectionModel(
                                                    i.isQCRequired,
                                                    i.isExpirable,
                                                    i.lineItemId,
                                                    i.balQty, d.currency,
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
                                                    getAllLocation, i.locationId, true,null,0,i.totalUnit,i.discountAmount,calculateLineItemTotal(i.grnQty,i.unitPrice,i.discountAmount,i.taxPercent).toString()

                                                )
                                            )
                                        }
                                    }
                                    grnMainItemAdapter!!.notifyDataSetChanged()
                                    for (r in resultResponse) {
                                        for (e in r.poLineItems) {
                                            val additionalValue =
                                                if (e.poUoM != "KGS") "0" else "0.000"
                                            val unitPrice = e.unitPrice
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

                                                    unitPrice, getAllLocation, e.locationId, false,null,0,0,0.0,"0"
                                                ).also { poLineItem.add(it) }
                                                Log.e("selectPolIne", poLineItem.toString())
                                            }
                                        }
                                    }
                                }
                            } else {
                                Toast.makeText(this, "List is Empty!!", Toast.LENGTH_SHORT).show()
                            }

                        } catch (e: Exception) {
                            Toasty.warning(
                                this@CompletedGRNActivity,
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
                            this@CompletedGRNActivity,
                            "Login failed - \nError Message: $errorMessage"
                        ).show()
                        session.showToastAndHandleErrors(errorMessage, this@CompletedGRNActivity)
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }
        viewModel.getAllLocationsMutableResponse.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let { resultResponse ->
                        try {
                            getAllLocation = resultResponse
                        } catch (e: Exception) {
                            Toasty.warning(
                                this@CompletedGRNActivity,
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
                            this@CompletedGRNActivity,
                            "failed - \nError Message: $errorMessage"
                        ).show()
                        session.showToastAndHandleErrors(errorMessage, this@CompletedGRNActivity)
                    }
                }

                is Resource.Loading -> {

                }
            }
        }
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
                            this@CompletedGRNActivity,
                            "failed - \nError Message: $errorMessage"
                        ).show()
                        session.showToastAndHandleErrors(errorMessage, this@CompletedGRNActivity)
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }
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
                            this@CompletedGRNActivity,
                            "failed - \nError Message: $errorMessage"
                        ).show()
                        session.showToastAndHandleErrors(errorMessage, this@CompletedGRNActivity)
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }
        viewModel.getGRNProductDetailsOnUnitIdItemMutableResponse.observe(this){ response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { dataMap ->
                        // Iterate through the map and handle fields dynamically

                        //Log.e("printer,string",bulkUsbPrintFlag.toString())
                        dataMap.forEachIndexed { index, item ->
                            item.forEach { (key, value) ->
                                formattedPrnForPrinter = printerPrnforGRN!!.replace(key, value.toString())
                            }
                            Log.e("printer,string", "${bulkUsbPrintFlag++}----------------===================$formattedPrnForPrinter")
                        }

                        //Log.e("printer,string",dataMap.toString())
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { errorMessage ->
                        Toasty.error(this@CompletedGRNActivity, "Error: $errorMessage").show()
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }

        ////batches dialog
        createBatchesDialogBinding =
            CompletedBatchesDialogBinding.inflate(LayoutInflater.from(this));
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
        createBatchesDialogBinding.mcvCancel.setOnClickListener {
            createBatchedDialog!!.dismiss()
        }
        createBatchesMainRcAdapter = CreateBatchesCompletedAdapter(
            this@CompletedGRNActivity,
            createBatchesList,
            onSave = { position, updatedItem ->
                Log.e("printertype",serverPrinterTypePrefText.toString())
                if(isPrinterConnected==false)
                {
                    Toasty.error(
                        this,
                        "Printer Not Connected.!!",
                        Toasty.LENGTH_LONG
                    ).show()
                }
                else if(serverPrinterTypePrefText!!.contains("USB"))
                {
                    printLabelForUSB(updatedItem)
                }
                else if(serverPrinterTypePrefText!!.contains("IP"))
                {
                    printLabelForGRN(updatedItem)
                }
                else{
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
                    Log.e("selectedBatchForPrint",selectedBatchForPrint.toString())

                } else {
                    selectedBatchForPrint.remove(item.lineItemUnitId)
                    Log.e("selectedBatchForPrint",selectedBatchForPrint.toString())
                }
            },
        )
        createBatchesDialogBinding.rcBatchs.adapter = createBatchesMainRcAdapter
        createBatchesDialogBinding.rcBatchs.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        createBatchesDialogBinding.ivPrintAll.setOnClickListener {
            ///Log.e("PRinter bulk",serverPrinterTypePrefText+"////${selectedBatchForPrint.toString()}")
            if(isPrinterConnected==false)
            {
                Toasty.error(
                    this,
                    "Printer Not Connected.!!",
                    Toasty.LENGTH_LONG
                ).show()
            }
            else if(serverPrinterTypePrefText!!.contains("USB"))
            {

                printLabelUSBForBulk()
            }
            else if(serverPrinterTypePrefText!!.contains("IP"))
            {
                printLabelForBulk()

            }
            else{
                Toasty.error(
                    this,
                    "Printer Not Set.!!",
                    Toasty.LENGTH_LONG
                ).show()
            }

            Log.e("selectedBatchForPrint",selectedBatchForPrint.toString())
        }
        createBatchesDialogBinding.ivBatchesSelection.setOnClickListener {
            createBatchesList.forEachIndexed { index, it ->
                it.isChecked = true
                selectedBatchForPrint.add(it.lineItemUnitId)
                createBatchesMainRcAdapter!!.notifyItemChanged(index)

            }
        }

    }
    private fun calculateLineItemTotal(
        quantity: Double,
        rate: Int?,
        discount: Double?,
        taxPercent: Double
    ):Double {


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
    override fun onDestroy() {
        super.onDestroy()
        usbPrinterHelper.unregisterReceiver()
    }
    override fun onPrinterStatusChanged(isConnected: Boolean) {
        runOnUiThread {
            if (isConnected) {
                //binding.ivWeightIndicator.visibility= View.VISIBLE
                Toast.makeText(this, "Printer connected", Toast.LENGTH_SHORT).show()
                isPrinterConnected=true
            } else {
                //binding.ivWeightIndicator.visibility= View.GONE
                Toast.makeText(this, "Printer disconnected", Toast.LENGTH_SHORT).show()
                isPrinterConnected=false
            }
        }
    }
    private fun setItemDescription(itemDesc: String) {
        itemDescriptionDialog?.show()
        itemDescriptionBinding.tvItemDescription.setText(itemDesc)
    }

    private fun getCommonCode(): String? {
        val checkedItem = getSuppliersPOsDDLResponse.find { it.isChecked }
        return checkedItem?.code
    }

    private fun setItemDescriptionDialog() {
        itemDescriptionDialog!!.setContentView(itemDescriptionBinding.root)
        itemDescriptionDialog!!.getWindow()?.setBackgroundDrawable(
            AppCompatResources.getDrawable(
                this@CompletedGRNActivity,
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

    private fun setSupplierSpinner() {

        supplierSpinnerAdapter =
            CustomArrayAdapter(this, android.R.layout.simple_spinner_item, supplierSpinnerArray)
        supplierSpinnerAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.tvSpinnerSupplier.adapter = supplierSpinnerAdapter
        if (grnId != 0) {
            val defaultBpName = getDraftGrnResponse?.grnTransaction?.bpName
            if (!defaultBpName.isNullOrEmpty()) {

                val defaultPosition = supplierSpinnerArray.indexOf(defaultBpName)
                binding.tvSpinnerSupplier.setSelection(defaultPosition)
                binding.tvSpinnerSupplier.isEnabled = false
                callParentLocationApi(getDraftGrnResponse?.grnTransaction?.bpCode.toString())
                if (defaultPosition != -1) {
                    binding.tvSpinnerSupplier.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                adapterView: AdapterView<*>,
                                view: View?,
                                i: Int,
                                l: Long
                            ) {
                                val selectedItem = adapterView.selectedItem.toString()
                                if (view != null && view is TextView) {
                                    // Set the text color of the selected view
                                    view.setTextColor(Color.BLACK)
                                }
                                if (!isIntialSelectSupplier) {
                                    if (supplierSpinnerArray[i] != "Select Supplier") {
                                        val selectedKey: String? =
                                            supplierMap.entries.find { it.value == selectedItem }?.key
                                        selectedSupplierCode = selectedKey!!
                                        selectedBpId =
                                            getActiveSuppliersDDLResponse.find { it.code == selectedSupplierCode }!!.value.toString()
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
                viewModel.getPosLineItemsOnPoIds(this,token, baseUrl, selectedPoFilteredList)
                selectPoDialog!!.dismiss()
            } else {
                Toasty.warning(
                    this@CompletedGRNActivity,
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
            viewModel.getActiveSuppliersDDL(this,token, baseUrl)
        } catch (e: Exception) {
            Toasty.error(
                this,
                e.message.toString(),
                Toasty.LENGTH_LONG
            ).show()
        }
    }


    private fun setSelectSupplierDialog() {
        selectPoDialog!!.setContentView(selectPoBinding.root)
        selectPoDialog!!.getWindow()?.setBackgroundDrawable(
            AppCompatResources.getDrawable(
                this@CompletedGRNActivity,
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
    private fun setCreateBatchesDialog(poModel: CustomPoLineItemSelectionModel) {
        setInfoValues(poModel)

        createBatchedDialog!!.show()
    }

    private fun setInfoValues(poModel: CustomPoLineItemSelectionModel) {
        Log.e("poModelfrombatches", poModel.toString())
        createBatchesDialogBinding.grnAddHeader.tvPoNoValue.setText(poModel.poNumber)
        createBatchesDialogBinding.grnAddHeader.tvLineItemDescValue.setText(poModel.itemCode)
        createBatchesDialogBinding.grnAddHeader.tvItemDescValue.setText(poModel.itemDescription)
        createBatchesDialogBinding.grnAddHeader.tvPuomValue.setText(poModel.pouom)
        createBatchesDialogBinding.grnAddHeader.tvPoQtyValue.setText(poModel.poqty.toString())
        createBatchesDialogBinding.grnAddHeader.tvMhTypeValue.setText(poModel.mhType)
        createBatchesDialogBinding.grnAddHeader.tvBalanceQuantity.setText(poModel.balQTY.toString())

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
            createBatchesDialogBinding.grnAddHeader.tvQtyValue.setText(total.toString())
        } else {
            createBatchesDialogBinding.grnAddHeader.tvQtyValue.setText(poModel.quantityReceived)
        }
        balanceQty = poModel.balQTY.toString()
        totalQty = poModel.balQTY.toString()
    }

    private fun logout() {
        session.logoutUser()
        startActivity(Intent(this@CompletedGRNActivity, LoginActivity::class.java))
        finish()
    }

    //edit case update the values
    private fun callDefaultData() {
        if (grnId != 0) {
            getDraftGrn()
        }
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
                                } catch (e: Exception) {
                                    Toasty.warning(
                                        this@CompletedGRNActivity,
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
                                this@CompletedGRNActivity,
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
                            this@CompletedGRNActivity,
                            "Login failed - \nError Message: $errorMessage"
                        ).show()
                        session.showToastAndHandleErrors(errorMessage, this@CompletedGRNActivity)
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }
    }

    private fun getDraftGrn() {
        try {
            viewModel.getDraftGRN(this,token, baseUrl, grnId!!.toInt())
        } catch (e: Exception) {
            Toasty.error(
                this@CompletedGRNActivity,
                "Failed - \nError Message: $e"
            ).show()
        }
    }

    private fun selectePoDefaultRc() {
        selectPoBinding.rcPo.adapter = GRNSelectPoCompletedAdapter(getSuppliersPOsDDLResponse)
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
        selectPoBinding.rcPo.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        selectPoBinding.btnSubmit.setOnClickListener {
            callSelectedPoLineItems()
        }
    }

}

