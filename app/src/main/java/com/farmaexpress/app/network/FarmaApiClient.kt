package com.farmaexpress.app.network

import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface FarmaApiService {
    @GET("api/v1/productos")
    fun getMedicamentos(): Call<MedicamentoResponse>
}

object FarmaApiClient {
    private const val BASE_URL = "http://192.168.100.169:3003/"

    val service: FarmaApiService by lazy {
        val client = OkHttpClient.Builder()
            .addInterceptor(BackoffRetryInterceptor())
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FarmaApiService::class.java)
    }
}

