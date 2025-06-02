package com.example.slfastener.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.demorfidapp.repository.SLFastenerRepository

class AdminSettingViewModelProviderFactory (
    private val application: Application,
    private val rfidReaderRepository: SLFastenerRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AdminSettingViewModel(application, rfidReaderRepository) as T
    }
}