package com.example.slfastener.model.generalrequest

data class GrnBatchDeleteResponse(
    val errorMessage: Any,
    val exception: Any,
    val responseMessage: String,
    val responseObject: Double,
    val statusCode: Int
)