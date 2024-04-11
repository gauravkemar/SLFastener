package com.example.slfastener.model

data class BatchInfoListModel(
    var ExpiryDate: String,
    var GDPONumber: String,
    var ReceivedQty: String,
    val balanceQuantity: Int,
    val generatedBarcodeNo: String,
    var itemCode: String,
    var itemDescription: String,
    var itemName: String,
    var lineNumber: String,
    var materialType: String,
    var poId: Int,
    var poLineItemId: Int,
    var poNumber:  String,
    var poQuantity: Int,
    var poUnitPrice: Double,
    var posapLineItemNumber: String,
    var pouom: String,
    var batchBarcodeNo:String,
    var isUpdate:Boolean
)