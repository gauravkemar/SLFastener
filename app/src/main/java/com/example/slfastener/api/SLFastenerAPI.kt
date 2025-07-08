package  com.example.demorfidapp.api


import com.example.demorfidapp.helper.Constants
import com.example.demorfidapp.helper.Constants.BARCODE_GENERATE_WITH_PREFIX
import com.example.demorfidapp.helper.Constants.DELETE_GRN_LINE_ITEM_ID
import com.example.demorfidapp.helper.Constants.DELETE_GRN_LINE_ITEM_UNIT
import com.example.demorfidapp.helper.Constants.DELETE_GR_LINE_ITEM_ID
import com.example.demorfidapp.helper.Constants.DELETE_GR_LINE_ITEM_UNIT
import com.example.demorfidapp.helper.Constants.GET_ACTIVE_SUPPLIERS_DDL
import com.example.demorfidapp.helper.Constants.GET_ALL_ACTIVE_DEVICE_LOCATION_MAPPING_ON_DEVICE_TYPE
import com.example.demorfidapp.helper.Constants.GET_ALL_GR
import com.example.demorfidapp.helper.Constants.GET_ALL_ITEM_MASTER
import com.example.demorfidapp.helper.Constants.GET_ALL_LOCATION
import com.example.demorfidapp.helper.Constants.GET_ALL_TAX
import com.example.demorfidapp.helper.Constants.GET_DRAFT_GR
import com.example.demorfidapp.helper.Constants.GET_DRAFT_GRN
import com.example.demorfidapp.helper.Constants.GET_GRN_FILTERED_GRN
import com.example.demorfidapp.helper.Constants.GET_GRN_PRODUCT_DETAILS_UNIT_ID
import com.example.demorfidapp.helper.Constants.GET_OTHER_CHARGES
import com.example.demorfidapp.helper.Constants.GET_POS_LINE_ITEMS_ON_POIDS
import com.example.demorfidapp.helper.Constants.GET_PRN_FILE_DETAIL
import com.example.demorfidapp.helper.Constants.GET_SELF_SYSTEM_MAPPING_DETAILS
import com.example.demorfidapp.helper.Constants.GET_SUPPLIERS_POS
import com.example.demorfidapp.helper.Constants.GET_SUPPLIERS_POS_DDL
import com.example.demorfidapp.helper.Constants.HTTP_HEADER_AUTHORIZATION
import com.example.demorfidapp.helper.Constants.LOGIN_URL
import com.example.demorfidapp.helper.Constants.PRINT_LABEL_BARCODE_For_GRN
import com.example.demorfidapp.helper.Constants.PROCESS_GR_LINE_ITEM
import com.example.demorfidapp.helper.Constants.PROCESS_GR_TRANSACTION
import com.example.demorfidapp.helper.Constants.SUBMIT_GR
import com.example.demorfidapp.helper.Constants.UPDATE_DEFAULT_PRINTER_ON_DEVICE
import com.example.demorfidapp.helper.Constants.UPDATE_OTHER_CHARGES
import com.example.slfastener.model.GetActiveSuppliersDDLResponse
import com.example.slfastener.model.GetPOsAndLineItemsOnPOIdsResponse
import com.example.slfastener.model.GetSupllierPOsDDLOriginalResponse
import com.example.slfastener.model.GetSuppliersPOsRequest
import com.example.slfastener.model.generalrequest.GRNLineItemDeleteResponse
import com.example.slfastener.model.generalrequest.GeneralResponse
import com.example.slfastener.model.generalrequest.GrnBatchDeleteResponse
import com.example.slfastener.model.getalllocation.GetAllWareHouseLocationResponse
import com.example.slfastener.model.getalltax.GetAllTaxItem
import com.example.slfastener.model.goodsreceipt.GetAllGRResponse
import com.example.slfastener.model.goodsreceipt.GetAllItemMasterResponse
import com.example.slfastener.model.goodsreceipt.PostProcessGRTransactionRequest
import com.example.slfastener.model.goodsreceipt.PostProcessGRTransactionResponse
import com.example.slfastener.model.goodsreceipt.ProcessGRLineItemRequest
import com.example.slfastener.model.goodsreceipt.ProcessGRLineItemResponse
import com.example.slfastener.model.goodsreceipt.SubmitGRRequest
import com.example.slfastener.model.goodsreceipt.grdraft.GetSingleGRByGRIdResponse
import com.example.slfastener.model.grn.GRNSaveToDraftDefaultRequest
import com.example.slfastener.model.grn.ProcessGRNLineItemsResponse
import com.example.slfastener.model.grndraftdata.GetDraftGrnResponse
import com.example.slfastener.model.grnlineitemmain.GrnLineItemResponse
import com.example.slfastener.model.grnmain.GetFilteredGRNRequest
import com.example.slfastener.model.grnmain.GetFilteredGRNResponse
import com.example.slfastener.model.grnmain.SubmitGRNRequest
import com.example.slfastener.model.login.LoginRequest
import com.example.slfastener.model.login.LoginResponse
import com.example.slfastener.model.othercharges.GetOtherChargesItem
import com.example.slfastener.model.grnlineitemmain.GRNUnitLineItemsSaveRequest
import com.example.slfastener.model.grnmain.UpdateGRNLineItem
import com.example.slfastener.model.printerprnmodel.GetAllActiveDeviceLocationDeviceType
import com.example.slfastener.model.printerprnmodel.GetPRNFileDetailOnKeyResponse
import com.example.slfastener.model.printerprnmodel.GetSelfSystemMappingDetailsResponse
import com.example.slfastener.model.printerprnmodel.PrinterDeviceLocationMappingIdRequest

import retrofit2.Response
import retrofit2.http.*


interface SLFastenerAPI {

    /* @POST(Constants.rfidTag)
     suspend fun submitRFIDDetails(
      @Body
      postRFIDReadRequest: PostRFIDReadRequest
     ): Response<String>*/
    @GET(GET_ACTIVE_SUPPLIERS_DDL)
    suspend fun getActiveSuppliersDDL(

    ): Response<ArrayList<GetActiveSuppliersDDLResponse>>

    @GET(GET_SUPPLIERS_POS_DDL)
    suspend fun getSuppliersPosDDLL(

        @Query("bpCode") bpCode: String?
    ): Response<ArrayList<GetSupllierPOsDDLOriginalResponse>>

    @POST(GET_SUPPLIERS_POS)
    suspend fun getSuppliersPOs(

        @Body
        getSuppliersPOsRequest: ArrayList<GetSuppliersPOsRequest>
    ): Response<String>
    /*
     @POST(Constants.GET_POS_LINE_ITEMS_ON_POIDS)
     suspend fun getPosLineItemsOnPoIds (
      @Body
      getSuppliersPOsRequest: ArrayList<Int>
     ): Response<ArrayList<GetPOsAndLineItemsOnPOIdsResponse>>*/

    @POST(GET_GRN_FILTERED_GRN)
    suspend fun getFilteredGRN(

        @Body
        getFilteredGRNRequest: GetFilteredGRNRequest
    ): Response<ArrayList<GetFilteredGRNResponse>>

    @POST(GET_GRN_FILTERED_GRN)
    suspend fun getFilteredGRNCompleted(

        @Body
        getFilteredGRNRequest: GetFilteredGRNRequest
    ): Response<ArrayList<GetFilteredGRNResponse>>

    @POST(GET_POS_LINE_ITEMS_ON_POIDS)
    suspend fun getPosLineItemsOnPoIds(
        @Header("Content-Type") contentType: String,
        @Header("User-Agent") userAgent: String,
        @Body poIds: MutableList<Int>
    ): Response<ArrayList<GetPOsAndLineItemsOnPOIdsResponse>>

    @POST(LOGIN_URL)
    suspend fun login(
        @Body
        loginRequest: LoginRequest
    ): Response<LoginResponse>

    /*  @POST(Constants.PROCESS_GRN)
        suspend fun processGRN(
          @Header(HTTP_HEADER_AUTHORIZATION) bearerToken: String,
            @Body
            grnSaveToDraftDefaultRequest: GRNSaveToDraftDefaultRequest
        ): Response<ProcessGRNLineItemsResponse> */
    @POST(Constants.PROCESS_GRN)
    suspend fun processGRND(

        @Body
        grnSaveToDraftDefaultRequest: GRNSaveToDraftDefaultRequest
    ): Response<ProcessGRNLineItemsResponse>

    @POST(Constants.PROCESS_SINGLE_GRN_GRN_ITEM_BATCHES)
    suspend fun processSingleGRNGRNItemBatches(

        @Body
        grnUnitLineItemsSaveRequest: GRNUnitLineItemsSaveRequest
    ): Response<GrnLineItemResponse>
    @POST(Constants.UPDATE_LINE_ITEM)
    suspend fun updateLineItem(

        @Body
        grnUnitLineItemsSaveRequest: UpdateGRNLineItem
    ): Response<GeneralResponse>

    @POST(Constants.PROCESS_SINGLE_GRN_GRN_ITEM_BATCHES)
    suspend fun processSingleGRNGRNItemBatchesForMultiple(

        @Body
        grnUnitLineItemsSaveRequest: GRNUnitLineItemsSaveRequest
    ): Response<GrnLineItemResponse>


    @GET(BARCODE_GENERATE_WITH_PREFIX)
    suspend fun getBarcodeValueWithPrefix(

        @Query("transactionPrefix") transactionPrefix: String?
    ): Response<GeneralResponse>

    @GET(BARCODE_GENERATE_WITH_PREFIX)
    suspend fun getBarcodeValueWithPrefixForExisitng(

        @Query("transactionPrefix") transactionPrefix: String?
    ): Response<GeneralResponse>

    @GET(BARCODE_GENERATE_WITH_PREFIX)
    suspend fun getBarcodeForMultipleBatches(

        @Query("transactionPrefix") transactionPrefix: String?
    ): Response<GeneralResponse>


    @GET(GET_DRAFT_GRN)
    suspend fun getDraftGRN(

        @Query("grnId") grnId: Int?
    ): Response<GetDraftGrnResponse>

    @POST(Constants.SUBMIT_GRN)
    suspend fun submitGRN(

        @Body
        submitGRNRequest: SubmitGRNRequest
    ): Response<GeneralResponse>

    @GET(DELETE_GRN_LINE_ITEM_UNIT)
    suspend fun deleteGRNLineItemsUnit(

        @Query("lineLineUnitId") lineLineUnitId: Int?
    ): Response<GrnBatchDeleteResponse>

    @GET(DELETE_GRN_LINE_ITEM_ID)
    suspend fun deleteGRNLineUnit(

        @Query("lineItemId") lineLineUnitId: Int?
    ): Response<GRNLineItemDeleteResponse>

    @GET(GET_ALL_LOCATION)
    suspend fun getAllLocations(

    ): Response<ArrayList<GetAllWareHouseLocationResponse>>

    @POST(PRINT_LABEL_BARCODE_For_GRN)
    suspend fun printLabelForGRN(

        @Body
        grnLineUnitItemId: ArrayList<Int>
    ): Response<GeneralResponse>

    @GET(GET_OTHER_CHARGES)
    suspend fun getOtherCharges(

        @QueryMap queryMap: Map<String, Int>
    ): Response<ArrayList<GetOtherChargesItem>>


    ////gr all

    @GET(GET_ALL_GR)
    suspend fun getAllGRResponse(

        @Query("Status") status: String?
    ): Response<ArrayList<GetAllGRResponse>>

    @GET(GET_ALL_GR)
    suspend fun getAllGRCompleteResponse(

        @Query("Status") status: String?
    ): Response<ArrayList<GetAllGRResponse>>

    @GET(GET_ALL_ITEM_MASTER)
    suspend fun getAllItemMaster(

    ): Response<ArrayList<GetAllItemMasterResponse>>

    @GET(GET_ACTIVE_SUPPLIERS_DDL)
    suspend fun getActiveSupplierForGR(

    ): Response<ArrayList<GetActiveSuppliersDDLResponse>>

    @POST(PROCESS_GR_TRANSACTION)
    suspend fun processGR(

        @Body
        postProcessGRTransactionRequest: PostProcessGRTransactionRequest
    ): Response<PostProcessGRTransactionResponse>


    @POST(PROCESS_GR_LINE_ITEM)
    suspend fun processSingleGRItem(

        @Body
        processGRLineItemRequest: ProcessGRLineItemRequest
    ): Response<ProcessGRLineItemResponse>


    @POST(PROCESS_GR_LINE_ITEM)
    suspend fun processSingleGRItemForMultiple(

        @Body
        processGRLineItemRequest: ProcessGRLineItemRequest
    ): Response<ProcessGRLineItemResponse>

    @GET(GET_DRAFT_GR)
    suspend fun getSingleGRByGRId(

        @Query("grId") grId: Int?
    ): Response<GetSingleGRByGRIdResponse>

    @GET(DELETE_GR_LINE_ITEM_UNIT)
    suspend fun deleteGRLineItemsUnit(

        @Query("lineLineUnitId") lineLineUnitId: Int?
    ): Response<GrnBatchDeleteResponse>

    @POST(SUBMIT_GR)
    suspend fun submitGR(

        @Body
        submitGRRequest: SubmitGRRequest
    ): Response<GeneralResponse>

    @GET(DELETE_GR_LINE_ITEM_ID)
    suspend fun deleteGRLineUnit(

        @Query("lineItemId") lineLineUnitId: Int?
    ): Response<GeneralResponse>

    @POST(PRINT_LABEL_BARCODE_For_GRN)
    suspend fun printLabelForGR(

        @Body
        grnLineUnitItemId: ArrayList<Int>
    ): Response<GeneralResponse>

    @GET(GET_PRN_FILE_DETAIL)
    suspend fun getPRNFleDetail(

        @Query("labelKey") labelKey: String
    ): Response<GetPRNFileDetailOnKeyResponse>

    @GET(GET_SELF_SYSTEM_MAPPING_DETAILS)
    suspend fun getSelfSystemMappingDetail(

    ): Response<GetSelfSystemMappingDetailsResponse>

    @GET(GET_ALL_ACTIVE_DEVICE_LOCATION_MAPPING_ON_DEVICE_TYPE)
    suspend fun getAllActiveDeviceLocationDeviceMapping(

        @Query("deviceType") deviceType: String
    ): Response<GetAllActiveDeviceLocationDeviceType>


    @POST(UPDATE_DEFAULT_PRINTER_ON_DEVICE)
    suspend fun updateDefaultPrinterOnDevice(

        @Body
        printerDeviceLocationMappingIdRequest: PrinterDeviceLocationMappingIdRequest
    ): Response<GeneralResponse>

    @POST(GET_GRN_PRODUCT_DETAILS_UNIT_ID)
    suspend fun getGRNProductDetailsOnUnitIdItem(

        @Body
        grnLineUnitItemId: ArrayList<Int>
    ): Response<ArrayList<Map<String, Any>>>

    @GET(GET_ALL_TAX)
    suspend fun getAllTax(

    ): Response<ArrayList<GetAllTaxItem>>

    @GET(UPDATE_OTHER_CHARGES)
    suspend fun updateOtherCharges(

        @Query("GrnId") grnId: Int,
        @Query("OtherCharges") otherCharges: Double
    ): Response<GeneralResponse>



}