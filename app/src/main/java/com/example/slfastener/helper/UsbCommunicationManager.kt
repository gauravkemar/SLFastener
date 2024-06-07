package com.example.slfastener.helper

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData


class UsbCommunicationManager(private val context: Context) {
    val usbSerialCommunication = UsbSerialCommunication(context)


    private val _receivedData = MutableLiveData<String?>()
    val receivedData: LiveData<String?>
        get() = _receivedData

    fun clearReceivedData() {
        _receivedData.postValue(null)
    }
    init {
        usbSerialCommunication.setDataReceivedListener(object : DataReceivedListener {
            override fun onDataReceived(data: String) {
                _receivedData.postValue(data)
            }
        })
    }
}