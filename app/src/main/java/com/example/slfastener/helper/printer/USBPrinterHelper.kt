package com.example.slfastener.helper.printer

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbConstants
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbEndpoint
import android.hardware.usb.UsbInterface
import android.hardware.usb.UsbManager
import android.widget.Toast
import java.io.File
import java.io.FileInputStream
import java.io.IOException

class USBPrinterHelper(private val context: Context, private val listener: PrinterStatusListener) {

    private lateinit var usbManager: UsbManager
    private var printerConnection: UsbDeviceConnection? = null
    private var printerEndpointOut: UsbEndpoint? = null
    private var printerInterface: UsbInterface? = null

    companion object {
        const val ACTION_USB_PERMISSION = "com.example.zebraprinter.USB_PERMISSION"
    }

    private val usbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                UsbManager.ACTION_USB_DEVICE_ATTACHED -> {
                    val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
                    device?.let { if (isZebraPrinter(it)) requestUsbPermission(it) }
                }

                UsbManager.ACTION_USB_DEVICE_DETACHED -> {
                    val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
                    device?.let { if (isZebraPrinter(it)) {
                        disconnectFromPrinter()
                        listener.onPrinterStatusChanged(false)
                    }
                    }
                }

                ACTION_USB_PERMISSION -> {
                    synchronized(this) {
                        val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
                        if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                            device?.let { connectToPrinter(it) }
                        } else {
                            Toast.makeText(
                                context,
                                "Permission denied for device $device",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    init {
        usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager

        val filter = IntentFilter().apply {
            addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
            addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
            addAction(ACTION_USB_PERMISSION)
        }

        context.registerReceiver(usbReceiver, filter)

        // Check if there are already connected USB devices
        usbManager.deviceList.values.forEach { device ->
            if (isZebraPrinter(device)) {
                requestUsbPermission(device)
            }
        }
    }

    private fun isZebraPrinter(device: UsbDevice): Boolean {
        return device.vendorId == 2655 && device.productId == 288
    }

    private fun requestUsbPermission(device: UsbDevice) {
        val permissionIntent = PendingIntent.getBroadcast(
            context,
            0,
            Intent(ACTION_USB_PERMISSION),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        usbManager.requestPermission(device, permissionIntent)
    }

    private fun connectToPrinter(device: UsbDevice) {
        val usbInterface = device.getInterface(0)
        val endpointOut = usbInterface.getEndpoint(0)

        if (endpointOut.type != UsbConstants.USB_ENDPOINT_XFER_BULK) {
            return
        }

        printerConnection = usbManager.openDevice(device)
        printerConnection?.claimInterface(usbInterface, true)
        printerEndpointOut = endpointOut
        printerInterface = usbInterface
        listener.onPrinterStatusChanged(true)
    }

    private fun disconnectFromPrinter() {
        printerInterface?.let { printerConnection?.releaseInterface(it) }
        printerConnection?.close()
        printerConnection = null
        printerEndpointOut = null
        printerInterface = null

    }

    fun printZplFile(zplContent: String) {
        printerConnection?.let { connection ->
            printerEndpointOut?.let { endpoint ->
                val data = zplContent.toByteArray()
                val result = connection.bulkTransfer(endpoint, data, data.size, 10000)
                if (result < 0) {
                    throw IOException("Error sending ZPL data")
                }
            }
        }
    }

    fun printSampleZpl(prnFile: File) {
        val prnContent = readPrnFile(prnFile)
        val zplContent = replaceBarcodePlaceholder(prnContent)
        printZplFile(zplContent)
    }

    private fun replaceBarcodePlaceholder(content: String): String {
        return content.replace("BarcodeTest", "G6165654654654")
    }

    private fun readPrnFile(file: File): String {
        return FileInputStream(file).use { inputStream ->
            inputStream.bufferedReader().use { reader ->
                reader.readText()
            }
        }
    }

    fun unregisterReceiver() {
        context.unregisterReceiver(usbReceiver)
        disconnectFromPrinter()
    }

    interface PrinterStatusListener {
        fun onPrinterStatusChanged(isConnected: Boolean)
    }
}