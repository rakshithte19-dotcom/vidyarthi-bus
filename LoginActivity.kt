package com.example.vidyarthibus

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnSignIn: Button

    override fun onStart() {
        super.onStart()
        // 1. SESSION MANAGEMENT:
        // If user is already logged in, take them to the ROUTE SELECTION screen
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            navigateToRouteSelection()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        etEmail = findViewById(R.id.editEmail)
        etPassword = findViewById(R.id.editPassword)
        btnSignIn = findViewById(R.id.btnLogin)

        btnSignIn.setOnClickListener {
            performLogin()
        }
    }

    private fun performLogin() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (email.isEmpty()) {
            etEmail.error = "Email is required"
            etEmail.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Please enter a valid college email"
            etEmail.requestFocus()
            return
        }

        if (password.length < 6) {
            etPassword.error = "Password must be at least 6 characters"
            etPassword.requestFocus()
            return
        }

        btnSignIn.isEnabled = false

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                btnSignIn.isEnabled = true

                if (task.isSuccessful) {
                    Toast.makeText(this, "Welcome back!", Toast.LENGTH_SHORT).show()
                    // FIX: Go to Route Selection, not directly to the Dashboard
                    navigateToRouteSelection()
                } else {
                    Toast.makeText(this, "Authentication Failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    // UPDATED FUNCTION NAME AND DESTINATION
    private fun navigateToRouteSelection() {
        val intent = Intent(this, RouteSelectionActivity::class.java)
        startActivity(intent)
        finish()
    }
}
