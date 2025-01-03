package com.example.subs1

import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView

class Subs1Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)

        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Domyślne ładowanie HomeFragment
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
            navigationView.setCheckedItem(R.id.nav_home)
        }

        // Obsługa nawigacji w NavigationView
        navigationView.setNavigationItemSelectedListener { menuItem ->
            val menu = navigationView.menu
            for (i in 0 until menu.size()) {
                menu.getItem(i).isChecked = false
            }
            menuItem.isChecked = true

            val fragment: Fragment = when (menuItem.itemId) {
                R.id.nav_home -> HomeFragment()
                R.id.nav_add_subscription -> AddSubscriptionFragment()
                R.id.nav_costs -> CostsFragment()
                R.id.nav_upcoming_payments -> UpcomingPaymentsFragment()
                R.id.nav_account_options -> AccountOptionsFragment()
                else -> HomeFragment()
            }

            loadFragment(fragment)
            drawerLayout.closeDrawers()
            true
        }
    }

    // Funkcja ładowania fragmentu
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}