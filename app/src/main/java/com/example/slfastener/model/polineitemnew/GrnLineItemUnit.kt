package com.example.slfastener.model.polineitemnew

data class GrnLineItemUnit(
/*    val barcode: String,
    val internalBatchNo: String,
    val isqtyExceeded: Boolean,
    val lineItemId: Int,
    val lineItemUnitId: Int,
    val previousValue: Int,
    val qty: Int,
    val supplierBatchNo: String,
    val uom: String,
    val weight: String*/

val lineItemId: Int,
val lineItemUnitId: Int,
val UOM: String,
val barcode: String,
val enabledddButton: Boolean,
val internalBatchNo: String,
val isdisabled: Boolean,
val isqtyExceeded: Boolean,
val previousValue: Int,
val qty: Double,
val supplierBatchNo: String,
val uom: String,
val weight: String
)

