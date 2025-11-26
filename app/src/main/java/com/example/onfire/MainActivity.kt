package com.example.onfire

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    //Boton de ruta

    private lateinit var buttoncalcular: Button

    //Bonon cerrar sesion
    private lateinit var logoutButton: FrameLayout


    //Variables para el mapa de los puntos de donde ir hasta donde ir

    private  var start: String = ""
    private  var end: String = ""

    //Pintar la ruta
    var poly: Polyline? = null
    private var endMarker: Marker? = null
    private var startMarker: Marker? = null


    private lateinit var analytics: FirebaseAnalytics

    //variable para almacenar el mapa
    private lateinit var map: GoogleMap

    //Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your
                // app.
                enableMyLocation()
            } else {
                // Explain to the user that the feature is unavailable because the
                // features requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
                Toast.makeText(this, "Debe aceptar los permisos para continuar", Toast.LENGTH_SHORT).show()
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val bundle = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")


        //GUARDAR DATOS

        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("provider", provider)
        prefs.apply()
        analytics = FirebaseAnalytics.getInstance(this)

        //ggogle
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync (this)

        //Location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        //Boton
        logoutButton = findViewById(R.id.logout)



        //Botones
        //---calcular ruta
        buttoncalcular = findViewById(R.id.ruta)

        buttoncalcular.setOnClickListener {
            poly?.remove()
            endMarker?.remove()
            end = ""
            if (::map.isInitialized){
                 if (start.isNotEmpty()) {
                    map.setOnMapClickListener {
                        if (end.isEmpty()) {
                            end = "${it.longitude},${it.latitude}"
                            
                            // Resize icon
                            val height = 60
                            val width = 60
                            val bitmap = (ContextCompat.getDrawable(this, R.mipmap.fuegologo) as BitmapDrawable).bitmap
                            val smallMarker = Bitmap.createScaledBitmap(bitmap, width, height, false)

                            // Add marker
                            val markerOptions = MarkerOptions()
                                .position(it)
                                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                            endMarker = map.addMarker(markerOptions)
                            createRoute()
                        }
                    }
                }
            }
        }

        logoutButton.setOnClickListener {
            showHome(email, provider)
        }


    }


    override fun onMapReady(map: GoogleMap) {
        //google
        this.map = map
        enableMyLocation()

    }

    private fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if(location != null){
                    startMarker?.remove()
                    val userLocation = LatLng(location.latitude, location.longitude)
                    start = "${location.longitude},${location.latitude}"

                    val height = 60
                    val width = 60
                    val bitmap = (ContextCompat.getDrawable(this, R.mipmap.arbollogo) as BitmapDrawable).bitmap
                    val smallMarker = Bitmap.createScaledBitmap(bitmap, width, height, false)

                    val markerOptions = MarkerOptions()
                        .position(userLocation)
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                    startMarker = map.addMarker(markerOptions)
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))
                }
            }
        } else {
            // Permission to access the location is missing. Show rationale and request permission
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }


    private fun createRoute() {
        CoroutineScope(Dispatchers.IO).launch {
            // Obtenemos la llave usando la referencia al string
            val apiKey = getString(R.string.open_route_service_key)

            val call = getRetrofit().create(ApiService::class.java).getRoute(
                apiKey, // <--- Aquí pasamos la variable
                start,
                end
            )

            if (call.isSuccessful) {
                drawRoute(call.body())
            } else {
                Log.i("aris", "ko")
            }
        }
    }

    private fun drawRoute(routeResponse: RuteResponse?){
        val polylineOptions = PolylineOptions()
        routeResponse?.features?.first()?.geometry?.coordinates?.forEach {
            polylineOptions.add(LatLng(it[1], it[0]))

        }
        polylineOptions.color(ContextCompat.getColor(this, R.color.route_color))
        runOnUiThread {
             poly = map.addPolyline(polylineOptions)
        }
    }



    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.openrouteservice.org/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun showHome(email: String?, provider: String?) {
        val homeIntent = Intent(this, LogoutActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider)
        }
        startActivity(homeIntent)
    }
}
