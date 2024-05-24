package com.example.slfastener.model.goodsreceipt

data class GRLineUnitItemSelection(
    val Barcode: String,
    val BatchNo: String,
    val isExpirable:Int,
    var ExpiryDate: String,
    val InternalBatchNo: String,
    val IsEdit: Boolean,
    val Isdisabled: Boolean,
    val LineItemId: Int,
    val LineItemUnitId: Int,
    var Qty: String,
    val UOM: String,
    val mhType:String,
    var isUpdate:Boolean
    )
