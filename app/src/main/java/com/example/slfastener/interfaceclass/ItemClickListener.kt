package com.example.slfastener.interfaceclass

import com.example.slfastener.model.BatchInfoListModel

interface ItemClickListener {
    fun onItemClick(position: Int, batchInfoListModel: BatchInfoListModel)
}