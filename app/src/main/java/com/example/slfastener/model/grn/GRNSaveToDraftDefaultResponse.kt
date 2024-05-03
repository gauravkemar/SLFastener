package com.example.slfastener.model.grn

data class GRNSaveToDraftDefaultResponse(
    val errorMessage: Any,
    val exception: Any,
    val responseMessage: String,
    val responseObject: ResponseObject,
    val statusCode: Int
)