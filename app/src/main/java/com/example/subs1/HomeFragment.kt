package com.example.subs1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
            val isDeleted = databaseHelper.deleteSubscription(subscription.id)
            if (isDeleted) {
                Toast.makeText(requireContext(), "Subskrypcja usunięta", Toast.LENGTH_SHORT).show()
                refreshSubscriptions(view.findViewById(R.id.emptyStateTextView))
            } else {
                Toast.makeText(
                    requireContext(),
                    "Nie udało się usunąć subskrypcji",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewSubscriptions)
        val emptyStateTextView = view.findViewById<TextView>(R.id.emptyStateTextView)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = subscriptionAdapter

        refreshSubscriptions(emptyStateTextView)

        return view
    }

    override fun onResume() {
        super.onResume()
        val emptyStateTextView = view?.findViewById<TextView>(R.id.emptyStateTextView)
        if (emptyStateTextView != null) {
            refreshSubscriptions(emptyStateTextView)
        }
    }

    private fun refreshSubscriptions(emptyStateTextView: TextView) {
        val subscriptions = databaseHelper.getAllSubscriptions()

        if (subscriptions.isEmpty()) {
            emptyStateTextView.visibility = View.VISIBLE
        } else {
            emptyStateTextView.visibility = View.GONE
        }

        subscriptionAdapter.setSubscriptions(subscriptions)
    }
}
