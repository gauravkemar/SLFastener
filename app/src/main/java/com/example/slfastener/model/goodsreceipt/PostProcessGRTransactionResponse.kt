package com.example.slfastener.model.goodsreceipt

import com.example.slfastener.model.grnlineitemmain.ResponseObject

data class PostProcessGRTransactionResponse(
    val errorMessage: Any,
    val exception: Any,
    val responseMessage: String,
    val responseObject: PostProcessGRTransactionData,
    val statusCode: Int
)
