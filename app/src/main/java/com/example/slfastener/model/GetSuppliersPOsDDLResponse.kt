package com.example.slfastener.model

data class GetSuppliersPOsDDLResponse(
    val code: String,
    val isActive: Boolean,
    val text: String,
    val value: Int,
    var isChecked:Boolean
)