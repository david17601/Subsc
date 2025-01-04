package com.example.subs1

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

data class Subscription(
    val id: Int,
    val name: String,
    val price: Double,
    val renewalDate: String,
    val frequency: Frequency,
    val icon: ByteArray? = null
)

enum class Frequency {
    MONTHLY,
    YEARLY
}

class SubscriptionAdapter(
    private var subscriptions: MutableList<Subscription>,
    private val onDeleteClick: (Subscription) -> Unit
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
        private val iconImageView: ImageView = itemView.findViewById(R.id.subscriptionIcon)
        private val deleteIcon: ImageView = itemView.findViewById(R.id.subscriptionDeleteIcon)

        fun bind(subscription: Subscription) {
            nameTextView.text = subscription.name
            nameTextView.textSize = 18f // Powiększenie tekstu nazwy subskrypcji
            priceTextView.text = formatPrice(subscription.price)
            renewalDateTextView.text = "Następna płatność: ${calculateNextPaymentDate(subscription.renewalDate, subscription.frequency)}"

            subscription.icon?.let {
                val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                iconImageView.setImageBitmap(bitmap)
            } ?: run {
                iconImageView.setImageResource(R.drawable.default_icon)
            }

            deleteIcon.setOnClickListener {
                onDeleteClick(subscription)
            }
        }

        private fun calculateNextPaymentDate(startDate: String, frequency: Frequency): String {
            val dateFormat = java.text.SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            return try {
                val date = dateFormat.parse(startDate) ?: return "Nieznana"
                val calendar = Calendar.getInstance()
                calendar.time = date
                when (frequency) {
                    Frequency.MONTHLY -> calendar.add(Calendar.MONTH, 1)
                    Frequency.YEARLY -> calendar.add(Calendar.YEAR, 1)
                }
                dateFormat.format(calendar.time)
            } catch (e: Exception) {
                "Nieznana"
            }
        }

        private fun formatPrice(price: Double): String {
            val symbols = DecimalFormatSymbols(Locale.getDefault()).apply {
                decimalSeparator = ','
                groupingSeparator = ' '
            }
            val formatter = DecimalFormat("Cena: #,##0.00 zł", symbols)
            return formatter.format(price)
        }
    }
}
