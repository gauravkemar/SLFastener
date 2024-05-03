package com.example.slfastener.model.offlinebatchsave

data class GrnLineItemUnitStore(
    val UOM: String,
    val barcode: String,
    val expiryDate: String,
    val internalBatchNo: String,
    val isChecked: Boolean,
    val lineItemId: Int,
    val lineItemUnitId: Int,
    var recevedQty: String,
    val supplierBatchNo: String,
    var isUpdate:Boolean
)