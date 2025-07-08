package  com.example.demorfidapp.api

import android.app.Activity
import com.example.slfastener.helper.TokenInterceptor
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
class RetrofitInstance(private val context: Activity) {

    fun create(baseUrl: String): Retrofit {
        // Create logging interceptor
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // Add TokenInterceptor for authentication/authorization
        val tokenInterceptor = TokenInterceptor(context)

        // Configure OkHttpClient
        val client = OkHttpClient.Builder()
            .protocols(listOf(Protocol.HTTP_1_1))
            .addInterceptor(logging)
            .addInterceptor(tokenInterceptor)
            .connectTimeout(100, TimeUnit.SECONDS)
            .readTimeout(100, TimeUnit.SECONDS)
            .writeTimeout(100, TimeUnit.SECONDS)
            .build()

        // Build Retrofit instance
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    fun api(baseUrl: String): SLFastenerAPI {
        val retrofit = create(baseUrl)
        return retrofit.create(SLFastenerAPI::class.java)
    }
}
/*
class RetrofitInstance {

  */
/*  companion object {
        private var baseUrl = ""
        private val retrofit by lazy {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS)
                .writeTimeout(100, TimeUnit.SECONDS)
                .build()
            Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }

        fun api(baseUrl: String): KDMSAPI {
            this.baseUrl = baseUrl
            return retrofit.create(KDMSAPI::class.java)
        }
    }*//*

  companion object {

      fun create(baseUrl: String): Retrofit {
          val logging = HttpLoggingInterceptor()
          logging.setLevel(HttpLoggingInterceptor.Level.BODY)

          val client = OkHttpClient.Builder()
              .addInterceptor(logging)
              .connectTimeout(100, TimeUnit.SECONDS)
              .readTimeout(100, TimeUnit.SECONDS)
              .writeTimeout(100, TimeUnit.SECONDS)
              .build()

          val gson = GsonBuilder()
              .setLenient()  // Enable lenient parsing
              .create()

          return Retrofit.Builder()
              .baseUrl(baseUrl)
              .addConverterFactory(ScalarsConverterFactory.create())
              .addConverterFactory(GsonConverterFactory.create(gson))
              .client(client)
              .build()
      }

      fun api(baseUrl: String): SLFastenerAPI {
          val retrofit = create(baseUrl)
          return retrofit.create(SLFastenerAPI::class.java)
      }
  }
}*/
