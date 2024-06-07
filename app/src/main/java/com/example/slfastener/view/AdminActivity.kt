package com.example.slfastener.view

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.example.demorfidapp.helper.Constants
import com.example.demorfidapp.helper.SessionManager
import com.example.slfastener.R
import com.example.slfastener.databinding.ActivityAdminBinding

class AdminActivity : AppCompatActivity() {
    lateinit var binding: ActivityAdminBinding
    private var builder: AlertDialog.Builder? = null
    private var alert: AlertDialog? = null
    private var serverIpSharedPrefText: String? = null
    private var serverHttpPrefText: String? = null
    private lateinit var session: SessionManager
    private lateinit var user: HashMap<String, Any?>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this,R.layout.activity_admin)
        session = SessionManager(this)
        user = session.getUserDetails()
        serverIpSharedPrefText = user[Constants.KEY_SERVER_IP].toString()
        serverHttpPrefText = user[Constants.KEY_HTTP].toString()

        binding.edServerIp.setText(serverIpSharedPrefText)
        if (serverHttpPrefText.toString() == "null") {
            binding.edHttp.setText("")
        } else {
            binding.edHttp.setText(serverHttpPrefText.toString())
        }
        binding.btnSave.setOnClickListener{
            val serverIp = binding.edServerIp.text.toString().trim()
            var edHttp = binding.edHttp.text.toString().trim()
            if (serverIp == "" ||  edHttp== "") {
                if (serverIp == ""  && edHttp== "") {
                    Toast.makeText(
                        this,
                        "Please enter all values!!",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.edServerIp.error = "Please enter Domain Name"
                    //binding.edPort.error = "Please enter value"
                    binding.edHttp.error = "Please enter value"
                } else if (serverIp == "") {
                    Toast.makeText(this, "Please Enter ServerIP!!", Toast.LENGTH_SHORT)
                        .show()
                    binding.edServerIp.error = "Please enter Domain Name"
                }
                else if(edHttp == "")
                {
                    Toast.makeText(
                        this,
                        "Please Enter Http/Https Number!!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                showDialog(serverIp,edHttp)
            }
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
                this@AdminActivity?.finishAffinity()
            }
            .setNegativeButton("No, Continue") { dialog: DialogInterface, id: Int ->
                dialog.cancel()
                binding.edServerIp.setText(serverIpSharedPrefText)
            }
        alert = builder!!.create()
        alert!!.show()
    }

}