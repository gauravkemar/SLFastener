package com.example.slfastener.model.generalrequest

data class GeneralResponse(
    val errorMessage: Any,
    val exception: Any,
    val responseMessage: String,
    val statusCode: Int
)