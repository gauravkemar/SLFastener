package com.example.slfastener.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.demorfidapp.helper.Constants
import com.example.demorfidapp.helper.Resource
import com.example.demorfidapp.helper.Utils
import com.example.demorfidapp.repository.SLFastenerRepository
import com.example.slfastener.model.generalrequest.GeneralResponse
import com.example.slfastener.model.printerprnmodel.GetAllActiveDeviceLocationDeviceType

import com.example.slfastener.model.printerprnmodel.GetPRNFileDetailOnKeyResponse
import com.example.slfastener.model.printerprnmodel.GetSelfSystemMappingDetailsResponse
import com.example.slfastener.model.printerprnmodel.PrinterDeviceLocationMappingIdRequest
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import java.io.IOException

class AdminSettingViewModel  (
    application: Application,
    private val rfidRepository: SLFastenerRepository
) : AndroidViewModel(application) {

    //for GR
    val getPrnForLabelGRMutable: MutableLiveData<Resource<GetPRNFileDetailOnKeyResponse>> = MutableLiveData()

    fun getPRNFleDetailGR(token:String,baseUrl: String, labelKey:String) {
        viewModelScope.launch {
            safeAPICallGetPRNFleDetailGR(token,baseUrl, labelKey)
        }
    }

    private suspend fun safeAPICallGetPRNFleDetailGR(token:String,baseUrl: String ,labelKey:String) {
        getPrnForLabelGRMutable.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.getPRNFleDetail(token,baseUrl, labelKey)
                getPrnForLabelGRMutable.postValue(handleGetPRNFleDetailGRResponse(response))
            } else {
                getPrnForLabelGRMutable.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> getPrnForLabelGRMutable.postValue(Resource.Error(Constants.CONFIG_ERROR))
                else -> getPrnForLabelGRMutable.postValue(Resource.Error("${t.message}"))
            }
        }
    }

    private fun handleGetPRNFleDetailGRResponse(response: Response<GetPRNFileDetailOnKeyResponse>): Resource<GetPRNFileDetailOnKeyResponse> {
        var errorMessage = ""
        if (response.isSuccessful) {
            response.body()?.let { appDetailsResponse ->
                return Resource.Success(appDetailsResponse)
            }
        } else if (response.errorBody() != null) {
            val errorObject = response.errorBody()?.let {
                JSONObject(it.charStream().readText())
            }
            errorObject?.let {
                errorMessage = it.getString(Constants.HTTP_ERROR_MESSAGE)
            }
        }
        return Resource.Error(errorMessage)
    }

    //for GRN
    val getPrnForLabelGRNMutable: MutableLiveData<Resource<GetPRNFileDetailOnKeyResponse>> = MutableLiveData()

    fun getPRNFleDetailGRN(token:String,baseUrl: String, labelKey:String) {
        viewModelScope.launch {
            safeAPICallGetPRNFleDetailGRN(token,baseUrl, labelKey)
        }
    }

    private suspend fun safeAPICallGetPRNFleDetailGRN(token:String,baseUrl: String ,labelKey:String) {
        getPrnForLabelGRNMutable.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.getPRNFleDetail(token,baseUrl, labelKey)
                getPrnForLabelGRNMutable.postValue(handleGetPRNFleDetailGRNResponse(response))
            } else {
                getPrnForLabelGRNMutable.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> getPrnForLabelGRNMutable.postValue(Resource.Error(Constants.CONFIG_ERROR))
                else -> getPrnForLabelGRNMutable.postValue(Resource.Error("${t.message}"))
            }
        }
    }

    private fun handleGetPRNFleDetailGRNResponse(response: Response<GetPRNFileDetailOnKeyResponse>): Resource<GetPRNFileDetailOnKeyResponse> {
        var errorMessage = ""
        if (response.isSuccessful) {
            response.body()?.let { appDetailsResponse ->
                return Resource.Success(appDetailsResponse)
            }
        } else if (response.errorBody() != null) {
            val errorObject = response.errorBody()?.let {
                JSONObject(it.charStream().readText())
            }
            errorObject?.let {
                errorMessage = it.getString(Constants.HTTP_ERROR_MESSAGE)
            }
        }
        return Resource.Error(errorMessage)
    }

    //get default printer
    val getSelfSystemMappingDetailMutable: MutableLiveData<Resource<GetSelfSystemMappingDetailsResponse>> = MutableLiveData()

    fun getSelfSystemMappingDetail(token:String,baseUrl: String) {
        viewModelScope.launch {
            safeAPICallGetSelfSystemMappingDetail(token,baseUrl)
        }
    }

    private suspend fun safeAPICallGetSelfSystemMappingDetail(token:String,baseUrl: String ) {
        getSelfSystemMappingDetailMutable.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.getSelfSystemMappingDetail(token,baseUrl)
                getSelfSystemMappingDetailMutable.postValue(handleGetSelfSystemMappingDetailResponse(response))
            } else {
                getSelfSystemMappingDetailMutable.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> getSelfSystemMappingDetailMutable.postValue(Resource.Error(Constants.CONFIG_ERROR))
                else -> getSelfSystemMappingDetailMutable.postValue(Resource.Error("${t.message}"))
            }
        }
    }

    private fun handleGetSelfSystemMappingDetailResponse(response: Response<GetSelfSystemMappingDetailsResponse>): Resource<GetSelfSystemMappingDetailsResponse> {
        var errorMessage = ""
        if (response.isSuccessful) {
            response.body()?.let { appDetailsResponse ->
                return Resource.Success(appDetailsResponse)
            }
        } else if (response.errorBody() != null) {
            val errorObject = response.errorBody()?.let {
                JSONObject(it.charStream().readText())
            }
            errorObject?.let {
                errorMessage = it.getString(Constants.HTTP_ERROR_MESSAGE)
            }
        }
        return Resource.Error(errorMessage)
    }


    //get default printe
    val getAllActiveDeviceLocationMappingMutable: MutableLiveData<Resource<GetAllActiveDeviceLocationDeviceType>> = MutableLiveData()

    fun getAllActiveDeviceLocationMapping(token:String,baseUrl: String, deviceType: String) {
        viewModelScope.launch {
            safeAPICallGetAllActiveDeviceLocationMapping(token,baseUrl,deviceType)
        }
    }

    private suspend fun safeAPICallGetAllActiveDeviceLocationMapping(token:String,baseUrl: String ,deviceType: String) {
        getAllActiveDeviceLocationMappingMutable.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.getAllActiveDeviceLocationDeviceMapping(token,baseUrl,deviceType)
                getAllActiveDeviceLocationMappingMutable.postValue(handleGetAllActiveDeviceLocationMappingResponse(response))
            } else {
                getAllActiveDeviceLocationMappingMutable.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> getAllActiveDeviceLocationMappingMutable.postValue(Resource.Error(Constants.CONFIG_ERROR))
                else -> getAllActiveDeviceLocationMappingMutable.postValue(Resource.Error("${t.message}"))
            }
        }
    }


    private fun handleGetAllActiveDeviceLocationMappingResponse(response: Response<GetAllActiveDeviceLocationDeviceType>): Resource<GetAllActiveDeviceLocationDeviceType> {
        var errorMessage = ""
        if (response.isSuccessful) {
            response.body()?.let { appDetailsResponse ->
                return Resource.Success(appDetailsResponse)
            }
        } else if (response.errorBody() != null) {
            val errorObject = response.errorBody()?.let {
                JSONObject(it.charStream().readText())
            }
            errorObject?.let {
                errorMessage = it.getString(Constants.HTTP_ERROR_MESSAGE)
            }
        }
        return Resource.Error(errorMessage)
    }


    //get default printe
    val updateDefaultPrinterOnDeviceMutable: MutableLiveData<Resource<GeneralResponse>> = MutableLiveData()
    fun updateDefaultPrinterOnDevice(token:String,baseUrl: String,  printerDeviceLocationMappingIdRequest: PrinterDeviceLocationMappingIdRequest) {
        viewModelScope.launch {
            safeAPICallUpdateDefaultPrinterOnDevice(token,baseUrl,printerDeviceLocationMappingIdRequest)
        }
    }
    private suspend fun safeAPICallUpdateDefaultPrinterOnDevice(token:String,baseUrl: String , printerDeviceLocationMappingIdRequest: PrinterDeviceLocationMappingIdRequest) {
        updateDefaultPrinterOnDeviceMutable.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.updateDefaultPrinterOnDevice(token,baseUrl,printerDeviceLocationMappingIdRequest)
                updateDefaultPrinterOnDeviceMutable.postValue(handleGetUpdateDefaultPrinterOnDeviceResponse(response))
            } else {
                updateDefaultPrinterOnDeviceMutable.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> updateDefaultPrinterOnDeviceMutable.postValue(Resource.Error(Constants.CONFIG_ERROR))
                else -> updateDefaultPrinterOnDeviceMutable.postValue(Resource.Error("${t.message}"))
            }
        }
    }


    private fun handleGetUpdateDefaultPrinterOnDeviceResponse(response: Response<GeneralResponse>): Resource<GeneralResponse> {
        var errorMessage = ""
        if (response.isSuccessful) {
            response.body()?.let { appDetailsResponse ->
                return Resource.Success(appDetailsResponse)
            }
        } else if (response.errorBody() != null) {
            val errorObject = response.errorBody()?.let {
                JSONObject(it.charStream().readText())
            }
            errorObject?.let {
                errorMessage = it.getString(Constants.HTTP_ERROR_MESSAGE)
            }
        }
        return Resource.Error(errorMessage)
    }
}