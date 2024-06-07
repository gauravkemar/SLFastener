package com.example.slfastener.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.example.demorfidapp.helper.SessionManager
import com.example.slfastener.R
import com.example.slfastener.databinding.ActivityHomeBinding


class HomeActivity : AppCompatActivity() {
    lateinit var binding: ActivityHomeBinding
    private lateinit var session: SessionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_home)
        session = SessionManager(this)
        binding.midCl1.setOnClickListener {
            var intent= Intent(this@HomeActivity,GRNMainActivity::class.java)
            startActivity(intent)
        }
        binding.midCl3.setOnClickListener {
            var intent= Intent(this@HomeActivity,GRMainActivity::class.java)
            startActivity(intent)
        }
        binding.logoutBtn.setOnClickListener {
            showLogoutDialog()
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
        startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
        finish()
    }
}