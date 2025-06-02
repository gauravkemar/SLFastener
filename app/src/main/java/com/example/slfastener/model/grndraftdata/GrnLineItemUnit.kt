package com.example.slfastener.model.grndraftdata

data class GrnLineItemUnit(
    val barcode: String,
    val expiryDate: String,
    val isQCRequired: Boolean,
    val kBatchNo: String,
    val lineItemId: Int,
    val lineItemUnitId: Int,
    val qcStatus: Any,
    val qty: Double,
    val supplierBatchNo: String,
    val uoM: String
)