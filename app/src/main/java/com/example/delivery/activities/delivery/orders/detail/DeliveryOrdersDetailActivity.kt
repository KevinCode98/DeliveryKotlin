package com.example.delivery.activities.delivery.orders.detail

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.delivery.R
import com.example.delivery.activities.restaurant.home.RestaurantHomeActivity
import com.example.delivery.adapters.OrderProductsAdapter
import com.example.delivery.models.Category
import com.example.delivery.models.Order
import com.example.delivery.models.ResponseHttp
import com.example.delivery.models.User
import com.example.delivery.providers.OrdersProvider
import com.example.delivery.providers.UsersProvider
import com.example.delivery.utils.SharedPref
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DeliveryOrdersDetailActivity : AppCompatActivity() {
    val TAG = "DeliveryOrdersDetail"
    val gson = Gson()
    var order: Order? = null
    var toolbar: Toolbar? = null
    var textViewClient: TextView? = null
    var textViewAddress: TextView? = null
    var textViewDate: TextView? = null
    var textViewTotal: TextView? = null
    var textViewStatus: TextView? = null
    var buttonUpdate: Button? = null
    var recyclerViewProducts: RecyclerView? = null
    var adapter: OrderProductsAdapter? = null
    var usersProvider: UsersProvider? = null
    var orderProvider: OrdersProvider? = null
    var user: User? = null
    var sharedPref: SharedPref? = null
    var idDelivery = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delivery_orders_detail)

        sharedPref = SharedPref(this)
        order = gson.fromJson(intent.getStringExtra("order"), Order::class.java)
        getUserFromSession()
        usersProvider = UsersProvider(user?.sessionToken)
        orderProvider = OrdersProvider(user?.sessionToken!!)

        toolbar = findViewById(R.id.toolbar)
        textViewClient = findViewById(R.id.textview_client)
        textViewAddress = findViewById(R.id.textview_address)
        textViewDate = findViewById(R.id.textview_date)
        textViewTotal = findViewById(R.id.textview_total)
        textViewStatus = findViewById(R.id.textview_status)
        buttonUpdate = findViewById(R.id.btn_update)
        recyclerViewProducts = findViewById(R.id.recyclerview_products)
        recyclerViewProducts?.layoutManager = LinearLayoutManager(this)

        adapter = OrderProductsAdapter(this, order?.products!!)
        recyclerViewProducts?.adapter = adapter

        toolbar?.setTitleTextColor(ContextCompat.getColor(this, R.color.black))
        toolbar?.title = "Orden #${order?.id}"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        textViewClient?.text = "${order?.client?.name}  ${order?.client?.lastname}"
        textViewAddress?.text = order?.address?.address
        textViewDate?.text = "${order?.timestamp}"
        textViewStatus?.text = order?.status

        Log.d(TAG, "Order: ${order.toString()}")
        getTotal()
        buttonUpdate?.setOnClickListener { updateOrder() }

        if (order?.status == "DESPACHADO") {
            buttonUpdate?.visibility = View.VISIBLE
        }
    }

    private fun updateOrder() {
        order?.idDelivery = idDelivery
        orderProvider?.updateToDispatched(order!!)?.enqueue(object : Callback<ResponseHttp> {
            override fun onResponse(call: Call<ResponseHttp>, response: Response<ResponseHttp>) {
                if (response.body() != null) {
                    if (response.body()?.isSuccess == true) {
                        Toast.makeText(
                            this@DeliveryOrdersDetailActivity,
                            "Repartidor asignado correctamente",
                            Toast.LENGTH_LONG
                        ).show()
                        goToOrders()
                    } else {
                        Toast.makeText(
                            this@DeliveryOrdersDetailActivity,
                            "No se pudo asignar el repartidor",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@DeliveryOrdersDetailActivity,
                        "No hubo respuesta del servidor",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                Toast.makeText(
                    this@DeliveryOrdersDetailActivity,
                    "Error: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }

        })
    }

    private fun goToOrders() {
        val i = Intent(this, RestaurantHomeActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(i)
    }


    private fun getTotal() {
        var total = 0.0
        for (p in order?.products!!) {
            total += (p.price * p.quantity!!)
        }
        textViewTotal?.text = "$ ${total}"
    }

    private fun getUserFromSession() {
        val gson = Gson()

        if (!sharedPref?.getData("user").isNullOrBlank()) {
            // EXISTE USUARIO
            user = gson.fromJson(sharedPref?.getData("user"), User::class.java)
        }
    }
}
