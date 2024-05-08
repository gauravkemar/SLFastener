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
    const val KEY_PORT = "port"
    const val HTTP_HEADER_AUTHORIZATION = "Authorization"

    //const val BASE_URL = "http://103.240.90.141:80/Service/api/"
    const val BASE_URL = "http://192.168.1.205:7510/service/api/"
    //const val BASE_URL = "http://192.168.1.231:5000/api/"


    const val LOGIN_URL = "UserManagement/authenticate"
    const val GET_ACTIVE_SUPPLIERS_DDL = "BP/GetActiveSuppliersDDL"
    const val GET_SUPPLIERS_POS_DDL = "PurchaseOrder/getSuppliersPOsDDL"
    const val GET_SUPPLIERS_POS = "PurchaseOrder/getSuppliersPOs"
    const val GET_POS_LINE_ITEMS_ON_POIDS = "PurchaseOrder/getPOsAndLineItemsOnPOIds"
    const val GET_GRN_FILTERED_GRN = "GRN/filteredGRN"
    const val PROCESS_GRN = "GRN/processGRN"
    const val PROCESS_SINGLE_GRN_GRN_ITEM_BATCHES = "GRN/processGRNLineItems"
    const val BARCODE_GENERATE_WITH_PREFIX = "BarcdeGenerator/GetBarcodeValueWithPrefix"
    const val GET_DRAFT_GRN = "GRN/getDraftGrn"
}