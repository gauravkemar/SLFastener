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
import com.example.slfastener.databinding.CreateBatchesMultipleDialogBinding
import com.example.slfastener.databinding.CreateBatchesSingleDialogBinding
import com.example.slfastener.databinding.SelectLineItemDialogBinding
import com.example.slfastener.databinding.SelectSupplierPoLineItemBinding
import com.example.slfastener.helper.CustomArrayAdapter
import com.example.slfastener.helper.CustomKeyboard
import com.example.slfastener.helper.UsbCommunicationManager
import com.example.slfastener.model.GetActiveSuppliersDDLResponse
import com.example.slfastener.model.GetPOsAndLineItemsOnPOIdsResponse
import com.example.slfastener.model.GetSuppliersPOsDDLResponse
import com.example.slfastener.model.getalllocation.GetAllWareHouseLocationResponse
import com.example.slfastener.model.grn.GRNSaveToDraftDefaultRequest
import com.example.slfastener.model.grn.ProcessGRNLineItemsResponse
import com.example.slfastener.model.grndraftdata.GetDraftGrnResponse
import com.example.slfastener.model.grnmain.SubmitGRNRequest
import com.example.slfastener.model.offlinebatchsave.GrnLineItemUnitStore
import com.example.slfastener.model.offlinebatchsave.PoLineItemSelectionModelNewStore
import com.example.slfastener.model.polineitemnew.GRNUnitLineItemsSaveRequest
import com.example.slfastener.model.polineitemnew.GrnLineItemUnit
import com.example.slfastener.viewmodel.GRNTransactionViewModel
import com.example.slfastener.viewmodel.GRNTransactionViewModelProviderFactory

import es.dmoral.toasty.Toasty
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class GRNAddActivity : AppCompatActivity() {
    lateinit var binding: ActivityGrnaddBinding
    lateinit var spinnerItems: MutableList<String>
    private var selectedReasonSpinnerString: String? = ""
    private lateinit var viewModel: GRNTransactionViewModel
    private var grnMainItemAdapter: GrnMainAddAdapter? = null

    private var selectPoLineAdapter: SelectPoLineAdapter? = null
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
    lateinit var getAllLocation: MutableList<GetAllWareHouseLocationResponse>
    var currentPoLineItemPosition = ""
    lateinit var currentSelectedPoModel: PoLineItemSelectionModelNewStore

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
    var selectedDate: String = ""
    var edInvoiceNo: String = ""
    var selectedCurrency: String = ""
    var selectedKGRN: String = ""
    var balanceQty = ""
    var totalQty = ""
    var currentGrnID = 0
    var lineItemUnitId = 0
    var lineItemId = 0
    var grnId=0
    var previousBatchesQtyTotal=""

    private lateinit var enterBatchesDialogBinding: CreateBatchesSingleDialogBinding
    var batchesDialog: Dialog? = null

    private lateinit var createBatchesMultipleDialogBinding: CreateBatchesMultipleDialogBinding
    var multipleBatchesDialog: Dialog? = null

    ///get data from draft
    private var getDraftGrnResponse: GetDraftGrnResponse? = null
    private lateinit var customKeyboard: CustomKeyboard

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_grnadd)
        session = SessionManager(this)
        selectPoBinding = SelectSupplierPoLineItemBinding.inflate(getLayoutInflater());
        selectLineItemBinding = SelectLineItemDialogBinding.inflate(LayoutInflater.from(this))
        selectPoDialog = Dialog(this)

        enterBatchesDialogBinding =
            CreateBatchesSingleDialogBinding.inflate(LayoutInflater.from(this))
        createBatchesMultipleDialogBinding =CreateBatchesMultipleDialogBinding.inflate(LayoutInflater.from(this))


        batchesDialog = Dialog(this)
        multipleBatchesDialog = Dialog(this)

        setCreateBatchesDialog()
        setCreateBatchesMultipleDialog()
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
        poLineItem = ArrayList()
        selectedPoLineItem = ArrayList()
        getAllLocation = ArrayList()
        getPOsAndLineItemsOnPOIdsResponse = ArrayList()
        userDetails = session.getUserDetails()
        token = userDetails["jwtToken"].toString()

        val receivedIntent = intent
        grnId = receivedIntent.getIntExtra("GRNID",0)

        val SLFastenerRepository = SLFastenerRepository()
        val viewModelProviderFactory =
            GRNTransactionViewModelProviderFactory(application, SLFastenerRepository)
        viewModel =
            ViewModelProvider(this, viewModelProviderFactory)[GRNTransactionViewModel::class.java]
        if(grnId!=0)
        {
            callDefaultData()
        }
        getAllLocations()
        getSupplierList()

        binding.mcvAddGrn.setOnClickListener {
            processGrn()
        }
        binding.mcvSubmitGRN.setOnClickListener {
          submitGrn()
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
        ///grnMainItemAdapter = GrnMainAddAdapter(selectedPoLineItem)

/*        grnMainItemAdapter?.setOnItemCheckClickListener { position, poline ->
            createBatchesList.clear()
            if (poline?.grnLineItemUnit != null) {
                lineItemId = poline.grnLineItemUnit?.firstOrNull()?.lineItemId!!
            } else {
                lineItemId = 0
            }
            lineItemUnitId = 0
            for(grnLineUnit in poline.grnLineItemUnit!!)
            {
                createBatchesList.add(GrnLineItemUnitStore(grnLineUnit.UOM,
                    grnLineUnit.barcode,
                    grnLineUnit.expiryDate,
                    grnLineUnit.internalBatchNo,
                    grnLineUnit.isChecked,
                    grnLineUnit.lineItemId,
                    grnLineUnit.lineItemUnitId,
                    grnLineUnit.recevedQty,
                    grnLineUnit.supplierBatchNo,
                    grnLineUnit.isUpdate,
                    ))
            }

            //poline.grnLineItemUnit?.let { createBatchesList.addAll(it) }
            currentPoLineItemPosition = position.toString()
            setCreateBatchesDialog(poline)
            createBatchesMainRcAdapter!!.notifyDataSetChanged()
            //  addNewBatch(position,poline)
        }*/

        grnMainItemAdapter = GrnMainAddAdapter(this@GRNAddActivity,
                selectedPoLineItem,
                onItemCheck = { position, poline ->
                    createBatchesList.clear()
                    if (poline?.grnLineItemUnit != null) {
                        lineItemId = poline.grnLineItemUnit?.firstOrNull()?.lineItemId!!
                    } else {
                        lineItemId = 0
                    }
                    lineItemUnitId = 0
                    for(grnLineUnit in poline.grnLineItemUnit!!)
                    {
                        createBatchesList.add(GrnLineItemUnitStore(grnLineUnit.UOM,
                            poline.mhType,
                            grnLineUnit.barcode,
                            grnLineUnit.expiryDate,
                            grnLineUnit.internalBatchNo,
                            grnLineUnit.isChecked,
                            grnLineUnit.lineItemId,
                            grnLineUnit.lineItemUnitId,
                            grnLineUnit.recevedQty,
                            grnLineUnit.supplierBatchNo,
                            grnLineUnit.isUpdate,
                        ))
                    }

                    if(poline.GDPONumber!=null)
                    {
                        selectedPoLineItem.forEach {
                            if(it.poNumber == poline.poNumber && it.GDPONumber==null)
                            {
                                it.GDPONumber =poline.GDPONumber
                            }
                        }
                    }

                    //poline.grnLineItemUnit?.let { createBatchesList.addAll(it) }
                    currentPoLineItemPosition = position.toString()
                    setCreateBatchesDialog(poline)
                    createBatchesMainRcAdapter!!.notifyDataSetChanged()
                    //  addNewBatch(position,poline)
                },

            )
        binding.rcGrnAdd!!.adapter = grnMainItemAdapter
        binding.rcGrnAdd.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)


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
                                if (grnId != null) {
                                    selectePoDefaultRc()
                                } else {
                                    selectePoRc()
                                }
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

                    hideProgressBar()
                    response.data?.let { resultResponse ->
                        try {
                            edInvoiceNo = binding.edInvoiceNumber.text.toString().trim()
                            //edGdpoNo = binding.edGdpo.text.toString().trim()
                            if (resultResponse.size > 0) {

                                if (grnId != null) {
                                    getPOsAndLineItemsOnPOIdsResponse.addAll(resultResponse)
                                    var d = getDraftGrnResponse!!.grnTransaction
                                    var df = getDraftGrnResponse!!.grnTransaction.grnLineItems

                                    for (i in df) {
                                        var poID = i.poId
                                        val convertedGrnLineItemUnits =
                                            i.grnLineItemUnit.map { grnLineItemUnit ->
                                                GrnLineItemUnitStore(
                                                    grnLineItemUnit.uom,
                                                    i.mhType,
                                                    grnLineItemUnit.barcode,
                                                    grnLineItemUnit.expiryDate,
                                                    grnLineItemUnit.internalBatchNo,
                                                    true,
                                                    grnLineItemUnit.lineItemId,
                                                    grnLineItemUnit.lineItemUnitId,
                                                    grnLineItemUnit.qty.toString(),
                                                    grnLineItemUnit.supplierBatchNo,
                                                    true
                                                )
                                            }.toMutableList()
                                        Log.e(
                                            "editcaseDefaultList",
                                            convertedGrnLineItemUnits.toString() + "//////${df.size}"
                                        )
                                        Log.e("editcaseDefaultListdf ", df.toString())

                                        val existingItem =
                                            selectedPoLineItem.find { it.poId == poID }
                                        if (existingItem == null) {
                                            selectedPoLineItem.add(
                                                PoLineItemSelectionModelNewStore(
                                                    i.lineItemId,
                                                    i.balQTY, d.currency,
                                                    convertedGrnLineItemUnits,
                                                    i.itemCode,
                                                    i.itemDescription,
                                                    i.itemName,
                                                    i.mhType,
                                                    i.poId,
                                                    i.poLineItemId,
                                                    i.poLineNo,
                                                    i.poNumber,
                                                    i.poqty,
                                                    i.posapLineItemNumber,
                                                    i.pouom,
                                                    i.grnQty.toString(),
                                                    true,
                                                    i.gdpoNumber,
                                                    i.unitPrice,
                                                    getAllLocation,i.locationId
                                                )
                                            )
                                        }
                                    }

                                    grnMainItemAdapter!!.notifyDataSetChanged()
                                    for (r in resultResponse) {
                                        for (e in r.poLineItems) {
                                            val additionalValue =
                                                if (e.pouom != "KGS") "0" else "0.000"
                                            val unitPrice =
                                                e.unitPrice ?: "" // Provide a default value here
                                            // Check if poNumber from df matches poNumber from resultResponse
                                            val poNumberMatches =
                                                df.any { it.poNumber == r.poNumber && it.itemCode == e.itemCode }
                                            if (!poNumberMatches) {
                                                PoLineItemSelectionModelNewStore(
                                                    e.poLineItemId,
                                                    e.balQTY.toDouble(),
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
                                                    additionalValue,
                                                    false,
                                                    "",
                                                    unitPrice,getAllLocation,e.locationId
                                                ).also { poLineItem.add(it) }
                                                Log.e("selectPolIne", poLineItem.toString())
                                            }
                                        }
                                    }
                                } else {
                                    for (r in resultResponse) {
                                        for (e in r.poLineItems) {
                                            val additionalValue =
                                                if (e.pouom != "KGS") "0" else "0.000"
                                            val unitPrice =
                                                e.unitPrice ?: "" // Provide a default value here
                                            PoLineItemSelectionModelNewStore(
                                                0,
                                                e.balQTY.toDouble(),
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
                                                additionalValue,
                                                false,
                                                "",
                                                unitPrice,getAllLocation,e.locationId
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
                                currentGrnID = resultResponse.responseObject.grnId
                                Log.e("processkgrn", grnSaveToDraftDefaultResponse.toString())
                                Log.e("processkgrn", selectedKGRN.toString())
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
                    batchesDialog!!.dismiss()
                    response.data?.let { resultResponse ->
                        try {
                            Log.e("generatedBarcode", resultResponse.responseMessage.toString())
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
                            if(resultResponse.responseObject.grnLineItemUnit.get(0).lineItemUnitId!=null)
                            {
                                lineItemUnitId = resultResponse.responseObject.grnLineItemUnit.get(0).lineItemUnitId
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
                                    if (grnLineItem.barcode == barcodeToMatch) {
                                        grnLineItem.lineItemId = updatedLineItemId
                                        grnLineItem.lineItemUnitId = updatedLineItemUnitId
                                    }
                                }
                                createBatchesList.forEach { grnLineItem ->
                                    if (grnLineItem.barcode == barcodeToMatch) {
                                        grnLineItem.lineItemId = updatedLineItemId
                                        grnLineItem.lineItemUnitId = updatedLineItemUnitId
                                    }
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
        viewModel.submitGRNMutableResponse.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { resultResponse ->
                        try {
                            Toast.makeText(this,resultResponse.responseMessage.toString(),Toast.LENGTH_SHORT).show()
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
                    binding.clSelectPo.visibility = View.GONE
                    response.message?.let { errorMessage ->
                        Toasty.error(
                            this@GRNAddActivity,
                            "failed - \nError Message: $errorMessage"
                        ).show()
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }
        viewModel.deleteGRNLineItemsUnitMutableResponse.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { resultResponse ->
                        try {

                            balanceQty=resultResponse.responseObject.toString()
                            selectedPoLineItem[currentPoLineItemPosition.toInt()].balQTY=balanceQty.toDouble()
                            createBatchesDialogBinding.grnAddHeader.tvBalanceQuantity.setText(
                                selectedPoLineItem[currentPoLineItemPosition.toInt()].balQTY.toString()
                            )
                            var totalReceived=totalQty.toDouble()-balanceQty.toDouble()
                            var totalReceivedTotalFromList = createBatchesList.sumByDouble { it.recevedQty.toDouble() }
                            val balQtyFormat = String.format("%.3f", totalReceived)

                            selectedPoLineItem[currentPoLineItemPosition.toInt()].quantityReceived =balQtyFormat
                            val sumOfReceivedQtyIncludingUpdatedItem = createBatchesList.sumByDouble { it.recevedQty.toDouble() }
                            createBatchesDialogBinding.grnAddHeader.tvQtyValue.setText( sumOfReceivedQtyIncludingUpdatedItem.toString())

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
                            "failed - \nError Message: $errorMessage"
                        ).show()
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
                    hideProgressBar()
                    response.data?.let { resultResponse ->
                        try {
                            getAllLocation =  resultResponse
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
                            "failed - \nError Message: $errorMessage"
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
            enterBatchesDialogBinding.et1.setText("")
            createBatchedDialog!!.dismiss()
        }
        createBatchesDialogBinding.mcvCancel.setOnClickListener {
            enterBatchesDialogBinding.et1.setText("")
            createBatchedDialog!!.dismiss()
        }
        createBatchesDialogBinding.mcvClearBatchBarcode.setOnClickListener {
            enterBatchesDialogBinding.et1.setText("")
        }

        val keyboardLayout = layoutInflater.inflate(R.layout.custom_number_keyboard, null)
        customKeyboard = CustomKeyboard(this, keyboardLayout)

        // createBatchesMainRcAdapter = CreateBatchesSingleList(createBatchesList)
        /* createBatchesMainRcAdapter = CreateBatchesNewSingleList(
             createBatchesList,

             ) {
         }*/

        createBatchesMainRcAdapter =
            CreateBatchesNewSingleList(
                this@GRNAddActivity,
                createBatchesList,
                addItem = { newItem ->
                    generateBarcodeForBatchesForExisitng()
                    addExisitngNewItem(
                        newItem.supplierBatchNo,
                        currentPoLineItemPosition.toInt(),
                        selectedPoLineItem.get(currentPoLineItemPosition.toInt())
                    )
                },
                addMultiItem = { newItem ->
                    addMultipleBatches(newItem)


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
                onDelete = { position,grnitem ->
                    if(grnitem.isUpdate==true)
                    {
                        selectedPoLineItem[currentPoLineItemPosition.toInt()].grnLineItemUnit!!.removeAt(
                            position
                        )
                        deleteBatches(grnitem)
                    }
                    else{
                        selectedPoLineItem[currentPoLineItemPosition.toInt()].grnLineItemUnit!!.removeAt(
                            position
                        )
                    }

                },
                customKeyboard = customKeyboard,
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
        binding.mcvNewLineItem.setOnClickListener {
            setSelectLineItemDialog()
        }
    }


    private fun deleteBatches(grnitem: GrnLineItemUnitStore)
    {
        try {

            viewModel.deleteGRNLineItemsUnit(token,Constants.BASE_URL,grnitem.lineItemUnitId)
        }
        catch (e:Exception)
        {
            Toast.makeText(
                this,
                e.message.toString(),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    private fun getAllLocations()
    {
        try {

            viewModel.getAllLocations(token,Constants.BASE_URL,)
        }
        catch (e:Exception)
        {
            Toast.makeText(
                this,
                e.message.toString(),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun getBarcodeForMultipleBatches()
    {
        try{
            viewModel.getBarcodeForMultipleBatches(token,Constants.BASE_URL,"G")
        }
        catch (e:Exception)
        {

        }
    }

    private fun updateAfterMultipleEntries(
        newItem: GrnLineItemUnitStore,
        newBatch: GrnLineItemUnitStore
    )
    {
        val newBalanceQty = balanceQty.toDouble() - newItem.recevedQty.toDouble()
        val balQtyFormat = String.format("%.3f", newBalanceQty)
        selectedPoLineItem[currentPoLineItemPosition.toInt()].balQTY = balQtyFormat.toDouble()
        createBatchesDialogBinding.grnAddHeader.tvBalanceQuantity.setText(
            selectedPoLineItem[currentPoLineItemPosition.toInt()].balQTY.toString()
        )
        val sumOfReceivedQtyIncludingUpdatedItem =
            createBatchesList.sumByDouble { it.recevedQty.toDouble() }
        selectedPoLineItem[currentPoLineItemPosition.toInt()].quantityReceived =
            sumOfReceivedQtyIncludingUpdatedItem.toString()
        createBatchesDialogBinding.grnAddHeader.tvQtyValue.setText( sumOfReceivedQtyIncludingUpdatedItem.toString())
        addSingleGrnLineUnitItemApiCall(newBatch)
        createBatchesMainRcAdapter?.notifyItemInserted(createBatchesList.size - 1)
        grnMainItemAdapter?.notifyItemChanged(currentPoLineItemPosition.toInt())
    }

    ///can default addSame for numbers as 0.000
    private fun calculateForNumbers(
        tempList: MutableList<GrnLineItemUnitStore>,
        position: Int,
        updatedItem: GrnLineItemUnitStore
    ) {

        if (updatedItem.recevedQty != "0") {
            val sumOfReceivedQtyIncludingUpdatedItem =
                tempList.sumByDouble { it.recevedQty.toDouble() }
            Log.e(
                "receivedQty",
                "sum" + sumOfReceivedQtyIncludingUpdatedItem.toString() + "  ////   ${tempList.toString()}" + "  ${balanceQty.toDouble()}  "
            )
            if (sumOfReceivedQtyIncludingUpdatedItem > totalQty.toDouble()) {
                Toast.makeText(
                    this,
                    "Value must not exceed the Balance Qty.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {

                val previousReceivedQty = selectedPoLineItem[currentPoLineItemPosition.toInt()].grnLineItemUnit!![position].recevedQty.toDouble()
                val updatedReceivedQty = updatedItem.recevedQty.toDouble()

                if (updatedReceivedQty != previousReceivedQty)
                { // Only proceed if received quantity has changed
                    val receivedQtyDifference = updatedReceivedQty - previousReceivedQty
                    if (receivedQtyDifference > 0) { // Received quantity increased
                        if (receivedQtyDifference > balanceQty.toDouble()) {
                            // Show error message, quantity must not exceed the balance quantity
                        } else {
                            // Subtract the difference from the balance quantity
                            val newBalanceQty = balanceQty.toDouble() - receivedQtyDifference
                            val balQtyFormat = String.format("%.3f", newBalanceQty)

                            selectedPoLineItem[currentPoLineItemPosition.toInt()].balQTY = balQtyFormat.toDouble()

                            // Update received quantity for the item being modified
                            selectedPoLineItem[currentPoLineItemPosition.toInt()].grnLineItemUnit!![position] =
                                updatedItem.copy()
                            createBatchesDialogBinding.grnAddHeader.tvBalanceQuantity.setText(
                                selectedPoLineItem[currentPoLineItemPosition.toInt()].balQTY.toString()
                            )
                            createBatchesList[position] = updatedItem.copy()

                            // Update total received quantity for the PO line item
                            val sumOfReceivedQtyIncludingUpdatedItem =
                                createBatchesList.sumByDouble { it.recevedQty.toDouble() }
                            selectedPoLineItem[currentPoLineItemPosition.toInt()].quantityReceived =
                                sumOfReceivedQtyIncludingUpdatedItem.toString()

                            createBatchesDialogBinding.grnAddHeader.tvQtyValue.setText( sumOfReceivedQtyIncludingUpdatedItem.toString())

                            addSingleGrnLineUnitItemApiCall(updatedItem)
                            createBatchesMainRcAdapter!!.notifyItemChanged(position)
                            grnMainItemAdapter!!.notifyItemChanged(currentPoLineItemPosition.toInt())
                        }
                    } else { // Received quantity decreased
                        // Add the difference to the balance quantity
                        val newBalanceQty = balanceQty.toDouble() + (-receivedQtyDifference)
                        val balQtyFormat = String.format("%.3f", newBalanceQty)

                        selectedPoLineItem[currentPoLineItemPosition.toInt()].balQTY = balQtyFormat.toDouble()
                        // Update received quantity for the item being modified
                        selectedPoLineItem[currentPoLineItemPosition.toInt()].grnLineItemUnit!![position] =
                            updatedItem.copy()
                        createBatchesDialogBinding.grnAddHeader.tvBalanceQuantity.setText(
                            selectedPoLineItem[currentPoLineItemPosition.toInt()].balQTY.toString()
                        )
                        balanceQty = balQtyFormat
                        createBatchesList[position] = updatedItem.copy()
                        // Update total received quantity for the PO line item
                        val sumOfReceivedQtyIncludingUpdatedItem =
                            createBatchesList.sumByDouble { it.recevedQty.toDouble() }

                        selectedPoLineItem[currentPoLineItemPosition.toInt()].quantityReceived =
                            sumOfReceivedQtyIncludingUpdatedItem.toString()

                        createBatchesDialogBinding.grnAddHeader.tvQtyValue.setText(sumOfReceivedQtyIncludingUpdatedItem.toString())
                        addSingleGrnLineUnitItemApiCall(updatedItem)
                        createBatchesMainRcAdapter!!.notifyItemChanged(position)
                        grnMainItemAdapter!!.notifyItemChanged(currentPoLineItemPosition.toInt())
                    }
                }
                else {
                    // Show message indicating that quantity hasn't changed
                }
            }
        } else {
            Toast.makeText(
                this,
                "Value must not be 0.",
                Toast.LENGTH_SHORT
            ).show()
        }


        /*if (updatedItem.recevedQty != "0") {
            val sumOfReceivedQtyIncludingUpdatedItem =
                tempList.sumByDouble { it.recevedQty.toDouble() }
            Log.e(
                "receivedQty",
                "sum" + sumOfReceivedQtyIncludingUpdatedItem.toString() + "  ////   ${tempList.toString()}" + "  ${balanceQty.toDouble()}  "
            )
            if (sumOfReceivedQtyIncludingUpdatedItem > totalQty.toDouble()) {
                Toast.makeText(
                    this,
                    "Value must not exceed the Balance Qty.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val previousReceivedQty = tempList[position].recevedQty.toDouble()
                val updatedReceivedQty = updatedItem.recevedQty.toDouble()
                if (updatedReceivedQty != previousReceivedQty) { // Only proceed if received quantity has changed
                    val receivedQtyDifference = updatedReceivedQty - previousReceivedQty
                    if (receivedQtyDifference > balanceQty.toDouble()) {
                        // Show error message, quantity must not exceed the balance quantity
                    } else {
                        // Subtract the difference from the balance quantity
                        val newBalanceQty = balanceQty.toDouble() - receivedQtyDifference
                        val balQtyFormat = String.format("%.2f", newBalanceQty)

                        selectedPoLineItem[currentPoLineItemPosition.toInt()].balQTY = balQtyFormat.toDouble()
                        // Update received quantity for the item being modified
                        selectedPoLineItem[currentPoLineItemPosition.toInt()].grnLineItemUnit!![position] =
                            updatedItem.copy()

                        createBatchesDialogBinding.grnAddHeader.tvBalanceQuantity.setText(
                            selectedPoLineItem[currentPoLineItemPosition.toInt()].balQTY.toString()
                        )
                        createBatchesList[position] = updatedItem.copy()

                        // Update total received quantity for the PO line item
                        val sumOfReceivedQtyIncludingUpdatedItem =
                            createBatchesList.sumByDouble { it.recevedQty.toDouble() }

                        balanceQty = balQtyFormat

                        selectedPoLineItem[currentPoLineItemPosition.toInt()].quantityReceived =
                            sumOfReceivedQtyIncludingUpdatedItem.toString()

                        createBatchesDialogBinding.grnAddHeader.tvQtyValue.setText(
                            sumOfReceivedQtyIncludingUpdatedItem.toString()
                        )

                        addSingleGrnLineUnitItemApiCall(updatedItem)
                        createBatchesMainRcAdapter!!.notifyItemChanged(position)
                        grnMainItemAdapter!!.notifyItemChanged(currentPoLineItemPosition.toInt())

                    }
                } else {

                }
            }
        } else {
            Toast.makeText(
                this,
                "Value must not be 0.",
                Toast.LENGTH_SHORT
            ).show()
        }*/

   /*     if (updatedItem.recevedQty != "0") {
            val sumOfReceivedQtyIncludingUpdatedItem =
                tempList.sumByDouble { it.recevedQty.toDouble() }
            Log.e(
                "receivedQty",
                "sum" + sumOfReceivedQtyIncludingUpdatedItem.toString() + "  ////   ${tempList.toString()}" + "  ${balanceQty.toDouble()}  "
            )
            if (sumOfReceivedQtyIncludingUpdatedItem > totalQty.toDouble())
            {
                Toast.makeText(
                    this,
                    "Value must not exceed the Balance Qty.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else {
                selectedPoLineItem[currentPoLineItemPosition.toInt()].balQTY =
                    (balanceQty.toDouble() - updatedItem.recevedQty.toDouble()).toDouble()

                selectedPoLineItem[currentPoLineItemPosition.toInt()].grnLineItemUnit!![position] =
                    updatedItem.copy()

                createBatchesDialogBinding.grnAddHeader.tvBalanceQuantity.setText(
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].balQTY.toString()
                )

                createBatchesList[position] = updatedItem.copy()

                balanceQty = (balanceQty.toDouble() - updatedItem.recevedQty.toDouble()).toString()

                val sumOfReceivedQtyIncludingUpdatedItem =
                    createBatchesList.sumByDouble { it.recevedQty.toDouble() }


                selectedPoLineItem[currentPoLineItemPosition.toInt()].quantityReceived =
                    sumOfReceivedQtyIncludingUpdatedItem.toString()

                createBatchesDialogBinding.grnAddHeader.tvBalanceQuantity.setText(
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].balQTY.toString()
                )

                createBatchesDialogBinding.grnAddHeader.tvQtyValue.setText(
                    sumOfReceivedQtyIncludingUpdatedItem.toString()
                )

                addSingleGrnLineUnitItemApiCall(updatedItem)

                createBatchesMainRcAdapter!!.notifyItemChanged(position)

                grnMainItemAdapter!!.notifyItemChanged(currentPoLineItemPosition.toInt())

            }
        } else {
            Toast.makeText(
                this,
                "Value must not be 0.",
                Toast.LENGTH_SHORT
            ).show()
        }*/

    }
    private fun addMultipleBatches(
        newItem: GrnLineItemUnitStore,

    ) {
        multipleBatchesDialog!!.show()

        var edMultiTextNum=createBatchesMultipleDialogBinding.edMultipleBatch.text.toString().trim()

        createBatchesMultipleDialogBinding.btnSubmit.setOnClickListener {
            submitMultipleBatches(newItem,edMultiTextNum.toInt())
        }
    }

    private fun submitMultipleBatches(newItem: GrnLineItemUnitStore, edMultiTextNum: Int)
    {
        if(edMultiTextNum>20)
        {
            var currentItemMultipleList=(newItem.recevedQty.toDouble() * edMultiTextNum.toInt())
            var totalQtyAfterMultipleEntry= createBatchesList.sumByDouble { it.recevedQty.toDouble() } + currentItemMultipleList

            if(currentItemMultipleList>balanceQty.toDouble())
            {
                Toast.makeText(
                    this,
                    "Value must not exceed the Balance Qty.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else
            {
                val hasItemWithZeroReceivedQuantity = selectedPoLineItem.any {
                    it.grnLineItemUnit?.any { it.recevedQty == "0.000" }
                        ?: false
                }

                if (hasItemWithZeroReceivedQuantity) {
                    Toasty.warning(
                        this@GRNAddActivity,
                        "Please complete current transaction!!"
                    ).show()
                    Log.d("edBatchNo.isNotEmpty()", "sad")
                }
                else
                {
                    repeat(edMultiTextNum){
                        getBarcodeForMultipleBatches()
                    }

                }
            }
            viewModel.getBarcodeForMultipleBatchesResponse.observe(this)
            { response ->
                when (response) {
                    is Resource.Success -> {
                        hideProgressBar()
                        response.data?.let { resultResponse ->
                            try {
                                val newBarcode = resultResponse.responseMessage.toString()
                                Log.e("generatedBarcode", newBarcode)
                                if (newItem.supplierBatchNo.isNotEmpty()) {
                                    val newBatch = createMultipleBatches(currentSelectedPoModel, newItem, newBarcode)
                                    if (currentPoLineItemPosition.toInt() != RecyclerView.NO_POSITION) {
                                        val model = selectedPoLineItem[currentPoLineItemPosition.toInt()]
                                        // Initialize batchInfoListModel if it's null
                                        if (model.grnLineItemUnit == null) {
                                            model.grnLineItemUnit = mutableListOf()
                                        }
                                        // Add new batch to batchInfoListModel
                                        model.grnLineItemUnit!!.add(newBatch)
                                        createBatchesList.add(newBatch)
                                        updateAfterMultipleEntries(newItem,newBatch)
                                        Log.d("edBatchNo.isNotEmpty()", selectedPoLineItem.toString())
                                    }
                                    else
                                    {
                                        Log.d("edBatchNoElse.isNotEmpty()", selectedPoLineItem.toString())
                                    }
                                } else {
                                    Toast.makeText(
                                        this@GRNAddActivity,
                                        "Please enter batch no!!",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                            }
                            catch (e: Exception) {
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
        }
        else
        {
            Toasty.error(
                this@GRNAddActivity,
                "Must not exceed 20 times"
            ).show()
        }


    }



    private fun calculateWeightUpdate(
        tempList: MutableList<GrnLineItemUnitStore>,
        position: Int,
        updatedItem: GrnLineItemUnitStore,
    ) {

        if (updatedItem.recevedQty != "0.000") {
            val sumOfReceivedQtyIncludingUpdatedItem =
                tempList.sumByDouble { it.recevedQty.toDouble() }
            Log.e(
                "receivedQty",
                "sum" + sumOfReceivedQtyIncludingUpdatedItem.toString() + "  ////   ${tempList.toString()}" + "  ${balanceQty.toDouble()}  "
            )
            if (sumOfReceivedQtyIncludingUpdatedItem > totalQty.toDouble()) {
                Toast.makeText(
                    this,
                    "Value must not exceed the Balance Qty.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {

                val previousReceivedQty = selectedPoLineItem[currentPoLineItemPosition.toInt()].grnLineItemUnit!![position].recevedQty.toDouble()
                val updatedReceivedQty = updatedItem.recevedQty.toDouble()

                if (updatedReceivedQty != previousReceivedQty)
                { // Only proceed if received quantity has changed
                    val receivedQtyDifference = updatedReceivedQty - previousReceivedQty
                    if (receivedQtyDifference > 0) { // Received quantity increased
                        if (receivedQtyDifference > balanceQty.toDouble()) {
                            // Show error message, quantity must not exceed the balance quantity
                        } else {
                            // Subtract the difference from the balance quantity
                            val newBalanceQty = balanceQty.toDouble() - receivedQtyDifference
                            val balQtyFormat = String.format("%.3f", newBalanceQty)

                            selectedPoLineItem[currentPoLineItemPosition.toInt()].balQTY = balQtyFormat.toDouble()

                            // Update received quantity for the item being modified
                            selectedPoLineItem[currentPoLineItemPosition.toInt()].grnLineItemUnit!![position] =
                                updatedItem.copy()
                            createBatchesDialogBinding.grnAddHeader.tvBalanceQuantity.setText(
                                selectedPoLineItem[currentPoLineItemPosition.toInt()].balQTY.toString()
                            )
                            createBatchesList[position] = updatedItem.copy()
                            balanceQty = balQtyFormat
                            // Update total received quantity for the PO line item
                            val sumOfReceivedQtyIncludingUpdatedItem =
                                createBatchesList.sumByDouble { it.recevedQty.toDouble() }
                            selectedPoLineItem[currentPoLineItemPosition.toInt()].quantityReceived =
                                sumOfReceivedQtyIncludingUpdatedItem.toString()

                            createBatchesDialogBinding.grnAddHeader.tvQtyValue.setText( sumOfReceivedQtyIncludingUpdatedItem.toString())

                            addSingleGrnLineUnitItemApiCall(updatedItem)
                            createBatchesMainRcAdapter!!.notifyItemChanged(position)
                            grnMainItemAdapter!!.notifyItemChanged(currentPoLineItemPosition.toInt())
                        }
                    } else { // Received quantity decreased
                        // Add the difference to the balance quantity
                        val newBalanceQty = balanceQty.toDouble() + (-receivedQtyDifference)
                        val balQtyFormat = String.format("%.3f", newBalanceQty)

                        selectedPoLineItem[currentPoLineItemPosition.toInt()].balQTY = balQtyFormat.toDouble()
                        // Update received quantity for the item being modified
                        selectedPoLineItem[currentPoLineItemPosition.toInt()].grnLineItemUnit!![position] =
                            updatedItem.copy()
                        createBatchesDialogBinding.grnAddHeader.tvBalanceQuantity.setText(
                            selectedPoLineItem[currentPoLineItemPosition.toInt()].balQTY.toString()
                        )
                        balanceQty = balQtyFormat
                        createBatchesList[position] = updatedItem.copy()
                        // Update total received quantity for the PO line item
                        val sumOfReceivedQtyIncludingUpdatedItem =
                            createBatchesList.sumByDouble { it.recevedQty.toDouble() }

                        selectedPoLineItem[currentPoLineItemPosition.toInt()].quantityReceived =
                            sumOfReceivedQtyIncludingUpdatedItem.toString()

                        createBatchesDialogBinding.grnAddHeader.tvQtyValue.setText(sumOfReceivedQtyIncludingUpdatedItem.toString())
                        addSingleGrnLineUnitItemApiCall(updatedItem)
                        createBatchesMainRcAdapter!!.notifyItemChanged(position)
                        grnMainItemAdapter!!.notifyItemChanged(currentPoLineItemPosition.toInt())
                    }
                }
                else {
                    // Show message indicating that quantity hasn't changed
                }
            }
        } else {
            Toast.makeText(
                this,
                "Value must not be 0.",
                Toast.LENGTH_SHORT
            ).show()
        }



      /*  if (updatedItem.recevedQty != "0.000") {
            val sumOfReceivedQtyIncludingUpdatedItem =
                tempList.sumByDouble { it.recevedQty.toDouble() }
            Log.e(
                "receivedQty",
                "sum" + sumOfReceivedQtyIncludingUpdatedItem.toString() + "  ////   ${tempList.toString()}" + "  ${balanceQty.toDouble()}  "
            )
            if (sumOfReceivedQtyIncludingUpdatedItem > totalQty.toDouble())
            {
                Toast.makeText(
                    this,
                    "Value must not exceed the Balance Qty.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else {

                var  balQty=   (balanceQty.toDouble() - sumOfReceivedQtyIncludingUpdatedItem)
                val balQtyFormat = String.format("%.2f", balQty)

                selectedPoLineItem[currentPoLineItemPosition.toInt()].balQTY =balQtyFormat.toDouble()
                selectedPoLineItem[currentPoLineItemPosition.toInt()].grnLineItemUnit!![position] =
                    updatedItem.copy()
                createBatchesDialogBinding.grnAddHeader.tvBalanceQuantity.setText(
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].balQTY.toString()
                )
                createBatchesList[position] = updatedItem.copy()

                balanceQty = balQtyFormat

              *//*  val sumOfReceivedQtyIncludingUpdatedItem =
                    createBatchesList.sumByDouble { it.recevedQty.toDouble() }*//*

               val sumOfReceivedQtyIncludingUpdatedItem =
                    createBatchesList.sumByDouble { it.recevedQty.toDouble() }


                selectedPoLineItem[currentPoLineItemPosition.toInt()].quantityReceived =
                    sumOfReceivedQtyIncludingUpdatedItem.toString()

                createBatchesDialogBinding.grnAddHeader.tvBalanceQuantity.setText(
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].balQTY.toString()
                )
                createBatchesDialogBinding.grnAddHeader.tvQtyValue.setText(
                    sumOfReceivedQtyIncludingUpdatedItem.toString()
                )

                addSingleGrnLineUnitItemApiCall(updatedItem)
                createBatchesMainRcAdapter!!.notifyItemChanged(position)
                grnMainItemAdapter!!.notifyItemChanged(currentPoLineItemPosition.toInt())

            }
        } else {
            Toast.makeText(
                this,
                "Value must not be 0.",
                Toast.LENGTH_SHORT
            ).show()
        }*/

    }

    fun convertToGrams(amount: Double, unit: String): Double {
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
                val formattedDate =
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectDt)
                selectedDate = formattedDate.toString()
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

        if (grnId != null) {
            val defaultBpName = getDraftGrnResponse?.grnTransaction?.bpName
            if (!defaultBpName.isNullOrEmpty()) {

                val defaultPosition = supplierSpinnerArray.indexOf(defaultBpName)
                binding.tvSpinnerSupplier.setSelection(defaultPosition)
                callParentLocationApi(getDraftGrnResponse?.grnTransaction?.bpCode.toString())
                if (defaultPosition != -1) {
                    binding.tvSpinnerSupplier.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                adapterView: AdapterView<*>?,
                                view: View?,
                                i: Int,
                                l: Long
                            ) {
                                val selectedItem = adapterView?.selectedItem.toString()
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

        } else {
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
                                selectedBpId =
                                    getActiveSuppliersDDLResponse.find { it.code == selectedSupplierCode }!!.value.toString()
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
    }

    private fun setSelectLineItemDialog() {
        selectLineItemDialog!!.show()
        setLineItemRc()
        selectLineItemBinding.closeDialogueTopButton.setOnClickListener {
            selectLineItemDialog!!.dismiss()
        }
        selectLineItemBinding.mcvSubmit.setOnClickListener {
            setPoLineItemList()
            grnMainItemAdapter!!.notifyDataSetChanged()
            selectLineItemDialog!!.dismiss()
        }
    }

    private fun setLineItemRc() {
        if (selectedCurrency.equals("INR")) {
            selectLineItemBinding.lineItemHeader.tvColumnGDPO.visibility = View.GONE
        } else {
            selectLineItemBinding.lineItemHeader.tvColumnGDPO.visibility = View.VISIBLE
        }

     /*   selectLineItemBinding.rcLineItem.adapter = SelectPoLineAdapter(poLineItem) { item ->
            if (item.isSelected) {
                selectedPoLineItem.add(item)
                Log.d("selectedItems", selectedPoLineItem.toString())
            } else {
                selectedPoLineItem.remove(item)
                Log.d("selectedItems", selectedPoLineItem.toString())
            }
        }*/


        selectPoLineAdapter = SelectPoLineAdapter(
            poLineItem,
            onItemCheckedChange = { item ->
                if (item.isSelected) {
                    selectedPoLineItem.add(item)
                    Log.d("selectedItems", selectedPoLineItem.toString())
                } else {
                    selectedPoLineItem.remove(item)
                    Log.d("selectedItems", selectedPoLineItem.toString())
                }
            },
            onGdpoAdded = { updatedItem ->
                updateTheGDPOForSamePoNumber(updatedItem)
            },

            )
        selectLineItemBinding.rcLineItem.adapter =selectPoLineAdapter

        selectLineItemBinding.rcLineItem.layoutManager = LinearLayoutManager(this)
        selectLineItemBinding.rcLineItem!!.setHasFixedSize(true)
    }

    private fun updateTheGDPOForSamePoNumber(updatedItem: PoLineItemSelectionModelNewStore) {
        poLineItem.forEachIndexed { index, item ->
            if (item.poNumber == updatedItem.poNumber) {
                item.GDPONumber = updatedItem.GDPONumber
                selectPoLineAdapter?.notifyItemChanged(index)
            }
        }

        // Update selectedPoLineItem
        selectedPoLineItem.forEachIndexed { index, item ->

            if (item.poNumber == updatedItem.poNumber) {
                item.GDPONumber = updatedItem.GDPONumber
                grnMainItemAdapter?.notifyItemChanged(index,item)
            }
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

    private fun callSelectedPoLineItems() {
        try {
            for (s in getSuppliersPOsDDLResponse) {
                if (s.isChecked) {
                    if (!selectedPoFilteredList.contains(s.value)) {
                        selectedPoFilteredList.add(s.value);
                    }
                }
            }
            if (selectedPoFilteredList.size > 0) {
                binding.tvCurrencyType.setText(getCommonCode().toString())
                selectedCurrency = getCommonCode().toString()
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
            if (selectedDate != "") {
                val polist = selectedPoFilteredList.joinToString(separator = "|")
                if (polist.isNotEmpty()) {
                    if (selectedCurrency != "" && selectedCurrency.equals("INR")) {
                        viewModel.processGRN(
                            token, Constants.BASE_URL, GRNSaveToDraftDefaultRequest(
                                currentGrnID,
                                selectedSupplierCode,
                                polist,
                                selectedBpId.toInt(),
                                null,
                                "Draft",
                                selectedDate!!,
                                edInvoiceNo,
                                "Domestic",
                                selectedCurrency
                            )
                        )
                    } else {
                        viewModel.processGRN(
                            token, Constants.BASE_URL, GRNSaveToDraftDefaultRequest(
                                currentGrnID,
                                selectedSupplierCode,
                                polist,
                                selectedBpId.toInt(),
                                null,
                                "Draft",
                                selectedDate!!,
                                edInvoiceNo,
                                "Import",
                                selectedCurrency
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

    private fun generateBarcodeForBatches() {
        try {
            viewModel.getBarcodeValueWithPrefix(token, Constants.BASE_URL, "G")
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
            viewModel.getBarcodeValueWithPrefixForExisitng(token, Constants.BASE_URL, "G")
        } catch (e: Exception) {
            Toasty.error(
                this,
                e.message.toString(),
                Toasty.LENGTH_LONG
            ).show()
        }

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

    private fun setCreateBatchesDialog() {
        batchesDialog!!.setContentView(enterBatchesDialogBinding.root)
        batchesDialog!!.getWindow()?.setBackgroundDrawable(
            AppCompatResources.getDrawable(
                this@GRNAddActivity,
                android.R.color.transparent
            )
        )
        batchesDialog!!.getWindow()?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        batchesDialog!!.setCancelable(true)
        enterBatchesDialogBinding.closeDialogueTopButton.setOnClickListener {
            selectPoDialog!!.dismiss()
        }
    }
   private fun setCreateBatchesMultipleDialog() {
       multipleBatchesDialog!!.setContentView(enterBatchesDialogBinding.root)
       multipleBatchesDialog!!.getWindow()?.setBackgroundDrawable(
            AppCompatResources.getDrawable(
                this@GRNAddActivity,
                android.R.color.transparent
            )
        )
       multipleBatchesDialog!!.getWindow()?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
       multipleBatchesDialog!!.setCancelable(true)
       createBatchesMultipleDialogBinding.closeDialogueTopButton.setOnClickListener {
            selectPoDialog!!.dismiss()
        }
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
    private fun setCreateBatchesDialog(poModel: PoLineItemSelectionModelNewStore) {
        setInfoValues(poModel)
        createBatchesDialogBinding.mcvAddBatches.setOnClickListener {
            batchesDialog!!.show()
        }
        enterBatchesDialogBinding.closeDialogueTopButton.setOnClickListener {
            enterBatchesDialogBinding.et1.setText("")
            batchesDialog!!.dismiss()
        }
        enterBatchesDialogBinding.btnSubmit.setOnClickListener {
            currentSelectedPoModel = poModel
            generateBarcodeForBatches()
        }
        createBatchedDialog!!.show()
    }

    private fun setInfoValues(poModel: PoLineItemSelectionModelNewStore) {
        createBatchesDialogBinding.grnAddHeader.tvPoNoValue.setText(poModel.poNumber)
        createBatchesDialogBinding.grnAddHeader.tvGdPoNoValue.setText(poModel.GDPONumber.toString())
        createBatchesDialogBinding.grnAddHeader.tvLineItemDescValue.setText(poModel.itemCode)
        createBatchesDialogBinding.grnAddHeader.tvItemDescValue.setText(poModel.itemDescription)
        createBatchesDialogBinding.grnAddHeader.tvPuomValue.setText(poModel.pouom)
        createBatchesDialogBinding.grnAddHeader.tvPoQtyValue.setText(poModel.poqty.toString())
        createBatchesDialogBinding.grnAddHeader.tvMhTypeValue.setText(poModel.mhType)
        createBatchesDialogBinding.grnAddHeader.tvBalanceQuantity.setText(poModel.balQTY.toString())

        if (poModel.mhType.equals("Serial")) {
            createBatchesDialogBinding.tvDialogueTitle.setText("Create Serial")
        } else {
            createBatchesDialogBinding.tvDialogueTitle.setText("Create Batches")
        }

        if(poModel.grnLineItemUnit!=null)
        {
            var total=  poModel.grnLineItemUnit!!.sumByDouble {
                it.recevedQty.toDouble()
            }
            createBatchesDialogBinding.grnAddHeader.tvQtyValue.setText(total.toString())
        }
        else
        {
            createBatchesDialogBinding.grnAddHeader.tvQtyValue.setText(poModel.quantityReceived)
        }
        balanceQty = poModel.balQTY.toString()
        totalQty = poModel.balQTY.toString()
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

    private fun addSingleGrnLineUnitItemApiCall(u: GrnLineItemUnitStore) {
        try {
            viewModel.processSingleGRNGRNItemBatches(
                token, Constants.BASE_URL,
              /*  GRNUnitLineItemsSaveRequest(
                    lineItemId,
                    currentGrnID.toInt(),
                    false,
                    false,
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].balQTY,
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].currency ?: "",
                    listOf(
                        GrnLineItemUnit(
                            u.lineItemId,
                            u.lineItemUnitId,
                            u.barcode,
                            u.internalBatchNo,
                            false,
                            0,
                            u.recevedQty.toInt(),
                            u.supplierBatchNo, u.UOM, ""
                        )
                    ),
                    false,
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].itemCode,
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].itemDescription,
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].itemName,
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].mhType,
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].poId,
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].poLineItemId,
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].poLineNo,
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].poNumber,
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].poqty,
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].posapLineItemNumber,
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].pouom,
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].quantityReceived,
                    ""
                )*/

                //weight not proper
                GRNUnitLineItemsSaveRequest(
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].balQTY,
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].currency ?: "",
                    null,null,
                    currentGrnID.toInt(),
                    listOf(
                        GrnLineItemUnit(
                            u.lineItemId,
                            u.lineItemUnitId,u.UOM,
                            u.barcode,
                            false,
                            u.internalBatchNo,
                            false,
                            false,0,
                            u.recevedQty.toDouble(),
                            u.supplierBatchNo,
                            u.UOM,
                            ""
                        )
                    ),
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].quantityReceived.toDouble() ,
                    true,
                    false,
                    false,"",
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].itemCode,
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].itemDescription,
                    "",
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].itemName,
                    lineItemId,
                    0,
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].mhType,
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].poId,
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].poLineItemId,
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].poLineNo,
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].poNumber,
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].poqty,
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].posapLineItemNumber,
                    selectedPoLineItem[currentPoLineItemPosition.toInt()].pouom,
                    null
                    )
            )
        } catch (e: Exception) {
            Toast.makeText(this@GRNAddActivity, "${e.message}", Toast.LENGTH_SHORT)
                .show()
        }

    }
    private fun addBatchNewItem(
        position: Int,
        poModel: PoLineItemSelectionModelNewStore,
        newBarcode: String
    ) {
        val edBatchNo = enterBatchesDialogBinding.et1.text.toString().trim()
        if (edBatchNo.isNotEmpty()) {

            val hasItemWithZeroReceivedQuantity = poModel.grnLineItemUnit?.any {
                it.recevedQty == "0" || it.recevedQty == "0.000"
            } ?: false
            Log.d("hasItemWithZeroReceivedQuantity",poModel.toString())
            if (poModel.mhType.equals("Serial")) {
                if (hasItemWithZeroReceivedQuantity) {
                    Toasty.warning(this@GRNAddActivity, "Please complete current transaction!!")
                        .show()
                    Log.d("edBatchNo.isNotEmpty()", "sad")
                } else {
                    if (poModel.grnLineItemUnit != null) {
                        val batchExists =
                            poModel.grnLineItemUnit!!.any { it.supplierBatchNo == edBatchNo }
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
                                )
                                    .show()
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
            }
            else {
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
        poModel: PoLineItemSelectionModelNewStore
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
                                    it.grnLineItemUnit?.any { it.recevedQty.toString() == "0.000" }
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
                                        createBatchesMainRcAdapter?.notifyItemInserted(
                                            createBatchesList.size - 1
                                        )
                                        grnMainItemAdapter?.notifyItemChanged(position)
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

                        }
                        catch (e: Exception) {
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
    }

    private fun createBatchInfo(
        poModel: PoLineItemSelectionModelNewStore,
        edBatchNo: String,
        newBarcode: String
    ): GrnLineItemUnitStore {

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
        return GrnLineItemUnitStore(
            poModel.pouom,
            poModel.mhType,
            newBarcode,
            "",
            "$edBatchNo/$selectedKGRN",
            false,
            lineItemId, 0,
            additionalValue, edBatchNo, false
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
private fun createMultipleBatches(
    poModel: PoLineItemSelectionModelNewStore,
    edBatchNo: GrnLineItemUnitStore,
    newBarcode: String
    ): GrnLineItemUnitStore {

        return GrnLineItemUnitStore(
            poModel.pouom,
            poModel.mhType,
            newBarcode,
            "",
            "${edBatchNo.supplierBatchNo}/$selectedKGRN",
            false,
            lineItemId, 0,
            edBatchNo.recevedQty, edBatchNo.supplierBatchNo, true
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

    //edit case update the values
    private fun callDefaultData() {
        if (grnId != null) {
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
                                binding.tvDate.setText(getDraftGrnResponse!!.grnTransaction.invoiceDate)
                                selectedKGRN = getDraftGrnResponse!!.grnTransaction.kgrnNumber
                                currentGrnID=getDraftGrnResponse!!.grnTransaction.grnId
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
    }

    private fun getDraftGrn() {
        try {
            viewModel.getDraftGRN(token, Constants.BASE_URL, grnId!!.toInt())
        } catch (e: Exception) {
            Toasty.error(
                this@GRNAddActivity,
                "Failed - \nError Message: $e"
            ).show()
        }
    }

    private fun selectePoDefaultRc() {
        selectPoBinding.rcPo.adapter = GRNSelectPoAdapter(getSuppliersPOsDDLResponse)
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

    private fun gotoMainPage()
    {
        var intent = Intent(this, GRNMainActivity::class.java)
        startActivity(intent)
    }
    private fun submitGrn()
    {
        try {
            viewModel.submitGRN(token!!,Constants.BASE_URL, SubmitGRNRequest(grnId!!.toInt()))
        }
       catch (e:Exception)
       {
           Toasty.error(
               this@GRNAddActivity,
               "failed - \nError Message: $e"
           ).show()
       }
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