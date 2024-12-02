package com.firstsportz.slicknewz.data.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "https://typical-dance-dd5b253203.strapiapp.com/"

    // Create OkHttpClient with custom timeouts
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS) // Connection timeout
            .writeTimeout(60, TimeUnit.SECONDS)  // Write timeout
            .readTimeout(60, TimeUnit.SECONDS)   // Read timeout
            .build()
    }

    // Initialize Retrofit instance with custom OkHttpClient
    val instance: StrapiApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // Use the custom OkHttpClient
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(StrapiApi::class.java)
    }
}
