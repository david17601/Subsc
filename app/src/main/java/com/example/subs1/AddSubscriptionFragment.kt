package com.example.subs1

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AddSubscriptionFragment : Fragment() {

    private var userId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_subscription, container, false)

        userId = arguments?.getInt("user_id", -1) ?: -1
        if (userId == -1) {
            Toast.makeText(requireContext(), "Błąd: użytkownik nie jest zalogowany!", Toast.LENGTH_SHORT).show()
            return view
        }

        val popularSubscriptionsRecyclerView = view.findViewById<RecyclerView>(R.id.popularSubscriptionsRecyclerView)
        val popularSubscriptions = listOf(
            PopularSubscription("Dodaj własną", R.drawable.icon_add_custom),
            PopularSubscription("Adobe", R.drawable.icon_adobe),
            PopularSubscription("Amazon", R.drawable.icon_amazon),
            PopularSubscription("Amazon Prime", R.drawable.icon_amazon_prime),
            PopularSubscription("Apple Music", R.drawable.icon_music),
            PopularSubscription("Apple TV", R.drawable.icon_appletv),
            PopularSubscription("BookBeat", R.drawable.icon_bookbeat),
            PopularSubscription("CDA Premium", R.drawable.icon_cda),
            PopularSubscription("Canal+", R.drawable.icon_canalplus),
            PopularSubscription("Canva", R.drawable.icon_canva),
            PopularSubscription("ChatGPT", R.drawable.icon_chatgpt),
            PopularSubscription("Discord", R.drawable.icon_discord),
            PopularSubscription("Disney+", R.drawable.icon_disney_plus),
            PopularSubscription("Duolingo", R.drawable.icon_duolingo),
            PopularSubscription("Figma", R.drawable.icon_figma),
            PopularSubscription("Google One", R.drawable.icon_one),
            PopularSubscription("HBO Max", R.drawable.icon_hbo_max),
            PopularSubscription("Ipla", R.drawable.icon_ipla),
            PopularSubscription("Legimi", R.drawable.icon_legimi),
            PopularSubscription("LinkedIn", R.drawable.icon_linkedin),
            PopularSubscription("MS 365", R.drawable.icon_ms365),
            PopularSubscription("Netflix", R.drawable.icon_netflix),
            PopularSubscription("NordVPN", R.drawable.icon_nordvpn),
            PopularSubscription("Player", R.drawable.icon_player),
            PopularSubscription("PlayStation+", R.drawable.icon_playsationplus),
            PopularSubscription("Revolut", R.drawable.icon_revolut),
            PopularSubscription("ShowMax", R.drawable.icon_showmax),
            PopularSubscription("Spotify", R.drawable.icon_spotify),
            PopularSubscription("Tidal", R.drawable.icon_tidal),
            PopularSubscription("Tinder", R.drawable.icon_tinder),
            PopularSubscription("Twitch", R.drawable.icon_twitch),
            PopularSubscription("Ubisoft+", R.drawable.icon_ubisoft),
            PopularSubscription("Uber Pass", R.drawable.icon_uber),
            PopularSubscription("Viaplay", R.drawable.icon_viaplay),
            PopularSubscription("Game Pass", R.drawable.icon_xbox),
            PopularSubscription("YouTube", R.drawable.icon_youtube),
        )

        popularSubscriptionsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        popularSubscriptionsRecyclerView.adapter = PopularSubscriptionAdapter(popularSubscriptions) { subscription ->
            val intent = Intent(requireContext(), AddSubscriptionDetailActivity::class.java).apply {
                putExtra("SUBSCRIPTION_NAME", subscription.name)
                putExtra("SUBSCRIPTION_ICON", subscription.logoResId)
                putExtra("USER_ID", userId)
            }
            startActivity(intent)
        }

        return view
    }
}