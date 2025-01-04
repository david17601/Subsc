package com.example.subs1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class Subscription(
    val id: Int,
    val name: String,
    val price: Double,
    val renewalDate: String
)

class SubscriptionAdapter(
    private var subscriptions: MutableList<Subscription>,
    private val onSubscriptionLongClick: (Subscription) -> Unit
) : RecyclerView.Adapter<SubscriptionAdapter.SubscriptionViewHolder>() {

    fun setSubscriptions(newSubscriptions: List<Subscription>) {
        subscriptions.clear()
        subscriptions.addAll(newSubscriptions)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubscriptionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_subscription, parent, false)
        return SubscriptionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubscriptionViewHolder, position: Int) {
        val subscription = subscriptions[position]
        holder.bind(subscription)
    }

    override fun getItemCount(): Int = subscriptions.size

    inner class SubscriptionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.subscriptionName)
        private val priceTextView: TextView = itemView.findViewById(R.id.subscriptionPrice)
        private val renewalDateTextView: TextView = itemView.findViewById(R.id.subscriptionRenewalDate)

        fun bind(subscription: Subscription) {
            nameTextView.text = subscription.name
            priceTextView.text = "${subscription.price} PLN"
            renewalDateTextView.text = subscription.renewalDate

            itemView.setOnLongClickListener {
                onSubscriptionLongClick(subscription)
                true
            }
        }
    }
}
