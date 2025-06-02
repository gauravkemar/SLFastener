package com.example.slfastener.model.grndraftdata

data class PoTransaction(
    val poId: Int,
    val poNumber: String,
    val sappoNumber: String,
    val poDate: String,
    val bpCode: String,
    val bpName: String?,
    val poValidity: String,
    val poCurrency: String?,
    val poStatus: String,
    val additionalExpenses: List<String>,
    val poLineItems: List<PoLineItem>,
    val isActive: Boolean,
    val createdBy: String,
    val createdDate: String,
    val modifiedBy: String?,
    val modifiedDate: String?
)