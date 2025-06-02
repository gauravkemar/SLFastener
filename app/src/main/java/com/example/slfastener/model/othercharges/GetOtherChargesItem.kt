package com.example.slfastener.model.othercharges

data class GetOtherChargesItem(
    val expenseAmount: Double,
    val expenseCode: String,
    val expenseName: String,
    val expensesId: Int,
    val poId: Int,
    val poNumber: String,
    val taxCode: String,
    var calculatedExpenseAmount:String,
)