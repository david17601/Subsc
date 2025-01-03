package com.example.subs1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class HomeFragment : Fragment() {

    private lateinit var subscriptionAdapter: SubscriptionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewSubscriptions)
        subscriptionAdapter = SubscriptionAdapter(mutableListOf())
        recyclerView.adapter = subscriptionAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Example subscriptions
        val subscriptions = listOf(
            Subscription("Netflix", 45.0, "2025-01-15"),
            Subscription("Spotify", 20.0, "2025-01-25"),
        )
        subscriptionAdapter.setSubscriptions(subscriptions)

        return view
    }
}
