package com.example.subs1

import android.app.Activity
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

class AddSubscriptionFragment : Fragment() {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var selectImageLauncher: ActivityResultLauncher<Intent>
    private var selectedImage: Bitmap? = null

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
            openEditSubscriptionDialog(subscription)
        }
        addCustomButton.setOnClickListener {
            customForm.visibility = if (customForm.visibility == View.GONE) View.VISIBLE else View.GONE
        }
        selectImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            selectImageLauncher.launch(intent)
        }
        addButton.setOnClickListener {
            val name = nameInput.text.toString()
            val price = priceInput.text.toString().toDoubleOrNull() ?: 0.0
            val startDate = startDateInput.text.toString()
            if (name.isNotEmpty() && startDate.isNotEmpty()) {
                databaseHelper.addSubscription(name, price, startDate)
                Toast.makeText(context, "Subskrypcja dodana!", Toast.LENGTH_SHORT).show()
                nameInput.text.clear()
                priceInput.text.clear()
                startDateInput.text.clear()
            } else {
                Toast.makeText(context, "Proszę wypełnić wszystkie wymagane pola!", Toast.LENGTH_SHORT).show()
            }
        }
        return view
    }

    private fun openEditSubscriptionDialog(subscription: PopularSubscription) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_subscription, null)
        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setTitle("Edytuj subskrypcję")
            .setView(dialogView)
            .setPositiveButton("Zapisz") { _, _ ->
                val name = dialogView.findViewById<EditText>(R.id.dialogNameInput).text.toString()
                val price = dialogView.findViewById<EditText>(R.id.dialogPriceInput).text.toString().toDoubleOrNull() ?: 0.0
                val renewalDate = dialogView.findViewById<EditText>(R.id.dialogRenewalDateInput).text.toString()
                if (name.isNotEmpty() && renewalDate.isNotEmpty()) {
                    databaseHelper.addSubscription(name, price, renewalDate)
                    Toast.makeText(requireContext(), "$name dodano do subskrypcji!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Proszę wypełnić wszystkie pola!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Anuluj", null)
            .create()
        dialog.show()
    }
}
