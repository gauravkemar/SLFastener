package com.example.slfastener.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.demorfidapp.helper.Constants
import com.example.demorfidapp.helper.Resource
import com.example.demorfidapp.helper.Utils
import com.example.demorfidapp.repository.SLFastenerRepository
import com.example.slfastener.model.GetActiveSuppliersDDLResponse
import com.example.slfastener.model.GetPOsAndLineItemsOnPOIdsResponse
import com.example.slfastener.model.GetSuppliersPOsDDLResponse
import com.example.slfastener.model.GetSuppliersPOsRequest
import com.example.slfastener.model.generalrequest.GeneralResponse
import com.example.slfastener.model.generalrequest.GrnBatchDeleteResponse
import com.example.slfastener.model.getalllocation.GetAllWareHouseLocationResponse
import com.example.slfastener.model.grn.GRNSaveToDraftDefaultRequest
import com.example.slfastener.model.grn.GRNSaveToDraftDefaultResponse
import com.example.slfastener.model.grn.ProcessGRNLineItemsResponse
import com.example.slfastener.model.grndraftdata.GetDraftGrnResponse
import com.example.slfastener.model.grnlineitemmain.GrnLineItemResponse
import com.example.slfastener.model.grnmain.GetFilteredGRNRequest
import com.example.slfastener.model.grnmain.GetFilteredGRNResponse
import com.example.slfastener.model.grnmain.SubmitGRNRequest
import com.example.slfastener.model.polineitemnew.GRNUnitLineItemsSaveRequest
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.Query
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

    fun getPosLineItemsOnPoIds(token: String,baseUrl: String, poIDs:MutableList<Int>) {
        viewModelScope.launch {
            safeAPICallGetPosLineItemsOnPoIds(token,baseUrl,poIDs)
        }
    }

    private suspend fun safeAPICallGetPosLineItemsOnPoIds(token: String,baseUrl: String, poIDs:MutableList<Int> ) {
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

    val getFilteredGRNCompletedMutableResponse: MutableLiveData<Resource<ArrayList<GetFilteredGRNResponse>>> = MutableLiveData()

    fun getFilteredGRNCompleted(token: String,baseUrl: String , getFilteredGRNRequest: GetFilteredGRNRequest) {
        viewModelScope.launch {
            safeAPICallGetFilteredGRNCompleted(token,baseUrl,getFilteredGRNRequest)
        }
    }
    private suspend fun safeAPICallGetFilteredGRNCompleted(token: String,baseUrl: String,getFilteredGRNRequest: GetFilteredGRNRequest ) {
        getFilteredGRNCompletedMutableResponse.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.getFilteredGRNCompleted(token,baseUrl,getFilteredGRNRequest )
                getFilteredGRNCompletedMutableResponse.postValue(handleGetFilteredGRNCompleted(response))
            } else {
                getFilteredGRNCompletedMutableResponse.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> getFilteredGRNCompletedMutableResponse.postValue(Resource.Error(Constants.CONFIG_ERROR))
                else -> getFilteredGRNCompletedMutableResponse.postValue(Resource.Error("${t.message}"))
            }
        }
    }

    private fun handleGetFilteredGRNCompleted(response: Response<ArrayList<GetFilteredGRNResponse>>): Resource<ArrayList<GetFilteredGRNResponse>>{
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

    val processGRNMutableResponse: MutableLiveData<Resource<ProcessGRNLineItemsResponse>> = MutableLiveData()
    fun processGRN(token: String,baseUrl: String , grnSaveToDraftDefaultRequest: GRNSaveToDraftDefaultRequest) {
        viewModelScope.launch {
            safeAPICallProcessGRN(token,baseUrl,grnSaveToDraftDefaultRequest)
        }
    }
    private suspend fun safeAPICallProcessGRN(token: String,baseUrl: String,grnSaveToDraftDefaultRequest: GRNSaveToDraftDefaultRequest) {
        processGRNMutableResponse.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.processGRND(token,baseUrl,grnSaveToDraftDefaultRequest )
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

    private fun handleProcessGRN(response: Response<ProcessGRNLineItemsResponse>): Resource<ProcessGRNLineItemsResponse>{
        var errorMessage = ""
        if (response.isSuccessful) {
            response.body()?.let { Response ->
                return Resource.Success(Response)
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

    val getBarcodeValueWithPrefixMutableResponse: MutableLiveData<Resource<GeneralResponse>> = MutableLiveData()

    fun getBarcodeValueWithPrefix(token: String,baseUrl: String ,transactionPrefix: String ) {
        viewModelScope.launch {
            safeAPICallGetBarcodeValueWithPrefix(token,baseUrl,transactionPrefix)
        }
    }

    private suspend fun safeAPICallGetBarcodeValueWithPrefix(token: String,baseUrl: String,transactionPrefix:  String ) {
        getBarcodeValueWithPrefixMutableResponse.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.getBarcodeValueWithPrefix(token,baseUrl,transactionPrefix )
                getBarcodeValueWithPrefixMutableResponse.postValue(handleGetBarcodeValueWithPrefix(response))
            } else {
                getBarcodeValueWithPrefixMutableResponse.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> getBarcodeValueWithPrefixMutableResponse.postValue(Resource.Error(Constants.CONFIG_ERROR))
                else -> getBarcodeValueWithPrefixMutableResponse.postValue(Resource.Error("${t.message}"))
            }
        }
    }

    private fun handleGetBarcodeValueWithPrefix(response: Response<GeneralResponse>): Resource<GeneralResponse>{
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

    val getBarcodeValueWithPrefixForExisitngMutableResponse: MutableLiveData<Resource<GeneralResponse>> = MutableLiveData()

    fun getBarcodeValueWithPrefixForExisitng(token: String,baseUrl: String ,transactionPrefix: String ) {
        viewModelScope.launch {
            safeAPICallGetBarcodeValueWithPrefixForExisitng(token,baseUrl,transactionPrefix)
        }
    }

    private suspend fun safeAPICallGetBarcodeValueWithPrefixForExisitng(token: String,baseUrl: String,transactionPrefix:  String ) {
        getBarcodeValueWithPrefixForExisitngMutableResponse.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.getBarcodeValueWithPrefixForExisitng(token,baseUrl,transactionPrefix )
                getBarcodeValueWithPrefixForExisitngMutableResponse.postValue(handleGetBarcodeValueWithPrefixForExisitng(response))
            } else {
                getBarcodeValueWithPrefixForExisitngMutableResponse.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> getBarcodeValueWithPrefixForExisitngMutableResponse.postValue(Resource.Error(Constants.CONFIG_ERROR))
                else -> getBarcodeValueWithPrefixForExisitngMutableResponse.postValue(Resource.Error("${t.message}"))
            }
        }
    }

    private fun handleGetBarcodeValueWithPrefixForExisitng(response: Response<GeneralResponse>): Resource<GeneralResponse>{
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

    val getBarcodeForMultipleBatchesResponse: MutableLiveData<Resource<GeneralResponse>> = MutableLiveData()

    fun getBarcodeForMultipleBatches(token: String,baseUrl: String ,transactionPrefix: String ) {
        viewModelScope.launch {
            safeAPICallGetBarcodeValueWithPrefixForMultipleBatches(token,baseUrl,transactionPrefix)
        }
    }

    private suspend fun safeAPICallGetBarcodeValueWithPrefixForMultipleBatches(token: String,baseUrl: String,transactionPrefix:  String ) {
        getBarcodeForMultipleBatchesResponse.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.getBarcodeForMultipleBatches(token,baseUrl,transactionPrefix )
                getBarcodeForMultipleBatchesResponse.postValue(HandleGetBarcodeForMultipleBatches(response))
            } else {
                getBarcodeForMultipleBatchesResponse.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> getBarcodeForMultipleBatchesResponse.postValue(Resource.Error(Constants.CONFIG_ERROR))
                else -> getBarcodeForMultipleBatchesResponse.postValue(Resource.Error("${t.message}"))
            }
        }
    }

    private fun HandleGetBarcodeForMultipleBatches(response: Response<GeneralResponse>): Resource<GeneralResponse>{
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

    val processSingleGRNGRNItemBatchesMutableResponse: MutableLiveData<Resource<GrnLineItemResponse>> = MutableLiveData()

    fun processSingleGRNGRNItemBatches(token: String,baseUrl: String ,  grnUnitLineItemsSaveRequest: GRNUnitLineItemsSaveRequest) {
        viewModelScope.launch {
            safeAPICallProcessSingleGRNGRNItemBatches(token,baseUrl,grnUnitLineItemsSaveRequest)
        }
    }

    private suspend fun safeAPICallProcessSingleGRNGRNItemBatches(token: String,baseUrl: String, grnUnitLineItemsSaveRequest: GRNUnitLineItemsSaveRequest) {
        processSingleGRNGRNItemBatchesMutableResponse.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.processSingleGRNGRNItemBatches(token,baseUrl,grnUnitLineItemsSaveRequest )
                processSingleGRNGRNItemBatchesMutableResponse.postValue(handleProcessSingleGRNGRNItemBatches(response))
            } else {
                processSingleGRNGRNItemBatchesMutableResponse.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> processSingleGRNGRNItemBatchesMutableResponse.postValue(Resource.Error(Constants.CONFIG_ERROR))
                else -> processSingleGRNGRNItemBatchesMutableResponse.postValue(Resource.Error("${t.message}"))
            }
        }
    }

    private fun handleProcessSingleGRNGRNItemBatches(response: Response<GrnLineItemResponse>): Resource<GrnLineItemResponse>{
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
    //defaultGRN Data
    val getDraftGRNMutableResponse: MutableLiveData<Resource<GetDraftGrnResponse>> = MutableLiveData()

    fun getDraftGRN(token: String,baseUrl: String ,grnId: Int ) {
        viewModelScope.launch {
            safeAPICallGetDraftGRN(token,baseUrl,grnId)
        }
    }

    private suspend fun safeAPICallGetDraftGRN(token: String,baseUrl: String,grnId: Int ) {
        getDraftGRNMutableResponse.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.getDraftGRN(token,baseUrl,grnId )
                getDraftGRNMutableResponse.postValue(handleGetDraftGRN(response))
            } else {
                getDraftGRNMutableResponse.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> getDraftGRNMutableResponse.postValue(Resource.Error(Constants.CONFIG_ERROR))
                else -> getDraftGRNMutableResponse.postValue(Resource.Error("${t.message}"))
            }
        }
    }

    private fun handleGetDraftGRN(response: Response<GetDraftGrnResponse>): Resource<GetDraftGrnResponse>{
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
    //submitGRN Data
    val submitGRNMutableResponse: MutableLiveData<Resource<GeneralResponse>> = MutableLiveData()

    fun submitGRN(token: String,baseUrl: String ,submitGRNRequest: SubmitGRNRequest) {
        viewModelScope.launch {
            safeAPICallSubmitGRN(token,baseUrl,submitGRNRequest)
        }
    }

    private suspend fun safeAPICallSubmitGRN(token: String,baseUrl: String,submitGRNRequest: SubmitGRNRequest ) {
        submitGRNMutableResponse.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.submitGRN(token,baseUrl,submitGRNRequest )
                submitGRNMutableResponse.postValue(handleSubmitGRN(response))
            } else {
                submitGRNMutableResponse.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> submitGRNMutableResponse.postValue(Resource.Error(Constants.CONFIG_ERROR))
                else -> submitGRNMutableResponse.postValue(Resource.Error("${t.message}"))
            }
        }
    }

    private fun handleSubmitGRN(response: Response<GeneralResponse>): Resource<GeneralResponse>{
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
    //submitGRN Data
    val deleteGRNLineItemsUnitMutableResponse: MutableLiveData<Resource<GrnBatchDeleteResponse>> = MutableLiveData()

    fun deleteGRNLineItemsUnit(token: String,baseUrl: String ,lineLineUnitId: Int) {
        viewModelScope.launch {
            safeAPICallDeleteGRNLineItemsUnit(token,baseUrl,lineLineUnitId)
        }
    }

    private suspend fun safeAPICallDeleteGRNLineItemsUnit(token: String,baseUrl: String,lineLineUnitId: Int ) {
        deleteGRNLineItemsUnitMutableResponse.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.deleteGRNLineItemsUnit(token,baseUrl,lineLineUnitId )
                deleteGRNLineItemsUnitMutableResponse.postValue(handleDeleteGRNLineItemsUnit(response))
            } else {
                deleteGRNLineItemsUnitMutableResponse.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> deleteGRNLineItemsUnitMutableResponse.postValue(Resource.Error(Constants.CONFIG_ERROR))
                else -> deleteGRNLineItemsUnitMutableResponse.postValue(Resource.Error("${t.message}"))
            }
        }
    }

    private fun handleDeleteGRNLineItemsUnit(response: Response<GrnBatchDeleteResponse>): Resource<GrnBatchDeleteResponse>{
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
    //submitGRN Data
    val getAllLocationsMutableResponse: MutableLiveData<Resource<ArrayList<GetAllWareHouseLocationResponse>>> = MutableLiveData()

    fun getAllLocations(token: String,baseUrl: String ) {
        viewModelScope.launch {
            safeAPICallGetAllLocations(token,baseUrl)
        }
    }

    private suspend fun safeAPICallGetAllLocations(token: String,baseUrl: String ) {
        getAllLocationsMutableResponse.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.getAllLocations(token,baseUrl )
                getAllLocationsMutableResponse.postValue(handleGetAllLocations(response))
            } else {
                getAllLocationsMutableResponse.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> getAllLocationsMutableResponse.postValue(Resource.Error(Constants.CONFIG_ERROR))
                else -> getAllLocationsMutableResponse.postValue(Resource.Error("${t.message}"))
            }
        }
    }

    private fun handleGetAllLocations(response: Response<ArrayList<GetAllWareHouseLocationResponse>>): Resource<ArrayList<GetAllWareHouseLocationResponse>>{
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