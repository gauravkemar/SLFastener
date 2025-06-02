package com.example.slfastener.model.getalltax

data class GetAllTaxItem(
    val grnLineItems: List<Any>,
    val percentage: Double,
    val taxCode: String,
    val taxId: Int,
    val taxName: String
)