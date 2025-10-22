package com.example.kmgc

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class VolunteersAdapter(
    private val volunteers: List<DatabaseHelper.Volunteer>
) : RecyclerView.Adapter<VolunteersAdapter.VolunteerViewHolder>() {

    class VolunteerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val volunteerNameText: TextView = itemView.findViewById(R.id.volunteerNameText)
        val volunteerEmailText: TextView = itemView.findViewById(R.id.volunteerEmailText)
        val phoneText: TextView = itemView.findViewById(R.id.phoneText)
        val roleText: TextView = itemView.findViewById(R.id.roleText)
        val timestampText: TextView = itemView.findViewById(R.id.timestampText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VolunteerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_volunteer, parent, false)
        return VolunteerViewHolder(view)
    }

    override fun onBindViewHolder(holder: VolunteerViewHolder, position: Int) {
        val volunteer = volunteers[position]
        holder.volunteerNameText.text = volunteer.volunteerName
        holder.volunteerEmailText.text = volunteer.volunteerEmail
        holder.phoneText.text = volunteer.phone
        holder.roleText.text = volunteer.role
        holder.timestampText.text = volunteer.timestamp
    }

    override fun getItemCount(): Int = volunteers.size
}
