package com.example.slfastener.model.offlinebatchsave

import com.example.slfastener.model.getalllocation.GetAllWareHouseLocationResponse
import com.example.slfastener.model.getalltax.GetAllTaxItem

data class CustomPoLineItemSelectionModel(
    val isQCRequired: Boolean,
    val isExpirable: Boolean,
    var lineItemId:Int,
    var balQTY: Double,
    val currency: String?,
    var grnLineItemUnit: MutableList<CustomGrnLineItemUnit?>?,
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
    val pouom: String?,
    var quantityReceived: String,
    var isSelected: Boolean,
    val unitPrice: Int?,
    val getAllLocation: MutableList<GetAllWareHouseLocationResponse>,
    var locationId: Int,
    var isUpdated:Boolean,
    var getAllTax: MutableList<GetAllTaxItem>?,
    var taxId:Int,
    var totalUnit:Int,
    var discountAmount:Double?,
    var lineAmount:String,

    )