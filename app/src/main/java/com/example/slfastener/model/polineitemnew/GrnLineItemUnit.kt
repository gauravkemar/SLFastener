package com.example.slfastener.model.polineitemnew

data class GrnLineItemUnit(
    val barcode: String,
    val internalBatchNo: String,
    val isqtyExceeded: Boolean,
    val previousValue: Int,
    val qty: Int,
    val supplierBatchNo: String,
    val uom: String,
    val weight: String
)