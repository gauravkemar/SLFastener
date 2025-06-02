package com.example.slfastener.model.printerprnmodel

data class Location(
    val createdBy: String,
    val createdDate: String,
    val displayName: String,
    val isActive: Boolean,
    val isWarehouse: Boolean,
    val locationCode: String,
    val locationId: Int,
    val locationName: String,
    val locationType: String,
    val modifiedBy: Any,
    val modifiedDate: Any,
    val parentLocationCode: Any,
    val remarks: Any,
    val storageCapacity: Any,
    val warehouseType: Any
)