package com.example.slfastener.model.grnlineitemmain

data class GrnLineItemResponse(
    val errorMessage: Any,
    val exception: Any,
    val responseMessage: String,
    val responseObject: ResponseObject,
    val statusCode: Int
)