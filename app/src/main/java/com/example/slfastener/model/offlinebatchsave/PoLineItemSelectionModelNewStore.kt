package com.example.slfastener.model.offlinebatchsave

import com.example.slfastener.model.getalllocation.GetAllWareHouseLocationResponse

data class PoLineItemSelectionModelNewStore(
    val isQCRequired: Boolean,
    val isExpirable: Boolean,
    var lineItemId:Int,
    var balQTY: Double,
    val currency: String?,
    var grnLineItemUnit: MutableList<GrnLineItemUnitStore>?,
    val itemCode: String,
    val itemDescription: String,
    val itemName: String,
    val mhType: String,
    val poId: Int,
    val poLineItemId: Int,
    val poLineNo: Int,
    val poNumber: String,
    val poqty: Double,
    val posapLineItemNumber: String,
    val pouom: String,
    var quantityReceived: String,
    var isSelected: Boolean,
    var GDPONumber: Any?,
    val unitPrice: Any?,
    val getAllLocation: MutableList<GetAllWareHouseLocationResponse>,
    var locationId: Int,
    var isUpdated:Boolean
)