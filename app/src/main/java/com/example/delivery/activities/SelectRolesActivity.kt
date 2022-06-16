package com.example.delivery.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.delivery.R
import com.example.delivery.adapters.RolesAdapter
import com.example.delivery.models.User
import com.example.delivery.utils.SharedPref
import com.google.gson.Gson

class SelectRolesActivity : AppCompatActivity() {
    var recyclerViewRoles: RecyclerView? = null
    var user: User? = null
    var adapter: RolesAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_roles)

        recyclerViewRoles = findViewById(R.id.recyclerview_roles)
        recyclerViewRoles?.layoutManager = LinearLayoutManager(this)

        getUserFromSession()
        adapter = RolesAdapter(this, user?.roles!!)
        recyclerViewRoles?.adapter = adapter
    }

    private fun getUserFromSession() {
        val sharedPref = SharedPref(this)
        val gson = Gson()

        if (!sharedPref.getData("user").isNullOrBlank()) {
            // EXISTE USUARIO
            user = gson.fromJson(sharedPref.getData("user"), User::class.java)
        }
    }
}