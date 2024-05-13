package  com.example.demorfidapp.api



import com.example.demorfidapp.helper.Constants
import com.example.demorfidapp.helper.Constants.BARCODE_GENERATE_WITH_PREFIX
import com.example.demorfidapp.helper.Constants.DELETE_GRN_LINE_ITEM_UNIT
import com.example.demorfidapp.helper.Constants.GET_ACTIVE_SUPPLIERS_DDL
import com.example.demorfidapp.helper.Constants.GET_ALL_LOCATION
import com.example.demorfidapp.helper.Constants.GET_DRAFT_GRN
import com.example.demorfidapp.helper.Constants.GET_GRN_FILTERED_GRN
import com.example.demorfidapp.helper.Constants.GET_POS_LINE_ITEMS_ON_POIDS
import com.example.demorfidapp.helper.Constants.GET_SUPPLIERS_POS
import com.example.demorfidapp.helper.Constants.GET_SUPPLIERS_POS_DDL
import com.example.demorfidapp.helper.Constants.HTTP_HEADER_AUTHORIZATION
import com.example.demorfidapp.helper.Constants.LOGIN_URL
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
import com.example.slfastener.model.login.LoginRequest
import com.example.slfastener.model.login.LoginResponse
import com.example.slfastener.model.polineitemnew.GRNUnitLineItemsSaveRequest

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
        @Header(HTTP_HEADER_AUTHORIZATION) bearerToken: String,
    ): Response<ArrayList<GetActiveSuppliersDDLResponse>>

    @GET(GET_SUPPLIERS_POS_DDL)
    suspend fun getSuppliersPosDDLL(
        @Header(HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        @Query("bpCode") bpCode: String?
    ): Response<ArrayList<GetSuppliersPOsDDLResponse>>

    @POST(GET_SUPPLIERS_POS)
    suspend fun getSuppliersPOs(
        @Header(HTTP_HEADER_AUTHORIZATION) bearerToken: String,
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
     suspend fun getFilteredGRN (
      @Header(HTTP_HEADER_AUTHORIZATION) bearerToken: String,
      @Body
      getFilteredGRNRequest: GetFilteredGRNRequest
     ): Response<ArrayList<GetFilteredGRNResponse>>

     @POST(GET_GRN_FILTERED_GRN)
     suspend fun getFilteredGRNCompleted (
      @Header(HTTP_HEADER_AUTHORIZATION) bearerToken: String,
      @Body
      getFilteredGRNRequest: GetFilteredGRNRequest
     ): Response<ArrayList<GetFilteredGRNResponse>>

    @POST(GET_POS_LINE_ITEMS_ON_POIDS)
    suspend fun getPosLineItemsOnPoIds(
        @Header(HTTP_HEADER_AUTHORIZATION) bearerToken: String,
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
      @Header(HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        @Body
        grnSaveToDraftDefaultRequest: GRNSaveToDraftDefaultRequest
    ): Response<ProcessGRNLineItemsResponse>

    @POST(Constants.PROCESS_SINGLE_GRN_GRN_ITEM_BATCHES)
    suspend fun processSingleGRNGRNItemBatches(
      @Header(HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        @Body
        grnUnitLineItemsSaveRequest: GRNUnitLineItemsSaveRequest
    ): Response<GrnLineItemResponse>


    @GET(BARCODE_GENERATE_WITH_PREFIX)
    suspend fun getBarcodeValueWithPrefix
    (
        @Header(HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        @Query("transactionPrefix") transactionPrefix: String?
    ): Response<GeneralResponse>

    @GET(BARCODE_GENERATE_WITH_PREFIX)
    suspend fun getBarcodeValueWithPrefixForExisitng
    (
        @Header(HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        @Query("transactionPrefix") transactionPrefix: String?
    ): Response<GeneralResponse>
    @GET(BARCODE_GENERATE_WITH_PREFIX)
    suspend fun getBarcodeForMultipleBatches
    (
        @Header(HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        @Query("transactionPrefix") transactionPrefix: String?
    ): Response<GeneralResponse>





    @GET(GET_DRAFT_GRN)
    suspend fun getDraftGRN
    (
        @Header(HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        @Query("grnId") grnId: Int?
    ): Response<GetDraftGrnResponse>

    @POST(Constants.SUBMIT_GRN)
    suspend fun submitGRN
    (
        @Header(HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        @Body
        submitGRNRequest: SubmitGRNRequest
    ): Response<GeneralResponse>

    @GET(DELETE_GRN_LINE_ITEM_UNIT)
    suspend fun deleteGRNLineItemsUnit
                (
        @Header(HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        @Query("lineLineUnitId") lineLineUnitId: Int?
    ): Response<GrnBatchDeleteResponse>

    @GET(GET_ALL_LOCATION)
    suspend fun getAllLocations(
        @Header(HTTP_HEADER_AUTHORIZATION) bearerToken: String,
    ): Response<ArrayList<GetAllWareHouseLocationResponse>>



}