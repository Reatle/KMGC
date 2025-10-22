package com.example.kmgc

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EditUser : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private var userId: Int = -1  // store user ID for updates/deletes

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user)

        dbHelper = DatabaseHelper(this)

        // Views
        val nameInput = findViewById<EditText>(R.id.editUserName)
        val emailInput = findViewById<EditText>(R.id.editUserEmail)
        val adminCheckBox = findViewById<CheckBox>(R.id.adminCheckBox)
        val saveButton = findViewById<Button>(R.id.saveButton)
        val deleteButton = findViewById<Button>(R.id.deleteButton)

        // Get email from intent
        val userEmail = intent.getStringExtra("USER_EMAIL")
        if (userEmail.isNullOrEmpty()) {
            Toast.makeText(this, "No user email provided", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Fetch user
        val user = dbHelper.getUserByEmail(userEmail)
        if (user == null) {
            Toast.makeText(this, "User not found", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Save user ID
        userId = user.id

        // Populate fields
        nameInput.setText(user.name)
        emailInput.setText(user.email)
        adminCheckBox.isChecked = user.isAdmin

        // Save button click
        saveButton.setOnClickListener {
            val newName = nameInput.text.toString().trim()
            val newEmail = emailInput.text.toString().trim()
            val isAdmin = adminCheckBox.isChecked

            // Validate
            if (newName.isEmpty()) {
                nameInput.error = "Name is required"
                return@setOnClickListener
            }
            if (newEmail.isEmpty()) {
                emailInput.error = "Email is required"
                return@setOnClickListener
            }

            val success = dbHelper.updateUser(userId, newEmail, newName, isAdmin)
            if (success) {
                Toast.makeText(this, "User updated successfully", Toast.LENGTH_SHORT).show()
                // Notify Admin activity to refresh
                setResult(RESULT_OK, Intent().apply {
                    putExtra("USER_UPDATED", true)
                })
                finish()
            } else {
                Toast.makeText(this, "Failed to update user", Toast.LENGTH_LONG).show()
                Log.e("EditUser", "Update failed for user id: $userId")
            }
        }

        // Delete button click
        deleteButton.setOnClickListener {
            val success = dbHelper.deleteUser(userId)
            if (success) {
                Toast.makeText(this, "User deleted successfully", Toast.LENGTH_SHORT).show()
                // Notify Admin activity to refresh
                setResult(RESULT_OK, Intent().apply {
                    putExtra("USER_UPDATED", true)
                })
                finish()
            } else {
                Toast.makeText(this, "Failed to delete user", Toast.LENGTH_LONG).show()
                Log.e("EditUser", "Delete failed for user id: $userId")
            }
        }
    }
}
