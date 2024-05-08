package com.example.slfastener.model




/*data class GetPOsAndLineItemsOnPOIdsResponse(
    val bpCode: String,
    val bpName: String,
    val createdBy: String,
    val createdDate: String,
    val isActive: Boolean,
    val modifiedBy: String,
    val modifiedDate: String,
    val poDate: String,
    val poId: Int,
    val poLineItems: List<PoLineItem>,
    val poNumber: String,
    val poStatus: String,
    val poValidity: String,
    val posapNumber: String
)*/
data class GetPOsAndLineItemsOnPOIdsResponse(
    val bpCode: String,
    val bpName: String,
    val createdBy: String,
    val createdDate: String,
    val isActive: Boolean,
    val modifiedBy: String,
    val modifiedDate: Any,
    val poCurrency: String,
    val poDate: String,
    val poId: Int,
    val poLineItems: List<PoLineItem>,
    val poNumber: String,
    val poStatus: String,
    val poValidity: String,
    val posapNumber: String
)