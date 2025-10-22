package com.example.kmgc

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Set up window insets for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.welcomeLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set up Get Started button click listener
        val getStartedButton = findViewById<Button>(R.id.btnGetStarted)
        getStartedButton.setOnClickListener {
            Log.d("MainActivity", "Get Started button clicked")
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }

        // Hide ProgressBar after 2 seconds (simulating loading)
        val progressBar = findViewById<ProgressBar>(R.id.loadingProgressBar)
        Handler(Looper.getMainLooper()).postDelayed({
            progressBar.visibility = View.GONE
        }, 2000)
    }
}