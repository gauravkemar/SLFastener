package com.example.slfastener.model.goodsreceipt

data class GRLineUnitItemSelection(
    val Barcode: String,
    val BatchNo: String,
    val isExpirable:Boolean,
    var ExpiryDate: String,
    val InternalBatchNo: String,
    val IsEdit: Boolean,
    val Isdisabled: Boolean,
    var LineItemId: Int,
    var LineItemUnitId: Int,
    var Qty: String,
    val UOM: String,
    val mhType:String,
    var isUpdate:Boolean,
    var isChecked:Boolean

)
