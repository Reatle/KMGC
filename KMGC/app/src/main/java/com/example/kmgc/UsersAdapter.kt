package com.example.kmgc

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UsersAdapter(
    private var users: List<DatabaseHelper.User>, // changed from val to var
    private val onEditClick: (DatabaseHelper.User) -> Unit,
    private val onDeleteClick: (DatabaseHelper.User) -> Unit
) : RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {

    // ViewHolder for user item
    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.findViewById(R.id.userName)
        val emailText: TextView = itemView.findViewById(R.id.userEmail)
        val roleText: TextView = itemView.findViewById(R.id.userRole)
        val editButton: Button = itemView.findViewById(R.id.btnEdit)
        val deleteButton: Button = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_item, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.nameText.text = user.name
        holder.emailText.text = user.email
        holder.roleText.text = if (user.isAdmin) "Admin" else "User"

        holder.editButton.setOnClickListener { onEditClick(user) }
        holder.deleteButton.setOnClickListener { onDeleteClick(user) }
    }

    override fun getItemCount(): Int = users.size

    // Function to refresh adapter data
    fun updateData(newUsers: List<DatabaseHelper.User>) {
        this.users = newUsers
        notifyDataSetChanged()
    }
}
