package com.example.slfastener.model.offlinebatchsave

data class CustomGrnLineItemUnit(
    val UOM: String?,
    val mhType:String,
    val barcode: String,
    var expiryDate: Any?,
    val isExpirable:Boolean,
    val internalBatchNo: String,
    var isChecked: Boolean,
    var lineItemId: Int,
    var lineItemUnitId: Int,
    var recevedQty: String,
    val supplierBatchNo: String,
    var isUpdate:Boolean,
    var totalUnits:Int
)