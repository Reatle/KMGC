package com.example.kmgc

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Dashboard : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)

        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize buttons
        val donateButton = findViewById<Button>(R.id.donateButton)
        val volunteerButton = findViewById<Button>(R.id.volunteerButton)
        val shareButton = findViewById<Button>(R.id.shareButton)
        val adminButton = findViewById<Button>(R.id.adminButton)
        val logoutButton = findViewById<Button>(R.id.logoutButton)

        // Retrieve user data
        val email = intent.getStringExtra("USER_EMAIL")
        val name = intent.getStringExtra("USER_NAME")
        val isAdmin = intent.getBooleanExtra("IS_ADMIN", false)

        // Show admin button only for admins
        adminButton.visibility = if (isAdmin) View.VISIBLE else View.GONE

        // Donate button → open Donation page
        donateButton.setOnClickListener {
            try {
                val intent = Intent(this, Donation::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                Log.e("DashboardActivity", "Error opening DonationActivity: ${e.message}", e)
                Toast.makeText(this, "Failed to open donation page", Toast.LENGTH_SHORT).show()
            }
        }

        // Volunteer button → open Volunteer page
        volunteerButton.setOnClickListener {
            try {
                val intent = Intent(this, Volunteer::class.java)
                intent.putExtra("USER_EMAIL", email)
                intent.putExtra("USER_NAME", name)
                startActivity(intent)
            } catch (e: Exception) {
                Log.e("DashboardActivity", "Error opening VolunteerActivity: ${e.message}", e)
                Toast.makeText(this, "Failed to open volunteer page", Toast.LENGTH_SHORT).show()
            }
        }

        // Share button → open Instagram
        shareButton.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = android.net.Uri.parse("https://www.instagram.com/kopanomanyano?igsh=aXFkN21vcnByMjU5")
                startActivity(intent)
            } catch (e: Exception) {
                Log.e("DashboardActivity", "Error opening Instagram: ${e.message}", e)
                Toast.makeText(this, "Failed to open Instagram page", Toast.LENGTH_SHORT).show()
            }
        }

        // Admin button → open AdminActivity
        adminButton.setOnClickListener {
            try {
                val intent = Intent(this, Admin::class.java)
                intent.putExtra("USER_EMAIL", email)
                startActivity(intent)
            } catch (e: Exception) {
                Log.e("DashboardActivity", "Error opening AdminActivity: ${e.message}", e)
                Toast.makeText(this, "Failed to open admin page", Toast.LENGTH_SHORT).show()
            }
        }

        // Logout button → return to LoginActivity
        logoutButton.setOnClickListener {
            try {
                // Optional: clear stored login data
                val sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE)
                sharedPref.edit().clear().apply()

                // Go back to login screen
                val intent = Intent(this, Login::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
                Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("DashboardActivity", "Error logging out: ${e.message}", e)
                Toast.makeText(this, "Logout failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
