package com.example.slfastener.model.grnsavebatches

data class GRNLineItemUnit(
    val Barcode: String,
    val InternalBatchNo: String,
    val LineItemId: Int,
    val LineItemUnitId: Int,
    val QTY: Int,
    val SupplierBatchNo: String,
    val UOM: String
)