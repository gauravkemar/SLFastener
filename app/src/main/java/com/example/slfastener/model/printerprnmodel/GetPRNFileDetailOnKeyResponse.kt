package com.example.slfastener.model.printerprnmodel

data class GetPRNFileDetailOnKeyResponse(
    val createdBy: String,
    val createdDate: String,
    val isActive: Boolean,
    val labelKey: String,
    val modifiedBy: String,
    val modifiedDate: String,
    val prnContent: String,
    val prnDescription: String,
    val prnFileName: String,
    val prnId: Int
)