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

    private lateinit var monthlyAdapter: SubscriptionAdapter
    private lateinit var yearlyAdapter: SubscriptionAdapter
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        databaseHelper = DatabaseHelper(requireContext())

        monthlyAdapter = SubscriptionAdapter(mutableListOf()) { subscription ->
            handleSubscriptionDelete(subscription, view)
        }

        yearlyAdapter = SubscriptionAdapter(mutableListOf()) { subscription ->
            handleSubscriptionDelete(subscription, view)
        }

        val monthlyRecyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewMonthlySubscriptions)
        val yearlyRecyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewYearlySubscriptions)
        val monthlyEmptyState = view.findViewById<TextView>(R.id.emptyStateMonthlyTextView)
        val yearlyEmptyState = view.findViewById<TextView>(R.id.emptyStateYearlyTextView)

        monthlyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        yearlyRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        monthlyRecyclerView.adapter = monthlyAdapter
        yearlyRecyclerView.adapter = yearlyAdapter

        refreshSubscriptions(monthlyEmptyState, yearlyEmptyState)

        return view
    }

    override fun onResume() {
        super.onResume()
        val monthlyEmptyState = view?.findViewById<TextView>(R.id.emptyStateMonthlyTextView)
        val yearlyEmptyState = view?.findViewById<TextView>(R.id.emptyStateYearlyTextView)
        if (monthlyEmptyState != null && yearlyEmptyState != null) {
            refreshSubscriptions(monthlyEmptyState, yearlyEmptyState)
        }
    }

    private fun refreshSubscriptions(monthlyEmptyState: TextView, yearlyEmptyState: TextView) {
        val subscriptions = databaseHelper.getAllSubscriptions()

        val monthlySubscriptions = subscriptions.filter { it.frequency == Frequency.MONTHLY }
        val yearlySubscriptions = subscriptions.filter { it.frequency == Frequency.YEARLY }

        updateAdapter(monthlyAdapter, monthlySubscriptions, monthlyEmptyState)
        updateAdapter(yearlyAdapter, yearlySubscriptions, yearlyEmptyState)
    }

    private fun updateAdapter(
        adapter: SubscriptionAdapter,
        subscriptions: List<Subscription>,
        emptyStateTextView: TextView
    ) {
        if (subscriptions.isEmpty()) {
            emptyStateTextView.visibility = View.VISIBLE
        } else {
            emptyStateTextView.visibility = View.GONE
        }
        adapter.setSubscriptions(subscriptions)
    }

    private fun handleSubscriptionDelete(subscription: Subscription, view: View) {
        val isDeleted = databaseHelper.deleteSubscription(subscription.id)
        if (isDeleted) {
            Toast.makeText(requireContext(), "Subskrypcja usunięta", Toast.LENGTH_SHORT).show()
            refreshSubscriptions(
                view.findViewById(R.id.emptyStateMonthlyTextView),
                view.findViewById(R.id.emptyStateYearlyTextView)
            )
        } else {
            Toast.makeText(requireContext(), "Nie udało się usunąć subskrypcji", Toast.LENGTH_SHORT).show()
        }
    }
}
