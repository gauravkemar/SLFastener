package com.example.slfastener.helper.weighing

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.slfastener.helper.DataReceivedListener
import com.hoho.android.usbserial.driver.UsbSerialDriver
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class USBSerialCommunicationNew(private val context: Context) {
    private var usbManager: UsbManager? = null
    private var usbSerialPort: UsbSerialPort? = null
    private var dataReceivedListener: DataReceivedListener? = null
    private var uniqueData: String? = null
    private val fifoArray = ArrayDeque<String>(10)
    private val ACTION_USB_PERMISSION = "com.example.slfastener.USB_PERMISSION"

    private val usbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                UsbManager.ACTION_USB_DEVICE_ATTACHED -> {
                    val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
                    device?.let {
                        if (isWeightMachine(it)) {
                            requestPermission(it)
                            dataReceivedListener?.onDeviceConnected(true)
                        }
                    }
                }
                UsbManager.ACTION_USB_DEVICE_DETACHED -> {
                    val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
                    device?.let {
                        if (isWeightMachine(it)) {
                            disconnectFromDevice()
                            dataReceivedListener?.onDeviceConnected(false)
                        }
                    }
                }
                ACTION_USB_PERMISSION -> {
                    synchronized(this) {
                        usbManager?.deviceList?.values?.forEach { device ->
                            if (isWeightMachine(device)) {
                              connectToDevice(device)
                            }
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
        usbManager?.deviceList?.values?.forEach { device ->
            if (isWeightMachine(device)) {
                requestPermission(device)
            }
        }
    }
    fun setDataReceivedListener(listener: DataReceivedListener) {
        dataReceivedListener = listener
    }
    private fun isWeightMachine(device: UsbDevice): Boolean {
        return device.vendorId == 6790 && device.productId == 29987
    }
    private fun requestPermission(device: UsbDevice) {
        val permissionIntent = PendingIntent.getBroadcast(
            context,
            0,
            Intent(ACTION_USB_PERMISSION),
            PendingIntent.FLAG_IMMUTABLE
        )
        usbManager?.requestPermission(device, permissionIntent)
    }
    private fun connectToDevice(device: UsbDevice) {
        Log.d("USBSerialCommunication", "Connecting to device: $device")
        val availableDrivers: List<UsbSerialDriver>? =
            UsbSerialProber.getDefaultProber().findAllDrivers(usbManager)
        if (availableDrivers.isNullOrEmpty()) {
            Log.e("UsbSerialCommunication", "No USB devices found")
            return
        }
        val driver: UsbSerialDriver = availableDrivers[0]
        val connection = usbManager?.openDevice(device) ?: run {
            Log.e("UsbSerialCommunication", "Failed to open device")
            return
        }
        val port: UsbSerialPort = driver.ports[0]
        usbSerialPort = port
        GlobalScope.launch(Dispatchers.IO) {
            try {
                port.open(connection)
                port.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)
                listenForData(port, this)
                dataReceivedListener?.onDeviceConnected(true)
            } catch (e: IOException) {
                Log.e("UsbSerialCommunication", "Error communicating over USB", e)
            }
        }
    }
    private suspend fun listenForData(port: UsbSerialPort, scope: CoroutineScope) {
        val buffer = ByteArray(8)
        var receivedBytes = ByteArray(16)
        //Log.e("UsbSerialCommunication", "Error reading data1")
        try {
            while (scope.isActive) {
              //  Log.e("UsbSerialCommunication", "Error reading data2")
                val numBytesRead = port.read(buffer, 2000)
                if (numBytesRead > 0) {
                    receivedBytes += buffer.copyOf(numBytesRead)
                    val receivedDataString = String(receivedBytes, Charsets.UTF_8).trim()
                   // Log.e("UsbSerialCommunication", "Error reading data3")
                    withContext(Dispatchers.Main) {
                        val pattern = Regex("""^\d+\.\d{3}$""")
                        val cleanedWeight = receivedDataString.replace(Regex("^\\+0"), "")
                        if (cleanedWeight.matches(pattern)) {
                            addWeightData(cleanedWeight)
                        }
                    }
                    if (receivedBytes.size > 8) {
                        receivedBytes = ByteArray(0)
                    }
                  //  Log.e("UsbSerialCommunication2", "${getUniqueData()}")
                    getUniqueData()?.let { dataReceivedListener?.onDataReceived(it) }
                   // Log.e("UsbSerialCommunication", "Error reading data5")
                }
            }
        } catch (e: IOException) {
            Log.e("UsbSerialCommunication", "Error reading data", e)
        } finally {
            port.close()
        }
    }
    fun addWeightData(weight: String) {
        val regex = Regex("^\\d+(\\.\\d+)?$")
        if (regex.matches(weight)) {
            if (fifoArray.count { it == weight } > 1 && weight != uniqueData) {
                uniqueData = weight
            }
            fifoArray.addLast(weight)
            if (fifoArray.size > 10) {
                val removed = fifoArray.removeFirst()
                if (fifoArray.count { it == removed } <= 1 && removed == uniqueData)
                {
                    uniqueData = null
                }
            }
        } else {
            Log.e("UsbSerialCommunication", "Invalid weight data: $weight")
        }
    }
    fun getUniqueData(): String? {
        return uniqueData
    }
    fun disconnectFromDevice() {
        try {
            usbSerialPort?.close()
            usbSerialPort = null
            dataReceivedListener?.onDeviceConnected(false)
        } catch (e: IOException) {
            Log.e("UsbSerialCommunication", "Error closing port", e)
        }
    }
    fun unregisterReceiver() {
        context.unregisterReceiver(usbReceiver)
        disconnectFromDevice()
    }
}