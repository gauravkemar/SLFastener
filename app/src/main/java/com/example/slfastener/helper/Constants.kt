package com.example.demorfidapp.helper

object Constants {


    const val GET = 1
    const val POST = 2
    const val HTTP_OK = 200
    const val HTTP_CREATED = 201
    const val HTTP_EXCEPTION = 202
    const val HTTP_UPDATED = 204
    const val HTTP_FOUND = 302
    const val HTTP_NOT_FOUND = 404
    const val HTTP_CONFLICT = 409
    const val HTTP_INTERNAL_SERVER_ERROR = 500
    const val HTTP_ERROR = 400
    const val NO_INTERNET = "No Internet Connection"
    const val CONFIG_ERROR = "Please configure network details"
    const val SHARED_PREF = "shared_pref"
    const val LOGGED_IN = "LOGGEDIN"
    const val KEY_ISLOGGEDIN = "isLoggedIn"
    const val HTTP_ERROR_MESSAGE = "message"
    const val KEY_USER_NAME = "userName"
    const val KEY_USER_FIRST_NAME = "firstName"
    const val KEY_USER_LAST_NAME= "lastName"
    const val KEY_USER_EMAIL = "email"
    const val KEY_USER_MOBILE_NUMBER = "mobileNumber"
    const val ROLE_NAME = "roleName"
    const val KEY_JWT_TOKEN = "jwtToken"
    const val KEY_SERVER_IP = "serverIp"
    const val KEY_HTTP = "http"
    const val KEY_PRINTER_TYPE = "printer_type"
    const val KEY_PORT = "port"
    const val KEY_GR_PRN = "gr_prn"
    const val KEY_GR_PRN_File_Name = "GR_Prn_File_Name"
    const val KEY_GRN_PRN = "grn_prn"
    const val KEY_GRN_PRN_FILE_NAME = "GRN_Prn_File_Name"
    const val HTTP_HEADER_AUTHORIZATION = "Authorization"
    const val SESSION_EXPIRE = "Session Expired ! Please relogin"
    const val KEY_REFRESH_TOKEN = "refreshToken"
    const val  getRefreshToken = "UserManagement/refreshtoken";
    //const val BASE_URL = "http://103.240.90.141:80/Service/api/"
    //const val BASE_URL = "http://192.168.1.205:7510/service/api/"
    const val BASE_URL = "http://192.168.1.231:5000/api/"

    const val LOGIN_URL = "UserManagement/authenticate"
    const val GET_ACTIVE_SUPPLIERS_DDL = "BP/GetActiveSuppliersDDL"
    const val GET_SUPPLIERS_POS_DDL = "PurchaseOrder/getAllOpenSuppliersPOsDDL"
    const val GET_SUPPLIERS_POS = "PurchaseOrder/getSuppliersPOs"
    const val GET_POS_LINE_ITEMS_ON_POIDS = "PurchaseOrder/getPOsAndLineItemsOnPOIds"
    const val GET_GRN_FILTERED_GRN = "GRN/filteredGRN"
    const val PROCESS_GRN = "GRN/processGRN"
    const val PROCESS_SINGLE_GRN_GRN_ITEM_BATCHES = "GRN/processGRNLineItems"
    const val BARCODE_GENERATE_WITH_PREFIX = "BarcdeGenerator/GetBarcodeValueWithPrefix"
    const val GET_DRAFT_GRN = "GRN/getDraftGrn"

    const val DELETE_GRN_LINE_ITEM_UNIT = "GRN/deleteGRNLineItemsUnit"
    const val DELETE_GRN_LINE_ITEM_ID = "GRN/deleteGRNLineItemonId"
    const val DELETE_GR_LINE_ITEM_ID = "GoodsReceipt/deleteGRLineItem"
    const val SUBMIT_GRN = "GRN/submitGRN"

    const val GET_ALL_LOCATION = "Location/getAllLocations"
    const val GET_ALL_ITEM_MASTER = "ItemMaster/getAllItemMaster"
    const val PROCESS_GR_TRANSACTION = "GoodsReceipt/processGRTransaction"
    const val PROCESS_GR_LINE_ITEM = "GoodsReceipt/processGRLineItem"
    const val GET_ALL_GR = "GoodsReceipt/getAllGR"
    const val GET_DRAFT_GR = "GoodsReceipt/getSingleGRByGRId"
    const val DELETE_GR_LINE_ITEM_UNIT = "GoodsReceipt/deleteGRLineItemsUnit"
    const val SUBMIT_GR = "GoodsReceipt/submitGR"
    const val GET_ALL_TAX= "GRN/GetAllTax"
    const val UPDATE_OTHER_CHARGES= "GRN/UpdateOtherCharges"
    const val UPDATE_LINE_ITEM= "GRN/UpdateLineItem"


    const val GET_OTHER_CHARGES= "GRN/GetAddtionalCharge"


    //printing
    const val PRINT_LABEL_BARCODE_For_GRN = "PrintProductLabel/printLabelbarcodeForGRNAsync"
    const val GET_PRN_FILE_DETAIL = "PrintPRNDetails/GetPRNFileDetailOnKeyAsync"
    const val GET_SELF_SYSTEM_MAPPING_DETAILS = "DeviceLocationMapping/GetSelfSystemMappingDetailAsync"
    const val GET_ALL_ACTIVE_DEVICE_LOCATION_MAPPING_ON_DEVICE_TYPE= "DeviceLocationMapping/GetAllActiveDeviceLocationMappingOnSystemIPAndDeviceType"
    const val UPDATE_DEFAULT_PRINTER_ON_DEVICE= "DeviceLocationMapping/UpdateDefaultPrinterOnDevice"
    const val GET_GRN_PRODUCT_DETAILS_UNIT_ID= "PrintProductLabel/GetGRNProductDetailsOnUnitId"



}