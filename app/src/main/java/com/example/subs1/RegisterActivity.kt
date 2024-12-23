package com.example.subs1

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class RegisterActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Ustawienie niestandardowego paska narzędzi
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Wyświetl strzałkę powrotu
        supportActionBar?.title = "" // Usuń tytuł

        dbHelper = DatabaseHelper(this)

        val loginInput = findViewById<EditText>(R.id.registerLoginInput)
        val emailInput = findViewById<EditText>(R.id.registerEmailInput)
        val passwordInput = findViewById<EditText>(R.id.registerPasswordInput)
        val registerButton = findViewById<Button>(R.id.registerButton)
        val backToLogin = findViewById<TextView>(R.id.backToLogin)

        registerButton.setOnClickListener {
            hideKeyboard()

            val login = loginInput.text.toString()
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()

            if (!email.contains("@")) {
                Toast.makeText(this, "Wpisz prawidłowy adres e-mail", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (login.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                if (dbHelper.isUserExists(login, email)) {
                    Toast.makeText(this, "Login lub e-mail są już zajęte", Toast.LENGTH_SHORT).show()
                } else {
                    val success = dbHelper.addUser(login, email, password)
                    if (success) {
                        Toast.makeText(this, "Konto utworzone pomyślnie!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Błąd podczas rejestracji. Spróbuj ponownie", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Porszę wypełnić wszystkie pola", Toast.LENGTH_SHORT).show()
            }
        }

        backToLogin.setOnClickListener {
            navigateToLogin()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                navigateToLogin()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        currentFocus?.let {
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }
}
