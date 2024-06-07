package com.example.slfastener.model.goodsreceipt.grdraft

data class GrLineItemUnit(
    val barcode: String,
    val batchNo: String,
    val createdBy: String,
    val createdDate: String,
    val expiryDate: String,
    val internalBatchNo: String,
    val isActive: Boolean,
    val lineItemId: Int,
    val lineItemUnitId: Int,
    val modifiedBy: String,
    val modifiedDate: Any,
    val qty: Double,
    val uom: String
)