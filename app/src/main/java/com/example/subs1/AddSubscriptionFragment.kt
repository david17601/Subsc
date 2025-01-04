package com.example.subs1

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class AddSubscriptionFragment : Fragment() {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var selectImageLauncher: ActivityResultLauncher<Intent>
    private var selectedImage: Bitmap? = null
    private var selectedFrequency: Frequency = Frequency.MONTHLY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri = result.data?.data
                val imageBitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, imageUri)
                selectedImage = imageBitmap
                view?.findViewById<ImageView>(R.id.selectedImageView)?.apply {
                    setImageBitmap(imageBitmap)
                    visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_subscription, container, false)
        databaseHelper = DatabaseHelper(requireContext())

        val customForm = view.findViewById<ScrollView>(R.id.customSubscriptionForm)
        val addCustomButton = view.findViewById<Button>(R.id.addCustomSubscriptionButton)
        val addButton = view.findViewById<Button>(R.id.addSubscriptionButton)
        val selectImageButton = view.findViewById<Button>(R.id.selectImageButton)
        val nameInput = view.findViewById<EditText>(R.id.nameInput)
        val priceInput = view.findViewById<EditText>(R.id.priceInput)
        val startDateInput = view.findViewById<EditText>(R.id.startDateInput)
        val frequencySpinner = view.findViewById<Spinner>(R.id.frequencySpinner)

        val frequencies = resources.getStringArray(R.array.frequency_options)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, frequencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        frequencySpinner.adapter = adapter

        frequencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedFrequency = if (position == 0) Frequency.MONTHLY else Frequency.YEARLY
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val popularSubscriptionsRecyclerView = view.findViewById<RecyclerView>(R.id.popularSubscriptionsRecyclerView)
        val popularSubscriptions = listOf(
            PopularSubscription("Netflix", R.drawable.icon_netflix),
            PopularSubscription("Spotify", R.drawable.icon_spotify),
            PopularSubscription("HBO Max", R.drawable.icon_hbo_max),
            PopularSubscription("Player", R.drawable.icon_player),
            PopularSubscription("Ipla", R.drawable.icon_ipla),
            PopularSubscription("Amazon Prime", R.drawable.icon_amazon_prime),
            PopularSubscription("Disney+", R.drawable.icon_disney_plus),
            PopularSubscription("Adobe", R.drawable.icon_adobe),
            PopularSubscription("Amazon", R.drawable.icon_amazon),
            PopularSubscription("Tidal", R.drawable.icon_tidal)
        )
        popularSubscriptionsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        popularSubscriptionsRecyclerView.adapter = PopularSubscriptionAdapter(popularSubscriptions) { subscription ->
            nameInput.setText(subscription.name)
            customForm.visibility = View.VISIBLE
        }

        addCustomButton.setOnClickListener {
            customForm.visibility = if (customForm.visibility == View.GONE) View.VISIBLE else View.GONE
        }

        selectImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            selectImageLauncher.launch(intent)
        }

        startDateInput.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePicker = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    val formattedDate = String.format("%02d-%02d-%04d", dayOfMonth, month + 1, year)
                    startDateInput.setText(formattedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        addButton.setOnClickListener {
            val name = nameInput.text.toString()
            val price = priceInput.text.toString().toDoubleOrNull() ?: 0.0
            val startDate = startDateInput.text.toString()

            if (name.isNotEmpty() && startDate.isNotEmpty()) {
                val added = databaseHelper.addSubscription(name, price, startDate, selectedFrequency)
                if (added) {
                    Toast.makeText(context, "Subskrypcja dodana!", Toast.LENGTH_SHORT).show()
                    nameInput.text.clear()
                    priceInput.text.clear()
                    startDateInput.text.clear()
                } else {
                    Toast.makeText(context, "Błąd podczas dodawania subskrypcji!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Proszę wypełnić wszystkie wymagane pola!", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}
