package com.example.slfastener.model.goodsreceipt

import com.example.slfastener.model.BatchInfoListModel
import com.example.slfastener.model.getalllocation.GetAllWareHouseLocationResponse
import com.example.slfastener.model.offlinebatchsave.GrnLineItemUnitStore

data class GetAllItemMasterSelection(
    val grId:Int,
    val auom: String,
    val code: String,
    val createdBy: String,
    val createdDate: String,
    var defaultLocationCode:String,
    var LineItemId:Int,
    var LocationId:Int,
    val description: String,
    val isActive: Boolean,
    val isExpirable: Int,
    val isPurchasable: Boolean,
    val isQCRequired: Boolean,
    val isSalable: Boolean,
    val itemGroup: String,
    val itemId: Int,
    val mhType: String,
    val modifiedBy: String,
    val modifiedDate: Any,
    val msq: Int,
    val name: String,
    val uom: String,
    val uomRatio: Double,
    var isSelected:Boolean,
    var balQty:String,
    var grLineItemUnit: MutableList<GRLineUnitItemSelection>?,
    val getAllLocation: MutableList<GetAllWareHouseLocationResponse>,
    val isUpdated: Boolean
)
