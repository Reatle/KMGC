package com.example.kmgc

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DonationsAdapter(
    private val donations: List<DatabaseHelper.Donation>
) : RecyclerView.Adapter<DonationsAdapter.DonationViewHolder>() {

    class DonationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val donorNameText: TextView = itemView.findViewById(R.id.donorNameText)
        val donorEmailText: TextView = itemView.findViewById(R.id.donorEmailText)
        val amountText: TextView = itemView.findViewById(R.id.amountText)
        val referenceText: TextView = itemView.findViewById(R.id.referenceText)
        val timestampText: TextView = itemView.findViewById(R.id.timestampText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_donation, parent, false)
        return DonationViewHolder(view)
    }

    override fun onBindViewHolder(holder: DonationViewHolder, position: Int) {
        val donation = donations[position]
        holder.donorNameText.text = donation.donorName
        holder.donorEmailText.text = donation.donorEmail
        holder.amountText.text = "ZAR ${donation.amount}"
        holder.referenceText.text = donation.reference
        holder.timestampText.text = donation.timestamp
    }

    override fun getItemCount(): Int = donations.size
}
