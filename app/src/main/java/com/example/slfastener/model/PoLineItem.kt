package com.example.slfastener.model

/*
data class PoLineItem(
    val createdBy: String,
    val createdDate: String,
    val isActive: Boolean,
    val itemCode: String,
    val itemDescription: String,
    val itemName: String,
    val lineNumber: String,
    val modifiedBy: String,
    val modifiedDate: String,
    val poId: Int,
    val poLineItemId: Int,
    val poQuantity: Int,
    val poUnitPrice: Double,
    val posapLineItemNumber: String,
    val pouom: String,
    val materialType:String,
)*/

data class PoLineItem(
    val balQTY: Double,
    val createdBy: String,
    val createdDate: String,
    val isActive: Boolean,
    val itemCode: String,
    val itemDescription: String,
    val itemName: String,
    val locationCode: String,
    val locationName: String,
    val mhType: String,
    val modifiedBy: String,
    val modifiedDate: Any,
    val poId: Int,
    val poLineItemId: Int,
    val poLineNo: Int,
    val poqty: Double,
    val posapLineItemNumber: String,
    val pouom: String,
    val unitPrice: Any
)