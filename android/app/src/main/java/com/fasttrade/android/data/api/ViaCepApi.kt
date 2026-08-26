package com.fasttrade.android.data.api

import com.fasttrade.android.data.model.ViaCepResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ViaCepApi {

    @GET("ws/{cep}/json/")
    suspend fun getAddress(@Path("cep") cep: String): Response<ViaCepResponse>
}
