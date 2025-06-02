package com.example.slfastener.model.grn

data class ProcessGRNLineItemsResponse(
    val responseObject: ResponseObject?,
    val errorMessage: String?,
    val exception: String?,
    val responseMessage: String,
    val statusCode: Int
)