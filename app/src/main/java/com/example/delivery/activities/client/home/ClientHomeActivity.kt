package com.example.delivery.activities.client.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.delivery.R
import com.example.delivery.activities.MainActivity
import com.example.delivery.fragments.client.ClientCategoriesFragment
import com.example.delivery.fragments.client.ClientOrdersFragment
import com.example.delivery.fragments.client.ClientProfileFragment
import com.example.delivery.models.User
import com.example.delivery.utils.SharedPref
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson

class ClientHomeActivity : AppCompatActivity() {
    private val TAG = "ClientHomeActivity"

    var sharedPref: SharedPref? = null
//    var buttonLogout: Button? = null

    var bottomNavigation: BottomNavigationView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_home)

        sharedPref = SharedPref(this)

//        buttonLogout?.setOnClickListener { logout() }
        openFragment(ClientCategoriesFragment())
        bottomNavigation = findViewById(R.id.bottom_navigation)
        bottomNavigation?.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.item_home -> {
                    openFragment(ClientCategoriesFragment())
                    true
                }

                R.id.item_orders -> {
                    openFragment(ClientOrdersFragment())
                    true
                }

                R.id.item_profile -> {
                    openFragment(ClientProfileFragment())
                    true
                }
                else -> false
            }
        }

        getUserFromSession()
    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun getUserFromSession() {
        val gson = Gson()

        if (!sharedPref?.getData("user").isNullOrBlank()) {
            // EXISTE USUARIO
            val user = gson.fromJson(sharedPref?.getData("user"), User::class.java)
            Log.d(TAG, "Usuario: $user")
        }
    }
}