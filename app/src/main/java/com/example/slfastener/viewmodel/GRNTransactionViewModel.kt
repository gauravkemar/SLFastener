package com.example.slfastener.viewmodel

import android.app.Activity
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
import com.example.slfastener.model.GetSupllierPOsDDLOriginalResponse
import com.example.slfastener.model.GetSuppliersPOsRequest
import com.example.slfastener.model.generalrequest.GRNLineItemDeleteResponse
import com.example.slfastener.model.generalrequest.GeneralResponse
import com.example.slfastener.model.generalrequest.GrnBatchDeleteResponse
import com.example.slfastener.model.getalllocation.GetAllWareHouseLocationResponse
import com.example.slfastener.model.getalltax.GetAllTaxItem
import com.example.slfastener.model.grn.GRNSaveToDraftDefaultRequest
import com.example.slfastener.model.grn.ProcessGRNLineItemsResponse
import com.example.slfastener.model.grndraftdata.GetDraftGrnResponse
import com.example.slfastener.model.grnlineitemmain.GrnLineItemResponse
import com.example.slfastener.model.grnmain.GetFilteredGRNRequest
import com.example.slfastener.model.grnmain.GetFilteredGRNResponse
import com.example.slfastener.model.grnmain.SubmitGRNRequest
import com.example.slfastener.model.othercharges.GetOtherChargesItem
import com.example.slfastener.model.grnlineitemmain.GRNUnitLineItemsSaveRequest
import com.example.slfastener.model.grnmain.UpdateGRNLineItem
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.QueryMap
import java.io.IOException

class GRNTransactionViewModel (
    application: Application,
    private val rfidRepository: SLFastenerRepository
) : AndroidViewModel(application) {
    val getActiveSuppliersDDLMutable: MutableLiveData<Resource<ArrayList<GetActiveSuppliersDDLResponse>>> = MutableLiveData()

    fun getActiveSuppliersDDL(        context: Activity,token: String,baseUrl: String, ) {
        viewModelScope.launch {
            safeAPICallGetActiveSuppliersDDL(context,token,baseUrl)
        }
    }

    private suspend fun safeAPICallGetActiveSuppliersDDL(        context: Activity,token: String,baseUrl: String) {
        getActiveSuppliersDDLMutable.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.getActiveSuppliersDDL(context,token,baseUrl )
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

    val getSuppliersPosDDLLMutableResponse: MutableLiveData<Resource<ArrayList<GetSupllierPOsDDLOriginalResponse>>> = MutableLiveData()

    fun getSuppliersPosDDLL(        context: Activity,token: String,baseUrl: String, bpCode: String?) {
        viewModelScope.launch {
            safeAPICallGetSuppliersPosDDLL(context,token,baseUrl,bpCode)
        }
    }

    private suspend fun safeAPICallGetSuppliersPosDDLL(        context: Activity,token: String,baseUrl: String,bpCode: String? ) {
        getSuppliersPosDDLLMutableResponse.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.getSuppliersPosDDLL(context,token,baseUrl,bpCode )
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

    private fun handleGetSuppliersPosDDLL(response: Response<ArrayList<GetSupllierPOsDDLOriginalResponse>>): Resource<ArrayList<GetSupllierPOsDDLOriginalResponse>> {
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

    fun getSuppliersPOs(context : Activity,token: String,baseUrl: String, getSelectedPoList:ArrayList<GetSuppliersPOsRequest>) {
        viewModelScope.launch {
            safeAPICallGetSuppliersPOs(context,token,baseUrl,getSelectedPoList)
        }
    }

    private suspend fun safeAPICallGetSuppliersPOs(context: Activity,token: String,baseUrl: String,getSelectedPoList:ArrayList<GetSuppliersPOsRequest> ) {
        getSuppliersPOsMutableResponse.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.getSuppliersPOs(context,token,baseUrl,getSelectedPoList )
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

    fun getPosLineItemsOnPoIds(        context: Activity,token: String,baseUrl: String, poIDs:MutableList<Int>) {
        viewModelScope.launch {
            safeAPICallGetPosLineItemsOnPoIds(context,token,baseUrl,poIDs)
        }
    }

    private suspend fun safeAPICallGetPosLineItemsOnPoIds(        context: Activity,token: String,baseUrl: String, poIDs:MutableList<Int> ) {
        getPosLineItemsOnPoIdsMutableResponse.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.getPosLineItemsOnPoIds(context,token,baseUrl,poIDs )
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

    fun getFilteredGRN(        context: Activity,token: String,baseUrl: String , getFilteredGRNRequest: GetFilteredGRNRequest) {
        viewModelScope.launch {
            safeAPICallGetFilteredGRN(context,token,baseUrl,getFilteredGRNRequest)
        }
    }

    private suspend fun safeAPICallGetFilteredGRN(        context: Activity,token: String,baseUrl: String,getFilteredGRNRequest: GetFilteredGRNRequest ) {
        getFilteredGRNMutableResponse.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.getFilteredGRN(context,token,baseUrl,getFilteredGRNRequest )
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

    fun getFilteredGRNCompleted(        context: Activity,token: String,baseUrl: String , getFilteredGRNRequest: GetFilteredGRNRequest) {
        viewModelScope.launch {
            safeAPICallGetFilteredGRNCompleted(context,token,baseUrl,getFilteredGRNRequest)
        }
    }
    private suspend fun safeAPICallGetFilteredGRNCompleted(        context: Activity,token: String,baseUrl: String,getFilteredGRNRequest: GetFilteredGRNRequest ) {
        getFilteredGRNCompletedMutableResponse.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.getFilteredGRNCompleted(context,token,baseUrl,getFilteredGRNRequest )
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
    fun processGRN(        context: Activity,token: String,baseUrl: String , grnSaveToDraftDefaultRequest: GRNSaveToDraftDefaultRequest) {
        viewModelScope.launch {
            safeAPICallProcessGRN(context,token,baseUrl,grnSaveToDraftDefaultRequest)
        }
    }
    private suspend fun safeAPICallProcessGRN(        context: Activity,token: String,baseUrl: String,grnSaveToDraftDefaultRequest: GRNSaveToDraftDefaultRequest) {
        processGRNMutableResponse.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.processGRND(context,token,baseUrl,grnSaveToDraftDefaultRequest )
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

    fun getBarcodeValueWithPrefix(context: Activity, token: String, baseUrl: String, transactionPrefix: String ) {
        viewModelScope.launch {
            safeAPICallGetBarcodeValueWithPrefix(context,token,baseUrl,transactionPrefix)
        }
    }

    private suspend fun safeAPICallGetBarcodeValueWithPrefix(        context: Activity,token: String,baseUrl: String,transactionPrefix:  String ) {
        getBarcodeValueWithPrefixMutableResponse.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.getBarcodeValueWithPrefix(context,token,baseUrl,transactionPrefix )
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

    fun getBarcodeValueWithPrefixForExisitng(        context: Activity,token: String,baseUrl: String ,transactionPrefix: String ) {
        viewModelScope.launch {
            safeAPICallGetBarcodeValueWithPrefixForExisitng(context,token,baseUrl,transactionPrefix)
        }
    }

    private suspend fun safeAPICallGetBarcodeValueWithPrefixForExisitng(        context: Activity,token: String,baseUrl: String,transactionPrefix:  String ) {
        getBarcodeValueWithPrefixForExisitngMutableResponse.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.getBarcodeValueWithPrefixForExisitng(context,token,baseUrl,transactionPrefix )
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

    fun getBarcodeForMultipleBatches(        context: Activity,token: String,baseUrl: String ,transactionPrefix: String ) {
        viewModelScope.launch {
            safeAPICallGetBarcodeValueWithPrefixForMultipleBatches(context,token,baseUrl,transactionPrefix)
        }
    }

    private suspend fun safeAPICallGetBarcodeValueWithPrefixForMultipleBatches(        context: Activity,token: String,baseUrl: String,transactionPrefix:  String ) {
        getBarcodeForMultipleBatchesResponse.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.getBarcodeForMultipleBatches(context,token,baseUrl,transactionPrefix )
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

    fun processSingleGRNGRNItemBatches(        context: Activity,token: String,baseUrl: String ,  grnUnitLineItemsSaveRequest: GRNUnitLineItemsSaveRequest) {
        viewModelScope.launch {
            safeAPICallProcessSingleGRNGRNItemBatches(context,token,baseUrl,grnUnitLineItemsSaveRequest)
        }
    }

    private suspend fun safeAPICallProcessSingleGRNGRNItemBatches(        context: Activity,token: String,baseUrl: String, grnUnitLineItemsSaveRequest: GRNUnitLineItemsSaveRequest) {
        processSingleGRNGRNItemBatchesMutableResponse.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.processSingleGRNGRNItemBatches(context,token,baseUrl,grnUnitLineItemsSaveRequest )
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

    val updateLineItemResponse: MutableLiveData<Resource<GeneralResponse>> = MutableLiveData()

    fun updateLineItem(        context: Activity,token: String,baseUrl: String ,  updateGRNLineItem: UpdateGRNLineItem) {
        viewModelScope.launch {
            safeAPICallUpdateLineItem(context,token,baseUrl,updateGRNLineItem)
        }
    }

    private suspend fun safeAPICallUpdateLineItem(        context: Activity,token: String,baseUrl: String,   updateGRNLineItem: UpdateGRNLineItem) {
        updateLineItemResponse.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.updateLineItem(context,token,baseUrl,updateGRNLineItem )
                updateLineItemResponse.postValue(handleUpdateLineItem(response))
            } else {
                updateLineItemResponse.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> updateLineItemResponse.postValue(Resource.Error(Constants.CONFIG_ERROR))
                else -> updateLineItemResponse.postValue(Resource.Error("${t.message}"))
            }
        }
    }

    private fun handleUpdateLineItem(response: Response<GeneralResponse>): Resource<GeneralResponse>{
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

    val processSingleGRNGRNItemBatchesForMultipleMutableResponse: MutableLiveData<Resource<GrnLineItemResponse>> = MutableLiveData()

    fun processSingleGRNGRNItemBatchesForMultiple(        context: Activity,token: String,baseUrl: String ,  grnUnitLineItemsSaveRequest: GRNUnitLineItemsSaveRequest) {
        viewModelScope.launch {
            safeAPICallProcessSingleGRNGRNItemBatchesForMultiple(context,token,baseUrl,grnUnitLineItemsSaveRequest)
        }
    }

    private suspend fun safeAPICallProcessSingleGRNGRNItemBatchesForMultiple(        context: Activity,token: String,baseUrl: String, grnUnitLineItemsSaveRequest: GRNUnitLineItemsSaveRequest) {
        processSingleGRNGRNItemBatchesForMultipleMutableResponse.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.processSingleGRNGRNItemBatchesForMultiple(context,token,baseUrl,grnUnitLineItemsSaveRequest )
                processSingleGRNGRNItemBatchesForMultipleMutableResponse.postValue(handleProcessSingleGRNGRNItemBatchesForMultiple(response))
            } else {
                processSingleGRNGRNItemBatchesForMultipleMutableResponse.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> processSingleGRNGRNItemBatchesForMultipleMutableResponse.postValue(Resource.Error(Constants.CONFIG_ERROR))
                else -> processSingleGRNGRNItemBatchesForMultipleMutableResponse.postValue(Resource.Error("${t.message}"))
            }
        }
    }

    private fun handleProcessSingleGRNGRNItemBatchesForMultiple(response: Response<GrnLineItemResponse>): Resource<GrnLineItemResponse>{
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

    fun getDraftGRN(        context: Activity,token: String,baseUrl: String ,grnId: Int ) {
        viewModelScope.launch {
            safeAPICallGetDraftGRN(context,token,baseUrl,grnId)
        }
    }

    private suspend fun safeAPICallGetDraftGRN(        context: Activity,token: String,baseUrl: String,grnId: Int ) {
        getDraftGRNMutableResponse.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.getDraftGRN(context,token,baseUrl,grnId )
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

    fun submitGRN(        context: Activity,token: String,baseUrl: String ,submitGRNRequest: SubmitGRNRequest) {
        viewModelScope.launch {
            safeAPICallSubmitGRN(context,token,baseUrl,submitGRNRequest)
        }
    }

    private suspend fun safeAPICallSubmitGRN(        context: Activity,token: String,baseUrl: String,submitGRNRequest: SubmitGRNRequest ) {
        submitGRNMutableResponse.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.submitGRN(context,token,baseUrl,submitGRNRequest )
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

    fun deleteGRNLineItemsUnit(        context: Activity,token: String,baseUrl: String ,lineLineUnitId: Int) {
        viewModelScope.launch {
            safeAPICallDeleteGRNLineItemsUnit(context,token,baseUrl,lineLineUnitId)
        }
    }

    private suspend fun safeAPICallDeleteGRNLineItemsUnit(        context: Activity,token: String,baseUrl: String,lineLineUnitId: Int ) {
        deleteGRNLineItemsUnitMutableResponse.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.deleteGRNLineItemsUnit(context,token,baseUrl,lineLineUnitId )
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
    }//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //submitGRN Data
    val deleteGRNLineUnitMutableResponse: MutableLiveData<Resource<GRNLineItemDeleteResponse>> = MutableLiveData()

    fun deleteGRNLineUnit(        context: Activity,token: String,baseUrl: String ,lineLineUnitId: Int) {
        viewModelScope.launch {
            safeAPICallDeleteGRNLineUnit(context,token,baseUrl,lineLineUnitId)
        }
    }

    private suspend fun safeAPICallDeleteGRNLineUnit(        context: Activity,token: String,baseUrl: String,lineLineUnitId: Int ) {
        deleteGRNLineUnitMutableResponse.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.deleteGRNLineUnit(context,token,baseUrl,lineLineUnitId )
                deleteGRNLineUnitMutableResponse.postValue(handleDeleteGRNLineUnit(response))
            } else {
                deleteGRNLineUnitMutableResponse.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> deleteGRNLineUnitMutableResponse.postValue(Resource.Error(Constants.CONFIG_ERROR))
                else -> deleteGRNLineUnitMutableResponse.postValue(Resource.Error("${t.message}"))
            }
        }
    }

    private fun handleDeleteGRNLineUnit(response: Response<GRNLineItemDeleteResponse>): Resource<GRNLineItemDeleteResponse>{
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

    fun getAllLocations(        context: Activity,token: String,baseUrl: String ) {
        viewModelScope.launch {
            safeAPICallGetAllLocations(context,token,baseUrl)
        }
    }

    private suspend fun safeAPICallGetAllLocations(        context: Activity,token: String,baseUrl: String ) {
        getAllLocationsMutableResponse.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.getAllLocations(context,token,baseUrl )
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


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //print
    val printLabelForGRNMutableResponse: MutableLiveData<Resource<GeneralResponse>> = MutableLiveData()

    fun printLabelForGRN(        context: Activity,token: String,baseUrl: String,    grnLineUnitItemId: ArrayList<Int>) {
        viewModelScope.launch {
            safeAPICallPrintLabelForGRN(context,token,baseUrl,grnLineUnitItemId)
        }
    }

    private suspend fun safeAPICallPrintLabelForGRN(        context: Activity,token: String,baseUrl: String,   grnLineUnitItemId: ArrayList<Int> ) {
        printLabelForGRNMutableResponse.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.printLabelForGRN(context,token,baseUrl ,grnLineUnitItemId)
                printLabelForGRNMutableResponse.postValue(handlePrintLabelForGRN(response))
            } else {
                printLabelForGRNMutableResponse.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> printLabelForGRNMutableResponse.postValue(Resource.Error(Constants.CONFIG_ERROR))
                else -> printLabelForGRNMutableResponse.postValue(Resource.Error("${t.message}"))
            }
        }
    }

    private fun handlePrintLabelForGRN(response: Response<GeneralResponse>): Resource<GeneralResponse>{
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
    //print bulk
    val printLabelForGRNBulkMutableResponse: MutableLiveData<Resource<GeneralResponse>> = MutableLiveData()

    fun printLabelForGRNBulk(        context: Activity,token: String,baseUrl: String,    grnLineUnitItemId: ArrayList<Int>) {
        viewModelScope.launch {
            safeAPICallPrintLabelForGRNBulk(context,token,baseUrl,grnLineUnitItemId)
        }
    }

    private suspend fun safeAPICallPrintLabelForGRNBulk(        context: Activity,token: String,baseUrl: String,   grnLineUnitItemId: ArrayList<Int> ) {
        printLabelForGRNBulkMutableResponse.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.printLabelForGRN(context,token,baseUrl ,grnLineUnitItemId)
                printLabelForGRNBulkMutableResponse.postValue(handlePrintLabelForGRNBulk(response))
            } else {
                printLabelForGRNBulkMutableResponse.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> printLabelForGRNBulkMutableResponse.postValue(Resource.Error(Constants.CONFIG_ERROR))
                else -> printLabelForGRNBulkMutableResponse.postValue(Resource.Error("${t.message}"))
            }
        }
    }

    private fun handlePrintLabelForGRNBulk(response: Response<GeneralResponse>): Resource<GeneralResponse>{
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
    //print USB
    val getGRNProductDetailsOnUnitIdItemMutableResponse: MutableLiveData<Resource<ArrayList<Map<String, Any>>>> = MutableLiveData()

    fun getGRNProductDetailsOnUnitIdItem(        context: Activity,token: String,baseUrl: String,    grnLineUnitItemId: ArrayList<Int>) {
        viewModelScope.launch {
            safeAPICallGetGRNProductDetailsOnUnitIdItem(context,token,baseUrl,grnLineUnitItemId)
        }
    }

    private suspend fun safeAPICallGetGRNProductDetailsOnUnitIdItem(      context: Activity,token: String,baseUrl: String,   grnLineUnitItemId: ArrayList<Int> ) {
        getGRNProductDetailsOnUnitIdItemMutableResponse.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.getGRNProductDetailsOnUnitIdItem(context,token,baseUrl ,grnLineUnitItemId)
                getGRNProductDetailsOnUnitIdItemMutableResponse.postValue(handleGetGRNProductDetailsOnUnitIdItem(response))
            } else {
                getGRNProductDetailsOnUnitIdItemMutableResponse.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> getGRNProductDetailsOnUnitIdItemMutableResponse.postValue(Resource.Error(Constants.CONFIG_ERROR))
                else -> getGRNProductDetailsOnUnitIdItemMutableResponse.postValue(Resource.Error("${t.message}"))
            }
        }
    }

    private fun handleGetGRNProductDetailsOnUnitIdItem(response: Response<ArrayList<Map<String, Any>>>): Resource<ArrayList<Map<String, Any>>> {
        return if (response.isSuccessful) {
            response.body()?.let {
                Resource.Success(it)
            } ?: Resource.Error("Empty response")
        } else {
            val errorMessage = response.errorBody()?.string() ?: "Unknown error"
            Resource.Error(errorMessage)
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //getAllTax
    val getAllTaxResponse: MutableLiveData<Resource<ArrayList<GetAllTaxItem>>> = MutableLiveData()

    fun getAllTax(        context: Activity,token: String,baseUrl: String) {
        viewModelScope.launch {
            safeAPICallGetAllTax(context,token,baseUrl)
        }
    }

    private suspend fun safeAPICallGetAllTax(        context: Activity,token: String,baseUrl: String) {
        getAllTaxResponse.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.getAllTax(context,token,baseUrl)
                getAllTaxResponse.postValue(handleGetAllTax(response))
            } else {
                getAllTaxResponse.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> getAllTaxResponse.postValue(Resource.Error(Constants.CONFIG_ERROR))
                else -> getAllTaxResponse.postValue(Resource.Error("${t.message}"))
            }
        }
    }

    private fun handleGetAllTax(response: Response<ArrayList<GetAllTaxItem>>): Resource<ArrayList<GetAllTaxItem>> {
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
    //update other charges
    val updateOtherChargesResponse: MutableLiveData<Resource< GeneralResponse>> = MutableLiveData()


    fun updateOtherCharges(        context: Activity,token: String,baseUrl: String,   grnId: Int, otherCharges: Double) {
        viewModelScope.launch {
            safeAPICallUpdateOtherCharges(context,token,baseUrl,grnId,otherCharges)
        }
    }

    private suspend fun safeAPICallUpdateOtherCharges(
        context: Activity,
        token: String,
        baseUrl: String,
        grnId: Int,
        otherCharges: Double
    ) {
        updateOtherChargesResponse.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.updateOtherCharges(context,token,baseUrl,grnId,otherCharges)
                updateOtherChargesResponse.postValue(handleUpdateOtherCharges(response))
            } else {
                updateOtherChargesResponse.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> updateOtherChargesResponse.postValue(Resource.Error(Constants.CONFIG_ERROR))
                else -> updateOtherChargesResponse.postValue(Resource.Error("${t.message}"))
            }
        }
    }

    private fun handleUpdateOtherCharges(response: Response<GeneralResponse>): Resource<GeneralResponse> {
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

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////update other charges
    val getOtherChargesMutable: MutableLiveData<Resource<ArrayList<GetOtherChargesItem>>> = MutableLiveData()


    fun getOtherCharges(        context: Activity,token: String,baseUrl: String, @QueryMap queryMap: Map<String, Int>) {
        viewModelScope.launch {
            safeAPICallGetOtherCharges(context,token,baseUrl, queryMap)
        }
    }

    private suspend fun safeAPICallGetOtherCharges(
        context: Activity,
        token: String,
        baseUrl: String,
        @QueryMap queryMap: Map<String, Int>
    ) {
        getOtherChargesMutable.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.getOtherCharges(context,token,baseUrl,queryMap)
                getOtherChargesMutable.postValue(handleGetOtherCharges(response))
            } else {
                getOtherChargesMutable.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> getOtherChargesMutable.postValue(Resource.Error(Constants.CONFIG_ERROR))
                else -> getOtherChargesMutable.postValue(Resource.Error("${t.message}"))
            }
        }
    }

    private fun handleGetOtherCharges(response: Response<ArrayList<GetOtherChargesItem>>): Resource<ArrayList<GetOtherChargesItem>> {
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