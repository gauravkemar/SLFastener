package com.example.slfastener.model.grndraftdata

data class PoLineItem(
    val poLineItemId: Int,
    val poId: Int,
    val posapLineItemNumber: String,
    val itemCode: String,
    val itemName: String,
    val locationId: Int,
    val itemDescription: String,
    val poLineNo: Int,
    val poUoM: String,
    val mhType: String,
    val poQty: Double,
    val balQty: Double,
    val unitPrice: Double,
    val isExpirable: Boolean,
    val isQCRequired: Boolean,
    val isActive: Boolean,
    val createdBy: String,
    val createdDate: String,
    val modifiedBy: String?,
    val modifiedDate: String?
)