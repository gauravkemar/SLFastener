package com.example.slfastener.model.goodsreceipt

import com.example.slfastener.model.getalllocation.GetAllWareHouseLocationResponse

data class GetAllItemMasterSelection(
    val grId:Int,
    val auom: String,
    val code: String,
    var defaultLocationCode:String,
    var LineItemId:Int,
    var LocationId:Int,
    val description: String,
    val isExpirable: Boolean,
    val isPurchasable: Boolean,
    val isQCRequired: Boolean,
    val isSalable: Boolean,
    val itemGroup: String,
    val itemId: Int,
    val mhType: String,
    val msq: Int,
    val name: String,
    val uom: String,
    val uomRatio: Double,
    var isSelected:Boolean,
    var balQty:String,
    var grLineItemUnit: MutableList<GRLineUnitItemSelection>?,
    val getAllLocation: MutableList<GetAllWareHouseLocationResponse>,
    var isUpdated: Boolean
)
