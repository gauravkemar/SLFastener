package com.example.slfastener.model.offlinebatchsave

data class PoLineItemSelectionModelNewStore(
    val balQTY: Int,
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
    val quantityReceived: String,
    var isSelected: Boolean,
    val GDPONumber: String,
    val unitPrice: String
)