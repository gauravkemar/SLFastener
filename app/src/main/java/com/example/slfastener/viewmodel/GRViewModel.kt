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
import com.example.slfastener.model.generalrequest.GeneralResponse
import com.example.slfastener.model.generalrequest.GrnBatchDeleteResponse
import com.example.slfastener.model.getalllocation.GetAllWareHouseLocationResponse
import com.example.slfastener.model.goodsreceipt.GetAllGRResponse
import com.example.slfastener.model.goodsreceipt.GetAllItemMasterResponse
import com.example.slfastener.model.goodsreceipt.PostProcessGRTransactionRequest
import com.example.slfastener.model.goodsreceipt.PostProcessGRTransactionResponse
import com.example.slfastener.model.goodsreceipt.ProcessGRLineItemRequest
import com.example.slfastener.model.goodsreceipt.ProcessGRLineItemResponse
import com.example.slfastener.model.goodsreceipt.SubmitGRRequest
import com.example.slfastener.model.goodsreceipt.grdraft.GetSingleGRByGRIdResponse
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import java.io.IOException

class GRViewModel(
    application: Application,
    private val rfidRepository: SLFastenerRepository
) : AndroidViewModel(application) {

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    val getAllGRResponseMutableList: MutableLiveData<Resource<ArrayList<GetAllGRResponse>>> =
        MutableLiveData()

    fun getAllGRResponse(token: String, baseUrl: String, status: String) {
        viewModelScope.launch {
            safeAPICallGetFilteredGRN(token, baseUrl, status)
        }
    }

    private suspend fun safeAPICallGetFilteredGRN(token: String, baseUrl: String, status: String) {
        getAllGRResponseMutableList.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.getAllGRResponse(token, baseUrl, status)
                getAllGRResponseMutableList.postValue(handleGetFilteredGR(response))
            } else {
                getAllGRResponseMutableList.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> getAllGRResponseMutableList.postValue(Resource.Error(Constants.CONFIG_ERROR))
                else -> getAllGRResponseMutableList.postValue(Resource.Error("${t.message}"))
            }
        }
    }

    private fun handleGetFilteredGR(response: Response<ArrayList<GetAllGRResponse>>): Resource<ArrayList<GetAllGRResponse>> {
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

    val getAllGRCompleteResponse: MutableLiveData<Resource<ArrayList<GetAllGRResponse>>> =
        MutableLiveData()

    fun getAllGRCompleteResponse(token: String, baseUrl: String, status: String) {
        viewModelScope.launch {
            safeAPICallGetFilteredGRNCompleted(token, baseUrl, status)
        }
    }

    private suspend fun safeAPICallGetFilteredGRNCompleted(
        token: String,
        baseUrl: String,
        status: String
    ) {
        getAllGRCompleteResponse.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.getAllGRCompleteResponse(token, baseUrl, status)
                getAllGRCompleteResponse.postValue(handleGetFilteredGRCompleted(response))
            } else {
                getAllGRCompleteResponse.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> getAllGRCompleteResponse.postValue(Resource.Error(Constants.CONFIG_ERROR))
                else -> getAllGRCompleteResponse.postValue(Resource.Error("${t.message}"))
            }
        }
    }

    private fun handleGetFilteredGRCompleted(response: Response<ArrayList<GetAllGRResponse>>): Resource<ArrayList<GetAllGRResponse>> {
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


    ////////
    val getAllItemMasterMutable: MutableLiveData<Resource<ArrayList<GetAllItemMasterResponse>>> =
        MutableLiveData()

    fun getAllItemMaster(token: String, baseUrl: String) {
        viewModelScope.launch {
            safeAPICallGetAllItemMaster(token, baseUrl)
        }
    }

    private suspend fun safeAPICallGetAllItemMaster(token: String, baseUrl: String) {
        getAllItemMasterMutable.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.getAllItemMaster(token, baseUrl)
                getAllItemMasterMutable.postValue(handleGetAllItemMaster(response))
            } else {
                getAllItemMasterMutable.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> getAllItemMasterMutable.postValue(Resource.Error(Constants.CONFIG_ERROR))
                else -> getAllItemMasterMutable.postValue(Resource.Error("${t.message}"))
            }
        }
    }

    private fun handleGetAllItemMaster(response: Response<ArrayList<GetAllItemMasterResponse>>): Resource<ArrayList<GetAllItemMasterResponse>> {
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


    ///getAll Suppliers
    val getActiveSupplierForGRMutable: MutableLiveData<Resource<ArrayList<GetActiveSuppliersDDLResponse>>> =
        MutableLiveData()

    fun getActiveSupplierForGR(token: String, baseUrl: String) {
        viewModelScope.launch {
            safeAPICallGetActiveSupplierForGR(token, baseUrl)
        }
    }

    private suspend fun safeAPICallGetActiveSupplierForGR(token: String, baseUrl: String) {
        getActiveSupplierForGRMutable.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.getActiveSupplierForGR(token, baseUrl)
                getActiveSupplierForGRMutable.postValue(handleGetActiveSupplierForGR(response))
            } else {
                getActiveSupplierForGRMutable.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> getActiveSupplierForGRMutable.postValue(Resource.Error(Constants.CONFIG_ERROR))
                else -> getActiveSupplierForGRMutable.postValue(Resource.Error("${t.message}"))
            }
        }
    }

    private fun handleGetActiveSupplierForGR(response: Response<ArrayList<GetActiveSuppliersDDLResponse>>): Resource<ArrayList<GetActiveSuppliersDDLResponse>> {
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


    // process gr transaction
    val processGRMutable: MutableLiveData<Resource<PostProcessGRTransactionResponse>> =
        MutableLiveData()

    fun processGR(
        token: String,
        baseUrl: String,
        postProcessGRTransactionRequest: PostProcessGRTransactionRequest
    ) {
        viewModelScope.launch {
            safeAPICallProcessGR(token, baseUrl, postProcessGRTransactionRequest)
        }
    }

    private suspend fun safeAPICallProcessGR(
        token: String,
        baseUrl: String,
        postProcessGRTransactionRequest: PostProcessGRTransactionRequest
    ) {
        processGRMutable.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response =
                    rfidRepository.processGR(token, baseUrl, postProcessGRTransactionRequest)
                processGRMutable.postValue(handleProcessGR(response))
            } else {
                processGRMutable.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> processGRMutable.postValue(Resource.Error(Constants.CONFIG_ERROR))
                else -> processGRMutable.postValue(Resource.Error("${t.message}"))
            }
        }
    }

    private fun handleProcessGR(response: Response<PostProcessGRTransactionResponse>): Resource<PostProcessGRTransactionResponse> {
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


    private fun handleProcessSingleGRItemMultipleBatches(response: Response<ProcessGRLineItemResponse>): Resource<ProcessGRLineItemResponse> {
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
    //
    val getAllLocationsMutableResponse: MutableLiveData<Resource<ArrayList<GetAllWareHouseLocationResponse>>> =
        MutableLiveData()

    fun getAllLocations(token: String, baseUrl: String) {
        viewModelScope.launch {
            safeAPICallGetAllLocations(token, baseUrl)
        }
    }

    private suspend fun safeAPICallGetAllLocations(token: String, baseUrl: String) {
        getAllLocationsMutableResponse.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.getAllLocations(token, baseUrl)
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

    private fun handleGetAllLocations(response: Response<ArrayList<GetAllWareHouseLocationResponse>>): Resource<ArrayList<GetAllWareHouseLocationResponse>> {
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

    val getBarcodeValueWithPrefixForExisitngMutableResponse: MutableLiveData<Resource<GeneralResponse>> =
        MutableLiveData()

    fun getBarcodeValueWithPrefixForExisitng(
        token: String,
        baseUrl: String,
        transactionPrefix: String
    ) {
        viewModelScope.launch {
            safeAPICallGetBarcodeValueWithPrefixForExisitng(token, baseUrl, transactionPrefix)
        }
    }

    private suspend fun safeAPICallGetBarcodeValueWithPrefixForExisitng(
        token: String,
        baseUrl: String,
        transactionPrefix: String
    ) {
        getBarcodeValueWithPrefixForExisitngMutableResponse.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.getBarcodeValueWithPrefixForExisitng(
                    token,
                    baseUrl,
                    transactionPrefix
                )
                getBarcodeValueWithPrefixForExisitngMutableResponse.postValue(
                    handleGetBarcodeValueWithPrefixForExisitng(response)
                )
            } else {
                getBarcodeValueWithPrefixForExisitngMutableResponse.postValue(
                    Resource.Error(
                        Constants.NO_INTERNET
                    )
                )
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> getBarcodeValueWithPrefixForExisitngMutableResponse.postValue(
                    Resource.Error(Constants.CONFIG_ERROR)
                )

                else -> getBarcodeValueWithPrefixForExisitngMutableResponse.postValue(
                    Resource.Error(
                        "${t.message}"
                    )
                )
            }
        }
    }

    private fun handleGetBarcodeValueWithPrefixForExisitng(response: Response<GeneralResponse>): Resource<GeneralResponse> {
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

    val getBarcodeValueWithPrefixMutableResponse: MutableLiveData<Resource<GeneralResponse>> =
        MutableLiveData()

    fun getBarcodeValueWithPrefix(token: String, baseUrl: String, transactionPrefix: String) {
        viewModelScope.launch {
            safeAPICallGetBarcodeValueWithPrefix(token, baseUrl, transactionPrefix)
        }
    }

    private suspend fun safeAPICallGetBarcodeValueWithPrefix(
        token: String,
        baseUrl: String,
        transactionPrefix: String
    ) {
        getBarcodeValueWithPrefixMutableResponse.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response =
                    rfidRepository.getBarcodeValueWithPrefix(token, baseUrl, transactionPrefix)
                getBarcodeValueWithPrefixMutableResponse.postValue(
                    handleGetBarcodeValueWithPrefix(
                        response
                    )
                )
            } else {
                getBarcodeValueWithPrefixMutableResponse.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> getBarcodeValueWithPrefixMutableResponse.postValue(
                    Resource.Error(
                        Constants.CONFIG_ERROR
                    )
                )

                else -> getBarcodeValueWithPrefixMutableResponse.postValue(Resource.Error("${t.message}"))
            }
        }
    }

    private fun handleGetBarcodeValueWithPrefix(response: Response<GeneralResponse>): Resource<GeneralResponse> {
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

    val getBarcodeForMultipleBatchesResponse: MutableLiveData<Resource<GeneralResponse>> =
        MutableLiveData()

    fun getBarcodeForMultipleBatches(token: String, baseUrl: String, transactionPrefix: String) {
        viewModelScope.launch {
            safeAPICallGetBarcodeValueWithPrefixForMultipleBatches(
                token,
                baseUrl,
                transactionPrefix
            )
        }
    }

    private suspend fun safeAPICallGetBarcodeValueWithPrefixForMultipleBatches(
        token: String,
        baseUrl: String,
        transactionPrefix: String
    ) {
        getBarcodeForMultipleBatchesResponse.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response =
                    rfidRepository.getBarcodeForMultipleBatches(token, baseUrl, transactionPrefix)
                getBarcodeForMultipleBatchesResponse.postValue(
                    HandleGetBarcodeForMultipleBatches(
                        response
                    )
                )
            } else {
                getBarcodeForMultipleBatchesResponse.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> getBarcodeForMultipleBatchesResponse.postValue(
                    Resource.Error(
                        Constants.CONFIG_ERROR
                    )
                )

                else -> getBarcodeForMultipleBatchesResponse.postValue(Resource.Error("${t.message}"))
            }
        }
    }

    private fun HandleGetBarcodeForMultipleBatches(response: Response<GeneralResponse>): Resource<GeneralResponse> {
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

    ///////
    val deleteGRLineUnitMutableResponse: MutableLiveData<Resource<GeneralResponse>> =
        MutableLiveData()

    fun deleteGRLineUnit(token: String, baseUrl: String, lineLineUnitId: Int) {
        viewModelScope.launch {
            safeAPICallDeleteGRNLineUnit(token, baseUrl, lineLineUnitId)
        }
    }

    private suspend fun safeAPICallDeleteGRNLineUnit(
        token: String,
        baseUrl: String,
        lineLineUnitId: Int
    ) {
        deleteGRLineUnitMutableResponse.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.deleteGRLineUnit(token, baseUrl, lineLineUnitId)
                deleteGRLineUnitMutableResponse.postValue(handleDeleteGRLineUnit(response))
            } else {
                deleteGRLineUnitMutableResponse.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> deleteGRLineUnitMutableResponse.postValue(Resource.Error(Constants.CONFIG_ERROR))
                else -> deleteGRLineUnitMutableResponse.postValue(Resource.Error("${t.message}"))
            }
        }
    }

    private fun handleDeleteGRLineUnit(response: Response<GeneralResponse>): Resource<GeneralResponse> {
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

    val deleteGRLineItemsUnitMutableResponse: MutableLiveData<Resource<GrnBatchDeleteResponse>> =
        MutableLiveData()

    fun deleteGRLineItemsUnit(token: String, baseUrl: String, lineLineUnitId: Int) {
        viewModelScope.launch {
            safeAPICallDeleteGRNLineItemsUnit(token, baseUrl, lineLineUnitId)
        }
    }

    private suspend fun safeAPICallDeleteGRNLineItemsUnit(
        token: String,
        baseUrl: String,
        lineLineUnitId: Int
    ) {
        deleteGRLineItemsUnitMutableResponse.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.deleteGRLineItemsUnit(token, baseUrl, lineLineUnitId)
                deleteGRLineItemsUnitMutableResponse.postValue(handleDeleteGRLineItemsUnit(response))
            } else {
                deleteGRLineItemsUnitMutableResponse.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> deleteGRLineItemsUnitMutableResponse.postValue(
                    Resource.Error(
                        Constants.CONFIG_ERROR
                    )
                )

                else -> deleteGRLineItemsUnitMutableResponse.postValue(Resource.Error("${t.message}"))
            }
        }
    }

    private fun handleDeleteGRLineItemsUnit(response: Response<GrnBatchDeleteResponse>): Resource<GrnBatchDeleteResponse> {
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


    /////// process for multiple
    val processSingleGRItemSingleBatchesMutable: MutableLiveData<Resource<ProcessGRLineItemResponse>> =
        MutableLiveData()

    fun processSingleGRItemSingleBatches(
        token: String,
        baseUrl: String,
        processGRLineItemRequest: ProcessGRLineItemRequest
    ) {
        viewModelScope.launch {
            safeAPICallProcessSingleGRItemSingleBatches(token, baseUrl, processGRLineItemRequest)
        }
    }

    private suspend fun safeAPICallProcessSingleGRItemMultipleBatches(
        token: String,
        baseUrl: String,
        processGRLineItemRequest: ProcessGRLineItemRequest
    ) {
        processSingleGRItemMultipleBatchesMutable.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.processSingleGRItemForMultiple(
                    token,
                    baseUrl,
                    processGRLineItemRequest
                )
                processSingleGRItemMultipleBatchesMutable.postValue(
                    handleProcessSingleGRItemMultipleBatches(response)
                )
            } else {
                processSingleGRItemMultipleBatchesMutable.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> processSingleGRItemMultipleBatchesMutable.postValue(
                    Resource.Error(
                        Constants.CONFIG_ERROR
                    )
                )

                else -> processSingleGRItemMultipleBatchesMutable.postValue(Resource.Error("${t.message}"))
            }
        }
    }

    /////// process for multiple
    val processSingleGRItemMultipleBatchesMutable: MutableLiveData<Resource<ProcessGRLineItemResponse>> =
        MutableLiveData()

    fun processSingleGRItemMultipleBatches(
        token: String,
        baseUrl: String,
        processGRLineItemRequest: ProcessGRLineItemRequest
    ) {
        viewModelScope.launch {
            safeAPICallProcessSingleGRItemMultipleBatches(token, baseUrl, processGRLineItemRequest)
        }
    }


    private suspend fun safeAPICallProcessSingleGRItemSingleBatches(
        token: String,
        baseUrl: String,
        processGRLineItemRequest: ProcessGRLineItemRequest
    ) {
        processSingleGRItemSingleBatchesMutable.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response =
                    rfidRepository.processSingleGRItem(token, baseUrl, processGRLineItemRequest)
                processSingleGRItemSingleBatchesMutable.postValue(
                    handleProcessSingleGRItemSingleBatches(response)
                )
            } else {
                processSingleGRItemSingleBatchesMutable.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> processSingleGRItemSingleBatchesMutable.postValue(
                    Resource.Error(
                        Constants.CONFIG_ERROR
                    )
                )

                else -> processSingleGRItemSingleBatchesMutable.postValue(Resource.Error("${t.message}"))
            }
        }
    }

    private fun handleProcessSingleGRItemSingleBatches(response: Response<ProcessGRLineItemResponse>): Resource<ProcessGRLineItemResponse> {
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
    //defaultGR Data
    val getSingleGRByGRIdMutableResponse: MutableLiveData<Resource<GetSingleGRByGRIdResponse>> =
        MutableLiveData()

    fun getSingleGRByGRId(token: String, baseUrl: String, grId: Int) {
        viewModelScope.launch {
            safeAPICallGetSingleGRByGRId(token, baseUrl, grId)
        }
    }

    private suspend fun safeAPICallGetSingleGRByGRId(token: String, baseUrl: String, grId: Int) {
        getSingleGRByGRIdMutableResponse.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.getSingleGRByGRId(token, baseUrl, grId)
                getSingleGRByGRIdMutableResponse.postValue(handleGetDraftGRN(response))
            } else {
                getSingleGRByGRIdMutableResponse.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> getSingleGRByGRIdMutableResponse.postValue(
                    Resource.Error(
                        Constants.CONFIG_ERROR
                    )
                )

                else -> getSingleGRByGRIdMutableResponse.postValue(Resource.Error("${t.message}"))
            }
        }
    }

    private fun handleGetDraftGRN(response: Response<GetSingleGRByGRIdResponse>): Resource<GetSingleGRByGRIdResponse> {
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
    val submitGRMutableResponse: MutableLiveData<Resource<GeneralResponse>> = MutableLiveData()

    fun submitGR(token: String, baseUrl: String, submitGRRequest: SubmitGRRequest) {
        viewModelScope.launch {
            safeAPICallSubmitGR(token, baseUrl, submitGRRequest)
        }
    }
    private suspend fun safeAPICallSubmitGR(
        token: String,
        baseUrl: String,
        submitGRRequest: SubmitGRRequest
    ) {
        submitGRMutableResponse.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.submitGR(token, baseUrl, submitGRRequest)
                submitGRMutableResponse.postValue(handleSubmitGR(response))
            } else {
                submitGRMutableResponse.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> submitGRMutableResponse.postValue(Resource.Error(Constants.CONFIG_ERROR))
                else -> submitGRMutableResponse.postValue(Resource.Error("${t.message}"))
            }
        }
    }

    private fun handleSubmitGR(response: Response<GeneralResponse>): Resource<GeneralResponse> {
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
    val printLabelForGRMutableResponse: MutableLiveData<Resource<GeneralResponse>> = MutableLiveData()

    fun printLabelForGR(token: String,baseUrl: String,    grnLineUnitItemId: ArrayList<Int>) {
        viewModelScope.launch {
            safeAPICallPrintLabelForGR(token,baseUrl,grnLineUnitItemId)
        }
    }

    private suspend fun safeAPICallPrintLabelForGR(token: String,baseUrl: String,   grnLineUnitItemId: ArrayList<Int> ) {
        printLabelForGRMutableResponse.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.printLabelForGR(token,baseUrl ,grnLineUnitItemId)
                printLabelForGRMutableResponse.postValue(handlePrintLabelForGR(response))
            } else {
                printLabelForGRMutableResponse.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> printLabelForGRMutableResponse.postValue(Resource.Error(Constants.CONFIG_ERROR))
                else -> printLabelForGRMutableResponse.postValue(Resource.Error("${t.message}"))
            }
        }
    }

    private fun handlePrintLabelForGR(response: Response<GeneralResponse>): Resource<GeneralResponse>{
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
    val printLabelForGRBulkMutableResponse: MutableLiveData<Resource<GeneralResponse>> = MutableLiveData()

    fun printLabelForGRBulk(token: String,baseUrl: String,    grnLineUnitItemId: ArrayList<Int>) {
        viewModelScope.launch {
            safeAPICallPrintLabelForGRBulk(token,baseUrl,grnLineUnitItemId)
        }
    }

    private suspend fun safeAPICallPrintLabelForGRBulk(token: String,baseUrl: String,   grnLineUnitItemId: ArrayList<Int> ) {
        printLabelForGRBulkMutableResponse.postValue(Resource.Loading())
        try {
            if (Utils.hasInternetConnection(getApplication())) {
                val response = rfidRepository.printLabelForGR(token,baseUrl ,grnLineUnitItemId)
                printLabelForGRBulkMutableResponse.postValue(handlePrintLabelForGRNBulk(response))
            } else {
                printLabelForGRBulkMutableResponse.postValue(Resource.Error(Constants.NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> printLabelForGRBulkMutableResponse.postValue(Resource.Error(Constants.CONFIG_ERROR))
                else -> printLabelForGRBulkMutableResponse.postValue(Resource.Error("${t.message}"))
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


}