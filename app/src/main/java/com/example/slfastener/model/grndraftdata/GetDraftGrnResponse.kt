package com.example.slfastener.model.grndraftdata

data class GetDraftGrnResponse(
    val grnTransaction: GrnTransaction,
    val poIds: List<Int>,
    val poTransaction: List<PoTransaction>
)