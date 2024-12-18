package com.example.subs1

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

data class Subscription(
    val name: String,
    val price: Double,
    val renewalDate: String
)

class Subs1Activity : AppCompatActivity() {
    private val subscriptions = mutableListOf<Subscription>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subs1)

        val nameInput = findViewById<EditText>(R.id.nameInput)
        val priceInput = findViewById<EditText>(R.id.priceInput)
        val dateInput = findViewById<EditText>(R.id.dateInput)
        val addButton = findViewById<Button>(R.id.addButton)
        val listTextView = findViewById<TextView>(R.id.listTextView)

        addButton.setOnClickListener {
            val name = nameInput.text.toString()
            val price = priceInput.text.toString().toDoubleOrNull() ?: 0.0
            val date = dateInput.text.toString()

            val subscription = Subscription(name, price, date)
            subscriptions.add(subscription)

            updateSubscriptionList(listTextView)
        }
    }

    private fun updateSubscriptionList(listTextView: TextView) {
        val subscriptionsText = subscriptions.joinToString("\n") {
            "${it.name} - ${it.price} PLN - ${it.renewalDate}"
        }
        listTextView.text = subscriptionsText.ifEmpty { "Brak subskrypcji." }
    }
}
