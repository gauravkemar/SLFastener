package com.example.slfastener.view

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.example.demorfidapp.helper.Constants
import com.example.demorfidapp.helper.Constants.KEY_GRN_PRN
import com.example.demorfidapp.helper.Constants.KEY_GR_PRN
import com.example.demorfidapp.helper.Constants.KEY_PRINTER_TYPE
import com.example.demorfidapp.helper.SessionManager
import com.example.demorfidapp.helper.Utils
import com.example.slfastener.R
import com.example.slfastener.databinding.ActivityAdminSettingPageBinding

class AdminSettingPageActivity : AppCompatActivity() {
    lateinit var binding: ActivityAdminSettingPageBinding
    var selectedHttp = ""
    var selectedPrinterType = ""
    var selectedGrPrn = ""
    var selectedGrnPrn = ""

    private lateinit var session: SessionManager
    private lateinit var user: HashMap<String, Any?>
    private var serverIpSharedPrefText: String? = null
    private var serverHttpPrefText: String? = null
    private var serverPrinterTypePrefText: String? = null
    private var builder: AlertDialog.Builder? = null
    private var alert: AlertDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_admin_setting_page)

        session = SessionManager(this)
        user = session.getUserDetails()
        serverIpSharedPrefText = user[Constants.KEY_SERVER_IP].toString()
        serverHttpPrefText = user[Constants.KEY_HTTP].toString()
        serverPrinterTypePrefText = user[Constants.KEY_PRINTER_TYPE].toString()

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
        val printerTypeAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, printerTypeArray)

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
        binding.autoCompleteGR.setOnItemClickListener { parent, view, position, id ->
            selectedGrPrn = parent.getItemAtPosition(position) as String
        }
        binding.autoCompleteGRN.setOnItemClickListener { parent, view, position, id ->
            selectedGrnPrn = parent.getItemAtPosition(position) as String
        }
        binding.btnUpdateGr.setOnClickListener {
            Utils.setSharedPrefs(this, KEY_GR_PRN, selectedGrPrn)
        }
        binding.btnUpdateGRN.setOnClickListener {
            Utils.setSharedPrefs(this, KEY_GRN_PRN, selectedGrnPrn)
        }

        binding.mcvIpSettings.setOnClickListener {
            toggleVisibility(binding.clIpSettings)
        }
        // Add click listeners for other card views
        binding.mcvPrinterSelect.setOnClickListener {
            toggleVisibility(binding.clPrinterSetting)
        }
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
                if(clPrinterSetting == binding.clPrinterSetting)
                {
                    binding.ivOpenClosePrinterSetting.setImageResource(R.drawable.ic_arrow_down_black)
                }
                else if(clPrinterSetting == binding.clIpSettings){
                    binding.ivOpenCloseBaseUrlSetting.setImageResource(R.drawable.ic_arrow_down_black)
                }


            } else {
                clPrinterSetting.visibility = View.VISIBLE
                if(clPrinterSetting == binding.clPrinterSetting)
                {
                    binding.ivOpenClosePrinterSetting.setImageResource(R.drawable.ic_up_arrow_black)
                }
                else if(clPrinterSetting == binding.clIpSettings){
                    binding.ivOpenCloseBaseUrlSetting.setImageResource(R.drawable.ic_up_arrow_black)
                }
            }
        }
    }
}