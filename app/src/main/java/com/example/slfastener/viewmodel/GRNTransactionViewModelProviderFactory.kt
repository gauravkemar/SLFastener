package com.example.slfastener.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.demorfidapp.repository.SLFastenerRepository
import com.kemarport.kdmsmahindra.viewmodel.PostRFIDDataViewModel

class GRNTransactionViewModelProviderFactory (
    private val application: Application,
    private val rfidReaderRepository: SLFastenerRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GRNTransactionViewModel(application, rfidReaderRepository) as T
    }
}