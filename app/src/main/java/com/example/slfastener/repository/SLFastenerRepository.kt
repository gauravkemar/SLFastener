package com.example.demorfidapp.repository

import android.app.Activity
import com.example.demorfidapp.api.RetrofitInstance
import com.example.demorfidapp.helper.Constants
import com.example.slfastener.model.GetSuppliersPOsRequest
import com.example.slfastener.model.goodsreceipt.PostProcessGRTransactionRequest
import com.example.slfastener.model.goodsreceipt.ProcessGRLineItemRequest
import com.example.slfastener.model.goodsreceipt.SubmitGRRequest
import com.example.slfastener.model.grn.GRNSaveToDraftDefaultRequest
import com.example.slfastener.model.grnmain.GetFilteredGRNRequest
import com.example.slfastener.model.grnmain.SubmitGRNRequest
import com.example.slfastener.model.login.LoginRequest
import com.example.slfastener.model.grnlineitemmain.GRNUnitLineItemsSaveRequest
import com.example.slfastener.model.grnmain.UpdateGRNLineItem
import com.example.slfastener.model.printerprnmodel.PrinterDeviceLocationMappingIdRequest
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Query
import retrofit2.http.QueryMap


class SLFastenerRepository {


    /* suspend fun submitRFIDDetails(
         baseUrl: String,
         postRFIDReadRequest: PostRFIDReadRequest
     ) = RetrofitInstance.api(baseUrl).submitRFIDDetails(postRFIDReadRequest)*/
    suspend fun login(
        context: Activity,
        baseUrl: String,
        loginRequest: LoginRequest
    ) = RetrofitInstance(context).api(baseUrl).login(loginRequest)

    suspend fun getActiveSuppliersDDL(
        context: Activity,
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
    ) = RetrofitInstance(context ).api(baseUrl).getActiveSuppliersDDL()

    suspend fun getSuppliersPosDDLL(
        context: Activity,
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        @Query("bpCode") bpCode: String?
    ) = RetrofitInstance(context).api(baseUrl).getSuppliersPosDDLL(bpCode)

    suspend fun getSuppliersPOs(
        context: Activity,
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        @Body
        getSuppliersPOsRequest: ArrayList<GetSuppliersPOsRequest>
    ) = RetrofitInstance(context ).api(baseUrl).getSuppliersPOs(getSuppliersPOsRequest)

    suspend fun getPosLineItemsOnPoIds(
        context: Activity,
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        @Body
        getSuppliersPOsRequest: MutableList<Int>
    ) = RetrofitInstance(context ).api(baseUrl).getPosLineItemsOnPoIds("application/json","PostmanRuntime/7.37.3",getSuppliersPOsRequest)

    suspend fun getFilteredGRN(
        context: Activity,
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        @Body
        getFilteredGRNRequest: GetFilteredGRNRequest
    ) = RetrofitInstance(context ).api(baseUrl).getFilteredGRN(getFilteredGRNRequest)

    suspend fun getFilteredGRNCompleted(
        context: Activity,
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        @Body
        getFilteredGRNRequest: GetFilteredGRNRequest
    ) = RetrofitInstance(context ).api(baseUrl).getFilteredGRNCompleted(getFilteredGRNRequest)

    suspend fun processGRND(
        context: Activity,
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        @Body
        grnSaveToDraftDefaultRequest: GRNSaveToDraftDefaultRequest
    ) = RetrofitInstance(context ).api(baseUrl).processGRND(grnSaveToDraftDefaultRequest)


    suspend fun processSingleGRNGRNItemBatches(
        context: Activity,
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        @Body
        grnUnitLineItemsSaveRequest: GRNUnitLineItemsSaveRequest
    ) = RetrofitInstance(context ).api(baseUrl).processSingleGRNGRNItemBatches(grnUnitLineItemsSaveRequest)
    suspend fun updateLineItem(
        context: Activity,
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        @Body
        updateGRNLineItem: UpdateGRNLineItem
    ) = RetrofitInstance(context ).api(baseUrl).updateLineItem(updateGRNLineItem)
    suspend fun processSingleGRNGRNItemBatchesForMultiple(
        context: Activity,
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        @Body
        grnUnitLineItemsSaveRequest: GRNUnitLineItemsSaveRequest
    ) = RetrofitInstance(context ).api(baseUrl).processSingleGRNGRNItemBatchesForMultiple(grnUnitLineItemsSaveRequest)

    suspend fun getBarcodeValueWithPrefix(
        context: Activity,
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        @Query("transactionPrefix") transactionPrefix: String?
    ) = RetrofitInstance(context ).api(baseUrl).getBarcodeValueWithPrefix(transactionPrefix)

    suspend fun getBarcodeValueWithPrefixForExisitng(
        context: Activity,
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        @Query("transactionPrefix") transactionPrefix: String?
    ) = RetrofitInstance(context ).api(baseUrl).getBarcodeValueWithPrefixForExisitng(transactionPrefix)

    suspend fun getBarcodeForMultipleBatches(
        context: Activity,
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        @Query("transactionPrefix") transactionPrefix: String?
    ) = RetrofitInstance(context ).api(baseUrl).getBarcodeForMultipleBatches(transactionPrefix)

    suspend fun getDraftGRN(
        context: Activity,
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        @Query("grnId") grnId: Int?
    ) = RetrofitInstance(context ).api(baseUrl).getDraftGRN(grnId)

    suspend fun submitGRN(
        context: Activity,
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        @Body
        submitGRNRequest: SubmitGRNRequest
    ) = RetrofitInstance(context ).api(baseUrl).submitGRN(submitGRNRequest)

    suspend fun deleteGRNLineItemsUnit(
        context: Activity,
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        @Query("lineLineUnitId") lineLineUnitId: Int?
    ) = RetrofitInstance(context ).api(baseUrl).deleteGRNLineItemsUnit(lineLineUnitId)

    suspend fun deleteGRNLineUnit(
        context: Activity,
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        @Query("lineItemId") lineLineUnitId: Int?
    ) = RetrofitInstance(context ).api(baseUrl).deleteGRNLineUnit(lineLineUnitId)

    suspend fun getAllLocations(
        context: Activity,
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
    ) = RetrofitInstance(context ).api(baseUrl).getAllLocations()

    suspend fun printLabelForGRN(
        context: Activity,
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        @Body
        grnLineUnitItemId: ArrayList<Int>
    ) = RetrofitInstance(context ).api(baseUrl).printLabelForGRN(grnLineUnitItemId)

    suspend fun getGRNProductDetailsOnUnitIdItem(
        context: Activity,
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        @Body
        grnLineUnitItemId: ArrayList<Int>
    ) = RetrofitInstance(context ).api(baseUrl).getGRNProductDetailsOnUnitIdItem(grnLineUnitItemId)



    suspend fun getAllItemMaster(
        context: Activity,
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
    ) = RetrofitInstance(context ).api(baseUrl).getAllItemMaster()

    suspend fun getActiveSupplierForGR(
        context: Activity,
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
    ) = RetrofitInstance(context ).api(baseUrl).getActiveSupplierForGR()

    suspend fun processGR(
        context: Activity,
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        @Body
        postProcessGRTransactionRequest: PostProcessGRTransactionRequest
    ) = RetrofitInstance(context ).api(baseUrl).processGR(postProcessGRTransactionRequest)

    suspend fun processSingleGRItem(
        context: Activity,
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        @Body
        processGRLineItemRequest: ProcessGRLineItemRequest
    ) = RetrofitInstance(context ).api(baseUrl).processSingleGRItem(processGRLineItemRequest)

    suspend fun processSingleGRItemForMultiple(
        context: Activity,
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        @Body
        processGRLineItemRequest: ProcessGRLineItemRequest
    ) = RetrofitInstance(context ).api(baseUrl).processSingleGRItemForMultiple(processGRLineItemRequest)

    suspend fun getAllGRResponse(
        context: Activity,
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        @Query("Status") status: String?
    ) = RetrofitInstance(context ).api(baseUrl).getAllGRResponse(status)

    suspend fun getAllGRCompleteResponse(
        context: Activity,
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        @Query("Status") status: String?
    ) = RetrofitInstance(context ).api(baseUrl).getAllGRCompleteResponse(status)
    suspend fun getSingleGRByGRId(
        context: Activity,
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        @Query("grId") grId: Int?
    ) = RetrofitInstance(context ).api(baseUrl).getSingleGRByGRId(grId)

    suspend fun deleteGRLineItemsUnit(
        context: Activity,
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        @Query("lineLineUnitId") lineLineUnitId: Int?
    ) = RetrofitInstance(context ).api(baseUrl).deleteGRLineItemsUnit(lineLineUnitId)

    suspend fun submitGR(
        context: Activity,
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        @Body
        submitGRRequest: SubmitGRRequest
    ) = RetrofitInstance(context ).api(baseUrl).submitGR(submitGRRequest)
    suspend fun deleteGRLineUnit(
        context: Activity,
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        @Query("lineItemId") lineLineUnitId: Int?
    ) = RetrofitInstance(context ).api(baseUrl).deleteGRLineUnit(lineLineUnitId)


    suspend fun printLabelForGR(
        context: Activity,
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        @Body
        grnLineUnitItemId: ArrayList<Int>
    ) = RetrofitInstance(context ).api(baseUrl).printLabelForGR(grnLineUnitItemId)

    suspend fun getPRNFleDetail(
        context: Activity,
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        @Query("labelKey") labelKey: String
    ) = RetrofitInstance(context ).api(baseUrl).getPRNFleDetail(labelKey)
    suspend fun getSelfSystemMappingDetail(
        context: Activity,
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
    ) = RetrofitInstance(context ).api(baseUrl).getSelfSystemMappingDetail()

    suspend fun getAllActiveDeviceLocationDeviceMapping(
        context: Activity,
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        @Query("deviceType") deviceType: String
    ) = RetrofitInstance(context ).api(baseUrl).getAllActiveDeviceLocationDeviceMapping(deviceType)

    suspend fun updateDefaultPrinterOnDevice(
        context: Activity,
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        @Body
        printerDeviceLocationMappingIdRequest: PrinterDeviceLocationMappingIdRequest
    ) = RetrofitInstance(context ).api(baseUrl).updateDefaultPrinterOnDevice(printerDeviceLocationMappingIdRequest)

    suspend fun getAllTax(
        context: Activity,
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
    ) = RetrofitInstance(context ).api(baseUrl).getAllTax()

    suspend fun updateOtherCharges(
        context: Activity,
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        @Query("GrnId") grnId: Int,
        @Query("OtherCharges") otherCharges: Double
    ) = RetrofitInstance(context ).api(baseUrl).updateOtherCharges(grnId,otherCharges)

    suspend fun getOtherCharges(
        context: Activity,
        @Header(Constants.HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        baseUrl: String,
        @QueryMap queryMap: Map<String, Int>
    ) = RetrofitInstance(context ).api(baseUrl).getOtherCharges(queryMap)



}