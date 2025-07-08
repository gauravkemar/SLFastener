package com.example.slfastener.api

import com.example.demorfidapp.helper.Constants
import com.example.slfastener.helper.refreshtoken.RefreshTokenRequest
import com.example.slfastener.helper.refreshtoken.RefreshTokenResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface RefreshTokenApi {
    @POST(Constants.getRefreshToken)
    fun refreshToken(@Body request: RefreshTokenRequest): Call<RefreshTokenResponse>
}