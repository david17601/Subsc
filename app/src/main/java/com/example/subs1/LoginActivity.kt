package com.example.subs1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginInput = findViewById<EditText>(R.id.loginInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val forgotPasswordButton = findViewById<Button>(R.id.forgotPasswordButton)
        val registerText = findViewById<TextView>(R.id.registerText)

        // Obsługa logowania
        loginButton.setOnClickListener {
            val login = loginInput.text.toString()
            val password = passwordInput.text.toString()

            if (login == "admin" && password == "admin") {
                Toast.makeText(this, "Zalogowano pomyślnie", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, Subs1Activity::class.java)
                startActivity(intent)
                finish() // Zamykamy ekran logowania
            } else {
                Toast.makeText(this, "Nieprawidłowy login lub hasło", Toast.LENGTH_SHORT).show()
            }
        }

        // Obsługa zapomnianego hasła
        forgotPasswordButton.setOnClickListener {
            Toast.makeText(this, "Opcja zapomnianego hasła wkrótce dostępna", Toast.LENGTH_SHORT).show()
        }

        // Obsługa rejestracji
        registerText.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}
