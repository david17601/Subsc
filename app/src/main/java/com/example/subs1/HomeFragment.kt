package com.example.subs1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeFragment : Fragment() {

    private lateinit var subscriptionAdapter: SubscriptionAdapter
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)


        databaseHelper = DatabaseHelper(requireContext())

        subscriptionAdapter = SubscriptionAdapter(mutableListOf()) { subscription ->
            Toast.makeText(
                requireContext(),
                "Długie naciśnięcie na subskrypcję: ${subscription.name}",
                Toast.LENGTH_SHORT
            ).show()

            val isDeleted = databaseHelper.deleteSubscription(subscription.id)
            if (isDeleted) {
                Toast.makeText(requireContext(), "Subskrypcja usunięta", Toast.LENGTH_SHORT).show()
                refreshSubscriptions()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Nie udało się usunąć subskrypcji",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewSubscriptions)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = subscriptionAdapter


        refreshSubscriptions()

        return view
    }

    override fun onResume() {
        super.onResume()
        refreshSubscriptions()
    }

    private fun refreshSubscriptions() {
        val subscriptions = databaseHelper.getAllSubscriptions()
        subscriptionAdapter.setSubscriptions(subscriptions)
    }
}
