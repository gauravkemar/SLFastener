package com.example.slfastener.model.goodsreceipt

data class GetAllItemMasterResponse(
    val aUoM: String,
    val code: String,
    val createdBy: String,
    val createdDate: String,
    val defaultLocationCode: String,
    val description: String,
    val invFrequency: String,
    val isActive: Boolean,
    val isExpirable: Boolean,
    val isPurchasable: Boolean,
    val isQCRequired: Boolean,
    val isSalable: Boolean,
    val itemGroup: String,
    val itemId: Int,
    val mhType: String,
    val modifiedBy: String,
    val modifiedDate: String,
    val msq: Int,
    val name: String,
    val uoM: String,
    val uoMRatio: Double
)