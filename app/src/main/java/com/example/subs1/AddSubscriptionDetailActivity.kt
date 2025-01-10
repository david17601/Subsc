package com.example.subs1

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import java.io.ByteArrayOutputStream
import java.util.*

class AddSubscriptionDetailActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var selectImageLauncher: ActivityResultLauncher<Intent>
    private var selectedImage: Bitmap? = null
    private var selectedFrequency: Frequency = Frequency.MONTHLY
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_subscription_detail)

        databaseHelper = DatabaseHelper(this)

        val nameInput = findViewById<EditText>(R.id.nameInput)
        val priceInput = findViewById<EditText>(R.id.priceInput)
        val startDateInput = findViewById<EditText>(R.id.startDateInput)
        val frequencySpinner = findViewById<Spinner>(R.id.frequencySpinner)
        val saveButton = findViewById<Button>(R.id.addSubscriptionButton)
        val iconImageView = findViewById<ImageView>(R.id.selectedImageView)
        val backToPopularButton = findViewById<TextView>(R.id.backToPopularButton)

        userId = intent.getIntExtra("USER_ID", -1)
        if (userId == -1) {
            Toast.makeText(this, "Błąd: użytkownik nie został poprawnie zidentyfikowany.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        priceInput.doAfterTextChanged {
            if (!it.isNullOrEmpty() && !it.endsWith(" zł")) {
                val text = "$it zł"
                priceInput.setText(text)
                priceInput.setSelection(text.length - 3)
            }
        }

        val subscriptionName = intent.getStringExtra("SUBSCRIPTION_NAME")
        val subscriptionIcon = intent.getIntExtra("SUBSCRIPTION_ICON", -1)

        if (!subscriptionName.isNullOrEmpty() && subscriptionName != "Dodaj własną") {
            nameInput.setText(subscriptionName)
        } else {
            nameInput.setText("")
        }

        if (subscriptionIcon != -1) {
            selectedImage = BitmapFactory.decodeResource(resources, subscriptionIcon)
            iconImageView.setImageBitmap(selectedImage)
        }

        iconImageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            selectImageLauncher.launch(intent)
        }

        selectImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri = result.data?.data
                val imageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                selectedImage = imageBitmap
                iconImageView.setImageBitmap(imageBitmap)
            }
        }

        val frequencies = resources.getStringArray(R.array.frequency_options)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, frequencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        frequencySpinner.adapter = adapter

        frequencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedFrequency = if (position == 0) Frequency.MONTHLY else Frequency.YEARLY
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedFrequency = Frequency.MONTHLY
            }
        }

        startDateInput.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val formattedDate = String.format("%02d-%02d-%04d", dayOfMonth, month + 1, year)
                    startDateInput.setText(formattedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        saveButton.setOnClickListener {
            val name = nameInput.text.toString()
            val price = priceInput.text.toString().replace(" zł", "").toDoubleOrNull() ?: 0.0
            val startDate = startDateInput.text.toString()

            if (name.isNotEmpty() && startDate.isNotEmpty()) {
                val added = databaseHelper.addSubscription(
                    name = name,
                    price = price,
                    renewalDate = startDate,
                    frequency = selectedFrequency,
                    icon = selectedImage?.let { bitmapToByteArray(it) },
                    userId = userId
                )
                if (added) {
                    Toast.makeText(this, "Subskrypcja dodana!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Błąd podczas dodawania subskrypcji!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Proszę wypełnić wszystkie pola!", Toast.LENGTH_SHORT).show()
            }
        }

        backToPopularButton.setOnClickListener {
            finish()
        }
    }

    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }
}
