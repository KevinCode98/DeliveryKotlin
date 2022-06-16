package com.example.delivery.activities.client.address.list

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.delivery.R
import com.example.delivery.activities.client.address.create.ClientAddressCreateActivity
import com.example.delivery.activities.client.payments.form.ClientPaymentFormActivity
import com.example.delivery.adapters.AddressAdapter
import com.example.delivery.adapters.ShoppingBagAdapter
import com.example.delivery.models.*
import com.example.delivery.providers.AddressProvider
import com.example.delivery.providers.OrdersProvider
import com.example.delivery.utils.SharedPref
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ClientAddressListActivity : AppCompatActivity() {
    var ordersProvider: OrdersProvider? = null
    var fabCreateAddress: FloatingActionButton? = null
    var buttonNext: Button? = null
    var toolbar: Toolbar? = null
    var recyclerViewAddress: RecyclerView? = null
    var adapter: AddressAdapter? = null
    var addressProvider: AddressProvider? = null
    var sharedPref: SharedPref? = null
    var user: User? = null
    var address = ArrayList<Address>()
    var selectedProducts = ArrayList<Product>()
    val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_address_list)
        sharedPref = SharedPref(this)
        getProductsFromSharedPref()

        fabCreateAddress = findViewById(R.id.fab_address_create)
        recyclerViewAddress = findViewById(R.id.recyclerview_address)
        buttonNext = findViewById(R.id.btn_next)
        recyclerViewAddress?.layoutManager = LinearLayoutManager(this)

        toolbar = findViewById(R.id.toolbar)
        toolbar?.setTitleTextColor(ContextCompat.getColor(this, R.color.black))
        toolbar?.title = "Mis direcciones"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        getUserFromSession()
        addressProvider = AddressProvider(user?.sessionToken!!)
        ordersProvider = OrdersProvider(user?.sessionToken!!)

        fabCreateAddress?.setOnClickListener { goToAddressCreate() }
        buttonNext?.setOnClickListener { getAddressFromSession() }
        getAddress()
    }

    private fun createOrder(idAddress: String) {
        val order = Order(
            products = selectedProducts,
            idClient = user?.id!!,
            idAddress = idAddress
        )

        ordersProvider?.create(order)?.enqueue(object : Callback<ResponseHttp> {
            override fun onResponse(call: Call<ResponseHttp>, response: Response<ResponseHttp>) {
                if (response.body() != null) {
                    Toast.makeText(
                        this@ClientAddressListActivity,
                        "${response.body()?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        this@ClientAddressListActivity,
                        "Ocurrio un error en la peticion",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                Toast.makeText(
                    this@ClientAddressListActivity,
                    "Error: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun getProductsFromSharedPref() {
        if (!sharedPref?.getData("order").isNullOrBlank()) { // Existe alguna orden
            val type = object : TypeToken<ArrayList<Product>>() {}.type
            selectedProducts = gson.fromJson(sharedPref?.getData("order"), type)
        }
    }

    private fun getAddressFromSession() {
        if (!sharedPref?.getData("address").isNullOrBlank()) {
            val a = gson.fromJson(sharedPref?.getData("address"), Address::class.java)
            createOrder(a.id!!)
//            goToPaymentsForm()
        } else {
            Toast.makeText(this, "Selecciona una direccion para continuar", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun goToPaymentsForm() {
        val i = Intent(this, ClientPaymentFormActivity::class.java)
        startActivity(i)
    }

    fun resetValue(position: Int) {
        val viewHolder = recyclerViewAddress?.findViewHolderForAdapterPosition(position)
        val view = viewHolder?.itemView
        val imageViewCheck = view?.findViewById<ImageView>(R.id.imageview_check)
        imageViewCheck?.visibility = View.GONE
    }

    private fun getAddress() {
        addressProvider?.getAddress(user?.id!!)?.enqueue(object : Callback<ArrayList<Address>> {
            override fun onResponse(
                call: Call<ArrayList<Address>>,
                response: Response<ArrayList<Address>>
            ) {
                if (response.body() != null) {
                    address = response.body()!!
                    adapter = AddressAdapter(this@ClientAddressListActivity, address)
                    recyclerViewAddress?.adapter = adapter
                }
            }

            override fun onFailure(call: Call<ArrayList<Address>>, t: Throwable) {
                Toast.makeText(
                    this@ClientAddressListActivity,
                    "Error: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }

        })
    }

    private fun goToAddressCreate() {
        val i = Intent(this, ClientAddressCreateActivity::class.java)
        startActivity(i)
    }

    private fun getUserFromSession() {
        val gson = Gson()

        if (!sharedPref?.getData("user").isNullOrBlank()) {
            // EXISTE USUARIO
            user = gson.fromJson(sharedPref?.getData("user"), User::class.java)
        }
    }
}