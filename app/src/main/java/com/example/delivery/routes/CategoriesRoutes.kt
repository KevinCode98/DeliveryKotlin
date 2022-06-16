package com.example.delivery.routes

import android.provider.ContactsContract
import com.example.delivery.models.Category
import com.example.delivery.models.ResponseHttp
import com.example.delivery.models.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface CategoriesRoutes {
    @Multipart
    @POST("categories/create")
    fun create(
        @Part image: MultipartBody.Part,
        @Part("category") category: RequestBody,
        @Header("Authorization") token:String
    ): Call<ResponseHttp>

    @GET("categories/getAll")
    fun getAll(
        @Header("Authorization") token:String
    ): Call<ArrayList<Category>>
}