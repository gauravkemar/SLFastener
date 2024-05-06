package com.example.slfastener.model.offlinebatchsave

data class PoLineItemSelectionModelNewStore(
    var balQTY: Double,
    val currency: String,
    var grnLineItemUnit: MutableList<GrnLineItemUnitStore>?,
    val itemCode: String,
    val itemDescription: String,
    val itemName: String,
    val mhType: String,
    val poId: Int,
    val poLineItemId: Int,
    val poLineNo: Int,
    val poNumber: String,
    val poqty: Int,
    val posapLineItemNumber: String,
    val pouom: String,
    var quantityReceived: String,
    var isSelected: Boolean,
    val GDPONumber: String,
    val unitPrice: String
)