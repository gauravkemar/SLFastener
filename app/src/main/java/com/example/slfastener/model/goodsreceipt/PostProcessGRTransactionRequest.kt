package com.example.slfastener.model.goodsreceipt

data class PostProcessGRTransactionRequest(
    val BPCode: String,
    val BPId: Int,
    val BPName: String,
    val GRId: Int,
    val Remark: String
)