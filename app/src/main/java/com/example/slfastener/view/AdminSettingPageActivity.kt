package com.example.slfastener.view

import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.demorfidapp.helper.Constants.KEY_GRN_PRN
import com.example.demorfidapp.helper.Constants.KEY_GRN_PRN_FILE_NAME
import com.example.demorfidapp.helper.Constants.KEY_GR_PRN
import com.example.demorfidapp.helper.Constants.KEY_GR_PRN_File_Name
import com.example.demorfidapp.helper.Constants.KEY_HTTP
import com.example.demorfidapp.helper.Constants.KEY_JWT_TOKEN
import com.example.demorfidapp.helper.Constants.KEY_PRINTER_TYPE
import com.example.demorfidapp.helper.Constants.KEY_SERVER_IP
import com.example.demorfidapp.helper.Resource
import com.example.demorfidapp.helper.SessionManager
import com.example.demorfidapp.helper.Utils
import com.example.demorfidapp.repository.SLFastenerRepository
import com.example.slfastener.R
import com.example.slfastener.databinding.ActivityAdminSettingPageBinding
import com.example.slfastener.model.printerprnmodel.GetPRNFileDetailOnKeyResponse
import com.example.slfastener.model.printerprnmodel.GetSelfSystemMappingDetailsResponse
import com.example.slfastener.model.printerprnmodel.PrinterDeviceLocationMappingIdRequest
import com.example.slfastener.model.printerprnmodel.ResponseObject
import com.example.slfastener.viewmodel.AdminSettingViewModel
import com.example.slfastener.viewmodel.AdminSettingViewModelProviderFactory
import es.dmoral.toasty.Toasty

class AdminSettingPageActivity : AppCompatActivity() {
    lateinit var binding: ActivityAdminSettingPageBinding
    private lateinit var viewModel: AdminSettingViewModel

    var selectedHttp = ""
    var selectedPrinterType = ""
    var selectedGrPrn = ""
    var selectedGrnPrn = ""

    private lateinit var session: SessionManager
    private lateinit var user: HashMap<String, Any?>
    private var serverIpSharedPrefText: String? = null
    private var serverHttpPrefText: String? = null
    private var serverPrinterTypePrefText: String? = null
    private var token: String? = null

    private var keyGRPrn: String? = null
    private var keyGRNPrn: String? = null

    private var builder: AlertDialog.Builder? = null
    private var alert: AlertDialog? = null
    var userType: String? = null
    private lateinit var progress: ProgressDialog
    private var baseUrl: String = ""

    private var getPRNFileDetailOnKeyGRResponse: GetPRNFileDetailOnKeyResponse? = null
    private var getPRNFileDetailOnKeyGRNResponse: GetPRNFileDetailOnKeyResponse? = null
    private var getSelfSystemMappingDetailsResponse: GetSelfSystemMappingDetailsResponse? = null
    private var printerList: MutableList<ResponseObject>? = null
    private var selectedDeviceLocationMappingId: Int = 0
    private var deviceLocationSpinnerAdapter: ArrayAdapter<String>? = null
    lateinit var deviceLocationSpinnerArray: MutableList<String>
    val deviceLocationMap = HashMap<Int, String>()
    private var defaultListApiCall = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_admin_setting_page)

        val SLFastenerRepository = SLFastenerRepository()
        val viewModelProviderFactory =
            AdminSettingViewModelProviderFactory(application, SLFastenerRepository)
        viewModel =
            ViewModelProvider(this, viewModelProviderFactory)[AdminSettingViewModel::class.java]
        progress = ProgressDialog(this)
        progress.setMessage("Loading...")
        session = SessionManager(this)
        user = session.getUserDetails()
        serverIpSharedPrefText = user[KEY_SERVER_IP].toString()
        serverHttpPrefText = user[KEY_HTTP].toString()
        serverPrinterTypePrefText = user[KEY_PRINTER_TYPE].toString()
        token = user[KEY_JWT_TOKEN].toString()
        keyGRPrn = user[KEY_GR_PRN].toString()
        keyGRNPrn = user[KEY_GRN_PRN].toString()
        baseUrl = "$serverHttpPrefText://$serverIpSharedPrefText/service/api/"
        printerList = ArrayList()
        deviceLocationSpinnerArray = ArrayList()

        val receivedIntent = intent
        userType = receivedIntent.getStringExtra("key_user_type")

        if (userType.equals("Admin")) {
            binding.logoutBtn.visibility = View.GONE
            binding.settingBtn.visibility = View.GONE
            binding.tvSupervisor.visibility = View.GONE
            binding.clBaseurlParent.visibility = View.VISIBLE
            binding.clPrinterSettingParent.visibility = View.GONE

        } else {

            binding.logoutBtn.visibility = View.VISIBLE
            binding.settingBtn.visibility = View.GONE
            binding.tvSupervisor.visibility = View.VISIBLE
            binding.clBaseurlParent.visibility = View.GONE
            binding.clPrinterSettingParent.visibility = View.VISIBLE
            getSelfSystemMappingDetail()
        }

        binding.edServerIp.setText(serverIpSharedPrefText)

        ///http spinner
        val httpArray = resources.getStringArray(R.array.http)
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, httpArray)

        binding.autoCompleteHttp.setAdapter(adapter)
        if (serverHttpPrefText.toString() == "null") {
            binding.autoCompleteHttp.setText(httpArray[0], false)
            selectedHttp = httpArray[0]
        } else {
            binding.autoCompleteHttp.setText(serverHttpPrefText, false)
            selectedHttp = serverHttpPrefText as String
        }
        binding.autoCompleteHttp.setOnItemClickListener { parent, view, position, id ->
            selectedHttp = parent.getItemAtPosition(position) as String
        }

        ///printertype spinner
        val printerTypeArray = resources.getStringArray(R.array.printer_type)
        val printerTypeAdapter =
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, printerTypeArray)

        binding.autoCompleteGeneral.setAdapter(printerTypeAdapter)
        if (serverPrinterTypePrefText.toString() == "null") {
            binding.autoCompleteGeneral.setText(printerTypeArray[0], false)
            selectedPrinterType = printerTypeArray[0]
        } else {
            binding.autoCompleteGeneral.setText(serverHttpPrefText, false)
            selectedPrinterType = serverHttpPrefText as String
        }
        binding.autoCompleteGeneral.setOnItemClickListener { parent, view, position, id ->
            selectedPrinterType = parent.getItemAtPosition(position) as String
        }

        binding.btnUpdateGeneral.setOnClickListener {
            Utils.setSharedPrefs(this, KEY_PRINTER_TYPE, selectedPrinterType)
        }
        binding.btnSave.setOnClickListener {
            val serverIp = binding.edServerIp.text.toString().trim()
            if (serverIp == "") {
                Toast.makeText(this, "Please Enter Domain Name!!", Toast.LENGTH_SHORT)
                    .show()
                binding.edServerIp.error = "Please enter Domain Name"

            } else {
                showDialog(serverIp, selectedHttp)
            }
        }

        /*binding.autoCompleteGR.setOnItemClickListener { parent, view, position, id ->
            selectedGrPrn = parent.getItemAtPosition(position) as String
        }
        binding.autoCompleteGRN.setOnItemClickListener { parent, view, position, id ->
            selectedGrnPrn = parent.getItemAtPosition(position) as String
        }*/
        binding.mcvIpSettings.setOnClickListener {
            toggleVisibility(binding.clIpSettings)
        }
        // Add click listeners for other card views
        binding.mcvPrinterSelect.setOnClickListener {
            toggleVisibility(binding.clPrinterSetting)
        }
        viewModel.getPrnForLabelGRMutable.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { resultResponse ->
                        try {
                            getPRNFileDetailOnKeyGRResponse = resultResponse
                            if (getPRNFileDetailOnKeyGRResponse != null) {
                                binding.tvFromApiPrn.setText(getPRNFileDetailOnKeyGRResponse!!.prnFileName)
                            }
                        } catch (e: Exception) {

                            Toasty.warning(
                                this@AdminSettingPageActivity,
                                e.printStackTrace().toString(),
                                Toasty.LENGTH_SHORT
                            ).show()

                        }
                    }
                }

                is Resource.Error<*> -> {
                    hideProgressBar()
                    response.message?.let { errorMessage ->
                        Toasty.error(
                            this@AdminSettingPageActivity,
                            "Login failed - \nError Message: $errorMessage"
                        ).show()
                        session.showToastAndHandleErrors(
                            errorMessage,
                            this@AdminSettingPageActivity
                        )
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }
        viewModel.getPrnForLabelGRNMutable.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { resultResponse ->
                        try {
                            getPRNFileDetailOnKeyGRNResponse = resultResponse
                            if (getPRNFileDetailOnKeyGRNResponse != null) {
                                binding.tvFromApiGRNPrn.setText(getPRNFileDetailOnKeyGRNResponse!!.prnFileName)
                            }
                        } catch (e: Exception) {
                            Toasty.warning(
                                this@AdminSettingPageActivity,
                                e.printStackTrace().toString(),
                                Toasty.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                is Resource.Error<*> -> {
                    hideProgressBar()
                    response.message?.let { errorMessage ->
                        Toasty.error(
                            this@AdminSettingPageActivity,
                            "Login failed - \nError Message: $errorMessage"
                        ).show()
                        session.showToastAndHandleErrors(
                            errorMessage,
                            this@AdminSettingPageActivity
                        )
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }
        viewModel.getSelfSystemMappingDetailMutable.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { resultResponse ->
                        try {

                            if (resultResponse != null) {
                                getSelfSystemMappingDetailsResponse = resultResponse
                                if (defaultListApiCall == 0) {
                                    defaultListApiCall++
                                    getAllActiveDeviceLocationMapping()
                                }
                            } else {
                                showDialog()
                            }
                        } catch (e: Exception) {
                            Toasty.warning(
                                this@AdminSettingPageActivity,
                                e.printStackTrace().toString(),
                                Toasty.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                is Resource.Error<*> -> {
                    hideProgressBar()
                    response.message?.let { errorMessage ->
                        Toasty.error(
                            this@AdminSettingPageActivity,
                            "Login failed - \nError Message: $errorMessage"
                        ).show()
                        session.showToastAndHandleErrors(
                            errorMessage,
                            this@AdminSettingPageActivity
                        )
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }
        viewModel.getAllActiveDeviceLocationMappingMutable.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { resultResponse ->
                        try {
                            if (resultResponse != null) {
                                //getSelfSystemMappingDetailsResponse=resultResponse
                                if (resultResponse.responseObject.size > 0) {
                                    for (e in resultResponse.responseObject) {
                                        deviceLocationMap[e.deviceLocationMappingId] = e.deviceName
                                        (deviceLocationSpinnerArray).add(e.deviceName)
                                    }
                                    setPrinterSpinner()
                                }

                            } else {
                                showDialog()
                            }
                        } catch (e: Exception) {
                            Toasty.warning(
                                this@AdminSettingPageActivity,
                                e.printStackTrace().toString(),
                                Toasty.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                is Resource.Error<*> -> {
                    hideProgressBar()
                    response.message?.let { errorMessage ->
                        Toasty.error(
                            this@AdminSettingPageActivity,
                            "Login failed - \nError Message: $errorMessage"
                        ).show()
                        session.showToastAndHandleErrors(
                            errorMessage,
                            this@AdminSettingPageActivity
                        )
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }



        if (keyGRPrn.toString() == "null") {
            binding.tvCurrentSelectedGRPrn.setText("")
        } else {
            binding.tvCurrentSelectedGRPrn.setText(keyGRPrn)
        }
        if (keyGRNPrn.toString() == "null") {
            binding.tvCurrentSelectedGRNPrn.setText("")

        } else {
            binding.tvCurrentSelectedGRNPrn.setText(keyGRNPrn)
        }

        binding.mcvGetGRPRN.setOnClickListener {
            getPRNFleDetailGR()
        }
        binding.mcvGetGRNPRN.setOnClickListener {
            getPRNFleDetailGRN()
        }

        binding.btnUpdateGRN.setOnClickListener {
            Log.e("passsss", getPRNFileDetailOnKeyGRNResponse!!.prnContent.toString())
            Utils.setSharedPrefs(
                this,
                KEY_GRN_PRN_FILE_NAME,
                getPRNFileDetailOnKeyGRNResponse!!.prnFileName
            )
            Utils.setSharedPrefs(
                this,
                KEY_GRN_PRN,
                getPRNFileDetailOnKeyGRNResponse!!.prnContent.toString()
            )
            binding.tvCurrentSelectedGRNPrn.setText(getPRNFileDetailOnKeyGRNResponse!!.prnFileName)
        }
        binding.btnUpdateGr.setOnClickListener {
            Utils.setSharedPrefs(
                this,
                KEY_GRN_PRN,
                getPRNFileDetailOnKeyGRResponse!!.prnFileName
            )
            Utils.setSharedPrefs(
                this,
                KEY_GR_PRN,
                getPRNFileDetailOnKeyGRResponse!!.prnContent
            )
            binding.tvCurrentSelectedGRPrn.setText(
                getPRNFileDetailOnKeyGRResponse!!.prnFileName
            )
        }
        binding.btnUpdateSelectPrinter.setOnClickListener {
            updateDefaultPrinterOnDevice()
        }
    }

    private fun setPrinterSpinner() {
        val autoCompleteTextView = binding.autoCompleteSelectPrinter
        // Initialize ArrayAdapter and set it to the AutoCompleteTextView
        deviceLocationSpinnerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            deviceLocationSpinnerArray
        )
        autoCompleteTextView.setAdapter(deviceLocationSpinnerAdapter)

        // Set threshold to 1 so that suggestions appear as soon as the user starts typing
        autoCompleteTextView.threshold = 1

        // If there is a default selection based on some condition

        val defaultDevice = getSelfSystemMappingDetailsResponse?.defaultPrinterIPId
        if (defaultDevice != null) {
            var findDefault = deviceLocationMap.entries.find { it.key == defaultDevice }?.value
            if (findDefault != null) {
                val defaultPosition = deviceLocationSpinnerArray.indexOf(findDefault)
                if (defaultPosition != -1) {
                    autoCompleteTextView.setText(findDefault, false)
                    autoCompleteTextView.isEnabled = false
                    autoCompleteTextView.setTextColor(Color.BLACK)
                }
            }

        }


        // Set listener to handle item selection
        autoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            handleDeviceLocationSelection(position)
        }

        // Show dropdown list when AutoCompleteTextView gains focus
        autoCompleteTextView.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                autoCompleteTextView.showDropDown()
            }
        }

    }

    private fun handleDeviceLocationSelection(position: Int) {
        val selectedItem = deviceLocationSpinnerArray[position]
        val selectedKey = deviceLocationMap.entries.find { it.value == selectedItem }?.key
        if (selectedKey != null) {
            selectedDeviceLocationMappingId = selectedKey

        }
    }

    private fun showDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Device Not Mapped!!")
        builder.setMessage("Please map your device in device mapping from Web!!.")

        // Add the "OK" button
        builder.setPositiveButton("OK") { dialog, _ ->
            // Action to take when "OK" is pressed
            dialog.dismiss()
        }

        // Create and show the dialog
        val dialog = builder.create()
        dialog.show()
    }

    private fun getPRNFleDetailGR() {
        try {
            viewModel.getPRNFleDetailGR(token!!, baseUrl, "PRNFILEFORGR")
        } catch (e: Exception) {
            Toasty.error(
                this@AdminSettingPageActivity,
                " Error Message: $e"
            ).show()
        }
    }

    private fun updateDefaultPrinterOnDevice() {
        try {
            viewModel.updateDefaultPrinterOnDevice(
                token!!, baseUrl,
                PrinterDeviceLocationMappingIdRequest(selectedDeviceLocationMappingId)
            )
        } catch (e: Exception) {
            Toasty.error(
                this@AdminSettingPageActivity,
                " Error Message: $e"
            ).show()
        }
    }


    private fun getPRNFleDetailGRN() {
        try {
            viewModel.getPRNFleDetailGRN(token!!, baseUrl, "PRNFILEFORGRN")
        } catch (e: Exception) {
            Toasty.error(
                this@AdminSettingPageActivity,
                " Error Message: $e"
            ).show()
        }
    }

    private fun getSelfSystemMappingDetail() {
        try {
            viewModel.getSelfSystemMappingDetail(token!!, baseUrl)
        } catch (e: Exception) {
            Toasty.error(
                this@AdminSettingPageActivity,
                " Error Message: $e"
            ).show()
        }
    }

    private fun getAllActiveDeviceLocationMapping() {
        try {
            viewModel.getAllActiveDeviceLocationMapping(token!!, baseUrl, "Printer")
        } catch (e: Exception) {
            Toasty.error(
                this@AdminSettingPageActivity,
                " Error Message: $e"
            ).show()
        }
    }


    private fun showProgressBar() {
        progress.show()
    }

    private fun hideProgressBar() {
        progress.cancel()
    }

    private fun showDialog(
        serverIp: String?,
        http: String?,
    ) {
        builder = AlertDialog.Builder(this)
        builder!!.setMessage("Changes will take effect after Re-Login!")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog: DialogInterface?, id: Int ->
                session.saveAdminDetails(serverIp, http)
                startActivity(Intent(this, LoginActivity::class.java))
                this@AdminSettingPageActivity?.finishAffinity()
            }
            .setNegativeButton("No, Continue") { dialog: DialogInterface, id: Int ->
                dialog.cancel()
                binding.edServerIp.setText(serverIpSharedPrefText)
            }
        alert = builder!!.create()
        alert!!.show()
    }

    private fun toggleVisibility(clPrinterSetting: ConstraintLayout) {
        if (clPrinterSetting == binding.clIpSettings && binding.clPrinterSetting.visibility == View.VISIBLE) {
            binding.clPrinterSetting.visibility = View.GONE
            binding.ivOpenClosePrinterSetting.setImageResource(R.drawable.ic_arrow_down_black)
            binding.clIpSettings.visibility = View.VISIBLE
            binding.ivOpenCloseBaseUrlSetting.setImageResource(R.drawable.ic_up_arrow_black)

        } else if (clPrinterSetting == binding.clPrinterSetting && binding.clIpSettings.visibility == View.VISIBLE) {
            binding.clIpSettings.visibility = View.GONE
            binding.ivOpenCloseBaseUrlSetting.setImageResource(R.drawable.ic_arrow_down_black)
            binding.clPrinterSetting.visibility = View.VISIBLE
            binding.ivOpenClosePrinterSetting.setImageResource(R.drawable.ic_up_arrow_black)

        } else {
            if (clPrinterSetting.visibility == View.VISIBLE) {
                clPrinterSetting.visibility = View.GONE
                if (clPrinterSetting == binding.clPrinterSetting) {
                    binding.ivOpenClosePrinterSetting.setImageResource(R.drawable.ic_arrow_down_black)
                } else if (clPrinterSetting == binding.clIpSettings) {
                    binding.ivOpenCloseBaseUrlSetting.setImageResource(R.drawable.ic_arrow_down_black)
                }

            } else {
                clPrinterSetting.visibility = View.VISIBLE
                if (clPrinterSetting == binding.clPrinterSetting) {
                    binding.ivOpenClosePrinterSetting.setImageResource(R.drawable.ic_up_arrow_black)
                } else if (clPrinterSetting == binding.clIpSettings) {
                    binding.ivOpenCloseBaseUrlSetting.setImageResource(R.drawable.ic_up_arrow_black)
                }
            }
        }
    }
}