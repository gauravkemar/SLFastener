package  com.example.demorfidapp.api



import com.example.demorfidapp.helper.Constants.GET_ACTIVE_SUPPLIERS_DDL
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
import com.example.slfastener.model.grn.GrnMainListResponse
import com.example.slfastener.model.grnmain.GetFilteredGRNRequest
import com.example.slfastener.model.grnmain.GetFilteredGRNResponse
import com.example.slfastener.model.login.LoginRequest
import com.example.slfastener.model.login.LoginResponse

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


    @POST(GET_POS_LINE_ITEMS_ON_POIDS)
    suspend fun getPosLineItemsOnPoIds(
        @Header(HTTP_HEADER_AUTHORIZATION) bearerToken: String,
        @Body poIds: List<Int>
    ): Response<ArrayList<GetPOsAndLineItemsOnPOIdsResponse>>

    @POST(LOGIN_URL)
    suspend fun login(
        @Body
        loginRequest: LoginRequest
    ): Response<LoginResponse>


}