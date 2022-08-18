package com.example.delivery.routes

import com.example.delivery.models.Address
import com.example.delivery.models.Order
import com.example.delivery.models.ResponseHttp
import retrofit2.Call
import retrofit2.http.*

interface OrdersRoutes {
    @POST("orders/create")
    fun create(
        @Body order: Order,
        @Header("Authorization") token: String
    ): Call<ResponseHttp>

    @PUT("orders/updateToDispatched")
    fun updateToDispatched(
        @Body order: Order,
        @Header("Authorization") token: String
    ): Call<ResponseHttp>

    @PUT("orders/updateToOnWay")
    fun updateToOnWay(
        @Body order: Order,
        @Header("Authorization") token: String
    ): Call<ResponseHttp>

    @PUT("orders/updateToDelivered")
    fun updateToDelivered(
        @Body order: Order,
        @Header("Authorization") token: String
    ): Call<ResponseHttp>

    @PUT("orders/updateLatLng")
    fun updateLatLng(
        @Body order: Order,
        @Header("Authorization") token: String
    ): Call<ResponseHttp>

    @GET("orders/findByStatus/{status}")
    fun getOrdersByStatus(
        @Path("status") status: String,
        @Header("Authorization") token: String
    ): Call<ArrayList<Order>>

    @GET("orders/findByClientAndStatus/{id_client}/{status}")
    fun getOrderByClientAndStatus(
        @Path("id_client") idClient: String,
        @Path("status") status: String,
        @Header("Authorization") token: String
    ): Call<ArrayList<Order>>

    @GET("orders/findByDeliveryAndStatus/{id_delivery}/{status}")
    fun getOrderByDeliveryAndStatus(
        @Path("id_delivery") idDelivery: String,
        @Path("status") status: String,
        @Header("Authorization") token: String
    ): Call<ArrayList<Order>>
}