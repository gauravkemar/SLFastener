package com.example.slfastener.model.offlinebatchsave

data class PoLineItemSelectionModelNewStore(
    var lineItemId:Int,
    var balQTY: Double,
    val currency: String?,
    var grnLineItemUnit: MutableList<GrnLineItemUnitStore>?,
    val itemCode: String,
    val itemDescription: String,
    val itemName: String,
    val mhType: String,
    val poId: Int,
    val poLineItemId: Int,
    val poLineNo: Int,
    val poNumber: String,
    val poqty: Double,
    val posapLineItemNumber: String,
    val pouom: String,
    var quantityReceived: String,
    var isSelected: Boolean,
    var GDPONumber: Any?,
    val unitPrice: Any?
)