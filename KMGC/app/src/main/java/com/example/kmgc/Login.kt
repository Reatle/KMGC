package com.example.kmgc

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class Login : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        dbHelper = DatabaseHelper(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val progressBar = findViewById<ProgressBar>(R.id.loginProgressBar)
        val buttonBack = findViewById<ImageButton>(R.id.buttonBack)
        val emailInput = findViewById<TextInputEditText>(R.id.editTextEmail)
        val passwordInput = findViewById<TextInputEditText>(R.id.editTextPassword)
        val emailLayout = findViewById<TextInputLayout>(R.id.emailInputLayout)
        val passwordLayout = findViewById<TextInputLayout>(R.id.passwordInputLayout)
        val loginButton = findViewById<MaterialButton>(R.id.buttonLogin)
        val signupPrompt = findViewById<TextView>(R.id.signupPrompt)

        buttonBack.setOnClickListener { finish() }

        loginButton.setOnClickListener {
            emailLayout.error = null
            passwordLayout.error = null

            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            var isValid = true

            if (email.isEmpty()) { emailLayout.error = "Email is required"; isValid = false }
            else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) { emailLayout.error = "Invalid email address"; isValid = false }

            if (password.isEmpty()) { passwordLayout.error = "Password is required"; isValid = false }

            if (!isValid) return@setOnClickListener

            progressBar.visibility = View.VISIBLE
            loginButton.isEnabled = false

            try {
                if (!dbHelper.isEmailRegistered(email)) {
                    progressBar.visibility = View.GONE
                    loginButton.isEnabled = true
                    emailLayout.error = "Email not registered"
                    return@setOnClickListener
                }

                val user = dbHelper.validateUser(email, password)
                if (user != null) {
                    progressBar.visibility = View.GONE
                    loginButton.isEnabled = true
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, Dashboard::class.java)
                    intent.putExtra("USER_EMAIL", user.email)
                    intent.putExtra("USER_NAME", user.name)
                    intent.putExtra("IS_ADMIN", user.isAdmin)
                    startActivity(intent)
                    finish()
                } else {
                    progressBar.visibility = View.GONE
                    loginButton.isEnabled = true
                    passwordLayout.error = "Incorrect password"
                }
            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                loginButton.isEnabled = true
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("LoginActivity", "Unexpected error: ${e.message}", e)
            }
        }

        signupPrompt.setOnClickListener {
            startActivity(Intent(this, SignUp::class.java))
            finish()
        }
    }
}
