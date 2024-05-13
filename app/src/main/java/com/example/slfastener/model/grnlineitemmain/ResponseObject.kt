package com.example.slfastener.model.grnlineitemmain

data class ResponseObject(
    val createdBy: String,
    val createdDate: String,
    val defautLocation: Any,
    val gdpoNumber: Any,
    val grnId: Int,
    val grnLineItemUnit: List<GrnLineItemUnit>,
    val grnQty: Int,
    val isActive: Boolean,
    val isQCRequired: Boolean,
    val itemCode: String,
    val itemDescription: String,
    val itemName: String,
    val lineItemId: Int,
    val locationId: Int,
    val mhType: String,
    val modifiedBy: String,
    val modifiedDate: Any,
    val poId: Int,
    val poLineItemId: Int,
    val poLineNo: Int,
    val poNumber: String,
    val poQty: Int,
    val pouom: String,
    val retBalQty: Double,
    val unitPrice: Any
)