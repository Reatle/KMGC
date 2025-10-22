package com.example.kmgc

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SpreadAwareness : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_spread_awareness)

        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize views
        val learnMoreButton = findViewById<Button>(R.id.learnMoreButton)
        val shareButton = findViewById<Button>(R.id.shareButton)


        // Verify views
        if (listOf(learnMoreButton, shareButton).any { it == null }) {
            Log.e("SpreadAwarenessActivity", "One or more views not found")
            Toast.makeText(this, "UI error: Contact support", Toast.LENGTH_LONG).show()
            return
        }

        // Learn More button
        learnMoreButton.setOnClickListener {
            Log.d("SpreadAwarenessActivity", "Learn More button clicked")
            try {
                val url = "https://www.instagram.com/kopanomanyano/"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            } catch (e: Exception) {
                Log.e("SpreadAwarenessActivity", "Error opening URL: ${e.message}", e)
                Toast.makeText(this, "Failed to open website", Toast.LENGTH_SHORT).show()
            }
        }

        // Share button
        shareButton.setOnClickListener {
            Log.d("SpreadAwarenessActivity", "Share button clicked")
            try {
                val shareText = "Join KMGC in fighting hunger and supporting communities! Learn more: https://www.instagram.com/kopanomanyano/"
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, "Support KMGC's Cause")
                    putExtra(Intent.EXTRA_TEXT, shareText)
                }
                startActivity(Intent.createChooser(shareIntent, "Share via"))
            } catch (e: Exception) {
                Log.e("SpreadAwarenessActivity", "Error sharing content: ${e.message}", e)
                Toast.makeText(this, "Failed to share", Toast.LENGTH_SHORT).show()
            }
        }


    }
}