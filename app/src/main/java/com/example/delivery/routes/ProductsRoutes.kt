package com.example.delivery.routes

import android.provider.ContactsContract
import com.example.delivery.models.Category
import com.example.delivery.models.Product
import com.example.delivery.models.ResponseHttp
import com.example.delivery.models.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ProductsRoutes {
    @Multipart
    @POST("products/create")
    fun create(
        @Part images: Array<MultipartBody.Part?>,
        @Part("product") product: RequestBody,
        @Header("Authorization") token: String
    ): Call<ResponseHttp>

    @GET("products/findByCategory/{id_category}")
    fun findByCategory(
        @Path("id_category") idCategory: String,
        @Header("Authorization") token: String
    ): Call<ArrayList<Product>>
}