package com.example.slfastener.model.grnsavebatches

data class ProcessGRNLineItemsRequest(
    val GRNId: Int,
    val GRNLineItemUnit: List<GRNLineItemUnit>,
    val GRNQty: Int,
    val IsQCRequired: Boolean,
    val ItemCode: String,
    val LocationId: Int,
    val POId: Int,
    val POLineNo: Int,
    val PONumber: String,
    val POQty: Int,
    val POUOM: String,
    val UnitPrice: Int,
    val retBalQty: Int
)