package com.example.slfastener.model.polineitemnew

import com.example.slfastener.model.BatchInfoListModel

data class PoLineItemSelectionWithBatchListModel(
    val itemCode: String,
    val itemDescription: String,
    val itemName: String,
    val lineNumber: Int,
    val poId: Int,
    val poLineItemId: Int,
    val poQuantity: Int,
    val poUnitPrice: Any?,
    val posapLineItemNumber: String,
    val pouom: String,
    val poNumber: String,
    val GDPONumber: String,
    val ExpiryDate:String,
    val ReceivedQty:String,
    var isSelected: Boolean?,
    var materialType: String,
    var batchInfoListModel: ArrayList<BatchInfoListModel>?
    )
