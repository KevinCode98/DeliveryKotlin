package com.example.delivery.fragments.client

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.delivery.R
import com.example.delivery.adapters.CategoriesAdapter
import com.example.delivery.activities.client.shopping_bag.ClientShoppingBagActivity
import com.example.delivery.models.Category
import com.example.delivery.models.User
import com.example.delivery.providers.CategoriesProvider
import com.example.delivery.utils.SharedPref
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ClientCategoriesFragment : Fragment() {
    val TAG = "CategoriesFragment"
    var myView: View? = null
    var recyclerViewCategories: RecyclerView? = null
    var adapter: CategoriesAdapter? = null
    var categoriesProvider: CategoriesProvider? = null
    var user: User? = null
    var sharedPref: SharedPref? = null
    var categories = ArrayList<Category>();
    var toolbar: androidx.appcompat.widget.Toolbar? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_client_categories, container, false)
        setHasOptionsMenu(true)

        toolbar = myView?.findViewById(R.id.toolbar)
        toolbar?.setTitleTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        toolbar?.title = "Categorias"
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        recyclerViewCategories = myView?.findViewById(R.id.recyclerview_catrgories)
        recyclerViewCategories?.layoutManager = LinearLayoutManager(requireContext())
        sharedPref = SharedPref(requireActivity())
        getUserFromSession()

        categoriesProvider = CategoriesProvider(user?.sessionToken!!)

        getCategories()

        return myView
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_shopping_bag, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.item_shopping_bag){
            goToShoppingBag()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun goToShoppingBag(){
        val i = Intent(requireContext(), ClientShoppingBagActivity::class.java)
        startActivity(i)
    }

    private fun getCategories() {
        categoriesProvider?.getAll()?.enqueue(object : Callback<ArrayList<Category>> {
            override fun onResponse(
                call: Call<ArrayList<Category>>,
                response: Response<ArrayList<Category>>
            ) {
                if (response.body() != null) {
                    categories = response.body()!!
                    adapter = CategoriesAdapter(requireActivity(), categories)
                    recyclerViewCategories?.adapter = adapter
                }
            }

            override fun onFailure(call: Call<ArrayList<Category>>, t: Throwable) {
                Log.d(TAG, "Error: ${t.message}")
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG).show()
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