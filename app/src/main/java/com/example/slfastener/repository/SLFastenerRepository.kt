package com.example.demorfidapp.repository

import com.example.demorfidapp.api.RetrofitInstance
import com.example.demorfidapp.helper.Constants
import com.example.slfastener.model.GetSuppliersPOsRequest
import com.example.slfastener.model.grn.GRNSaveToDraftDefaultRequest
import com.example.slfastener.model.grnmain.GetFilteredGRNRequest
import com.example.slfastener.model.login.LoginRequest
import com.example.slfastener.model.polineitemnew.GRNUnitLineItemsSaveRequest
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Query


class SLFastenerRepository {


   /* suspend fun submitRFIDDetails(
        baseUrl: String,
        postRFIDReadRequest: PostRFIDReadRequest
    ) = RetrofitInstance.api(baseUrl).submitRFIDDetails(postRFIDReadRequest)*/
   suspend fun login(
       baseUrl: String,
       loginRequest: LoginRequest
   ) = RetrofitInstance.api(baseUrl).login(loginRequest)

    suspend fun getActiveSuppliersDDL(
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
    ) = RetrofitInstance.api(baseUrl).getActiveSuppliersDDL(bearerToken)

    suspend fun getSuppliersPosDDLL(
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        @Query("bpCode") bpCode: String?
    ) = RetrofitInstance.api(baseUrl).getSuppliersPosDDLL(bearerToken,bpCode)

    suspend fun getSuppliersPOs(
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        @Body
        getSuppliersPOsRequest: ArrayList<GetSuppliersPOsRequest>
    ) = RetrofitInstance.api(baseUrl).getSuppliersPOs(bearerToken,getSuppliersPOsRequest)

    suspend fun getPosLineItemsOnPoIds(
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        @Body
        getSuppliersPOsRequest: MutableList<Int>
    ) = RetrofitInstance.api(baseUrl).getPosLineItemsOnPoIds("Bearer $bearerToken","application/json","PostmanRuntime/7.37.3",getSuppliersPOsRequest)

    suspend fun getFilteredGRN(
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        @Body
        getFilteredGRNRequest: GetFilteredGRNRequest
    ) = RetrofitInstance.api(baseUrl).getFilteredGRN(bearerToken,getFilteredGRNRequest)

    suspend fun getFilteredGRNCompleted(
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        @Body
        getFilteredGRNRequest: GetFilteredGRNRequest
    ) = RetrofitInstance.api(baseUrl).getFilteredGRNCompleted(bearerToken,getFilteredGRNRequest)

    suspend fun processGRND(
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        @Body
        grnSaveToDraftDefaultRequest: GRNSaveToDraftDefaultRequest
    ) = RetrofitInstance.api(baseUrl).processGRND(bearerToken,grnSaveToDraftDefaultRequest)


    suspend fun processSingleGRNGRNItemBatches(
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        @Body
        grnUnitLineItemsSaveRequest: GRNUnitLineItemsSaveRequest
    ) = RetrofitInstance.api(baseUrl).processSingleGRNGRNItemBatches(bearerToken,grnUnitLineItemsSaveRequest)

    suspend fun getBarcodeValueWithPrefix(
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        @Query("transactionPrefix") transactionPrefix: String?
    ) = RetrofitInstance.api(baseUrl).getBarcodeValueWithPrefix(bearerToken,transactionPrefix)

    suspend fun getBarcodeValueWithPrefixForExisitng(
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        @Query("transactionPrefix") transactionPrefix: String?
    ) = RetrofitInstance.api(baseUrl).getBarcodeValueWithPrefixForExisitng(bearerToken,transactionPrefix)

    suspend fun getDraftGRN(
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        @Query("grnId") grnId: Int?
    ) = RetrofitInstance.api(baseUrl).getDraftGRN(bearerToken,grnId)

}