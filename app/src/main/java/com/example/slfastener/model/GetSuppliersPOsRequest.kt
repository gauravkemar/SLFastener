package com.example.slfastener.model

data class GetSuppliersPOsRequest(
    val code: String,
    val isActive: Boolean,
    val text: String,
    val value: Int
)