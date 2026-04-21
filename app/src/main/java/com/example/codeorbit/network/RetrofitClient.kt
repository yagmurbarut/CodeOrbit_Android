package com.example.codeorbit.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "https://codeorbitbackend-production.up.railway.app/"

    // Token buraya set edilecek (login/register sonrası)
    var authToken: String = ""
    var userId: Int = 0
    private val authInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder().apply {
            if (authToken.isNotEmpty()) {
                addHeader("Authorization", "Bearer $authToken")
            }
        }.build()
        chain.proceed(request)
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    fun getUserIdFromToken(): Int {
        return try {
            val parts = authToken.split(".")
            if (parts.size < 2) return userId
            val payload = parts[1]
            val decoded = android.util.Base64.decode(
                payload.padEnd((payload.length + 3) / 4 * 4, '='),
                android.util.Base64.URL_SAFE or android.util.Base64.NO_WRAP
            )
            val json = String(decoded)
            val key = "claims/nameidentifier\":"
            val start = json.indexOf(key) + key.length + 1
            val end = json.indexOf("\"", start)
            json.substring(start, end).toInt()
        } catch (e: Exception) {
            userId
        }
    }
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}