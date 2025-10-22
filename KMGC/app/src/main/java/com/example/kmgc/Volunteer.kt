package com.example.kmgc

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Volunteer : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_volunteer)

        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize views
        val nameInput = findViewById<EditText>(R.id.volunteerName)
        val emailInput = findViewById<EditText>(R.id.volunteerEmail)
        val phoneInput = findViewById<EditText>(R.id.volunteerPhone)
        val roleSpinner = findViewById<Spinner>(R.id.volunteerRoleSpinner)
        val submitButton = findViewById<Button>(R.id.submitVolunteer)

        // Verify views exist
        if (listOf(nameInput, emailInput, phoneInput, roleSpinner, submitButton).any { it == null }) {
            Log.e("VolunteerActivity", "One or more views not found")
            Toast.makeText(this, "UI error: Contact support", Toast.LENGTH_LONG).show()
            return
        }

        // Set up Spinner for preferred roles
        val roles = arrayOf("Select Role", "Cook", "Serve", "Organize Food Drives", "Other")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, roles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        roleSpinner.adapter = adapter

        // Handle "Sign Up" button click
        submitButton.setOnClickListener {
            // Get user input
            val name = nameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val phone = phoneInput.text.toString().trim()
            val role = roleSpinner.selectedItem.toString()

            var isValid = true

            // Validation
            if (name.isEmpty()) {
                nameInput.error = "Name is required"
                isValid = false
            } else {
                nameInput.error = null
            }

            if (email.isEmpty()) {
                emailInput.error = "Email is required"
                isValid = false
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailInput.error = "Invalid email address"
                isValid = false
            } else {
                emailInput.error = null
            }

            if (phone.isEmpty()) {
                phoneInput.error = "Phone is required"
                isValid = false
            } else if (phone.length != 10) {
                phoneInput.error = "Phone must be 10 digits"
                isValid = false
            } else {
                phoneInput.error = null
            }

            if (role == "Select Role") {
                Toast.makeText(this, "Please select a preferred role", Toast.LENGTH_SHORT).show()
                isValid = false
            }

            // If valid, save and navigate
            if (isValid) {
                val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                val dbHelper = DatabaseHelper(this)
                val volunteerSaved = dbHelper.insertVolunteer(name, email, phone, role, timestamp)

                if (volunteerSaved) {
                    Log.d("VolunteerActivity", "Volunteer saved: Name=$name, Email=$email, Phone=$phone, Role=$role, Timestamp=$timestamp")

                    // ✅ Redirect to success screen
                    val intent = Intent(this, VolunteerSuccess::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Log.e("VolunteerActivity", "Failed to save volunteer")
                    Toast.makeText(this, "Failed to save volunteer submission", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
