package com.example.delivery.routes

import com.example.delivery.models.Address
import com.example.delivery.models.ResponseHttp
import retrofit2.Call
import retrofit2.http.*

interface AddressRoutes {
    @POST("address/create")
    fun create(
        @Body address: Address,
        @Header("Authorization") token: String
    ): Call<ResponseHttp>

    @GET("address/findByUser/{id_user}")
    fun getAddress(
        @Path("id_user") idUser: String,
        @Header("Authorization") token: String
    ): Call<ArrayList<Address>>
}