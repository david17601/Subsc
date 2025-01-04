package com.example.subs1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class PopularSubscription(
    val name: String,
    val logoResId: Int
)

class PopularSubscriptionAdapter(
    private val subscriptions: List<PopularSubscription>,
    private val onSubscriptionClick: (PopularSubscription) -> Unit
) : RecyclerView.Adapter<PopularSubscriptionAdapter.PopularSubscriptionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularSubscriptionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_popular_subscription, parent, false)
        return PopularSubscriptionViewHolder(view)
    }

    override fun onBindViewHolder(holder: PopularSubscriptionViewHolder, position: Int) {
        val subscription = subscriptions[position]
        holder.bind(subscription)
    }

    override fun getItemCount(): Int = subscriptions.size

    inner class PopularSubscriptionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val logoImageView: ImageView = itemView.findViewById(R.id.popularSubscriptionIcon)
        private val nameTextView: TextView = itemView.findViewById(R.id.popularSubscriptionName)

        fun bind(subscription: PopularSubscription) {
            // Ustawienie ikony
            logoImageView.setImageResource(subscription.logoResId)

            // Ustawienie nazwy
            nameTextView.text = subscription.name

            // Obsługa kliknięcia
            itemView.setOnClickListener {
                onSubscriptionClick(subscription)
            }
        }
    }
}
