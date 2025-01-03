package com.example.subs1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class CostsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_costs, container, false)

        val costSummary = view.findViewById<TextView>(R.id.costSummary)
        val exampleCosts = "Łączny koszt: 300 PLN"

        costSummary.text = exampleCosts

        return view
    }
}
