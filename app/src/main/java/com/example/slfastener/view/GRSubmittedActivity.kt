package com.example.slfastener.view

import android.app.Dialog
import android.app.ProgressDialog
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
import com.example.demorfidapp.helper.Constants
import com.example.demorfidapp.helper.Resource
import com.example.demorfidapp.helper.SessionManager
import com.example.demorfidapp.repository.SLFastenerRepository
import com.example.slfastener.R
import com.example.slfastener.adapter.completedgrn.CreateBatchesCompletedAdapter
import com.example.slfastener.adapter.gradapters.CreateBatchesForGRAdapter
import com.example.slfastener.adapter.gradapters.GRItemSelectionAdapter
import com.example.slfastener.adapter.gradapters.GrMainAddAdapter
import com.example.slfastener.adapter.gradapters.completedGr.CreateBatchesGRCompletedAdapter
import com.example.slfastener.adapter.gradapters.completedGr.GRMainAddCompletedAdapter
import com.example.slfastener.databinding.ActivityGrsubmittedBinding
import com.example.slfastener.databinding.CompletedBatchesDialogBinding
import com.example.slfastener.databinding.CreateBatchesDialogBinding
import com.example.slfastener.databinding.DescriptionInfoDialogBinding
import com.example.slfastener.databinding.SelectItemFromItemMasterDialogBinding
import com.example.slfastener.model.GetActiveSuppliersDDLResponse
import com.example.slfastener.model.getalllocation.GetAllWareHouseLocationResponse
import com.example.slfastener.model.goodsreceipt.GRLineUnitItemSelection
import com.example.slfastener.model.goodsreceipt.GetAllItemMasterSelection
import com.example.slfastener.model.goodsreceipt.grdraft.GetSingleGRByGRIdResponse
import com.example.slfastener.viewmodel.GRViewModel
import com.example.slfastener.viewmodel.GRViewModelFactory
import es.dmoral.toasty.Toasty

class GRSubmittedActivity : AppCompatActivity() {
    lateinit var binding:ActivityGrsubmittedBinding
    private lateinit var viewModel: GRViewModel
    //session
    private lateinit var session: SessionManager
    private lateinit var userDetails: HashMap<String, Any?>
    var token: String = ""
    private lateinit var progress: ProgressDialog


    //getalllocation
    lateinit var getAllLocation: MutableList<GetAllWareHouseLocationResponse>

    ///supplier
    var selectedSupplierCode: String = ""
    private var supplierSpinnerAdapter: ArrayAdapter<String>? = null
    val supplierMap = HashMap<String, String>()
    lateinit var supplierSpinnerArray: MutableList<String>
    lateinit var getActiveSuppliersDDLResponse: MutableList<GetActiveSuppliersDDLResponse>

    //draft
    private var getDraftGRResponse: GetSingleGRByGRIdResponse? = null

//line item
    lateinit var lineItem: MutableList<GetAllItemMasterSelection>
    lateinit var selectedLineItem: MutableList<GetAllItemMasterSelection>
    private var grMainAddAdapter: GRMainAddCompletedAdapter? = null

    //create batches
    lateinit var createBatchesList: MutableList<GRLineUnitItemSelection>
    private var createBatchesMainRcAdapter: CreateBatchesGRCompletedAdapter? = null

    lateinit var createBatchesDialogBinding: CompletedBatchesDialogBinding
    lateinit var createBatchedDialog: AppCompatDialog


    //variable
    var grId = 0
    var currentGrID = 0
    var lineItemUnitId = 0
    var lineItemId = 0
    var selectedKGRN: String = ""
    private var selectedBpId = ""
    private var selectedBpName = ""
    var currentPoLineItemPosition = ""

    private lateinit var itemDescriptionBinding: DescriptionInfoDialogBinding
    var itemDescriptionDialog: Dialog? = null

    private var baseUrl: String =""
    private var serverIpSharedPrefText: String? = null
    private var serverHttpPrefText: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_grsubmitted)
        val SLFastenerRepository = SLFastenerRepository()
        val viewModelProviderFactory = GRViewModelFactory(application, SLFastenerRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory)[GRViewModel::class.java]
        getAllLocation = ArrayList()
        supplierSpinnerArray = ArrayList()
        getActiveSuppliersDDLResponse = ArrayList()
        selectedLineItem = ArrayList()
        lineItem = ArrayList()
        createBatchesList = ArrayList()
        session = SessionManager(this)
        userDetails = session.getUserDetails()
        token = userDetails["jwtToken"].toString()
        serverIpSharedPrefText = userDetails!![Constants.KEY_SERVER_IP].toString()
        serverHttpPrefText = userDetails!![Constants.KEY_HTTP].toString()
        baseUrl = "$serverHttpPrefText://$serverIpSharedPrefText/service/api/"
        itemDescriptionBinding = DescriptionInfoDialogBinding.inflate(LayoutInflater.from(this))
        itemDescriptionDialog = Dialog(this)
        progress = ProgressDialog(this)
        progress.setMessage("Loading...")
        getAllLocations()
        getSupplierList()
        setItemDescriptionDialog()
        val receivedIntent = intent
        grId = receivedIntent.getIntExtra("GRNID", 0)
        if (grId != 0) {
            currentGrID = grId
            callDefaultData()
            binding.mcvKGRNNo.visibility = View.VISIBLE
          //  binding.mcvNewLineItem.visibility = View.VISIBLE
        } else
        {
            binding.mcvKGRNNo.visibility = View.GONE

            //binding.mcvAddGrn.visibility = View.VISIBLE
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
                                this@GRSubmittedActivity,
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
                            this@GRSubmittedActivity,
                            "failed - \nError Message: $errorMessage"
                        ).show()
                        session.showToastAndHandleErrors(errorMessage, this@GRSubmittedActivity)
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }
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
                                this@GRSubmittedActivity,
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
                            this@GRSubmittedActivity,
                            "Login failed - \nError Message: $errorMessage"
                        ).show()
                        session.showToastAndHandleErrors(errorMessage, this@GRSubmittedActivity)
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
                                this@GRSubmittedActivity,
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
                            this@GRSubmittedActivity,
                            "Login failed - \nError Message: $errorMessage"
                        ).show()
                        session.showToastAndHandleErrors(errorMessage, this@GRSubmittedActivity)
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
                                                        false,
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
                                                        false,
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
                                        grMainAddAdapter!!.notifyDataSetChanged()

                                }
                            }
                        } catch (e: Exception) {
                            Toasty.warning(
                                this@GRSubmittedActivity,
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
                            this@GRSubmittedActivity,
                            "failed - \nError Message: $errorMessage"
                        ).show()
                        session.showToastAndHandleErrors(errorMessage, this@GRSubmittedActivity)
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }

        grMainAddAdapter = GRMainAddCompletedAdapter(this@GRSubmittedActivity,
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
            }
        )
        binding.rcGrAdd!!.adapter = grMainAddAdapter
        binding.rcGrAdd.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)


        createBatchesDialogBinding = CompletedBatchesDialogBinding.inflate(LayoutInflater.from(this));
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
        createBatchesMainRcAdapter =
            CreateBatchesGRCompletedAdapter(this@GRSubmittedActivity,
                createBatchesList,
                onSave = { position, updatedItem -> },
            )
        createBatchesDialogBinding.rcBatchs.adapter = createBatchesMainRcAdapter
        createBatchesDialogBinding.rcBatchs.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.mcvCancel.setOnClickListener {
            onBackPressed()
        }
    }
    private fun setCreateBatchesDialog(poModel: GetAllItemMasterSelection) {
        setInfoValues(poModel)
        createBatchedDialog!!.show()
    }
    private fun setItemDescriptionDialog() {
        itemDescriptionDialog!!.setContentView(itemDescriptionBinding.root)
        itemDescriptionDialog!!.getWindow()?.setBackgroundDrawable(
            AppCompatResources.getDrawable(
                this@GRSubmittedActivity,
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
    private fun setInfoValues(poModel: GetAllItemMasterSelection) {
        Log.e("poModelfrombatches", poModel.toString())
        createBatchesDialogBinding.grnAddHeader.tvLineItemDescValue.setText(poModel.code)
        createBatchesDialogBinding.grnAddHeader.tvItemDescValue.setText(poModel.description)
        createBatchesDialogBinding.grnAddHeader.tvPuomValue.setText(poModel.uom)
        createBatchesDialogBinding.grnAddHeader.tvMhTypeValue.setText(poModel.mhType)
        createBatchesDialogBinding.grnAddHeader.tvQtyValue.setText(poModel.balQty)
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
        //balanceQty = poModel.balQty.toString()
        //totalQty = poModel.balQty.toString()
    }
    private fun setItemDescription(itemDesc: String) {
        itemDescriptionDialog?.show()
        itemDescriptionBinding.tvItemDescription.setText(itemDesc)
    }
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
                this@GRSubmittedActivity,
                "Failed - \nError Message: $e"
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
    private fun showProgressBar() {
        progress.show()
    }

    private fun hideProgressBar() {
        progress.cancel()
    }

}
