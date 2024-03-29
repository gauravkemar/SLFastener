package com.example.slfastener.model.grn

data class GrnMainAddListResponse(
    val BarcodeNo: String,
    val BatchNo: String,
    val DGPONo: String,
    val ExpiryDt: String,
    val ItemDesc: String,
    val LineItem: String,
    val OpenQty: String,
    val PONo: String,
    val PUOM: String,
    val Qty: String
)