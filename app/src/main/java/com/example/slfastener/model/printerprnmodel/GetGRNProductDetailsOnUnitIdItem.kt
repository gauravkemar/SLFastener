package com.example.slfastener.model.printerprnmodel

data class GetGRNProductDetailsOnUnitIdItem(
    val barcodeValue: String,
    val currDate: String,
    val expiryDate: String,
    val internalBatch: String,
    val isExpirable: Boolean,
    val itemCode: String,
    val itemDescription: String,
    val stockQty: String,
    val supplierBatchNumber: String,
    val supplierName: String,
    val uoM: String,
    val userName: String
)