package com.example.slfastener.model.grnlineitemmain

data class GrnLineItemUnit(
    val barcode: String,
    val createdBy: String,
    val createdDate: String,
    val expiryDate: Any,
    val internalBatchNo: String,
    val isActive: Boolean,
    val lineItemId: Int,
    val lineItemUnitId: Int,
    val modifiedBy: String,
    val modifiedDate: Any,
    val qty: Int,
    val supplierBatchNo: String,
    val uom: String
)