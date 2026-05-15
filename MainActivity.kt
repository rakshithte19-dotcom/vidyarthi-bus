package com.example.vidyarthibus

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var database: DatabaseReference
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var googleMap: GoogleMap? = null
    private lateinit var mapView: MapView

    // College Bus Stop Coordinates (Update these for your specific college)
    private val BUS_STOP_LAT = 12.9716
    private val BUS_STOP_LNG = 77.5946

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Initialize Components
        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // 2. Bind UI Elements
        val mainLayout = findViewById<LinearLayout>(R.id.mainLayout)
        val txtStatus = findViewById<TextView>(R.id.txtStatus)
        val txtLoc = findViewById<TextView>(R.id.txtLocationStatus)
        val txtAutoNumber = findViewById<TextView>(R.id.txtAutoNumber)
        val btnVerify = findViewById<Button>(R.id.btnVerifyLocation)
        val btnReport = findViewById<Button>(R.id.btnReportFull)
        val btnHome = findViewById<Button>(R.id.btnHome)
        val btnSignOut = findViewById<Button>(R.id.btnSignOut)
        val crowdProgressBar = findViewById<ProgressBar>(R.id.crowdProgressBar)

        // 3. Firebase Setup
        val selectedRoute = intent.getStringExtra("SELECTED_ROUTE") ?: "Route_21"
        database = FirebaseDatabase.getInstance().getReference("buses/$selectedRoute")

        // 4. Click Listeners
        btnHome.setOnClickListener {
            val intent = Intent(this, RouteSelectionActivity::class.java)
            startActivity(intent)
            finish()
        }

        txtAutoNumber.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:919876543210"))
            startActivity(dialIntent)
        }

        btnVerify.setOnClickListener { checkProximity(txtLoc, btnReport) }

        btnReport.setOnClickListener {
            database.child("crowd_level").setValue(100).addOnSuccessListener {
                Toast.makeText(this, "Bus Reported as Full!", Toast.LENGTH_SHORT).show()
                // Auto-disable button after reporting to prevent spam
                btnReport.isEnabled = false
                btnReport.text = "Reported"
            }
        }

        btnSignOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // 5. Live Database Listener
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val level = snapshot.child("crowd_level").getValue(Int::class.java) ?: 0
                crowdProgressBar.progress = level

                if (level >= 100) {
                    mainLayout.setBackgroundColor(Color.parseColor("#D32F2F")) // Red
                    txtStatus.text = "Status: BUS FULL"
                    txtStatus.setTextColor(Color.WHITE)
                } else if (level >= 70) {
                    mainLayout.setBackgroundColor(Color.parseColor("#FBC02D")) // Yellow/Orange
                    txtStatus.text = "Status: CROWDED"
                    txtStatus.setTextColor(Color.BLACK)
                } else {
                    mainLayout.setBackgroundColor(Color.parseColor("#4CAF50")) // Green
                    txtStatus.text = "Status: SEATS AVAILABLE"
                    txtStatus.setTextColor(Color.WHITE)
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun checkProximity(txtLoc: TextView, btnReport: Button) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
            return
        }

        txtLoc.text = "⌛ Checking GPS..."
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val results = FloatArray(1)
                Location.distanceBetween(location.latitude, location.longitude, BUS_STOP_LAT, BUS_STOP_LNG, results)
                val distance = results[0]

                if (distance <= 500) { // Allowed within 500 meters
                    txtLoc.text = "✅ Verified: At Bus Stop"
                    btnReport.isEnabled = true
                    btnReport.alpha = 1.0f
                } else {
                    txtLoc.text = "❌ Too far (${distance.toInt()}m)"
                    btnReport.isEnabled = false
                    btnReport.alpha = 0.5f
                    Toast.makeText(this, "Must be near the bus stop to report!", Toast.LENGTH_SHORT).show()
                }
            } else {
                txtLoc.text = "❌ Error: Turn on GPS"
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        val stop = LatLng(BUS_STOP_LAT, BUS_STOP_LNG)
        googleMap?.addMarker(MarkerOptions().position(stop).title("Bus Stop"))
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(stop, 15f))

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap?.isMyLocationEnabled = true
        }
    }

    override fun onResume() { super.onResume(); mapView.onResume() }
    override fun onPause() { super.onPause(); mapView.onPause() }
    override fun onDestroy() { super.onDestroy(); mapView.onDestroy() }
    override fun onLowMemory() { super.onLowMemory(); mapView.onLowMemory() }
}
