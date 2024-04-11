package com.example.slfastener.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.slfastener.R
import com.example.slfastener.helper.UsbCommunicationManager
import com.example.slfastener.helper.UsbSerialCommunication

class CreateBatchesActivity : AppCompatActivity() {
    private lateinit var usbCommunicationManager: UsbCommunicationManager



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_batches)

        usbCommunicationManager = UsbCommunicationManager(this)
        usbCommunicationManager.receivedData.observe(this, { data ->
         Log.e("receive from CreateBatchActivity", data.toString())
        })
    }
}