package com.example.slfastener.model.goodsreceipt

import com.example.slfastener.model.goodsreceipt.grdraft.GrLineItemUnit

data class ResponseObject(
    val createdBy: String,
    val createdDate: String,
    val defautLocation: String,
    val grId: Int,
    val grLineItemUnit: List<GrLineItemUnit>,
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
    val qty: Double,
    val unitPrice: Any,
    val uom: String
)