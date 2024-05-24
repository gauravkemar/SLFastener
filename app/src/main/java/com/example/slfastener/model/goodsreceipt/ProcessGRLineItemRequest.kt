package com.example.slfastener.model.goodsreceipt

data class ProcessGRLineItemRequest(
    val DefautLocation: String,
    val GRId: Int,
    val GRLineItemUnit: List<GRLineItemUnit>,
    val ItemCode: String,
    val ItemDescription: String,
    val ItemName: String,
    val LineItemId: Int,
    val LocationId: Int,
    val MHType: String,
    val Qty: Double,
    val UOM: String
)