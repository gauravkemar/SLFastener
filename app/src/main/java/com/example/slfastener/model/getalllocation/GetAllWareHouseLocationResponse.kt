package com.example.slfastener.model.getalllocation

data class GetAllWareHouseLocationResponse(
    val createdBy: String,
    val createdDate: String,
    val displayName: String,
    val isActive: Boolean,
    val isStorable: Boolean,
    val isWarehouse: Boolean,
    val locationCode: String,
    val locationId: Int,
    val locationName: String,
    val locationType: String,
    val modifiedBy: String,
    val modifiedDate: Any,
    val parentLocationCode: Any,
    val remarks: Any,
    val storageCapacity: Any,
    val warehouseId: Int,
    val warehouseType: String
)