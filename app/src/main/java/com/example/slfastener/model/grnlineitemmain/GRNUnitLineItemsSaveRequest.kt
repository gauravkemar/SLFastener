package com.example.slfastener.model.grnlineitemmain

import com.example.slfastener.model.grndraftdata.GrnLineItemUnit


/*
data class GRNUnitLineItemsSaveRequest(

    val balQTY: Double,
    val currency: String,
    val defautLocation: Any?,
    val grnId: Int,
    val grnLineItemUnit: List<GrnLineItemUnit>,
    val grnQty: Double,
    val isActive: Boolean,
    val isDelete: Boolean,
    val isQCRequired: Boolean,
    val itemCategory: String,
    val itemCode: String,
    val itemDescription: String,
    val itemGroup: String,
    val itemName: String,
    val lineItemId: Int,
    val locationId: Int,
    val mhType: String,
    val poId: Int,
    val poLineItemId: Int,
    val poLineNo: Int,
    val poNumber: String,
    val poQty: Double,
    val posapLineItemNumber: String,
    val pouom: String?,
    val unitPrice: Any?,
    val taxId: Int,
    val totalUnit: Int,
    val amount: String,
)
*/

data class GRNUnitLineItemsSaveRequest(
    val balQty: Double, // Corresponds to balQTY in the old model
    val currency: String,
    val defaultLocation: String?, // Renamed from defautLocation
    val grnId: Int,
    val grnLineItemUnit: List<GrnLineItemUnit>,
    val grnqty: Double,
    val isQCRequired: Boolean,
    val itemCategory: String,
    val itemCode: String,
    val itemDescription: String,
    val itemGroup: String,
    val itemName: String,
    val lineItemId: Int,
    val locationId: Int,
    val mhType: String,
    val poId: Int,
    val poLineItemId: Int,
    val poLineNo: Int,
    val poNumber: String,
    val poQty: Double,
    val posapLineItemNumber: String,
    val poUoM: String,
    val totalUnit: Int,
    val unitPrice: Int,
    val lineAmount: String,

    // New fields that were not present in the old model are added below
    val taxId: Int,
    val discountAmount: Int,
)





