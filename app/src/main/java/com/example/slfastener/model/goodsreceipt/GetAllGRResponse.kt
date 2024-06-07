package com.example.slfastener.model.goodsreceipt

data class GetAllGRResponse(
    val bpCode: String,
    val bpId: Int,
    val bpName: String,
    val createdBy: String,
    val createdDate: String,
    val grId: Int,
    val grLineItems: List<Any>,
    val grStatus: String,
    val isActive: Boolean,
    val isStockSynced: Boolean,
    val kgrDate: String,
    val kgrNumber: String,
    val modifiedBy: String,
    val modifiedDate: Any,
    val remark: String,
    val sapResponseMessage: String,
    val sapSubmittedDate: Any,
    val sgrDate: Any,
    val sgrNumber: Any
)