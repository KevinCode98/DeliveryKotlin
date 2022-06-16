package com.example.delivery.adapters

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.delivery.R
import com.example.delivery.activities.client.home.ClientHomeActivity
import com.example.delivery.activities.client.products.detail.ClientProductsDetailActivity
import com.example.delivery.activities.client.shopping_bag.ClientShoppingBagActivity
import com.example.delivery.activities.delivery.home.DeliveryHomeActivity
import com.example.delivery.activities.restaurant.home.RestaurantHomeActivity
import com.example.delivery.models.Category
import com.example.delivery.models.Product
import com.example.delivery.models.Rol
import com.example.delivery.utils.SharedPref
import com.google.gson.Gson

class OrderProductsAdapter(val context: Activity, val products: ArrayList<Product>) :
    RecyclerView.Adapter<OrderProductsAdapter.OrderProductsViewHolder>() {

    val sharedPref = SharedPref(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderProductsViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.cardview_order_products, parent, false)
        return OrderProductsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    override fun onBindViewHolder(holder: OrderProductsViewHolder, position: Int) {
        val product = products[position] // CADA CATEFORIA
        holder.textViewName.text = product.name
        if (product.quantity != null) {
            holder.textViewQuantity.text = "${product.quantity!!}"
        }
        Glide.with(context).load(product.image1).into(holder.imageViewProduct)
    }

    private fun goToDetail(product: Product) {
        val i = Intent(context, ClientProductsDetailActivity::class.java)
        i.putExtra("product", product.toJson())
        context.startActivity(i)
    }

    class OrderProductsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageViewProduct: ImageView
        val textViewName: TextView
        val textViewQuantity: TextView

        init {
            imageViewProduct = view.findViewById(R.id.imageview_product)
            textViewName = view.findViewById(R.id.textview_name)
            textViewQuantity = view.findViewById(R.id.textview_quantity)
        }
    }
}