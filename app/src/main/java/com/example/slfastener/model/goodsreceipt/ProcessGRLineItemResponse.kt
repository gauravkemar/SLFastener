package com.example.slfastener.model.goodsreceipt

data class ProcessGRLineItemResponse(
    val errorMessage: Any,
    val exception: Any,
    val responseMessage: String,
    val responseObject: ResponseObject,
    val statusCode: Int
)