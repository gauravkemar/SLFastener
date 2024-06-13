package com.example.slfastener.helper

interface DataReceivedListener {
    fun onDataReceived(data: String)
    fun onDeviceConnected(isConnected: Boolean)
}