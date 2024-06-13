package com.example.slfastener.helper.weighing

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.slfastener.helper.DataReceivedListener


class UsbCommunicationManager(private val context: Context) {
    private val usbSerialCommunication = USBSerialCommunicationNew(context)

    private val _receivedData = MutableLiveData<String?>()
    val receivedData: LiveData<String?>
        get() = _receivedData


    private val _isDeviceConnected = MutableLiveData<Boolean?>()
    val isDeviceConnected: LiveData<Boolean?>
        get() = _isDeviceConnected

    init {
        usbSerialCommunication.setDataReceivedListener(object : DataReceivedListener {
            override fun onDataReceived(data: String) {
                _receivedData.postValue(data)
            }
            override fun onDeviceConnected(isConnected: Boolean) {
                _isDeviceConnected.postValue(isConnected)
            }
        })
    }


    fun clearReceivedData() {
        _receivedData.postValue(null)
    }

    fun unregisterReceiver() {
        // Assuming you have a method in UsbSerialCommunication to unregister the receiver
        usbSerialCommunication.unregisterReceiver()
    }
}