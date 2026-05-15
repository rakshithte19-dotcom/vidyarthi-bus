package com.example.vidyarthibus

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class RouteSelectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_route_selection)

        val btn21 = findViewById<Button>(R.id.btnRoute21)
        val btn22 = findViewById<Button>(R.id.btnRoute22)
        val btn25 = findViewById<Button>(R.id.btnRoute25)

        btn21.setOnClickListener { openMain("Route_21") }
        btn22.setOnClickListener { openMain("Route_22") }
        btn25.setOnClickListener { openMain("Route_25") }
    }

    private fun openMain(routeName: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("SELECTED_ROUTE", routeName)
        startActivity(intent)
    }
}
