package com.example.slfastener.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.demorfidapp.helper.Constants
import com.example.demorfidapp.helper.Constants.KEY_GRN_PRN
import com.example.demorfidapp.helper.Constants.KEY_GR_PRN
import com.example.demorfidapp.helper.SessionManager
import com.example.demorfidapp.helper.Utils
import com.example.slfastener.R
import com.example.slfastener.databinding.ActivityAdminBinding
import com.example.slfastener.databinding.ActivityAdminSettingPageBinding

class AdminSettingPageActivity : AppCompatActivity() {
    lateinit var binding: ActivityAdminSettingPageBinding
    var selectedHttp = ""
    var selectedGrPrn = ""
    var selectedGrnPrn = ""

    private lateinit var session: SessionManager
    private lateinit var user: HashMap<String, Any?>
    private var serverIpSharedPrefText: String? = null
    private var serverHttpPrefText: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_admin_setting_page)

        session = SessionManager(this)
        user = session.getUserDetails()
        serverIpSharedPrefText = user[Constants.KEY_SERVER_IP].toString()
        serverHttpPrefText = user[Constants.KEY_HTTP].toString()
        val items = resources.getStringArray(R.array.http)
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, items)
        binding.autoCompleteHttp.setAdapter(adapter)
        if (serverHttpPrefText.toString() == "null") {
            binding.autoCompleteHttp.setText(serverHttpPrefText, false)
        }
        // Optionally, save the selected item back to SharedPreferences when the selection changes
        binding.autoCompleteHttp.setOnItemClickListener { parent, view, position, id ->
            selectedHttp = parent.getItemAtPosition(position) as String
        }
        binding.btnSave.setOnClickListener {
            val serverIp = binding.edServerIp.text.toString().trim()
            if (serverIp == "") {
                Toast.makeText(this, "Please Enter Domain Name!!", Toast.LENGTH_SHORT)
                    .show()
                binding.edServerIp.error = "Please enter Domain Name"

            } else {
                session.saveAdminDetails(serverIp, selectedHttp)
            }
        }

        binding.autoCompleteGR.setOnItemClickListener { parent, view, position, id ->
            selectedGrPrn = parent.getItemAtPosition(position) as String
        }
        binding.autoCompleteGRN.setOnItemClickListener { parent, view, position, id ->
            selectedGrnPrn = parent.getItemAtPosition(position) as String
        }
        binding.btnUpdateGr.setOnClickListener {
            Utils.setSharedPrefs(this,KEY_GR_PRN,selectedGrPrn)
        }
        binding.btnUpdateGRN.setOnClickListener {
            Utils.setSharedPrefs(this,KEY_GRN_PRN,selectedGrnPrn)
        }
    }
}