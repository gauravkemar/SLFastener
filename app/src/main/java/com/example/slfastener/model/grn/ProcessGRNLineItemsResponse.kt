package com.example.slfastener.model.grn

data class ProcessGRNLineItemsResponse(
    val errorMessage: Any,
    val exception: Any,
    val responseMessage: String,
    val responseObject: ResponseObjectX,
    val statusCode: Int
)