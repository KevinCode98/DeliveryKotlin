package com.example.delivery.activities.restaurant.orders.detail

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
import com.example.delivery.adapters.OrderProductsAdapter
import com.example.delivery.models.Category
import com.example.delivery.models.Order
import com.example.delivery.models.User
import com.example.delivery.providers.UsersProvider
import com.example.delivery.utils.SharedPref
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RestaurantOrdersDetailActivity : AppCompatActivity() {
    val TAG = "RestaurantOrdersDetail"
    val gson = Gson()
    var order: Order? = null
    var toolbar: Toolbar? = null
    var textViewClient: TextView? = null
    var textViewAddress: TextView? = null
    var textViewDate: TextView? = null
    var textViewTotal: TextView? = null
    var textViewStatus: TextView? = null
    var spinnerDeliveryMen: Spinner? = null
    var recyclerViewProducts: RecyclerView? = null
    var adapter: OrderProductsAdapter? = null
    var usersProvider: UsersProvider? = null
    var user: User? = null
    var sharedPref: SharedPref? = null
    var idDelivery = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_orders_detail)

        sharedPref = SharedPref(this)
        order = gson.fromJson(intent.getStringExtra("order"), Order::class.java)
        getUserFromSession()
        usersProvider = UsersProvider(user?.sessionToken)

        toolbar = findViewById(R.id.toolbar)
        textViewClient = findViewById(R.id.textview_client)
        textViewAddress = findViewById(R.id.textview_address)
        textViewDate = findViewById(R.id.textview_date)
        textViewTotal = findViewById(R.id.textview_total)
        textViewStatus = findViewById(R.id.textview_status)
        spinnerDeliveryMen = findViewById(R.id.spinner_delivery_men)
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
        getDeliveryMen()
    }

    private fun getDeliveryMen() {
        usersProvider?.getDeliveryMen()?.enqueue(object : Callback<ArrayList<User>> {
            override fun onResponse(
                call: Call<ArrayList<User>>,
                response: Response<ArrayList<User>>
            ) {
                if (response.body() != null) {
                    val deliveryMen = response.body()

                    val arrayAdapter = ArrayAdapter<User>(
                        this@RestaurantOrdersDetailActivity,
                        android.R.layout.simple_dropdown_item_1line,
                        deliveryMen!!
                    )
                    spinnerDeliveryMen?.adapter = arrayAdapter
                    spinnerDeliveryMen?.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                adapterView: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                l: Long
                            ) {
                                idDelivery = deliveryMen[position].id!!
                                Log.d(TAG, "Id Delivery: $idDelivery")
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {

                            }
                        }
                }
            }

            override fun onFailure(call: Call<ArrayList<User>>, t: Throwable) {
                Toast.makeText(
                    this@RestaurantOrdersDetailActivity,
                    "Error: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }

        })
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
