package com.example.kmgc

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Admin : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var usersRecyclerView: RecyclerView
    private lateinit var usersAdapter: UsersAdapter

    private val editUserLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            refreshUsersList() // refresh users list after returning from EditUser
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin)

        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DatabaseHelper(this)

        // Check admin access
        val email = intent.getStringExtra("USER_EMAIL")
        if (email == null || !dbHelper.isAdmin(email)) {
            Log.e("AdminActivity", "Unauthorized access attempt: $email")
            Toast.makeText(this, "Access denied: Admins only", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Initialize views
        val donationsRecyclerView = findViewById<RecyclerView>(R.id.donationsRecyclerView)
        val volunteersRecyclerView = findViewById<RecyclerView>(R.id.volunteersRecyclerView)
        usersRecyclerView = findViewById<RecyclerView>(R.id.usersRecyclerView)
        val backButton = findViewById<Button>(R.id.buttonBack)

        // Donations list
        donationsRecyclerView.layoutManager = LinearLayoutManager(this)
        val donations = dbHelper.getAllDonations()
        donationsRecyclerView.adapter = DonationsAdapter(donations)

        // Volunteers list
        volunteersRecyclerView.layoutManager = LinearLayoutManager(this)
        val volunteers = dbHelper.getAllVolunteers()
        volunteersRecyclerView.adapter = VolunteersAdapter(volunteers)

        // Users list (with edit & delete)
        usersRecyclerView.layoutManager = LinearLayoutManager(this)
        val users = dbHelper.getAllUsers()
        usersAdapter = UsersAdapter(
            users,
            onDeleteClick = { user ->
                dbHelper.deleteUser(user.id)
                Toast.makeText(this, "${user.name} deleted", Toast.LENGTH_SHORT).show()
                refreshUsersList()
            },
            onEditClick = { user ->
                val intent = Intent(this, EditUser::class.java)
                intent.putExtra("USER_EMAIL", user.email)
                editUserLauncher.launch(intent)
            }
        )
        usersRecyclerView.adapter = usersAdapter

        // Back button
        backButton.setOnClickListener {
            Log.d("AdminActivity", "Back button clicked")
            finish()
        }
    }

    private fun refreshUsersList() {
        val updatedUsers = dbHelper.getAllUsers()
        usersAdapter.updateData(updatedUsers)
    }
}
