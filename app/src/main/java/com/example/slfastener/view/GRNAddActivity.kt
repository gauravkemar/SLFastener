package com.example.slfastener.view

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
import com.example.slfastener.adapter.GRNSelectPoAdapter
import com.example.slfastener.adapter.GrnMainAddAdapter
import com.example.slfastener.adapter.LineItemAdapter
import com.example.slfastener.databinding.ActivityGrnaddBinding
import com.example.slfastener.databinding.SelectLineItemDialogBinding
import com.example.slfastener.databinding.SelectSupplierDialogBinding
import com.example.slfastener.helper.CustomArrayAdapter
import com.example.slfastener.model.GetPOsAndLineItemsOnPOIdsResponse
import com.example.slfastener.model.GetSuppliersPOsDDLResponse
import com.example.slfastener.model.PoLineItem
import com.example.slfastener.model.PoLineItemSelectionModel
import com.example.slfastener.model.grn.GrnMainAddListResponse
import com.example.slfastener.viewmodel.GRNTransactionViewModel
import com.example.slfastener.viewmodel.GRNTransactionViewModelProviderFactory
import es.dmoral.toasty.Toasty
import java.util.Calendar

class GRNAddActivity : AppCompatActivity() {
    lateinit var binding: ActivityGrnaddBinding
    lateinit var spinnerItems: MutableList<String>
    private var selectedReasonSpinnerString: String? = ""
    private lateinit var viewModel: GRNTransactionViewModel
    private var grnMainItemAdapter: GrnMainAddAdapter? = null
    private var grnSelectPoAdapter: GRNSelectPoAdapter? = null
    private var lineItemAdapter: LineItemAdapter? = null
    lateinit var grnMainResponse: ArrayList<GrnMainAddListResponse>
    lateinit var getSuppliersPOsDDLResponse: ArrayList<GetSuppliersPOsDDLResponse>
    lateinit var getPOsAndLineItemsOnPOIdsResponse: ArrayList<GetPOsAndLineItemsOnPOIdsResponse>
    lateinit var poLineItem: ArrayList<PoLineItemSelectionModel>
    lateinit var selectedPoLineItem: ArrayList<PoLineItemSelectionModel>
    private lateinit var session: SessionManager
    private lateinit var progress: ProgressDialog
    lateinit var selectPoBinding: SelectSupplierDialogBinding
    lateinit var supplierSpinnerArray: ArrayList<String>
    lateinit var selectedPoFilteredList: ArrayList<Int>
    val supplierMap = HashMap<String, String>()
    var selectedSupplierCode:String=""

    private var supplierSpinnerAdapter: CustomArrayAdapter? = null
    private var isIntialSelectSupplier = true
    var selectPoDialog: Dialog? = null


    lateinit var selectLineItemBinding: SelectLineItemDialogBinding
    var selectLineItemDialog: Dialog? = null

    var selectedDate:String=""
    var edInvoiceNo:String=""
    var edGdpoNo:String=""
    var receivedQty:String=""
    private lateinit var userDetails: HashMap<String, Any?>
    var token:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_grnadd)
        session = SessionManager(this)
        selectPoBinding = SelectSupplierDialogBinding.inflate(getLayoutInflater());
        selectLineItemBinding = SelectLineItemDialogBinding.inflate(getLayoutInflater());
        selectPoDialog = Dialog(this)
        selectLineItemDialog= Dialog(this)
        progress = ProgressDialog(this)
        progress.setMessage("Loading...")
        supplierSpinnerArray = ArrayList()
        selectedPoFilteredList = ArrayList()
        getSuppliersPOsDDLResponse = ArrayList()
        poLineItem = ArrayList()
        selectedPoLineItem = ArrayList()
        getPOsAndLineItemsOnPOIdsResponse = ArrayList()
        userDetails = session.getUserDetails()
        token = userDetails["jwtToken"].toString()




        val SLFastenerRepository = SLFastenerRepository()
        val viewModelProviderFactory = GRNTransactionViewModelProviderFactory(application, SLFastenerRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory)[GRNTransactionViewModel::class.java]

        getApplicationVersionDetails()


        grnMainResponse = ArrayList()
        grnMainItemAdapter = GrnMainAddAdapter()

        binding.mcvAddGrn.setOnClickListener {
            if(selectedPoFilteredList.size>0)
            {
                callSelectedPoLineItems(selectedPoFilteredList )
            }
            else{
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
                            if(resultResponse.size>0)
                            {
                                getSuppliersPOsDDLResponse.addAll(resultResponse)
                                selectePoRc(getSuppliersPOsDDLResponse)
                            }
                            else
                            {
                                Toast.makeText(this,"List is Empty!!",Toast.LENGTH_SHORT).show()
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


        viewModel.getPosLineItemsOnPoIdsMutableResponse.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    supplierMap.clear()
                    supplierSpinnerArray.clear()
                    (supplierSpinnerArray).add("Select Supplier")
                    hideProgressBar()
                    response.data?.let { resultResponse ->
                        try {
                            edInvoiceNo=binding.edInvoiceNumber.text.toString().trim()
                            edGdpoNo=binding.edGdpo.text.toString().trim()


                            if(resultResponse.size>0)
                            {
                                getPOsAndLineItemsOnPOIdsResponse.addAll(resultResponse)

                                for(r in resultResponse)
                                {

                                    for(e in r.poLineItems)
                                    {
                                        poLineItem.add(PoLineItemSelectionModel(
                                            e.itemCode,e.itemDescription,
                                            e.itemName,e.lineNumber,
                                            e.poId,e.poLineItemId,
                                            e.poQuantity,e.poUnitPrice,
                                            e.posapLineItemNumber,e.pouom,
                                            r.poNumber,edGdpoNo,"",receivedQty))
                                    }

                                }

                                binding.mcvNewLineItem.setOnClickListener {
                                    setSelectLineItemDialog(poLineItem)
                                }

                            }
                            else
                            {
                                Toast.makeText(this,"List is Empty!!",Toast.LENGTH_SHORT).show()
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

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this,
            DatePickerDialog.OnDateSetListener { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                // Do something with the selected date
                val selectedDateCal = Calendar.getInstance()
                selectedDateCal.set(selectedYear, selectedMonth, selectedDay)
                // You can handle the selected date here, for example, set it to a TextView
                binding.tvDate.text = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            }, year, month, dayOfMonth)

        // Set maximum date
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis() // Optional: Set maximum date to current date
        datePickerDialog.show()
    }
    private fun setSupplierSpinner() {

        //supplierSpinnerAdapter = ArrayAdapter<String>(this, R.layout.spinner_layout, supplierSpinnerArray)

        supplierSpinnerAdapter = CustomArrayAdapter(this, android.R.layout.simple_spinner_item, supplierSpinnerArray)
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
        selectPoBinding.rcPo.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        grnSelectPoAdapter!!.setOnItemCheckClickListener {
            //submitInvoiceCheckedList.add(product);
            //Log.d("thisChecked","checked "+submitInvoiceCheckedList.toString());
             var selectedPoList: ArrayList<Int>
            selectedPoList = ArrayList()
            for (poCode in selectedPoFilteredList) {
                selectedPoList.add(poCode)
                Log.e("onitemclick",poCode.toString())
            }
            if (!selectedPoList.contains(it.value)) {
                selectedPoFilteredList.add(it.value)
            }

        }
        grnSelectPoAdapter!!.setOnItemUncheckClickListener {
            val iterator: MutableIterator<Int> = selectedPoFilteredList.iterator()
            while (iterator.hasNext()) {
                val item: Int = iterator.next()
                if (item==it.value) {
                    iterator.remove()
                }
            }
            Log.d("selectedPoFilteredListghj",selectedPoFilteredList.toString())
        }

        selectPoBinding.btnSubmit.setOnClickListener {
            if(selectedPoFilteredList.size>0)
            {
                callSelectedPoLineItems(selectedPoFilteredList )
            }
            else{
                Toasty.warning(
                    this@GRNAddActivity,
                    "Please Select Po from list!!",
                    Toasty.LENGTH_SHORT
                ).show()
            }

        }

    }

    private fun callParentLocationApi(selectedKey: String) {
        try {
            viewModel.getSuppliersPosDDLL(token,Constants.BASE_URL,selectedKey)
        }
        catch (e:Exception)
        {
            Toasty.error(
                this,
                e.message.toString(),
                Toasty.LENGTH_LONG
            ).show()
        }
    }

    private fun callSelectedPoLineItems(poCode: ArrayList<Int>) {
        try {

            viewModel.getPosLineItemsOnPoIds(token,Constants.BASE_URL,poCode )
        }
        catch (e:Exception)
        {
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
            viewModel.getActiveSuppliersDDL(token,Constants.BASE_URL)
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
        grnMainItemAdapter?.setGrnMainList(poLineItemSelected, this@GRNAddActivity)
        binding.rcGrnAdd!!.adapter = grnMainItemAdapter
        binding.rcGrnAdd.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
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

    private fun setSelectLineItemDialog(poLineItem: ArrayList<PoLineItemSelectionModel>) {
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

        setLineItemRc(poLineItem)

        selectLineItemBinding.closeDialogueTopButton.setOnClickListener {
            selectLineItemDialog!!.dismiss()
        }
        selectLineItemDialog!!.show()
    }



    private fun setLineItemRc (lineItems: ArrayList<PoLineItemSelectionModel>) {

        lineItemAdapter = LineItemAdapter()
        lineItemAdapter?.setGrnMainList(lineItems, this@GRNAddActivity)
        selectLineItemBinding.rcLineItem .adapter = lineItemAdapter
        selectLineItemBinding.rcLineItem .layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        lineItemAdapter!!.setOnItemCheckClickListener {
            //submitInvoiceCheckedList.add(product);
            //Log.d("thisChecked","checked "+submitInvoiceCheckedList.toString());
            var selectedPoList: ArrayList<String>
            selectedPoList = ArrayList()
            for (poCode in selectedPoLineItem) {
                selectedPoList.add(poCode.itemCode)
                Log.e("onitemclick",poCode.toString())
            }
            if (!selectedPoList.contains(it.itemCode)) {
                selectedPoLineItem.add(it)
            }
            setPoLineItemList(selectedPoLineItem)

        }
        lineItemAdapter!!.setOnItemUncheckClickListener {
            val iterator: MutableIterator<PoLineItemSelectionModel> = selectedPoLineItem.iterator()
            while (iterator.hasNext()) {
                val item: PoLineItemSelectionModel = iterator.next()
                if (item.itemCode == it.itemCode) {
                    iterator.remove()
                }
            }
            Log.d("selectedPoFilteredListghj",selectedPoLineItem.toString())
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
    private fun logout(){
        session.logoutUser()
        startActivity(Intent(this@GRNAddActivity, LoginActivity::class.java))
        finish()
    }
}












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