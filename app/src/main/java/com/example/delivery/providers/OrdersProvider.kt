package com.example.delivery.providers

import com.example.delivery.api.ApiRoutes
import com.example.delivery.models.Address
import com.example.delivery.models.Order
import com.example.delivery.models.ResponseHttp
import com.example.delivery.routes.AddressRoutes
import com.example.delivery.routes.OrdersRoutes
import retrofit2.Call

class OrdersProvider(val token: String) {
    private var ordersRoutes: OrdersRoutes? = null

    init {
        val api = ApiRoutes()
        ordersRoutes = api.getOrdersRoutes(token)
    }

    fun create(order: Order): Call<ResponseHttp>? {
        return ordersRoutes?.create(order, token)
    }

    fun updateToDispatched(order: Order): Call<ResponseHttp>? {
        return ordersRoutes?.updateToDispatched(order, token)
    }

    fun getOrdersByStatus(status: String): Call<ArrayList<Order>>? {
        return ordersRoutes?.getOrdersByStatus(status, token)
    }

    fun getOrdersByClientAndStatus(idClient: String, status: String): Call<ArrayList<Order>>? {
        return ordersRoutes?.getOrderByClientAndStatus(idClient, status, token)
    }
}