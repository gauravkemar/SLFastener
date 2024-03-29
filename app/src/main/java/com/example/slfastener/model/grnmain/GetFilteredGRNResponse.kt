package com.example.slfastener.model.grnmain

data class GetFilteredGRNResponse(
    val bpCode: String,
    val bpId: Int,
    val bpName: String,
    val createdBy: String,
    val createdDate: String,
    val gdpoNumber: String,
    val grnId: Int,
    val grnLineItems: List<Any>,
    val grnStatus: String,
    val invoiceDate: String,
    val invoiceNumber: String,
    val isActive: Boolean,
    val kgrnDate: String,
    val kgrnNumber: String,
    val modifiedBy: String,
    val modifiedDate: Any,
    val sgrnDate: Any,
    val sgrnKye: String,
    val sgrnNumber: String,
    val transactionType: String
)