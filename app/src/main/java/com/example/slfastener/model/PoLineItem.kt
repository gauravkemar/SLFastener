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
    val balanceQuantity: Int,
    val createdBy: String,
    val createdDate: String,
    val isActive: Boolean,
    val itemCode: String,
    val itemDescription: String,
    val itemName: String,
    val lineNumber: String,
    val materialType: String,
    val modifiedBy: String,
    val modifiedDate: Any,
    val poId: Int,
    val poLineItemId: Int,
    val poQuantity: Int,
    val poUnitPrice: Double,
    val posapLineItemNumber: String,
    val pouom: String
)