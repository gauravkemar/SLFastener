package com.example.slfastener.model.grnlineitemmain

import com.example.slfastener.model.grndraftdata.GrnLineItemUnit

data class ResponseObject(
    val amount: String,
    val createdBy: String,
    val createdDate: String,
    val defautLocation: Any,
    val gdpoNumber: Any,
    val grnId: Int,
    val grnLineItemUnit: List<GrnLineItemUnit>,
    val grnQty: Double,
    val isActive: Boolean,
    val isExpirable: Boolean,
    val isQCRequired: Boolean,
    val itemCode: String,
    val itemDescription: String,
    val itemName: String,
    val lineItemId: Int,
    val locationId: Int,
    val mhType: String,
    val modifiedBy: String,
    val modifiedDate: String,
    val poId: Int,
    val poLineItemId: Int,
    val poLineNo: Int,
    val poNumber: String,
    val poQty: Double,
    val poUoM: String,
    val retBalQty: Double,
    val taxId: Int,
    val totalUnit: Int,
    val unitPrice: Double
)
