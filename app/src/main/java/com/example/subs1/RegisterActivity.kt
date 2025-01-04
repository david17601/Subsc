package com.example.subs1

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        dbHelper = DatabaseHelper(this)

        val nameEditText = findViewById<EditText>(R.id.registerLoginInput)
        val emailEditText = findViewById<EditText>(R.id.registerEmailInput)
        val passwordEditText = findViewById<EditText>(R.id.registerPasswordInput)
        val registerButton = findViewById<Button>(R.id.registerButton)
        val backToLoginText = findViewById<TextView>(R.id.backToLogin)

        registerButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                val db = dbHelper.readableDatabase
                val cursor = db.rawQuery("SELECT * FROM users WHERE email = ? OR login = ?", arrayOf(email, name))
                if (cursor.count > 0) {
                    cursor.close()
                    Toast.makeText(this, "Użytkownik z takim e-mailem lub loginem już istnieje", Toast.LENGTH_SHORT).show()
                } else {
                    cursor.close()
                    val writableDb = dbHelper.writableDatabase
                    val values = ContentValues().apply {
                        put("login", name)
                        put("email", email)
                        put("password", password)
                    }
                    val result = writableDb.insert("users", null, values)
                    if (result != -1L) {
                        Toast.makeText(this, "Rejestracja zakończona sukcesem", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Błąd podczas rejestracji", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Wypełnij wszystkie pola", Toast.LENGTH_SHORT).show()
            }
        }

        backToLoginText.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
