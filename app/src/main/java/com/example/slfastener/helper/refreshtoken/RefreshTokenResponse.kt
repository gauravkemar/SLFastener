package com.example.slfastener.helper.refreshtoken


data class RefreshTokenResponse(
    val jwtToken: String,
    val refreshToken: String
)
