package com.example.subs1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class UpcomingPaymentsFragment : Fragment() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SubscriptionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_upcoming_payments, container, false)

        dbHelper = DatabaseHelper(requireContext())
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = SubscriptionAdapter(mutableListOf()) { subscription ->
            val isDeleted = dbHelper.deleteSubscription(subscription.id)
            if (isDeleted) {
                Toast.makeText(
                    requireContext(),
                    "Usunięto: ${subscription.name}",
                    Toast.LENGTH_SHORT
                ).show()
                loadUpcomingPayments()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Błąd podczas usuwania subskrypcji",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        recyclerView.adapter = adapter

        loadUpcomingPayments()

        return view
    }

    private fun loadUpcomingPayments() {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM subscriptions", null)

        val upcomingSubscriptions = mutableListOf<Subscription>()

        val today = Calendar.getInstance()
        today.set(Calendar.HOUR_OF_DAY, 0)
        today.set(Calendar.MINUTE, 0)
        today.set(Calendar.SECOND, 0)
        today.set(Calendar.MILLISECOND, 0)
        val currentDate = today.time

        val nextWeek = Calendar.getInstance()
        nextWeek.time = currentDate
        nextWeek.add(Calendar.DAY_OF_YEAR, 7)
        val nextWeekDate = nextWeek.time

        val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                val price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"))
                val dateString = cursor.getString(cursor.getColumnIndexOrThrow("renewal_date"))
                val frequency = cursor.getString(cursor.getColumnIndexOrThrow("frequency"))
                val iconBlob = cursor.getBlob(cursor.getColumnIndexOrThrow("icon"))

                try {
                    val renewalDate = formatter.parse(dateString)
                    if (renewalDate != null) {
                        val normalizedRenewalDate = Calendar.getInstance()
                        normalizedRenewalDate.time = renewalDate
                        normalizedRenewalDate.set(Calendar.HOUR_OF_DAY, 0)
                        normalizedRenewalDate.set(Calendar.MINUTE, 0)
                        normalizedRenewalDate.set(Calendar.SECOND, 0)
                        normalizedRenewalDate.set(Calendar.MILLISECOND, 0)

                        if (normalizedRenewalDate.time >= currentDate && normalizedRenewalDate.time <= nextWeekDate) {
                            upcomingSubscriptions.add(
                                Subscription(
                                    id,
                                    name,
                                    price,
                                    dateString,
                                    Frequency.valueOf(frequency.uppercase(Locale.getDefault())),
                                    iconBlob
                                )
                            )
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } while (cursor.moveToNext())
        }
        cursor.close()

        adapter.setSubscriptions(upcomingSubscriptions)

        if (upcomingSubscriptions.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "Brak nadchodzących płatności w ciągu najbliższych 7 dni",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}