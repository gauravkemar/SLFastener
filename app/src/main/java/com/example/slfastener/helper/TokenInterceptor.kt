package com.example.slfastener.helper

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.text.Html
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.example.demorfidapp.helper.Constants
import com.example.slfastener.api.RefreshTokenApi
import com.example.slfastener.helper.refreshtoken.RefreshTokenRequest
import com.example.slfastener.helper.refreshtoken.RefreshTokenResponse
import com.example.slfastener.view.LoginActivity
import es.dmoral.toasty.Toasty
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class TokenInterceptor(
    private val context: Activity
) : Interceptor {

    var PRIVATE_MODE = 0
    override fun intercept(chain: Interceptor.Chain): Response {
        var baseUrl: String =""
        var serverIpSharedPrefText: String? = null
        var serverHttpPrefText: String? = null

        val sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREF, PRIVATE_MODE)
        var accessToken = sharedPreferences.getString(Constants.KEY_JWT_TOKEN, null)
        var refreshToken = sharedPreferences.getString(Constants.KEY_REFRESH_TOKEN, null)

        Log.e("currentJwtToken",accessToken.toString())
        Log.e("currentRefreshToken",refreshToken.toString())


        serverIpSharedPrefText = sharedPreferences.getString(Constants.KEY_SERVER_IP,null)
        serverHttpPrefText = sharedPreferences.getString(Constants.KEY_HTTP,null)
        baseUrl = "$serverHttpPrefText://$serverIpSharedPrefText/service/api/"

        // Add the Access Token to the request
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()

        if (!accessToken.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "$accessToken")
        }

        val request = requestBuilder.build()
        val response = chain.proceed(request)

        Log.e("ResponseFromToken","response.toString()")

        // If response is 401 (Unauthorized), refresh the token
        if (response.code == 401) {
            //response.close()
            synchronized(this) {
                val newAccessToken = refreshAccessToken(refreshToken,baseUrl)

                if (newAccessToken != null) {
                    // Save new access token
                    sharedPreferences.edit().putString(Constants.KEY_JWT_TOKEN, newAccessToken.jwtToken).apply()
                    sharedPreferences.edit().putString(Constants.KEY_REFRESH_TOKEN, newAccessToken.refreshToken).apply()
                    accessToken=newAccessToken.jwtToken
                    refreshToken=newAccessToken.refreshToken
                    response.close()
                    val newRequest = originalRequest.newBuilder()
                        .removeHeader("Authorization")
                        .addHeader("Authorization", "${newAccessToken.jwtToken}")
                        .build()
                    //Log.e("ResponseFromNewToken",response.toString())
                    return chain.proceed(newRequest)
                } else {
                    Log.e("ResponseFromNewToken","NavigateCalled")
                    // If refresh token fails, navigate to login
                    //navigateToLogin(context)
                    // showToastAndHandleErrors(Constants.SESSION_EXPIRE,context)
                }
            }
        }

        return response
    }
    private fun refreshAccessToken(refreshToken: String?, baseUrl: String): RefreshTokenResponse? {
        if (refreshToken.isNullOrEmpty()) return null

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .protocols(listOf(Protocol.HTTP_1_1))
            .addInterceptor(logging)
            .connectTimeout(100, TimeUnit.SECONDS)
            .readTimeout(100, TimeUnit.SECONDS)
            .writeTimeout(100, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        val refreshTokenApi = retrofit.create(RefreshTokenApi::class.java)
        val response = refreshTokenApi.refreshToken(RefreshTokenRequest(refreshToken)).execute()

        return if (response.isSuccessful) {
            val body = response.body()
            // Return the response body which contains both the accessToken and refreshToken
            RefreshTokenResponse(
                jwtToken = body?.jwtToken ?: "",
                refreshToken = body?.refreshToken ?: ""
            )
        }
        else {
            null
        }
    }

  /*  private fun refreshAccessToken(refreshToken: String?, baseUrl: String): String? {
        if (refreshToken.isNullOrEmpty()) return null
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .protocols(listOf(Protocol.HTTP_1_1))
            .addInterceptor(logging)
            .connectTimeout(100, TimeUnit.SECONDS)
            .readTimeout(100, TimeUnit.SECONDS)
            .writeTimeout(100, TimeUnit.SECONDS)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        val refreshTokenApi = retrofit.create(RefreshTokenApi::class.java)
        val response = refreshTokenApi.refreshToken(RefreshTokenRequest(refreshToken)).execute()

        return if (response.isSuccessful) {
            response.body()?.jwtToken
        } else {
            null
        }
    }
*/

    private fun navigateToLogin(context: Activity) {

          val sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREF, PRIVATE_MODE)
          sharedPreferences.edit().clear().apply()

        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)

    }
}