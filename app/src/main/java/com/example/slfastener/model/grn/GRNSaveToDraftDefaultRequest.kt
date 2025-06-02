package com.example.slfastener.model.grn

data class GRNSaveToDraftDefaultRequest(
    val BPCode: String,
    val BPId: Int,
    val BPName: String,
    val Currency: String,
    val GRNId: Int,
    val GRNStatus: String,
    val InvoiceDate: String,
    val InvoiceNumber: String,
    val POIds: String,
    val TransactionType: String,
    val UnitPrice: String
)