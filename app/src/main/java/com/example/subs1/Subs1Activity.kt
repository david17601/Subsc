package com.example.subs1

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class Subs1Activity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private var loggedInUserId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subs1)

        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)

        loggedInUserId = getLoggedInUserId()

        if (loggedInUserId == -1) {
            logout()
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.nav_add_subscription -> {
                    replaceFragment(AddSubscriptionFragment())
                    true
                }
                R.id.nav_costs -> {
                    replaceFragment(CostsFragment())
                    true
                }
                R.id.nav_upcoming_payments -> {
                    replaceFragment(UpcomingPaymentsFragment())
                    true
                }
                R.id.nav_logout -> {
                    logout()
                    true
                }
                else -> false
            }
        }

        bottomNavigationView.selectedItemId = R.id.nav_home
    }

    private fun replaceFragment(fragment: Fragment) {
        val args = Bundle()
        args.putInt("user_id", loggedInUserId)
        fragment.arguments = args

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, fragment)
            .commit()
    }

    private fun logout() {
        sharedPreferences.edit()
            .putBoolean("is_logged_in", false)
            .putInt("logged_in_user_id", -1)
            .apply()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun getLoggedInUserId(): Int {
        return sharedPreferences.getInt("logged_in_user_id", -1)
    }
}
