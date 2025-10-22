package com.example.kmgc

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import android.database.sqlite.SQLiteConstraintException

class SignUp : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)

        // Initialize DatabaseHelper
        dbHelper = DatabaseHelper(this)

        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Find views
        val usernameInput = findViewById<TextInputEditText>(R.id.editTextUsername)
        val emailInput = findViewById<TextInputEditText>(R.id.editTextEmail)
        val passwordInput = findViewById<TextInputEditText>(R.id.editTextPassword)
        val usernameLayout = findViewById<TextInputLayout>(R.id.usernameInputLayout)
        val emailLayout = findViewById<TextInputLayout>(R.id.emailInputLayout)
        val passwordLayout = findViewById<TextInputLayout>(R.id.passwordInputLayout)
        val termsCheckBox = findViewById<CheckBox>(R.id.termsCheckBox)
        val signupButton = findViewById<MaterialButton>(R.id.signupButton)
        val progressBar = findViewById<ProgressBar>(R.id.submitProgressBar)
        val loginPrompt = findViewById<TextView>(R.id.loginPrompt)
        val signupCard = findViewById<CardView>(R.id.signupCard)

        // Verify views
        if (listOf(usernameInput, emailInput, passwordInput, usernameLayout, emailLayout, passwordLayout,
                termsCheckBox, signupButton, progressBar, loginPrompt, signupCard).any { it == null }) {
            Log.e("SignUp", "One or more views not found")
            Toast.makeText(this, "UI error: Contact support", Toast.LENGTH_LONG).show()
            return
        }

        // Animate CardView
        signupCard.animate()
            .alpha(1.0f)
            .setDuration(800)
            .setStartDelay(200)
            .start()

        // Sign Up button click listener
        signupButton.setOnClickListener {
            // Reset errors
            usernameLayout.error = null
            emailLayout.error = null
            passwordLayout.error = null

            // Validate inputs
            val username = usernameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            var isValid = true

            if (username.isEmpty()) {
                usernameLayout.error = "Username is required"
                isValid = false
            } else if (username.length !in 3..50) {
                usernameLayout.error = "Username must be 3-50 characters"
                isValid = false
            }

            if (email.isEmpty()) {
                emailLayout.error = "Email is required"
                isValid = false
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailLayout.error = "Invalid email address"
                isValid = false
            }

            if (password.isEmpty()) {
                passwordLayout.error = "Password is required"
                isValid = false
            } else if (password.length < 6) {
                passwordLayout.error = "Password must be at least 6 characters"
                isValid = false
            }

            if (!termsCheckBox.isChecked) {
                Toast.makeText(this, "Please agree to the Terms and Conditions", Toast.LENGTH_SHORT).show()
                isValid = false
            }

            if (isValid) {
                progressBar.visibility = View.VISIBLE
                signupButton.isEnabled = false

                try {
                    // Check if email already exists
                    if (dbHelper.isEmailRegistered(email)) {
                        emailLayout.error = "Email already registered"
                        return@setOnClickListener
                    }

                    // Determine if user is admin (hardcoded admin email)
                    val isAdmin = email.equals("kopanomanyano@gmail.com", ignoreCase = true)

                    // Attempt to save user
                    val result = dbHelper.addUser(email, password, username, isAdmin)

                    if (result) {
                        Toast.makeText(this, "Sign Up successful!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, Login::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Failed to save user. Please try again.", Toast.LENGTH_LONG).show()
                        Log.e("SignUp", "Failed to insert user into SQLite")
                    }
                } catch (e: SQLiteConstraintException) {
                    emailLayout.error = "Email already registered"
                    Log.e("SignUp", "SQLite constraint error: ${e.message}", e)
                } catch (e: Exception) {
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    Log.e("SignUp", "Unexpected error: ${e.message}", e)
                } finally {
                    progressBar.visibility = View.GONE
                    signupButton.isEnabled = true
                }
            }
        }

        // Login prompt click listener
        loginPrompt.setOnClickListener {
            Log.d("SignUp", "Login prompt clicked")
            startActivity(Intent(this, Login::class.java))
            finish()
        }
    }
}
