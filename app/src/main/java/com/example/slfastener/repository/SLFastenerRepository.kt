package com.example.demorfidapp.repository

import com.example.demorfidapp.api.RetrofitInstance
import com.example.demorfidapp.helper.Constants
import com.example.slfastener.model.GetSuppliersPOsRequest
import com.example.slfastener.model.grnmain.GetFilteredGRNRequest
import com.example.slfastener.model.login.LoginRequest
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
        getSuppliersPOsRequest: ArrayList<Int>
    ) = RetrofitInstance.api(baseUrl).getPosLineItemsOnPoIds(bearerToken,getSuppliersPOsRequest)

    suspend fun getFilteredGRN(
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        @Body
        getFilteredGRNRequest: GetFilteredGRNRequest
    ) = RetrofitInstance.api(baseUrl).getFilteredGRN(bearerToken,getFilteredGRNRequest)

}