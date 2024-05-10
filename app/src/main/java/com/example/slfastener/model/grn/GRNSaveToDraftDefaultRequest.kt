package com.example.slfastener.model.grn

data class GRNSaveToDraftDefaultRequest(
    val GRNId:Int,
    val BPCode: String,
    val POIds: String,
    val BPId: Int,
    val GDPONumber: String?,
    val GRNStatus: String,
    val InvoiceDate: String,
    val InvoiceNumber: String,
    val TransactionType: String,
    val Currency: String
)