package com.example.kmgc

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class VolunteerSuccess : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_volunteer_success)

        val backButton = findViewById<Button>(R.id.btnBackHome)
        backButton.setOnClickListener {
            val intent = Intent(this, Dashboard::class.java) // or your home screen activity
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }
    }
}
