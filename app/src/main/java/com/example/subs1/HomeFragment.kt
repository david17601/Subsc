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
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var monthlyAdapter: SubscriptionAdapter
    private lateinit var yearlyAdapter: SubscriptionAdapter
    private lateinit var databaseHelper: DatabaseHelper
    private val dateFormatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

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

        val monthlySubscriptions = subscriptions.map { it.copy(renewalDate = updateNextRenewalDate(it)) }
            .filter { it.frequency == Frequency.MONTHLY }

        val yearlySubscriptions = subscriptions.map { it.copy(renewalDate = updateNextRenewalDate(it)) }
            .filter { it.frequency == Frequency.YEARLY }

        updateAdapter(monthlyAdapter, monthlySubscriptions, monthlyEmptyState)
        updateAdapter(yearlyAdapter, yearlySubscriptions, yearlyEmptyState)
    }

    private fun updateNextRenewalDate(subscription: Subscription): String {
        val currentDate = Calendar.getInstance()
        val renewalDate = Calendar.getInstance()

        renewalDate.time = dateFormatter.parse(subscription.renewalDate) ?: return "Błąd daty"

        if (currentDate.get(Calendar.YEAR) == renewalDate.get(Calendar.YEAR) &&
            currentDate.get(Calendar.MONTH) == renewalDate.get(Calendar.MONTH) &&
            currentDate.get(Calendar.DAY_OF_MONTH) == renewalDate.get(Calendar.DAY_OF_MONTH)
        ) {
            return dateFormatter.format(renewalDate.time)
        }

        while (renewalDate.before(currentDate) || renewalDate.time == currentDate.time) {
            when (subscription.frequency) {
                Frequency.MONTHLY -> renewalDate.add(Calendar.MONTH, 1)
                Frequency.YEARLY -> renewalDate.add(Calendar.YEAR, 1)
            }
        }

        return dateFormatter.format(renewalDate.time)
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
