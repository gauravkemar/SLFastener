package com.example.slfastener.model.grndraftdata

data class GrnTransaction(
    val bpCode: String,
    val bpId: Int,
    val bpName: String,
    val createdBy: String,
    val createdDate: String,
    val currency: String,
    val grnId: Int,
    val grnLineItems: List<GrnLineItem>,
    val grnStatus: String,
    val invoiceDate: String,
    val invoiceNumber: String,
    val isActive: Boolean,
    val isStockSynced: Boolean,
    val kgrnDate: String,
    val kgrnNumber: String,
    val modifiedBy: String,
    val modifiedDate: Any,
    val poIds: String,
    val sapResponseMessage: String,
    val sapSubmittedDate: Any,
    val sgrnDate: Any,
    val sgrnNumber: Any,
    val transactionType: String
)