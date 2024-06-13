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
import android.widget.ArrayAdapter
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
import com.example.slfastener.adapter.gradapters.CreateBatchesForGRAdapter
import com.example.slfastener.adapter.gradapters.GRItemSelectionAdapter
import com.example.slfastener.adapter.gradapters.GrMainAddAdapter
import com.example.slfastener.databinding.ActivityGoodsReceiptBinding
import com.example.slfastener.databinding.CreateBatchesDialogBinding
import com.example.slfastener.databinding.CreateBatchesMultipleDialogBinding
import com.example.slfastener.databinding.CreateBatchesSingleDialogBinding
import com.example.slfastener.databinding.DescriptionInfoDialogBinding
import com.example.slfastener.databinding.SelectItemFromItemMasterDialogBinding
import com.example.slfastener.helper.CustomKeyboard
import com.example.slfastener.helper.weighing.UsbCommunicationManager
import com.example.slfastener.model.GetActiveSuppliersDDLResponse
import com.example.slfastener.model.getalllocation.GetAllWareHouseLocationResponse
import com.example.slfastener.model.goodsreceipt.GRLineItemUnit
import com.example.slfastener.model.goodsreceipt.GRLineUnitItemSelection
import com.example.slfastener.model.goodsreceipt.GetAllItemMasterSelection
import com.example.slfastener.model.goodsreceipt.PostProcessGRTransactionRequest
import com.example.slfastener.model.goodsreceipt.PostProcessGRTransactionResponse
import com.example.slfastener.model.goodsreceipt.ProcessGRLineItemRequest
import com.example.slfastener.model.goodsreceipt.SubmitGRRequest
import com.example.slfastener.model.goodsreceipt.grdraft.GetSingleGRByGRIdResponse
import com.example.slfastener.viewmodel.GRViewModel
import com.example.slfastener.viewmodel.GRViewModelFactory
import es.dmoral.toasty.Toasty

class GoodsReceiptActivity : AppCompatActivity() {
    lateinit var binding: ActivityGoodsReceiptBinding
    private lateinit var viewModel: GRViewModel

    //setSupplier default
    val supplierMap = HashMap<String, String>()
    var selectedSupplierCode: String = ""
    private var supplierSpinnerAdapter: ArrayAdapter<String>? = null
    private var isIntialSelectSupplier = true
    lateinit var supplierSpinnerArray: MutableList<String>
    lateinit var getActiveSuppliersDDLResponse: MutableList<GetActiveSuppliersDDLResponse>

    private var selectedBpId = ""
    private var selectedBpName = ""

    //get processGrData
    lateinit var grnSaveToDraftDefaultResponse: PostProcessGRTransactionResponse

    //session
    private lateinit var session: SessionManager
    private lateinit var userDetails: HashMap<String, Any?>
    var token: String = ""
    private lateinit var progress: ProgressDialog

    //getalllocation
    lateinit var getAllLocation: MutableList<GetAllWareHouseLocationResponse>

    //
    lateinit var lineItem: MutableList<GetAllItemMasterSelection>
    lateinit var selectedLineItem: MutableList<GetAllItemMasterSelection>
    private var grItemSelectionAdapter: GRItemSelectionAdapter? = null
    private var grMainAddAdapter: GrMainAddAdapter? = null

    //create batches
    lateinit var createBatchesList: MutableList<GRLineUnitItemSelection>
    private var createBatchesMainRcAdapter: CreateBatchesForGRAdapter? = null
    lateinit var createBatchesDialogBinding: CreateBatchesDialogBinding
    lateinit var createBatchedDialog: AppCompatDialog
    private lateinit var enterBatchesDialogBinding: CreateBatchesSingleDialogBinding
    var batchesDialog: Dialog? = null
    private lateinit var createBatchesMultipleDialogBinding: CreateBatchesMultipleDialogBinding
    var multipleBatchesDialog: Dialog? = null

    //select line item dialog
    private lateinit var selectLineItemBinding: SelectItemFromItemMasterDialogBinding
    var selectLineItemDialog: Dialog? = null

    //variables
    var lineItemUnitId = 0
    var lineItemId = 0
    var currentPoLineItemPosition = ""
    var deleteLineItem = 0
    var deleteLineItemPosition = ""
    var selectedKGRN: String = ""
    var balanceQty = ""
    var totalQty = ""
    var responsesReceived = 0
    var numberOfTimesToDublicate = 0
    var currentBatchTobeDublicated: GRLineUnitItemSelection? = null
    var multibarcodeMaintain = ""
    var deleteBatchUnitItemPosition = ""
    var grId = 0
    var currentGrID = 0
    var isGRNUpdate = false

    lateinit var currentSelectedPoModel: GetAllItemMasterSelection
    private var getDraftGRResponse: GetSingleGRByGRIdResponse? = null
    private lateinit var usbCommunicationManager: UsbCommunicationManager
    private lateinit var customKeyboard: CustomKeyboard
    private var lastUpdateTime: Long = 0
    private var previousData: String? = null
    private val DEBOUNCE_PERIOD = 1000L

    private lateinit var itemDescriptionBinding: DescriptionInfoDialogBinding
    var itemDescriptionDialog: Dialog? = null


    private var baseUrl: String =""
    private var serverIpSharedPrefText: String? = null
    private var serverHttpPrefText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_goods_receipt)
        session = SessionManager(this)
        userDetails = session.getUserDetails()
        progress = ProgressDialog(this)
        progress.setMessage("Loading...")
        token = userDetails["jwtToken"].toString()
        serverIpSharedPrefText = userDetails!![Constants.KEY_SERVER_IP].toString()
        serverHttpPrefText = userDetails!![Constants.KEY_HTTP].toString()
        baseUrl = "$serverHttpPrefText://$serverIpSharedPrefText/service/api/"

        selectLineItemBinding = SelectItemFromItemMasterDialogBinding.inflate(layoutInflater)
        selectLineItemDialog = AppCompatDialog(this).apply {
            setContentView(selectLineItemBinding.root)
            window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
            getWindow()?.setBackgroundDrawable(
                AppCompatResources.getDrawable(
                    this@GoodsReceiptActivity,
                    android.R.color.transparent
                )
            )
        }

        enterBatchesDialogBinding =
            CreateBatchesSingleDialogBinding.inflate(LayoutInflater.from(this))
        createBatchesMultipleDialogBinding = CreateBatchesMultipleDialogBinding.inflate(LayoutInflater.from(this))
        multipleBatchesDialog = Dialog(this)
        batchesDialog = Dialog(this)
        itemDescriptionBinding = DescriptionInfoDialogBinding.inflate(LayoutInflater.from(this))
        itemDescriptionDialog = Dialog(this)

        val SLFastenerRepository = SLFastenerRepository()
        val viewModelProviderFactory = GRViewModelFactory(application, SLFastenerRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory)[GRViewModel::class.java]
        supplierSpinnerArray = ArrayList()
        getActiveSuppliersDDLResponse = ArrayList()
        getAllLocation = ArrayList()
        lineItem = ArrayList()
        selectedLineItem = ArrayList()
        createBatchesList = ArrayList()
        getAllLocations()
        getSupplierList()
        setCreateBatchesDialog()
        setCreateBatchesMultipleDialog()
        setItemDescriptionDialog()
        val receivedIntent = intent
        grId = receivedIntent.getIntExtra("GRNID", 0)
        if (grId != 0) {
            currentGrID = grId
            callDefaultData()
            binding.mcvKGRNNo.visibility = View.VISIBLE
            binding.mcvNewLineItem.visibility = View.VISIBLE
            //binding.mcvAddGrn.visibility = View.GONE
        } else {
            binding.mcvKGRNNo.visibility = View.GONE

            //binding.mcvAddGrn.visibility = View.VISIBLE
        }

        //getAllItemMaster()

        ///get list of suppliers PO
        viewModel.getActiveSupplierForGRMutable.observe(this) { response ->
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
                                this@GoodsReceiptActivity,
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
                            this@GoodsReceiptActivity,
                            "Login failed - \nError Message: $errorMessage"
                        ).show()
                        session.showToastAndHandleErrors(errorMessage, this@GoodsReceiptActivity)
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
                            getAllLocation = resultResponse
                        } catch (e: Exception) {
                            Toasty.warning(
                                this@GoodsReceiptActivity,
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
                            this@GoodsReceiptActivity,
                            "failed - \nError Message: $errorMessage"
                        ).show()
                        session.showToastAndHandleErrors(errorMessage, this@GoodsReceiptActivity)
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }
        viewModel.processGRMutable.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { resultResponse ->
                        try {
                            grnSaveToDraftDefaultResponse = resultResponse
                            selectedKGRN = resultResponse.responseObject.kgrNumber
                            currentGrID = resultResponse.responseObject.grId
                            binding.mcvDraftItem.visibility = View.GONE
                            binding.mcvKGRNNo.visibility = View.VISIBLE
                            binding.mcvNewLineItem.visibility = View.VISIBLE
                            binding.edRemark.isEnabled = false
                            binding.tvSpinnerSupplier.isEnabled = false
                            binding.tvKGRNNo.setText(selectedKGRN.toString())
                            getAllItemMaster()

                        } catch (e: Exception) {
                            Toasty.warning(
                                this@GoodsReceiptActivity,
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
                            this@GoodsReceiptActivity,
                            "failed - \nError Message: $errorMessage"
                        ).show()
                        session.showToastAndHandleErrors(errorMessage, this@GoodsReceiptActivity)
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }
        viewModel.getAllItemMasterMutable.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { resultResponse ->
                        try {
                            if (resultResponse != null) {
                                if (grId != 0) {
                                    var findGrnLineItemSize = getDraftGRResponse!!.grLineItems
                                    if (isGRNUpdate) {
                                        for (r in resultResponse) {
                                            val additionalValue =
                                                if (r.uom != "KGS") "0" else "0.000"
                                            lineItem.add(
                                                GetAllItemMasterSelection(
                                                    currentGrID,
                                                    r.auom,
                                                    r.code,
                                                    r.defaultLocationCode,
                                                    0,
                                                    0,
                                                    r.description,
                                                    r.isExpirable,
                                                    r.isPurchasable,
                                                    r.isQCRequired,
                                                    r.isSalable,
                                                    r.itemGroup,
                                                    r.itemId,
                                                    r.mhType,
                                                    r.msq,
                                                    r.name,
                                                    r.uom,
                                                    r.uomRatio,
                                                    false,
                                                    "0.00",
                                                    null,
                                                    getAllLocation,
                                                    false
                                                )
                                            )
                                        }
                                        grItemSelectionAdapter!!.notifyDataSetChanged()
                                        grMainAddAdapter!!.notifyDataSetChanged()
                                    } else if (findGrnLineItemSize.size == 0) {
                                        for (r in resultResponse) {
                                            lineItem.add(
                                                GetAllItemMasterSelection(
                                                    currentGrID,
                                                    r.auom,
                                                    r.code,
                                                    r.defaultLocationCode,
                                                    0,
                                                    0,
                                                    r.description,
                                                    r.isExpirable,
                                                    r.isPurchasable,
                                                    r.isQCRequired,
                                                    r.isSalable,
                                                    r.itemGroup,
                                                    r.itemId,
                                                    r.mhType,
                                                    r.msq,
                                                    r.name,
                                                    r.uom,
                                                    r.uomRatio,
                                                    false,
                                                    "0.00",
                                                    null,
                                                    getAllLocation,
                                                    false
                                                )
                                            )
                                        }
                                        grItemSelectionAdapter!!.notifyDataSetChanged()
                                    }
                                    else {
                                        //getPOsAndLineItemsOnPOIdsResponse.addAll(resultResponse)
                                        var df = getDraftGRResponse!!.grLineItems
                                        for (i in df) {
                                            var itemCode = i.itemCode

                                            val convertedGrnLineItemUnits =
                                                i.grLineItemUnit.map { grLineItemUnit ->
                                                    var expiryDate = grLineItemUnit.expiryDate ?: "Empty"
                                                    GRLineUnitItemSelection(
                                                        grLineItemUnit.barcode,
                                                        grLineItemUnit.batchNo,
                                                        i.isExpirable,
                                                        expiryDate.toString(),
                                                        grLineItemUnit.internalBatchNo,
                                                        false,
                                                        false,
                                                        grLineItemUnit.lineItemId,
                                                        grLineItemUnit.lineItemUnitId,
                                                        grLineItemUnit.qty.toString(),
                                                        grLineItemUnit.uom,
                                                        i.mhType,
                                                        true,
                                                    )
                                                }.toMutableList()
                                            Log.e(
                                                "editcaseDefaultList",
                                                convertedGrnLineItemUnits.toString() + "//////${df.size}"
                                            )
                                            Log.e("editcaseDefaultListdf ", df.toString())
                                            val existingItem =
                                                selectedLineItem.find { it.code == itemCode }
                                            if (existingItem == null) {
                                                selectedLineItem.add(
                                                    GetAllItemMasterSelection(
                                                        grId,
                                                        i.uom,
                                                        i.itemCode,
                                                        i.defautLocation,
                                                        i.lineItemId,
                                                        i.locationId,
                                                        i.itemDescription,
                                                        i.isExpirable,
                                                        false,
                                                        i.isQCRequired,
                                                        false,
                                                        "",
                                                        0,
                                                        i.mhType,
                                                        0,
                                                        i.itemName,
                                                        i.uom,
                                                        0.00,
                                                        true,
                                                        i.qty.toString(),
                                                        convertedGrnLineItemUnits,
                                                        getAllLocation,
                                                        true
                                                    )
                                                )

                                            }
                                        }
                                        for (r in resultResponse) {
                                            val poNumberMatches =
                                                df.any { it.itemCode == r.code }
                                            if (!poNumberMatches) {
                                                lineItem.add(
                                                    GetAllItemMasterSelection(
                                                        grId,
                                                        r.auom,
                                                        r.code,
                                                        r.defaultLocationCode,
                                                        0,
                                                        0,
                                                        r.description,
                                                        r.isExpirable ,
                                                        r.isPurchasable,
                                                        r.isQCRequired,
                                                        r.isSalable,
                                                        r.itemGroup,
                                                        r.itemId,
                                                        r.mhType,
                                                        r.msq,
                                                        r.name,
                                                        r.uom,
                                                        r.uomRatio,
                                                        false,
                                                        "0.00",
                                                        null,
                                                        getAllLocation,
                                                        false
                                                    )
                                                )
                                            }
                                        }
                                        grMainAddAdapter!!.notifyDataSetChanged()
                                    }
                                }
                                else {
                                    for (r in resultResponse) {
                                        lineItem.add(
                                            GetAllItemMasterSelection(
                                                currentGrID,
                                                r.auom,
                                                r.code,
                                                r.defaultLocationCode,
                                                0,
                                                0,
                                                r.description,
                                                r.isExpirable,
                                                r.isPurchasable,
                                                r.isQCRequired,
                                                r.isSalable,
                                                r.itemGroup,
                                                r.itemId,
                                                r.mhType,
                                                r.msq,
                                                r.name,
                                                r.uom,
                                                r.uomRatio,
                                                false,
                                                "0.00",
                                                null,
                                                getAllLocation,
                                                false
                                            )
                                        )
                                    }
                                    grItemSelectionAdapter!!.notifyDataSetChanged()
                                }
                            }
                        } catch (e: Exception) {
                            Toasty.warning(
                                this@GoodsReceiptActivity,
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
                            this@GoodsReceiptActivity,
                            "failed - \nError Message: $errorMessage"
                        ).show()
                        session.showToastAndHandleErrors(errorMessage, this@GoodsReceiptActivity)
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }
        viewModel.processSingleGRItemSingleBatchesMutable.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { resultResponse ->
                        try {
                            if (lineItemId == 0) {
                                lineItemId = resultResponse.responseObject.lineItemId
                            }
                            if (resultResponse.responseObject.grLineItemUnit.get(0).lineItemUnitId != null) {
                                lineItemUnitId = resultResponse.responseObject.grLineItemUnit.get(0).lineItemUnitId
                            }
                            balanceQty = resultResponse.responseObject.qty.toString()
                            val receivedGrnLineItem = resultResponse.responseObject.grLineItemUnit.getOrNull(0)

                            if (receivedGrnLineItem != null) {
                                val barcodeToMatch = receivedGrnLineItem.barcode
                                val updatedLineItemId = receivedGrnLineItem.lineItemId
                                val updatedLineItemUnitId = receivedGrnLineItem.lineItemUnitId
                                // Update GrnLineItemUnitStore objects based on barcode matching
                                selectedLineItem[currentPoLineItemPosition.toInt()].grLineItemUnit!!.forEach { grnLineItem ->
                                    if (grnLineItem.Barcode == barcodeToMatch) {
                                        grnLineItem.LineItemId = updatedLineItemId
                                        grnLineItem.LineItemUnitId = updatedLineItemUnitId
                                    }
                                }

                                if (selectedLineItem[currentPoLineItemPosition.toInt()].LineItemId != lineItemId) {
                                    selectedLineItem[currentPoLineItemPosition.toInt()].LineItemId =
                                        lineItemId
                                }

                                createBatchesList.forEach { grnLineItem ->
                                    if (grnLineItem.Barcode == barcodeToMatch) {
                                        grnLineItem.LineItemId = updatedLineItemId
                                        grnLineItem.LineItemUnitId = updatedLineItemUnitId
                                    }
                                }
                            }
                            Log.e("createBatchesListfromresponse",createBatchesList.toString())

                        } catch (e: Exception) {
                            Toasty.warning(
                                this@GoodsReceiptActivity,
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
                            this@GoodsReceiptActivity,
                            "Login failed - \nError Message: $errorMessage"
                        ).show()
                        session.showToastAndHandleErrors(errorMessage, this@GoodsReceiptActivity)
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            } }
        viewModel.processSingleGRItemMultipleBatchesMutable.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { resultResponse ->
                        try {
                            if (lineItemId == 0) {
                                lineItemId = resultResponse.responseObject.lineItemId
                            }
                            if (resultResponse.responseObject.grLineItemUnit.get(0).lineItemUnitId != null) {
                                lineItemUnitId = resultResponse.responseObject.grLineItemUnit.get(0).lineItemUnitId
                            }
                            balanceQty = resultResponse.responseObject.qty.toString()
                            val receivedGrnLineItem = resultResponse.responseObject.grLineItemUnit.getOrNull(0)

                            if (receivedGrnLineItem != null) {
                                val barcodeToMatch = receivedGrnLineItem.barcode
                                val updatedLineItemId = receivedGrnLineItem.lineItemId
                                val updatedLineItemUnitId = receivedGrnLineItem.lineItemUnitId
                                // Update GrnLineItemUnitStore objects based on barcode matching
                                selectedLineItem[currentPoLineItemPosition.toInt()].grLineItemUnit!!.forEach { grnLineItem ->
                                    if (grnLineItem.Barcode == barcodeToMatch) {
                                        grnLineItem.LineItemId = updatedLineItemId
                                        grnLineItem.LineItemUnitId = updatedLineItemUnitId
                                    }
                                }

                                if (selectedLineItem[currentPoLineItemPosition.toInt()].LineItemId != lineItemId) {
                                    selectedLineItem[currentPoLineItemPosition.toInt()].LineItemId =
                                        lineItemId
                                }

                                createBatchesList.forEach { grnLineItem ->
                                    if (grnLineItem.Barcode == barcodeToMatch) {
                                        grnLineItem.LineItemId = updatedLineItemId
                                        grnLineItem.LineItemUnitId = updatedLineItemUnitId
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
                                this@GoodsReceiptActivity,
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
                            this@GoodsReceiptActivity,
                            "Login failed - \nError Message: $errorMessage"
                        ).show()
                        session.showToastAndHandleErrors(errorMessage, this@GoodsReceiptActivity)
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }
        viewModel.getBarcodeValueWithPrefixMutableResponse.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    batchesDialog!!.dismiss()
                    response.data?.let { resultResponse ->
                        try {
                            Log.e("generatedBarcode", resultResponse.responseMessage.toString())
                            currentSelectedPoModel =
                                selectedLineItem[currentPoLineItemPosition.toInt()]
                            addBatchNewItem(
                                currentPoLineItemPosition.toInt(),
                                currentSelectedPoModel,
                                resultResponse.responseMessage
                            )
                        } catch (e: Exception) {
                            Toasty.warning(
                                this@GoodsReceiptActivity,
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
                            this@GoodsReceiptActivity,
                            "Login failed - \nError Message: $errorMessage"
                        ).show()
                        session.showToastAndHandleErrors(errorMessage, this@GoodsReceiptActivity)
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }
        viewModel.getBarcodeForMultipleBatchesResponse.observe(this) { response ->
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
                                if (currentBatchTobeDublicated!!.BatchNo.isNotEmpty()) {
                                    currentSelectedPoModel =
                                        selectedLineItem[currentPoLineItemPosition.toInt()]
                                    val newBatch = createMultipleBatches(
                                        currentSelectedPoModel,
                                        currentBatchTobeDublicated!!,
                                        multibarcodeMaintain
                                    )
                                    if (currentPoLineItemPosition.toInt() != RecyclerView.NO_POSITION) {
                                        val model =
                                            selectedLineItem[currentPoLineItemPosition.toInt()]
                                        if (model.grLineItemUnit == null) {
                                            model.grLineItemUnit = mutableListOf()
                                        }
                                        model.grLineItemUnit!!.add(newBatch)
                                        createBatchesList.add(newBatch)
                                        updateAfterMultipleEntries(
                                            currentBatchTobeDublicated!!,
                                            newBatch
                                        )
                                        Log.d(
                                            "edBatchNo.isNotEmpty()",
                                            selectedLineItem.toString()
                                        )
                                    } else {
                                        Log.d(
                                            "edBatchNoElse.isNotEmpty()",
                                            selectedLineItem.toString()
                                        )
                                    }


                                } else {
                                    Toast.makeText(
                                        this@GoodsReceiptActivity,
                                        "Please enter batch no!!",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                            } else {

                            }
                        } catch (e: Exception) {
                            Toasty.warning(
                                this@GoodsReceiptActivity,
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
                            this@GoodsReceiptActivity,
                            "Login failed - \nError Message: $errorMessage"
                        ).show()
                        session.showToastAndHandleErrors(errorMessage, this@GoodsReceiptActivity)
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }
        viewModel.deleteGRLineItemsUnitMutableResponse.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { resultResponse ->
                        try {
                            selectedLineItem[currentPoLineItemPosition.toInt()].grLineItemUnit?.removeAt(
                                deleteBatchUnitItemPosition.toInt()
                            )
                            createBatchesList.removeAt(deleteBatchUnitItemPosition.toInt())
                            lineItemUnitId = 0
                            balanceQty = resultResponse.responseObject.toString()
                            selectedLineItem[currentPoLineItemPosition.toInt()].balQty =
                                balanceQty
                            createBatchesDialogBinding.grnAddHeader.tvBalanceQuantity.setText(
                                selectedLineItem[currentPoLineItemPosition.toInt()].balQty.toString()
                            )
                            //var totalReceived = totalQty.toDouble() - balanceQty.toDouble()

                            var totalReceivedTotalFromList = 0.000
                            if (createBatchesList != null) {
                                totalReceivedTotalFromList =
                                    createBatchesList.sumByDouble { it.Qty.toDouble() }
                            }
                            val sumOfReceivedQtyIncludingUpdatedItem =
                                createBatchesList.sumByDouble { it.Qty.toDouble() }
                            createBatchesDialogBinding.grnAddHeader.tvQtyValue.setText(
                                sumOfReceivedQtyIncludingUpdatedItem.toString()
                            )
                            createBatchesMainRcAdapter!!.notifyItemRemoved(
                                deleteBatchUnitItemPosition.toInt()
                            )
                            createBatchesMainRcAdapter!!.notifyItemRangeChanged(
                                deleteBatchUnitItemPosition.toInt(),
                                createBatchesList.size
                            )
                            grMainAddAdapter?.notifyItemChanged(currentPoLineItemPosition.toInt())
                        } catch (e: Exception) {
                            Toasty.warning(
                                this@GoodsReceiptActivity,
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
                            this@GoodsReceiptActivity,
                            "failed - \nError Message: $errorMessage"
                        ).show()
                        session.showToastAndHandleErrors(errorMessage, this@GoodsReceiptActivity)
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }
        viewModel.deleteGRLineUnitMutableResponse.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { resultResponse ->
                        try {
                            selectedLineItem.removeAt(deleteLineItemPosition.toInt())
                            grMainAddAdapter!!.notifyItemRemoved(deleteLineItemPosition.toInt())
                            grMainAddAdapter!!.notifyItemRangeChanged(
                                deleteLineItemPosition.toInt(),
                                selectedLineItem.size
                            )
                            lineItemId = 0
                            if (selectedLineItem.size == 0) {
                                if (grId != 0) {
                                    isGRNUpdate = true
                                    getAllItemMaster()
                                }
                            }

                        } catch (e: Exception) {
                            Toasty.warning(
                                this@GoodsReceiptActivity,
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
                            this@GoodsReceiptActivity,
                            "failed - \nError Message: $errorMessage"
                        ).show()
                        session.showToastAndHandleErrors(errorMessage, this@GoodsReceiptActivity)
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }
        viewModel.getSingleGRByGRIdMutableResponse.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { resultResponse ->
                        try {
                            if (resultResponse != null) {
                                getDraftGRResponse = resultResponse
                                Log.e("getDraftGRResponse", getDraftGRResponse!!.bpName.toString())
                                selectedKGRN = getDraftGRResponse!!.kgrNumber
                                binding.tvKGRNNo.setText(selectedKGRN.toString())
                                currentGrID = getDraftGRResponse!!.grId
                                binding.edRemark.isEnabled = false
                                binding.edRemark.setText(getDraftGRResponse!!.remark.toString())
                                getSupplierList()
                                getAllItemMaster()
                            }
                        } catch (e: Exception) {
                            Toasty.warning(
                                this@GoodsReceiptActivity,
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
                            this@GoodsReceiptActivity,
                            "Login failed - \nError Message: $errorMessage"
                        ).show()
                        session.showToastAndHandleErrors(errorMessage, this@GoodsReceiptActivity)
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }
        viewModel.submitGRMutableResponse.observe(this) { response ->
            when (response)
            {
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
                                this@GoodsReceiptActivity,
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
                            this@GoodsReceiptActivity,
                            "failed - \nError Message: $errorMessage"
                        ).show()
                        session.showToastAndHandleErrors(errorMessage, this@GoodsReceiptActivity)
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }
        binding.mcvDraftItem.setOnClickListener {
            processGrn()
        }
        binding.mcvCancel.setOnClickListener {
            onBackPressed()
        }
        grItemSelectionAdapter = GRItemSelectionAdapter(
            lineItem,
            onItemCheckedChange = { item ->
                if (item.isSelected) {
                    selectedLineItem.add(item)
                    Log.d("selectedItemsitemitem", item.toString())
                    Log.d("selectedItems", selectedLineItem.toString())
                } else {
                    selectedLineItem.remove(item)
                    Log.d("selectedItems", selectedLineItem.toString())
                }
            },
        )
        selectLineItemBinding.rcLineItem.adapter = grItemSelectionAdapter
        selectLineItemBinding.rcLineItem.layoutManager = LinearLayoutManager(this)
        selectLineItemBinding.rcLineItem!!.setHasFixedSize(true)
        binding.mcvNewLineItem.setOnClickListener {
            setSelectLineItemDialog()
        }
        selectLineItemBinding.lineItemHeader.ivSelection.setOnClickListener {
            lineItem.forEachIndexed { index, it ->
                it.isSelected = true
                selectedLineItem.add(it)
                grMainAddAdapter!!.notifyItemChanged(index)
            }
        }

        grMainAddAdapter = GrMainAddAdapter(this@GoodsReceiptActivity,
            selectedLineItem,
            itemDescription = {
                setItemDescription(it)
            },
            onItemCheck = { position, poline ->
                createBatchesList.clear()
                if (poline?.grLineItemUnit != null) {
                    lineItemId = poline.itemId
                    for (grnLineUnit in poline.grLineItemUnit!!) {
                        createBatchesList.add(grnLineUnit)
                    }
                } else {
                    lineItemId = 0
                }
                lineItemUnitId = 0
                currentPoLineItemPosition = position.toString()
                setCreateBatchesDialog(poline)
                createBatchesMainRcAdapter!!.notifyDataSetChanged()
                //  addNewBatch(position,poline)
            },
            onItemDelete = { position, grnitem ->
                if (grnitem.isUpdated == true) {
                    deleteLineItem = grnitem.LineItemId
                    deleteLineItemPosition = position.toString()
                    deleteLineItem(grnitem)
                } else {
                    lineItem.forEachIndexed { index, poLineItemSelectionModelNewStore ->
                        if (grnitem.itemId == poLineItemSelectionModelNewStore.itemId) {
                            poLineItemSelectionModelNewStore.isSelected = false
                            grItemSelectionAdapter?.notifyItemChanged(index)
                        }
                    }
                    selectedLineItem.removeAt(position)
                    grMainAddAdapter!!.notifyItemRemoved(position)
                    grMainAddAdapter!!.notifyItemRangeChanged(
                        position,
                        selectedLineItem.size
                    )

                }

            }
        )
        binding.rcGrAdd!!.adapter = grMainAddAdapter
        binding.rcGrAdd.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
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
            CreateBatchesForGRAdapter(
                this@GoodsReceiptActivity,
                createBatchesList,
                addItem = { newItem ->
                    generateBarcodeForBatchesForExisitng()
                    addExisitngNewItem(
                        newItem.BatchNo,
                        currentPoLineItemPosition.toInt(),
                        selectedLineItem.get(currentPoLineItemPosition.toInt())
                    )

                },
                addMultiItem = { newItem ->
                    currentBatchTobeDublicated = newItem
                    addMultipleBatches()
                },
                onSave = { position, updatedItem ->
                    if (selectedLineItem[currentPoLineItemPosition.toInt()].uom.equals("KGS")) {
                        calculateWeightUpdate(position, updatedItem)
                    } else {
                        calculateForNumbers(position, updatedItem)
                    }
                },
                onDelete = { position, grnitem ->
                    if (grnitem.isUpdate == true) {
                        deleteBatchUnitItemPosition = position.toString()
                        lineItemUnitId = grnitem.LineItemUnitId
                        Log.e("Lineitemunitid", lineItemUnitId.toString())
                        deleteBatches(grnitem)
                    } else {
                        selectedLineItem[currentPoLineItemPosition.toInt()].grLineItemUnit!!.removeAt(
                            position
                        )
                        grMainAddAdapter!!.notifyItemRemoved(currentPoLineItemPosition.toInt())
                        grMainAddAdapter!!.notifyItemRangeChanged(
                            currentPoLineItemPosition.toInt(),
                            selectedLineItem.size
                        )
                        createBatchesList.removeAt(position)
                        createBatchesMainRcAdapter!!.notifyItemRemoved(position)
                        createBatchesMainRcAdapter!!.notifyItemRangeChanged(
                            position,
                            createBatchesList.size
                        )
                    }

                },
                customKeyboard = customKeyboard,
            )
        createBatchesDialogBinding.rcBatchs.adapter = createBatchesMainRcAdapter
        createBatchesDialogBinding.rcBatchs.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        usbCommunicationManager = UsbCommunicationManager(this)
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
        binding.mcvSubmitGRN.setOnClickListener {
            submitGrn()
        }
    }

    private fun deleteLineItem(poLineItem: GetAllItemMasterSelection) {
        try {
            viewModel.deleteGRLineUnit(token,baseUrl, poLineItem.LineItemId)
        } catch (e: Exception) {
            Toast.makeText(
                this,
                e.message.toString(),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setCreateBatchesDialog() {
        batchesDialog!!.setContentView(enterBatchesDialogBinding.root)
        batchesDialog!!.getWindow()?.setBackgroundDrawable(
            AppCompatResources.getDrawable(
                this@GoodsReceiptActivity,
                android.R.color.transparent
            )
        )
        batchesDialog!!.getWindow()?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        batchesDialog!!.setCancelable(true)
        enterBatchesDialogBinding.closeDialogueTopButton.setOnClickListener {
            batchesDialog!!.dismiss()
        }
    }

    private fun setCreateBatchesMultipleDialog() {
        multipleBatchesDialog!!.setContentView(createBatchesMultipleDialogBinding.root)
        multipleBatchesDialog!!.getWindow()?.setBackgroundDrawable(
            AppCompatResources.getDrawable(
                this@GoodsReceiptActivity,
                android.R.color.transparent
            )
        )
        multipleBatchesDialog!!.getWindow()?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        multipleBatchesDialog!!.setCancelable(true)
        createBatchesMultipleDialogBinding.closeDialogueTopButton.setOnClickListener {
            multipleBatchesDialog!!.dismiss()
        }
    }

    private fun setItemDescriptionDialog() {
        itemDescriptionDialog!!.setContentView(itemDescriptionBinding.root)
        itemDescriptionDialog!!.getWindow()?.setBackgroundDrawable(
            AppCompatResources.getDrawable(
                this@GoodsReceiptActivity,
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


    private fun calculateWeightUpdate(
        position: Int,
        updatedItem: GRLineUnitItemSelection,
    ) {

        if (updatedItem.Qty != "0.000" && updatedItem.Qty != "") {
            val previousReceivedQty =
                selectedLineItem[currentPoLineItemPosition.toInt()].grLineItemUnit!![position].Qty.toDouble()
            val updatedReceivedQty = updatedItem.Qty.toDouble()
            if (updatedReceivedQty != previousReceivedQty) { // Only proceed if received quantity has changed
                createBatchesList[position] = updatedItem.copy()
                val sumOfReceivedQtyIncludingUpdatedItem =
                    createBatchesList.sumByDouble { it.Qty.toDouble() }
                selectedLineItem[currentPoLineItemPosition.toInt()].balQty =
                    sumOfReceivedQtyIncludingUpdatedItem.toString()
                selectedLineItem[currentPoLineItemPosition.toInt()].isUpdated = true
                // Update received quantity for the item being modified
                selectedLineItem[currentPoLineItemPosition.toInt()].grLineItemUnit!![position] =
                    updatedItem.copy()
                addSingleGrnLineUnitItemApiCall(updatedItem)
                createBatchesMainRcAdapter!!.notifyItemChanged(position)
                grMainAddAdapter!!.notifyItemChanged(currentPoLineItemPosition.toInt())


            } else {
                val previousExpiryDate =
                    selectedLineItem[currentPoLineItemPosition.toInt()].grLineItemUnit!![position].ExpiryDate
                if (!updatedItem?.ExpiryDate!!.equals(previousExpiryDate)) {
                    selectedLineItem[currentPoLineItemPosition.toInt()].grLineItemUnit!![position].ExpiryDate =
                        updatedItem?.ExpiryDate!!
                    createBatchesList[position] = updatedItem.copy()
                    addSingleGrnLineUnitItemApiCall(updatedItem)
                    createBatchesMainRcAdapter!!.notifyItemChanged(position)
                    grMainAddAdapter!!.notifyItemChanged(currentPoLineItemPosition.toInt())
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
        position: Int,
        updatedItem: GRLineUnitItemSelection
    ) {
        Log.e("updatedItem", updatedItem.toString())
        if (updatedItem.Qty != "0" && updatedItem.Qty != "") {
            val previousReceivedQty =
                selectedLineItem[currentPoLineItemPosition.toInt()].grLineItemUnit!![position].Qty.toDouble()
            val updatedReceivedQty = updatedItem.Qty.toDouble()
            Log.e(
                "updatedItem",
                updatedItem.toString() + "Previous--" + previousReceivedQty + "//" + "updatedvalue--" + updatedReceivedQty
            )
           // if (updatedReceivedQty != previousReceivedQty) {
                createBatchesList[position] = updatedItem.copy()
                val sumOfReceivedQtyIncludingUpdatedItem =
                    createBatchesList.sumByDouble { it.Qty.toDouble() }
                selectedLineItem[currentPoLineItemPosition.toInt()].balQty =
                    sumOfReceivedQtyIncludingUpdatedItem.toString()
                selectedLineItem[currentPoLineItemPosition.toInt()].isUpdated = true
                // Update received quantity for the item being modified
                selectedLineItem[currentPoLineItemPosition.toInt()].grLineItemUnit!![position] =
                    updatedItem.copy()
                createBatchesDialogBinding.grnAddHeader.tvQtyValue.setText(
                    sumOfReceivedQtyIncludingUpdatedItem.toString()
                )
                selectedLineItem[currentPoLineItemPosition.toInt()].grLineItemUnit!![position].ExpiryDate =
                    updatedItem?.ExpiryDate!!
                addSingleGrnLineUnitItemApiCall(updatedItem)
                createBatchesMainRcAdapter!!.notifyItemChanged(position)
                grMainAddAdapter!!.notifyItemChanged(currentPoLineItemPosition.toInt())
          /*  } else {
                val previousExpiryDate =
                    selectedLineItem[currentPoLineItemPosition.toInt()].grLineItemUnit!![position].ExpiryDate
                if (!updatedItem?.ExpiryDate!!.equals(previousExpiryDate)) {
                    selectedLineItem[currentPoLineItemPosition.toInt()].grLineItemUnit!![position].ExpiryDate =
                        updatedItem?.ExpiryDate!!
                    createBatchesList[position] = updatedItem.copy()
                    addSingleGrnLineUnitItemApiCall(updatedItem)
                    createBatchesMainRcAdapter!!.notifyItemChanged(position)
                    grMainAddAdapter!!.notifyItemChanged(currentPoLineItemPosition.toInt())
                }
            }*/
        } else {
            Toast.makeText(
                this,
                "Value must not be 0.",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    private fun addSingleGrnLineUnitItemApiCall(u: GRLineUnitItemSelection) {
        try {
            viewModel.processSingleGRItemSingleBatches(
                token, baseUrl,
                ProcessGRLineItemRequest(
                    selectedLineItem[currentPoLineItemPosition.toInt()].defaultLocationCode,
                    selectedLineItem[currentPoLineItemPosition.toInt()].grId,
                    listOf(
                        GRLineItemUnit(
                            u.Barcode,
                            u.BatchNo,
                            u.ExpiryDate,
                            u.InternalBatchNo,
                            false,
                            false,
                            u.LineItemId, u.LineItemUnitId,
                            u.Qty.toDouble(),
                            u.UOM
                        )
                    ),
                    selectedLineItem[currentPoLineItemPosition.toInt()].code,
                    selectedLineItem[currentPoLineItemPosition.toInt()].description,
                    selectedLineItem[currentPoLineItemPosition.toInt()].name,
                    selectedLineItem[currentPoLineItemPosition.toInt()].LineItemId,
                    selectedLineItem[currentPoLineItemPosition.toInt()].LocationId,
                    selectedLineItem[currentPoLineItemPosition.toInt()].mhType,
                    selectedLineItem[currentPoLineItemPosition.toInt()].balQty.toDouble(),
                    selectedLineItem[currentPoLineItemPosition.toInt()].uom,
                )
            )
        } catch (e: Exception) {
            Toast.makeText(this@GoodsReceiptActivity, "${e.message}", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun updateAfterMultipleEntries(
        newItem: GRLineUnitItemSelection,
        newBatch: GRLineUnitItemSelection,
    ) {
        val newBalanceQty = balanceQty.toDouble() - newItem.Qty.toDouble()
        val balQtyFormat = String.format("%.3f", newBalanceQty)
        val sumOfReceivedQtyIncludingUpdatedItem =
            createBatchesList.sumByDouble { it.Qty.toDouble() }
        selectedLineItem[currentPoLineItemPosition.toInt()].balQty = sumOfReceivedQtyIncludingUpdatedItem.toString()
        createBatchesDialogBinding.grnAddHeader.tvQtyValue.setText(selectedLineItem[currentPoLineItemPosition.toInt()].balQty)
        processSingleItemBatchesForMultiple(newBatch)
        createBatchesMainRcAdapter?.notifyItemInserted(createBatchesList.size - 1)
        grMainAddAdapter?.notifyItemChanged(currentPoLineItemPosition.toInt())

    }

    private fun setCreateBatchesDialog(poModel: GetAllItemMasterSelection) {
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

    private fun setInfoValues(poModel: GetAllItemMasterSelection) {
        Log.e("poModelfrombatches", poModel.toString())


        createBatchesDialogBinding.grnAddHeader.tvLineItemDescValue.setText(poModel.code)
        createBatchesDialogBinding.grnAddHeader.tvItemDescValue.setText(poModel.description)
        createBatchesDialogBinding.grnAddHeader.tvPuomValue.setText(poModel.uom)
        createBatchesDialogBinding.grnAddHeader.tvMhTypeValue.setText(poModel.mhType)
        createBatchesDialogBinding.grnAddHeader.tvQtyValue.setText(poModel.balQty)




        /*           createBatchesDialogBinding.grnAddHeader.tvPoNoValue.setText(poModel.poNumber)
                   createBatchesDialogBinding.grnAddHeader.tvGdPoNoValue.setText(poModel.GDPONumber.toString())
                   createBatchesDialogBinding.grnAddHeader.tvLineItemDescValue.setText(poModel.itemCode)
                   createBatchesDialogBinding.grnAddHeader.tvItemDescValue.setText(poModel.itemDescription)
                   createBatchesDialogBinding.grnAddHeader.tvPuomValue.setText(poModel.pouom)
                   createBatchesDialogBinding.grnAddHeader.tvPoQtyValue.setText(poModel.poqty.toString())
                   createBatchesDialogBinding.grnAddHeader.tvMhTypeValue.setText(poModel.mhType)
                   createBatchesDialogBinding.grnAddHeader.tvBalanceQuantity.setText(poModel.balQTY.toString())*/

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

        if (poModel.grLineItemUnit != null) {
            var total = poModel.grLineItemUnit!!.sumByDouble {
                it.Qty.toDouble()
            }
            createBatchesDialogBinding.grnAddHeader.tvQtyValue.setText(total.toString())
        } else {
            createBatchesDialogBinding.grnAddHeader.tvQtyValue.setText(poModel.balQty)
        }
        balanceQty = poModel.balQty.toString()
        totalQty = poModel.balQty.toString()
    }

    private fun setSelectLineItemDialog() {
        selectLineItemDialog!!.show()
        selectLineItemBinding.closeDialogueTopButton.setOnClickListener {
            selectLineItemDialog!!.dismiss()
        }
        selectLineItemBinding.mcvSubmit.setOnClickListener {
            grMainAddAdapter!!.notifyDataSetChanged()
            selectLineItemDialog!!.dismiss()
        }
    }

    private fun setSupplierSpinner() {
        val autoCompleteTextView = binding.tvSpinnerSupplier

        // Initialize ArrayAdapter and set it to the AutoCompleteTextView
        supplierSpinnerAdapter =
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, supplierSpinnerArray)
        autoCompleteTextView.setAdapter(supplierSpinnerAdapter)

        // Set threshold to 1 so that suggestions appear as soon as the user starts typing
        autoCompleteTextView.threshold = 1

        Log.e("getDraftGRResponse", getDraftGRResponse.toString())

        // If there is a default selection based on some condition
        if (grId != 0) {
            if (getDraftGRResponse != null) {
                val defaultBpName = getDraftGRResponse!!.bpName ?: ""
                Log.e("defaultBpName", defaultBpName)
                if (defaultBpName.isNotEmpty()) {
                    val defaultPosition = supplierSpinnerArray.indexOf(defaultBpName)
                    if (defaultPosition != -1) {
                        autoCompleteTextView.setText(defaultBpName, false)
                        autoCompleteTextView.setTextColor(Color.BLACK)
                        autoCompleteTextView.isEnabled = false
                    }
                }
            }
        }

        // Set listener to handle item selection
        autoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            Log.e("clickedDropdown", position.toString())
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
        Log.e("clickedDropdown", selectedItem.toString())
        // if (!isIntialSelectSupplier) {
        //if (selectedItem != "Select Supplier") {
        selectedBpName = selectedItem
        Log.e("supplierselected1", selectedBpName.toString())
        val selectedKey = supplierMap.entries.find { it.value == selectedItem }?.key
        if (selectedKey != null) {
            selectedSupplierCode = selectedKey
            val supplier =
                getActiveSuppliersDDLResponse.find { it.code == selectedSupplierCode }
            if (supplier != null) {
                selectedBpId = supplier.value.toString()
            }
            Log.e("supplierselected2", selectedBpName.toString())
        }
        Log.e("supplierselected3", selectedBpName.toString())
        //   } else {
        //      selectedSupplierCode = ""
        //  }
        //  }
        // isIntialSelectSupplier = false
    }

    private fun processGrn() {
        try {
            var edRemark = binding.edRemark.text.toString().trim()
            val bpId = if (selectedBpId != "") selectedBpId.toInt() else 0
            viewModel.processGR(
                token,
                baseUrl,
                PostProcessGRTransactionRequest(
                    selectedSupplierCode ?: "",
                    bpId,
                    selectedBpName ?: "",
                    0,
                    edRemark
                )
            )

        } catch (e: Exception) {
            Toasty.error(
                this,
                e.message.toString() + "askjdhasjhdsa",
                Toasty.LENGTH_LONG
            ).show()
        }
    }


    private fun getSupplierList() {
        try {
            viewModel.getActiveSupplierForGR(token, baseUrl)
        } catch (e: Exception) {
            Toasty.error(
                this,
                e.message.toString(),
                Toasty.LENGTH_LONG
            ).show()
        }
    }

    private fun getAllItemMaster() {
        try {
            try {
                viewModel.getAllItemMaster(token, baseUrl)
            } catch (e: Exception) {
                Toasty.error(
                    this,
                    e.message.toString(),
                    Toasty.LENGTH_LONG
                ).show()
            }

        } catch (e: Exception) {

        }
    }

    private fun generateBarcodeForBatches() {
        try {
            viewModel.getBarcodeValueWithPrefix(token, baseUrl, "G")
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
            viewModel.getAllLocations(token, baseUrl)
        } catch (e: Exception) {
            Toast.makeText(
                this,
                e.message.toString(),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun addMultipleBatches() {
        Log.e("multi","ashdasjd")
        multipleBatchesDialog!!.show()
        createBatchesMultipleDialogBinding.btnSubmit.setOnClickListener {
            var edMultiTextNum =
                createBatchesMultipleDialogBinding.edMultipleBatch.text.toString().trim()
            submitMultipleBatches(edMultiTextNum.toInt())
        }
    }

    private fun submitMultipleBatches(edMultiTextNum: Int) {
        Log.e("thisNum", edMultiTextNum.toString())

        if (edMultiTextNum < 20) {
            val hasItemWithZeroReceivedQuantity = selectedLineItem.any {
                it.grLineItemUnit?.any { it.Qty == "0.000" }
                    ?: false
            }
            if (hasItemWithZeroReceivedQuantity) {
                Toasty.warning(
                    this@GoodsReceiptActivity,
                    "Please complete current transaction!!"
                ).show()
                Log.d("edBatchNo.isNotEmpty()", "sad")
            } else {
                multipleBatchesDialog!!.dismiss()
                numberOfTimesToDublicate = edMultiTextNum
                getBarcodeForMultipleBatches()

            }
        } else {
            Toasty.error(
                this@GoodsReceiptActivity,
                "Must not exceed 20 times"
            ).show()
        }
    }

    private fun deleteBatches(grnitem: GRLineUnitItemSelection) {
        try {
            viewModel.deleteGRLineItemsUnit(token, baseUrl, grnitem.LineItemUnitId)
        } catch (e: Exception) {
            Toast.makeText(
                this,
                e.message.toString(),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun getBarcodeForMultipleBatches() {
        try {
            viewModel.getBarcodeForMultipleBatches(token, baseUrl, "G")
        } catch (e: Exception) {
            Toast.makeText(
                this,
                e.message.toString(),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun processSingleItemBatchesForMultiple(u: GRLineUnitItemSelection) {
        try {
            viewModel.processSingleGRItemMultipleBatches(
                token, baseUrl,
                ProcessGRLineItemRequest(
                    selectedLineItem[currentPoLineItemPosition.toInt()].defaultLocationCode,
                    selectedLineItem[currentPoLineItemPosition.toInt()].grId,
                    listOf(
                        GRLineItemUnit(
                            u.Barcode,
                            u.BatchNo,
                            u.ExpiryDate,
                            u.InternalBatchNo,
                            false,
                            false,
                            u.LineItemId, u.LineItemUnitId,
                            u.Qty.toDouble(),
                            u.UOM

                        )
                    ),
                    selectedLineItem[currentPoLineItemPosition.toInt()].code,
                    selectedLineItem[currentPoLineItemPosition.toInt()].description,
                    selectedLineItem[currentPoLineItemPosition.toInt()].name,
                    selectedLineItem[currentPoLineItemPosition.toInt()].LineItemId,
                    selectedLineItem[currentPoLineItemPosition.toInt()].LocationId,
                    selectedLineItem[currentPoLineItemPosition.toInt()].mhType,
                    selectedLineItem[currentPoLineItemPosition.toInt()].balQty.toDouble(),
                    selectedLineItem[currentPoLineItemPosition.toInt()].uom,
                )
            )
        } catch (e: Exception) {
            Toast.makeText(this@GoodsReceiptActivity, "${e.message}", Toast.LENGTH_SHORT)
                .show()
        }

    }

    private fun addBatchNewItem(
        position: Int,
        poModel: GetAllItemMasterSelection,
        newBarcode: String
    ) {
        val edBatchNo = enterBatchesDialogBinding.et1.text.toString().trim()
        if (edBatchNo.isNotEmpty()) {

            val hasItemWithZeroReceivedQuantity = poModel.grLineItemUnit?.any {
                it.Qty == "0" || it.Qty == "0.000"
            } ?: false
            Log.d("hasItemWithZeroReceivedQuantity", poModel.toString())
            if (poModel.mhType.equals("Serial")) {
                if (hasItemWithZeroReceivedQuantity) {
                    Toasty.warning(
                        this@GoodsReceiptActivity,
                        "Please complete current transaction!!"
                    )
                        .show()
                    Log.d("edBatchNo.isNotEmpty()", "sad")
                } else {
                    if (poModel.grLineItemUnit != null) {
                        val batchExists =
                            poModel.grLineItemUnit!!.any { it.BatchNo == edBatchNo }
                        if (!batchExists) {
                            if (newBarcode != "") {
                                val newBatch = createBatchInfo(poModel, edBatchNo, newBarcode)
                                if (position != RecyclerView.NO_POSITION) {
                                    val model = selectedLineItem[position]
                                    // Initialize batchInfoListModel if it's null
                                    if (model.grLineItemUnit == null) {
                                        model.grLineItemUnit = mutableListOf()
                                    }
                                    // Add new batch to batchInfoListModel
                                    model.grLineItemUnit!!.add(newBatch)
                                    createBatchesList.add(
                                        createBatchInfo(
                                            poModel,
                                            edBatchNo,
                                            newBarcode
                                        )
                                    )
                                    createBatchesMainRcAdapter?.notifyItemInserted(createBatchesList.size - 1)
                                    grMainAddAdapter?.notifyItemChanged(position)
                                    Log.d("edBatchNo.isNotEmpty()", selectedLineItem.toString())
                                } else {

                                }
                            } else {
                                Toast.makeText(
                                    this@GoodsReceiptActivity,
                                    "Batch No is Empty!!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                this@GoodsReceiptActivity,
                                "Batch already exists!!",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    } else {
                        if (newBarcode != "") {
                            val newBatch = createBatchInfo(poModel, edBatchNo, newBarcode)
                            if (position != RecyclerView.NO_POSITION) {
                                val model = selectedLineItem[position]
                                // Initialize batchInfoListModel if it's null
                                if (model.grLineItemUnit == null) {
                                    model.grLineItemUnit = mutableListOf()
                                }
                                // Add new batch to batchInfoListModel
                                model.grLineItemUnit!!.add(newBatch)
                                createBatchesList.add(
                                    createBatchInfo(
                                        poModel,
                                        edBatchNo,
                                        newBarcode
                                    )
                                )
                                createBatchesMainRcAdapter?.notifyItemInserted(createBatchesList.size - 1)
                                grMainAddAdapter?.notifyItemChanged(position)
                                Log.d("edBatchNo.isNotEmpty()", selectedLineItem.toString())
                            } else {

                            }
                        } else {
                            Toast.makeText(
                                this@GoodsReceiptActivity,
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
                    Toasty.warning(
                        this@GoodsReceiptActivity,
                        "Please complete current transaction!!"
                    )
                        .show()
                    Log.d("edBatchNo.isNotEmpty()", "sad")
                } else {
                    if (newBarcode != "") {
                        val newBatch = createBatchInfo(poModel, edBatchNo, newBarcode)
                        if (position != RecyclerView.NO_POSITION) {
                            val model = selectedLineItem[position]
                            if (model.grLineItemUnit == null) {
                                model.grLineItemUnit = mutableListOf()
                            }
                            model.grLineItemUnit!!.add(newBatch)
                            createBatchesList.add(createBatchInfo(poModel, edBatchNo, newBarcode))
                            createBatchesMainRcAdapter?.notifyItemInserted(createBatchesList.size - 1)
                            grMainAddAdapter?.notifyItemChanged(position)
                            Log.d("edBatchNo.isNotEmpty()", selectedLineItem.toString())
                        } else {

                        }
                    } else {
                        Toast.makeText(
                            this@GoodsReceiptActivity,
                            "Barcode is null!!",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
            }
        } else {
            Toast.makeText(this@GoodsReceiptActivity, "Please enter batch no!!", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun addExisitngNewItem(
        edBatchNo: String,
        position: Int,
        poModel: GetAllItemMasterSelection,
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
                                val hasItemWithZeroReceivedQuantity = selectedLineItem.any {
                                    it.grLineItemUnit?.any { it.Qty.toString() == "0.000" || it.Qty == "0" }
                                        ?: false
                                }
                                if (hasItemWithZeroReceivedQuantity) {
                                    Toasty.warning(
                                        this@GoodsReceiptActivity,
                                        "Please complete current transaction!!"
                                    ).show()
                                    Log.d("edBatchNo.isNotEmpty()", "sad")
                                } else {
                                    val newBatch = createBatchInfo(poModel, edBatchNo, newBarcode)
                                    if (position != RecyclerView.NO_POSITION) {

                                        val barcodeExistsInParent =
                                            selectedLineItem[currentPoLineItemPosition.toInt()].grLineItemUnit!!.any { it.Barcode == newBarcode }

                                        if (!barcodeExistsInParent) {
                                            val model = selectedLineItem[position]
                                            // Initialize batchInfoListModel if it's null
                                            if (model.grLineItemUnit == null) {
                                                model.grLineItemUnit = mutableListOf()
                                            }
                                            // Add new batch to batchInfoListModel
                                            model.grLineItemUnit!!.add(newBatch)
                                            grMainAddAdapter?.notifyItemChanged(
                                                currentPoLineItemPosition.toInt()
                                            )
                                        }
                                        val barcodeExistsInBatches =
                                            createBatchesList!!.any { it.Barcode == newBarcode }
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
                                            selectedLineItem.toString()
                                        )
                                    } else {

                                    }
                                }
                            } else {
                                Toast.makeText(
                                    this@GoodsReceiptActivity,
                                    "Please enter batch no!!",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }

                        } catch (e: Exception) {
                            Toasty.warning(
                                this@GoodsReceiptActivity,
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
                            this@GoodsReceiptActivity,
                            "Login failed - \nError Message: $errorMessage"
                        ).show()
                        session.showToastAndHandleErrors(errorMessage, this@GoodsReceiptActivity)
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }
    }

    private fun createBatchInfo(
        poModel: GetAllItemMasterSelection,
        edBatchNo: String,
        newBarcode: String
    ): GRLineUnitItemSelection {

        val additionalValue = if (poModel.uom != "KGS") "0" else "0.000"
        return GRLineUnitItemSelection(
            newBarcode,
            edBatchNo,
            poModel.isExpirable,
            "",
            "$edBatchNo/$selectedKGRN",
            false, false, lineItemId,
            0,
            additionalValue,
            poModel.uom, poModel.mhType,
            false
        )
    }

    private fun createMultipleBatches(
        poModel: GetAllItemMasterSelection,
        edBatchNo: GRLineUnitItemSelection,
        newBarcode: String
    ): GRLineUnitItemSelection {

        return GRLineUnitItemSelection(
            newBarcode,
            edBatchNo.BatchNo,
            poModel.isExpirable,
            edBatchNo.ExpiryDate.toString(),
            "${edBatchNo.BatchNo}/$selectedKGRN",
            false, false, lineItemId, 0, edBatchNo.Qty,
            poModel.uom,
            poModel.mhType,
            true,
        )

    }

    private fun generateBarcodeForBatchesForExisitng() {
        try {
            viewModel.getBarcodeValueWithPrefixForExisitng(token, baseUrl, "G")
        } catch (e: Exception) {
            Toasty.error(
                this,
                e.message.toString(),
                Toasty.LENGTH_LONG
            ).show()
        }

    }

    //edit case update the values
    private fun callDefaultData() {
        if (grId != 0) {
            getDraftGr()
        }

    }

    private fun getDraftGr() {
        try {
            viewModel.getSingleGRByGRId(token, baseUrl, grId!!.toInt())
        } catch (e: Exception) {
            Toasty.error(
                this@GoodsReceiptActivity,
                "Failed - \nError Message: $e"
            ).show()
        }
    }


    private fun gotoMainPage() {
        var intent = Intent(this, GRNMainActivity::class.java)
        startActivity(intent)
    }

    private fun submitGrn() {
        try {
            viewModel.submitGR(token!!, baseUrl, SubmitGRRequest(currentGrID))
        } catch (e: Exception) {
            Toasty.error(
                this@GoodsReceiptActivity,
                "failed - \nError Message: $e"
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

}