package com.example.subs1

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import java.util.*

class CostsFragment : Fragment(R.layout.fragment_costs) {

    private lateinit var costSummary: TextView
    private lateinit var startDateInput: EditText
    private lateinit var endDateInput: EditText
    private lateinit var databaseHelper: DatabaseHelper
    private val dateFormatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        costSummary = view.findViewById(R.id.costSummary)
        startDateInput = view.findViewById(R.id.startDateInput)
        endDateInput = view.findViewById(R.id.endDateInput)
        databaseHelper = DatabaseHelper(requireContext())

        val buttonPrevWeek = view.findViewById<Button>(R.id.buttonPrevWeek)
        val buttonThisWeek = view.findViewById<Button>(R.id.buttonThisWeek)
        val buttonNextWeek = view.findViewById<Button>(R.id.buttonNextWeek)
        val buttonPrevMonth = view.findViewById<Button>(R.id.buttonPrevMonth)
        val buttonCurrentMonth = view.findViewById<Button>(R.id.buttonCurrentMonth)
        val buttonNextMonth = view.findViewById<Button>(R.id.buttonNextMonth)

        startDateInput.setOnClickListener { showDatePicker(startDateInput) }
        endDateInput.setOnClickListener { showDatePicker(endDateInput) }

        buttonPrevWeek.setOnClickListener { setDatesAndCalculate(-7, -1, Calendar.DAY_OF_YEAR) }
        buttonThisWeek.setOnClickListener { setDatesAndCalculate(0, 6, Calendar.DAY_OF_WEEK) }
        buttonNextWeek.setOnClickListener { setDatesAndCalculate(7, 13, Calendar.DAY_OF_YEAR) }
        buttonPrevMonth.setOnClickListener { setDatesAndCalculate(-1, -1, Calendar.MONTH) }
        buttonCurrentMonth.setOnClickListener { setDatesAndCalculate(0, 0, Calendar.MONTH) }
        buttonNextMonth.setOnClickListener { setDatesAndCalculate(1, 1, Calendar.MONTH) }
    }

    private fun showDatePicker(editText: EditText) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val formattedDate = String.format("%02d-%02d-%04d", dayOfMonth, month + 1, year)
                editText.setText(formattedDate)
                recalculateCosts()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun setDatesAndCalculate(offsetStart: Int, offsetEnd: Int, field: Int) {
        val calendar = Calendar.getInstance()

        when (field) {
            Calendar.MONTH -> {
                calendar.add(Calendar.MONTH, offsetStart)
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                val startDate = calendar.time

                calendar.add(Calendar.MONTH, offsetEnd)
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
                val endDate = calendar.time

                updateDateFields(startDate, endDate)
            }
            Calendar.DAY_OF_WEEK -> {
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                calendar.add(Calendar.DAY_OF_YEAR, offsetStart)
                val startDate = calendar.time

                calendar.add(Calendar.DAY_OF_YEAR, offsetEnd)
                val endDate = calendar.time

                updateDateFields(startDate, endDate)
            }
            Calendar.DAY_OF_YEAR -> {
                calendar.add(Calendar.DAY_OF_YEAR, offsetStart)
                val startDate = calendar.time

                calendar.add(Calendar.DAY_OF_YEAR, offsetEnd - offsetStart)
                val endDate = calendar.time

                updateDateFields(startDate, endDate)
            }
        }
        recalculateCosts()
    }

    private fun updateDateFields(startDate: Date, endDate: Date) {
        startDateInput.setText(dateFormatter.format(startDate))
        endDateInput.setText(dateFormatter.format(endDate))
    }

    private fun recalculateCosts() {
        val startDate = dateFormatter.parse(startDateInput.text.toString())
        val endDate = dateFormatter.parse(endDateInput.text.toString())

        if (startDate != null && endDate != null) {
            calculateCosts(startDate, endDate)
        }
    }

    private fun calculateCosts(startDate: Date, endDate: Date) {
        val subscriptions = databaseHelper.getAllSubscriptions()
        val totalCost = subscriptions.sumOf { subscription ->
            calculateSubscriptionCosts(subscription, startDate, endDate)
        }

        costSummary.text = "Łączny koszt: %.2f PLN".format(totalCost)
    }

    private fun calculateSubscriptionCosts(subscription: Subscription, startDate: Date, endDate: Date): Double {
        val renewalCalendar = Calendar.getInstance()
        val formatter = dateFormatter

        renewalCalendar.time = formatter.parse(subscription.renewalDate) ?: return 0.0

        var totalCost = 0.0

        while (renewalCalendar.time.before(startDate)) {
            when (subscription.frequency) {
                Frequency.MONTHLY -> renewalCalendar.add(Calendar.MONTH, 1)
                Frequency.YEARLY -> renewalCalendar.add(Calendar.YEAR, 1)
            }
        }

        while (!renewalCalendar.time.after(endDate)) {
            totalCost += subscription.price

            when (subscription.frequency) {
                Frequency.MONTHLY -> renewalCalendar.add(Calendar.MONTH, 1)
                Frequency.YEARLY -> renewalCalendar.add(Calendar.YEAR, 1)
            }
        }

        return totalCost
    }
}
