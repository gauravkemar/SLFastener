package com.example.slfastener.model

data class PoLineItemSelectionModel(
    val itemCode: String,
    val itemDescription: String,
    val itemName: String,
    val lineNumber: Int,
    val poId: Int,
    val poLineItemId: Int,
    val poQuantity: Double,
    val poUnitPrice: Any?,
    val posapLineItemNumber: String,
    val pouom: String,
    val poNumber: String,
    val GDPONumber: String,
    val ExpiryDate:String,
    val ReceivedQty:String,
    var isSelected: Boolean,
    var materialType: String,
    var batchInfoListModel: MutableList<BatchInfoListModel>?
    )
