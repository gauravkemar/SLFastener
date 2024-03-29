package com.kemarport.kdmsmahindra.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.demorfidapp.repository.SLFastenerRepository


class PostRFIDDataViewModelFactory (
    private val application: Application,
    private val rfidReaderRepository: SLFastenerRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PostRFIDDataViewModel(application, rfidReaderRepository) as T
    }
}