package com.example.delivery.activities.client.orders.map

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.example.delivery.R
import com.example.delivery.activities.delivery.home.DeliveryHomeActivity
import com.example.delivery.models.Order
import com.example.delivery.models.ResponseHttp
import com.example.delivery.models.User
import com.example.delivery.providers.OrdersProvider
import com.example.delivery.utils.SharedPref
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ClientOrdersMapActivity : AppCompatActivity(), OnMapReadyCallback {
    val TAG = "DeliveryOrdersMap"
    val PERMISSION_ID = 42
    val REQUEST_PHONE_CALL = 30
    var gson = Gson()
    var googleMap: GoogleMap? = null
    var fusedLocationClient: FusedLocationProviderClient? = null
    var addressLatLng: LatLng? = null
    var markerDelivery: Marker? = null
    var markerAddress: Marker? = null
    var myLocationLatLng: LatLng? = null
    var deliveryLatLng: LatLng? = null
    var textViewClient: TextView? = null
    var textViewAddress: TextView? = null
    var textViewNeighborhood: TextView? = null
    var circleImageUser: CircleImageView? = null
    var imageViewPhone: ImageView? = null
    var orderProvider: OrdersProvider? = null
    var sharedPref: SharedPref? = null
    var order: Order? = null
    var user: User? = null
    var city = ""
    var country = ""
    var address = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_orders_map)
        sharedPref = SharedPref(this)
        getUserFromSession()

        order = gson.fromJson(intent.getStringExtra("order"), Order::class.java)
        if (order?.lat != null && order?.lng != null) {
            deliveryLatLng = LatLng(order?.lat!!, order?.lng!!)
        }

        orderProvider = OrdersProvider(user?.sessionToken!!)
        addressLatLng = LatLng(order?.address?.lat!!, order?.address?.lng!!)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        textViewClient = findViewById(R.id.textview_delivery)
        textViewAddress = findViewById(R.id.textview_address)
        textViewNeighborhood = findViewById(R.id.textview_neighborhood)
        circleImageUser = findViewById(R.id.circleimage_user)
        imageViewPhone = findViewById(R.id.imageview_phone)


        getLastLocation()

        textViewClient?.text = "${order?.delivery?.name} ${order?.delivery?.lastname}"
        textViewAddress?.text = order?.address?.address
        textViewNeighborhood?.text = order?.address?.neighborhood

        if (!order?.client?.image.isNullOrBlank()) {
            Glide.with(this).load(order?.delivery?.image).into(circleImageUser!!)
        }

        imageViewPhone?.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CALL_PHONE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.CALL_PHONE),
                    REQUEST_PHONE_CALL
                )
            } else {
                call()
            }
        }
    }


    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            // Se obtiene varias veces para obtener la informacion actual
            var lastLocation = locationResult.lastLocation
            myLocationLatLng = LatLng(lastLocation.latitude, lastLocation.longitude)

            removeDeliveryMarker()
            addDeliveryMarker()
            Log.d("LOCALIZACION", "Callback: $lastLocation")
        }
    }

    private fun removeDeliveryMarker() {
        markerDelivery?.remove()
    }

    private fun drawRoute() {
        if (deliveryLatLng != null) {
            val addressLocation = LatLng(order?.address?.lat!!, order?.address?.lng!!)

            // Dibujar Ruta! :c es de pago! :c
        }
    }


    private fun addDeliveryMarker() {
        if (deliveryLatLng != null) {
            markerDelivery = googleMap?.addMarker(
                MarkerOptions().position(deliveryLatLng).title("Posicion del repartidor")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.delivery))
            )
        }
    }

    private fun addAddressMarker() {
        val addressLocation = LatLng(order?.address?.lat!!, order?.address?.lng!!)

        markerAddress = googleMap?.addMarker(
            MarkerOptions().position(addressLocation).title("Entregar aqui")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.home))
        )
    }


    private fun goToHome() {
        val i = Intent(this, DeliveryHomeActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(i)
    }


    private fun call() {
        val i = Intent(Intent.ACTION_CALL)
        i.data = Uri.parse("tel:${order?.delivery?.phone}")

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this, "Permiso denegado para realizar la llamada", Toast.LENGTH_SHORT)
                .show()
            return
        }

        startActivity(i)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (locationCallback != null && fusedLocationClient != null) {
            fusedLocationClient?.removeLocationUpdates(locationCallback)
        }
    }

    private fun getLastLocation() {
        if (checkPermission()) {

            if (isLocationEnabled()) {
                fusedLocationClient?.lastLocation?.addOnCompleteListener { task ->
                    // Se obtiene solamente una vez la localizacion
                    var location = task.result

                    if (location != null) {
                        myLocationLatLng = LatLng(location.latitude, location.longitude)

                        removeDeliveryMarker()
                        addDeliveryMarker()
                        addAddressMarker()

                        if (deliveryLatLng != null) {
                            googleMap?.moveCamera(
                                CameraUpdateFactory.newCameraPosition(
                                    CameraPosition.builder().target(
                                        LatLng(
                                            deliveryLatLng?.latitude!!,
                                            deliveryLatLng?.longitude!!
                                        )
                                    ).zoom(15f).build()
                                )
                            )
                        }

                    }
                }

            } else {
                Toast.makeText(this, "Habilita la localizacion", Toast.LENGTH_LONG).show()
                val i = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(i)
            }

        } else {
            requestPermissions()
        }
    }

    private fun requestNewLocationData() {
        val locationRequest = LocationRequest.create().apply {
            interval = 100
            fastestInterval = 50
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient?.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper() // Inicializa la posicion en tiempo real
        )
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }

        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_ID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            }
        }

        if (requestCode == REQUEST_PHONE_CALL) {
            call()
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap?.uiSettings?.isZoomControlsEnabled = true
    }

    private fun getUserFromSession() {
        val gson = Gson()

        if (!sharedPref?.getData("user").isNullOrBlank()) {
            // EXISTE USUARIO
            user = gson.fromJson(sharedPref?.getData("user"), User::class.java)
        }
    }
}