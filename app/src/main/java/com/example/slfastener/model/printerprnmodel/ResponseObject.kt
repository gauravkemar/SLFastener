package com.example.slfastener.model.printerprnmodel

data class ResponseObject(
    val areaPart: String,
    val createdBy: String,
    val createdDate: String,
    val defaultPrinterIPId: Int,
    val deviceIP: String,
    val deviceLocationMappingId: Int,
    val deviceName: String,
    val deviceType: String,
    val isActive: Boolean,
    val location: Any,
    val locationId: Int,
    val locationName: Any,
    val modifiedBy: String,
    val modifiedDate: String,
    val port: Int,
    val remark: String
)