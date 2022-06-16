package com.example.delivery.fragments.restaurant

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.delivery.R
import com.example.delivery.adapters.OrdersClientAdapter
import com.example.delivery.adapters.OrdersRestaurantAdapter
import com.example.delivery.models.Order
import com.example.delivery.models.User
import com.example.delivery.providers.OrdersProvider
import com.example.delivery.utils.SharedPref
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RestaurantOrdersStatusFragment : Fragment() {
    var status = ""
    var myView: View? = null
    var user: User? = null
    var ordersProvider: OrdersProvider? = null
    var sharedPref: SharedPref? = null
    var recyclerViewOrders: RecyclerView? = null
    var adapter: OrdersRestaurantAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_restaurant_orders_status, container, false)
        status = arguments?.getString("status")!!

        sharedPref = SharedPref(requireActivity())
        getUserFromSession()
        ordersProvider = OrdersProvider(user?.sessionToken!!)

        recyclerViewOrders = myView?.findViewById(R.id.recyclerview_orders)
        recyclerViewOrders?.layoutManager = LinearLayoutManager(requireContext())

        getOrders()
        return myView
    }

    private fun getOrders() {
        ordersProvider?.getOrdersByStatus(status)?.enqueue(object : Callback<ArrayList<Order>> {
            override fun onResponse(
                call: Call<ArrayList<Order>>,
                response: Response<ArrayList<Order>>
            ) {
                if (response.body() != null) {
                    val orders = response.body()
                    adapter = OrdersRestaurantAdapter(requireActivity(), orders!!)
                    recyclerViewOrders?.adapter = adapter
                }
            }

            override fun onFailure(call: Call<ArrayList<Order>>, t: Throwable) {
                Toast.makeText(requireActivity(), "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun getUserFromSession() {
        val gson = Gson()

        if (!sharedPref?.getData("user").isNullOrBlank()) {
            // EXISTE USUARIO
            user = gson.fromJson(sharedPref?.getData("user"), User::class.java)
        }
    }
}