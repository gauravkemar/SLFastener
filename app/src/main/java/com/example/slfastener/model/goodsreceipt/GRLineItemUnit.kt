package com.example.slfastener.model.goodsreceipt

data class GRLineItemUnit(
    val Barcode: String,
    val BatchNo: String,
    val ExpiryDate: String,
    val InternalBatchNo: String,
    val IsEdit: Boolean,
    val Isdisabled: Boolean,
    val LineItemId: Int,
    val LineItemUnitId: Int,
    val Qty: Double,
    val UOM: String
)