package com.example.slfastener.model.offlinebatchsave

data class GrnLineItemUnitStore(
    val UOM: String,
    val mhType:String,
    val barcode: String,
    var expiryDate: Any?,
    val isExpirable:Boolean,
    val internalBatchNo: String,
    val isChecked: Boolean,
    var lineItemId: Int,
    var lineItemUnitId: Int,
    var recevedQty: String,
    val supplierBatchNo: String,
    var isUpdate:Boolean
)