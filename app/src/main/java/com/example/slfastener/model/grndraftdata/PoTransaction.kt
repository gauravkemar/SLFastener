package com.example.slfastener.model.grndraftdata

data class PoTransaction(
    val bpCode: String,
    val bpName: Any,
    val createdBy: String,
    val createdDate: String,
    val isActive: Boolean,
    val modifiedBy: String,
    val modifiedDate: Any,
    val poCurrency: Any,
    val poDate: String,
    val poId: Int,
    val poLineItems: List<Any>,
    val poNumber: String,
    val poStatus: String,
    val poValidity: String,
    val posapNumber: String
)