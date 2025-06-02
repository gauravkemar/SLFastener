package com.example.slfastener.model.grn

data class ResponseObject(
    val grnId: Int,
    val kgrnNumber: String,
    val sgrnNumber: String?,
    val kgrnDate: String,
    val sgrnDate: String?,
    val bpId: Int,
    val bpCode: String,
    val bpName: String,
    val invoiceNumber: String,
    val invoiceDate: String,
    val grnStatus: String,
    val transactionType: String,
    val currency: String,
    val poIds: String,
    val sapSubmittedDate: String?,
    val sapResponseMessage: String,
    val isStockSynced: Boolean,
    val grnLineItems: List<Any>,
    val isActive: Boolean,
    val createdBy: String,
    val createdDate: String,
    val modifiedBy: String,
    val modifiedDate: String?
)