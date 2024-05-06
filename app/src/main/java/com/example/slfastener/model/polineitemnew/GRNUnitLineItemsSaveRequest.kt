package com.example.slfastener.model.polineitemnew

data class GRNUnitLineItemsSaveRequest(
    val GRNId: Int,
    val IsDGPOMandatory: Boolean,
    val IsGRNLineItem: Boolean,
    val balQTY: Double,
    val currency: String,
    val grnLineItemUnit: List<GrnLineItemUnit>,
    val isDelete: Boolean,
    val itemCode: String,
    val itemDescription: String,
    val itemName: String,
    val mhType: String,
    val poId: Int,
    val poLineItemId: Int,
    val poLineNo: Int,
    val poNumber: String,
    val poqty: Int,
    val posapLineItemNumber: String,
    val pouom: String,
    val quantityReceived: String,
    val unitPrice: Any
)