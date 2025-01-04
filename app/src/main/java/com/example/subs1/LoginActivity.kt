package com.example.subs1

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        dbHelper = DatabaseHelper(this)
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)

        // Sprawdzenie czy użytkownik jest już zalogowany
        if (isLoggedIn()) {
            navigateToHome()
        }

        val loginEditText = findViewById<EditText>(R.id.loginInput)
        val passwordEditText = findViewById<EditText>(R.id.passwordInput)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val registerText = findViewById<TextView>(R.id.registerText)

        // Obsługa logowania
        loginButton.setOnClickListener {
            val loginOrEmail = loginEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (loginOrEmail.isNotEmpty() && password.isNotEmpty()) {
                val db = dbHelper.readableDatabase
                val cursor = db.rawQuery(
                    "SELECT * FROM users WHERE (email = ? OR login = ?) AND password = ?",
                    arrayOf(loginOrEmail, loginOrEmail, password)
                )

                if (cursor.moveToFirst()) {
                    cursor.close()
                    setLoggedIn()
                    Toast.makeText(this, "Logowanie zakończone sukcesem", Toast.LENGTH_SHORT).show()

                    // Przejście do głównego ekranu
                    navigateToHome()
                } else {
                    cursor.close()
                    Toast.makeText(this, "Niepoprawne dane logowania", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Podaj poprawne dane logowania", Toast.LENGTH_SHORT).show()
            }
        }

        // Obsługa przejścia na ekran rejestracji
        registerText.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("is_logged_in", false)
    }

    private fun setLoggedIn() {
        sharedPreferences.edit().putBoolean("is_logged_in", true).apply()
    }

    private fun navigateToHome() {
        val intent = Intent(this, Subs1Activity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
