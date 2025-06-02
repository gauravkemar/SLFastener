package com.example.slfastener.model



/*
data class PoLineItem(
    val isQCRequired: Boolean,
    val isExpirable: Boolean,
    val balQTY: Double,
    val createdBy: String,
    val createdDate: String,
    val isActive: Boolean,
    val itemCode: String,
    val itemDescription: String,
    val itemName: String,
    val locationCode: String,
    val locationId: Int,
    val locationName: String,
    val mhType: String,
    val modifiedBy: String?,
    val modifiedDate: Any?,
    val poId: Int,
    val poLineItemId: Int,
    val poLineNo: Int,
    val poqty: Double,
    val posapLineItemNumber: String,
    val pouom: String,
    val unitPrice: Any
)*/
data class PoLineItem(
    val balQty: Double,
    val createdBy: String,
    val createdDate: String,
    val isActive: Boolean,
    val isExpirable: Boolean,
    val isQCRequired: Boolean,
    val itemCode: String,
    val itemDescription: String,
    val itemName: String,
    val locationId: Int,
    val mhType: String,
    val modifiedBy: Any,
    val modifiedDate: Any,
    val poId: Int,
    val poLineItemId: Int,
    val poLineNo: Int,
    val poQty: Double,
    val poUoM: String,
    val posapLineItemNumber: String,
    val unitPrice: Int
)