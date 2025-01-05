package com.example.subs1

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AddSubscriptionFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_subscription, container, false)

        val popularSubscriptionsRecyclerView = view.findViewById<RecyclerView>(R.id.popularSubscriptionsRecyclerView)
        val popularSubscriptions = listOf(
            PopularSubscription("Dodaj własną", R.drawable.icon_add_custom),
            PopularSubscription("Netflix", R.drawable.icon_netflix),
            PopularSubscription("Spotify", R.drawable.icon_spotify),
            PopularSubscription("Player", R.drawable.icon_player),
            PopularSubscription("HBO Max", R.drawable.icon_hbo_max),
            PopularSubscription("Amazon Prime", R.drawable.icon_amazon_prime),
            PopularSubscription("Ipla", R.drawable.icon_ipla),
            PopularSubscription("Disney+", R.drawable.icon_disney_plus),
            PopularSubscription("Adobe", R.drawable.icon_adobe),
            PopularSubscription("Amazon", R.drawable.icon_amazon),
            PopularSubscription("YouTube", R.drawable.icon_youtube),
            PopularSubscription("ShowMax", R.drawable.icon_showmax),
            PopularSubscription("Apple Music", R.drawable.icon_music),
            PopularSubscription("Apple TV", R.drawable.icon_appletv),
            PopularSubscription("Canal+", R.drawable.icon_canalplus),
            PopularSubscription("Tidal", R.drawable.icon_tidal),
            PopularSubscription("MS 365", R.drawable.icon_ms365),
            PopularSubscription("Viaplay", R.drawable.icon_viaplay),
            PopularSubscription("Google One", R.drawable.icon_one)
        )

        popularSubscriptionsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        popularSubscriptionsRecyclerView.adapter = PopularSubscriptionAdapter(popularSubscriptions) { subscription ->
            val intent = Intent(requireContext(), AddSubscriptionDetailActivity::class.java).apply {
                putExtra("SUBSCRIPTION_NAME", subscription.name)
                putExtra("SUBSCRIPTION_ICON", subscription.logoResId)
            }
            startActivity(intent)
        }

        return view
    }
}
