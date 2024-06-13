package com.example.slfastener.helper.weighing

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbEndpoint
import android.hardware.usb.UsbManager
import android.util.Log
import com.example.slfastener.helper.DataReceivedListener
import com.hoho.android.usbserial.driver.UsbSerialDriver
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
class UsbSerialCommunication(private val context: Context) {
    private var usbDevice: UsbDevice? = null
    private var usbDeviceConnection: UsbDeviceConnection? = null
    private var usbEndpointIn: UsbEndpoint? = null
    private lateinit var usbSerialPort: UsbSerialPort
    private val ACTION_USB_PERMISSION = "com.example.demoprojscreen.USB_PERMISSION"
    private var isPermissionRequested = false
    private val fifoArray = ArrayDeque<String>(10) // FIFO array with maximum size 10
    private var uniqueData: String? = null
    private var usbManager: UsbManager? = null
    private var dataReceivedListener: DataReceivedListener? = null
    init {
        usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
        requestPermission()
        findWeightMachine()
    }
    fun setDataReceivedListener(listener: DataReceivedListener) {
        dataReceivedListener = listener
    }
    fun findWeightMachine() {
        val deviceList: HashMap<String, UsbDevice>? = usbManager?.deviceList
        deviceList?.values?.forEach { device ->
            if (device.vendorId == 6790 && device.productId == 29987) { // Adjust vendorId and productId according to your weight machine
                usbDevice = device
                setupUsbSerial()
                return
            }
        }
    }
    fun requestPermission() {
        try {
            if (usbDevice != null && !isPermissionRequested) {
                val permissionIntent = PendingIntent.getBroadcast(context, 0, Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_IMMUTABLE)
                usbManager?.requestPermission(usbDevice, permissionIntent)
                isPermissionRequested = true
            }
        }catch (e:Exception)
        {
            Log.e("Exception: ", "$e")
        }
    }
    fun setupUsbSerial() {
        try {
            val availableDrivers: List<UsbSerialDriver>? =
                UsbSerialProber.getDefaultProber().findAllDrivers(usbManager)
            if (availableDrivers == null || availableDrivers.isEmpty()) {
                Log.e("UsbSerialCommunication", "No USB devices found")
                return
            }
            val driver: UsbSerialDriver = availableDrivers[0]
            val device: UsbDevice = driver.device
            val connection = usbManager?.openDevice(device) ?: run {
                Log.e("UsbSerialCommunication", "Failed to open device")
                return
            }
            val port: UsbSerialPort = driver.ports[0]
            try {
                GlobalScope.launch {
                    port.open(connection)
                    port.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)
                    val buffer = ByteArray(8)
                    var receivedBytes = ByteArray(16)

                    while (isActive) {
                        try {
                            val numBytesRead = port.read(buffer, 2000)
                            if (numBytesRead > 0) {
                                receivedBytes += buffer.copyOf(numBytesRead)
                                val receivedDataString = String(receivedBytes, Charsets.UTF_8).toString().trim()
                                withContext(Dispatchers.Main) {
                                   // Log.d("Weight", "Received weight: $receivedDataString grams")
                                    val pattern: Regex = """^\d+\.\d{3}$""".toRegex()
                                    val cleanedWeight = receivedDataString.replace(Regex("^\\+0"), "")
                                    if(cleanedWeight.matches(pattern))
                                    {
                                        addWeightData(cleanedWeight)
                                    }
                                }
                                if(receivedBytes.size>8)
                                {
                                    receivedBytes = ByteArray(0) // Reset received bytes
                                }
                               // Log.d("Weight", "Received weight Array: ${getFifoArray()} grams")
                               // Log.d("Weight", "Received weight Unique: ${getUniqueData()} grams")

                                getUniqueData()?.let { dataReceivedListener?.onDataReceived(it) } // Notify listener
                            }
                        }catch (e:Exception)
                        {
                            Log.d("Weight", "Exceptiont: $e")
                        }
                    }
                }
            } catch (e: IOException) {
                Log.e("UsbSerialCommunication", "Error communicating over USB", e)
            } finally {
                try {
                    port.close()
                } catch (e: IOException) {
                    Log.e("UsbSerialCommunication", "Error closing port", e)
                }
            }
        }
        catch (e:Exception)
        {
            Log.e("UsbSerialCommunication", "$e")
        }
    }

    fun addWeightData(weight: String) {
        val regex = Regex("^\\d+(\\.\\d+)?\$")
        if (regex.matches(weight)) {
            if (fifoArray.count { it == weight } > 1 && weight != uniqueData) {
                uniqueData = weight
            }
            fifoArray.addLast(weight)
            if (fifoArray.size > 10) {
                val removed = fifoArray.removeFirst()
                if (fifoArray.count { it == removed } <= 1 && removed == uniqueData) {
                    uniqueData = null
                }
            }
        } else {
            println("Invalid weight data: $weight")
        }
    }
    fun getFifoArray(): List<String> {
        return fifoArray.toList()
    }

    fun getUniqueData(): String? {
        return uniqueData
    }
}
