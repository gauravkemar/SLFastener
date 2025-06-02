package com.example.slfastener.model.grndraftdata

data class GetDraftGrnResponse(
    val poIds: List<Int>,
    val grnTransaction: GrnTransaction,
    val poTransaction: List<PoTransaction>
)