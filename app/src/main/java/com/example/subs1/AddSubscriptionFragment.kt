package com.example.subs1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment

class AddSubscriptionFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_subscription, container, false)

        val nameInput = view.findViewById<EditText>(R.id.nameInput)
        val priceInput = view.findViewById<EditText>(R.id.priceInput)
        val startDateInput = view.findViewById<EditText>(R.id.startDateInput)
        val frequencyInput = view.findViewById<EditText>(R.id.frequencyInput)
        val addButton = view.findViewById<Button>(R.id.addSubscriptionButton)

        addButton.setOnClickListener {
            val name = nameInput.text.toString()
            val price = priceInput.text.toString().toDoubleOrNull() ?: 0.0
            val startDate = startDateInput.text.toString()
            val frequency = frequencyInput.text.toString()

            if (name.isNotEmpty() && startDate.isNotEmpty() && frequency.isNotEmpty()) {
                // Save subscription
                Toast.makeText(context, "Subskrypcja dodana!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Proszę wypełnić wszystkie pola!", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}
