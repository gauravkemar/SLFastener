package com.example.slfastener.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.demorfidapp.helper.Constants
import com.example.demorfidapp.helper.Resource
import com.example.demorfidapp.helper.Utils
import com.example.demorfidapp.repository.SLFastenerRepository
import com.example.slfastener.model.GeneralResponse
import com.example.slfastener.model.GetActiveSuppliersDDLResponse
import com.example.slfastener.model.GetPOsAndLineItemsOnPOIdsResponse
import com.example.slfastener.model.GetSuppliersPOsDDLResponse
import com.example.slfastener.model.GetSuppliersPOsRequest
import com.example.slfastener.model.grn.GRNSaveToDraftDefaultRequest
import com.example.slfastener.model.grnmain.GetFilteredGRNRequest
import com.example.slfastener.model.grnmain.GetFilteredGRNResponse
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import java.io.IOException

class GRNTransactionViewModel (
    application: Application,
    private val rfidRepository: SLFastenerRepository
) : AndroidViewModel(application) {
     val getActiveSuppliersDDLMutable: MutableLiveData<Resource<ArrayList<GetActiveSuppliersDDLResponse>>> = MutableLiveData()

    fun getActiveSuppliersDDL(token: String,baseUrl: String, ) {
        viewModelScope.launch {
            safeAPICallGetActiveSuppliersDDL(token,baseUrl)
        }
    }

    private suspend fun safeAPICallGetActiveSuppliersDDL(token: String,baseUrl: String) {
        getActiveSuppliersDDLMutable.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.getActiveSuppliersDDL(token,baseUrl )
                getActiveSuppliersDDLMutable.postValue(handleGetActiveSuppliersDDL(response))
            } else {
                getActiveSuppliersDDLMutable.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> getActiveSuppliersDDLMutable.postValue(Resource.Error(Constants.CONFIG_ERROR))
                else -> getActiveSuppliersDDLMutable.postValue(Resource.Error("${t.message}"))
            }
        }
    }

    private fun handleGetActiveSuppliersDDL(response: Response<ArrayList<GetActiveSuppliersDDLResponse>>): Resource<ArrayList<GetActiveSuppliersDDLResponse>> {
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

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    val getSuppliersPosDDLLMutableResponse: MutableLiveData<Resource<ArrayList<GetSuppliersPOsDDLResponse>>> = MutableLiveData()

    fun getSuppliersPosDDLL(token: String,baseUrl: String, bpCode: String?) {
        viewModelScope.launch {
            safeAPICallGetSuppliersPosDDLL(token,baseUrl,bpCode)
        }
    }

    private suspend fun safeAPICallGetSuppliersPosDDLL(token: String,baseUrl: String,bpCode: String? ) {
        getSuppliersPosDDLLMutableResponse.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.getSuppliersPosDDLL(token,baseUrl,bpCode )
                getSuppliersPosDDLLMutableResponse.postValue(handleGetSuppliersPosDDLL(response))
            } else {
                getSuppliersPosDDLLMutableResponse.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> getSuppliersPosDDLLMutableResponse.postValue(Resource.Error(Constants.CONFIG_ERROR))
                else -> getSuppliersPosDDLLMutableResponse.postValue(Resource.Error("${t.message}"))
            }
        }
    }

    private fun handleGetSuppliersPosDDLL(response: Response<ArrayList<GetSuppliersPOsDDLResponse>>): Resource<ArrayList<GetSuppliersPOsDDLResponse>> {
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
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    val getSuppliersPOsMutableResponse: MutableLiveData<Resource<String>> = MutableLiveData()

    fun getSuppliersPOs(token: String,baseUrl: String, getSelectedPoList:ArrayList<GetSuppliersPOsRequest>) {
        viewModelScope.launch {
            safeAPICallGetSuppliersPOs(token,baseUrl,getSelectedPoList)
        }
    }

    private suspend fun safeAPICallGetSuppliersPOs(token: String,baseUrl: String,getSelectedPoList:ArrayList<GetSuppliersPOsRequest> ) {
        getSuppliersPOsMutableResponse.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.getSuppliersPOs(token,baseUrl,getSelectedPoList )
                getSuppliersPOsMutableResponse.postValue(handleGetSuppliersPOs(response))
            } else {
                getSuppliersPOsMutableResponse.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> getSuppliersPOsMutableResponse.postValue(Resource.Error(Constants.CONFIG_ERROR))
                else -> getSuppliersPOsMutableResponse.postValue(Resource.Error("${t.message}"))
            }
        }
    }

    private fun handleGetSuppliersPOs(response: Response<String>): Resource<String> {
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


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    val getPosLineItemsOnPoIdsMutableResponse: MutableLiveData<Resource<ArrayList<GetPOsAndLineItemsOnPOIdsResponse>>> = MutableLiveData()

    fun getPosLineItemsOnPoIds(token: String,baseUrl: String, poIDs:ArrayList<Int>) {
        viewModelScope.launch {
            safeAPICallGetPosLineItemsOnPoIds(token,baseUrl,poIDs)
        }
    }

    private suspend fun safeAPICallGetPosLineItemsOnPoIds(token: String,baseUrl: String, poIDs:ArrayList<Int> ) {
        getPosLineItemsOnPoIdsMutableResponse.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.getPosLineItemsOnPoIds(token,baseUrl,poIDs )
                getPosLineItemsOnPoIdsMutableResponse.postValue(handleGetPosLineItemsOnPoIds(response))
            } else {
                getPosLineItemsOnPoIdsMutableResponse.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> getPosLineItemsOnPoIdsMutableResponse.postValue(Resource.Error(Constants.CONFIG_ERROR))
                else -> getPosLineItemsOnPoIdsMutableResponse.postValue(Resource.Error("${t.message}"))
            }
        }
    }

    private fun handleGetPosLineItemsOnPoIds(response: Response<ArrayList<GetPOsAndLineItemsOnPOIdsResponse>>): Resource<ArrayList<GetPOsAndLineItemsOnPOIdsResponse>>{
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

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    val getFilteredGRNMutableResponse: MutableLiveData<Resource<ArrayList<GetFilteredGRNResponse>>> = MutableLiveData()

    fun getFilteredGRN(token: String,baseUrl: String , getFilteredGRNRequest: GetFilteredGRNRequest) {
        viewModelScope.launch {
            safeAPICallGetFilteredGRN(token,baseUrl,getFilteredGRNRequest)
        }
    }

    private suspend fun safeAPICallGetFilteredGRN(token: String,baseUrl: String,getFilteredGRNRequest: GetFilteredGRNRequest ) {
        getFilteredGRNMutableResponse.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.getFilteredGRN(token,baseUrl,getFilteredGRNRequest )
                getFilteredGRNMutableResponse.postValue(handleGetFilteredGRN(response))
            } else {
                getFilteredGRNMutableResponse.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> getFilteredGRNMutableResponse.postValue(Resource.Error(Constants.CONFIG_ERROR))
                else -> getFilteredGRNMutableResponse.postValue(Resource.Error("${t.message}"))
            }
        }
    }

    private fun handleGetFilteredGRN(response: Response<ArrayList<GetFilteredGRNResponse>>): Resource<ArrayList<GetFilteredGRNResponse>>{
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


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    val processGRNMutableResponse: MutableLiveData<Resource<GeneralResponse>> = MutableLiveData()

    fun processGRN(token: String,baseUrl: String , grnSaveToDraftDefaultRequest: GRNSaveToDraftDefaultRequest) {
        viewModelScope.launch {
            safeAPICallProcessGRN(token,baseUrl,grnSaveToDraftDefaultRequest)
        }
    }

    private suspend fun safeAPICallProcessGRN(token: String,baseUrl: String,grnSaveToDraftDefaultRequest: GRNSaveToDraftDefaultRequest) {
        processGRNMutableResponse.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.processGRN(token,baseUrl,grnSaveToDraftDefaultRequest )
                processGRNMutableResponse.postValue(handleProcessGRN(response))
            } else {
                processGRNMutableResponse.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> processGRNMutableResponse.postValue(Resource.Error(Constants.CONFIG_ERROR))
                else -> processGRNMutableResponse.postValue(Resource.Error("${t.message}"))
            }
        }
    }

    private fun handleProcessGRN(response: Response<GeneralResponse>): Resource<GeneralResponse>{
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