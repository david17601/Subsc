package com.example.subs1

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "users.db"
        private const val DATABASE_VERSION = 2

        private const val TABLE_USERS = "users"
        private const val COLUMN_ID = "id"
        private const val COLUMN_LOGIN = "login"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PASSWORD = "password"

        private const val TABLE_SUBSCRIPTIONS = "subscriptions"
        private const val COLUMN_SUBSCRIPTION_ID = "id"
        private const val COLUMN_SUBSCRIPTION_NAME = "name"
        private const val COLUMN_SUBSCRIPTION_PRICE = "price"
        private const val COLUMN_SUBSCRIPTION_RENEWAL_DATE = "renewal_date"
        private const val COLUMN_SUBSCRIPTION_FREQUENCY = "frequency"
    }

    override fun onCreate(db: SQLiteDatabase) {
        Log.d("DatabaseHelper", "Tworzenie tabeli USERS")
        val createUsersTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_LOGIN TEXT,
                $COLUMN_EMAIL TEXT,
                $COLUMN_PASSWORD TEXT
            )
        """.trimIndent()
        db.execSQL(createUsersTable)
        Log.d("DatabaseHelper", "Tabela USERS utworzona")

        Log.d("DatabaseHelper", "Tworzenie tabeli SUBSCRIPTIONS")
        val createSubscriptionsTable = """
            CREATE TABLE $TABLE_SUBSCRIPTIONS (
                $COLUMN_SUBSCRIPTION_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_SUBSCRIPTION_NAME TEXT,
                $COLUMN_SUBSCRIPTION_PRICE REAL,
                $COLUMN_SUBSCRIPTION_RENEWAL_DATE TEXT,
                $COLUMN_SUBSCRIPTION_FREQUENCY TEXT
            )
        """.trimIndent()
        db.execSQL(createSubscriptionsTable)
        Log.d("DatabaseHelper", "Tabela SUBSCRIPTIONS utworzona")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.d("DatabaseHelper", "Aktualizacja bazy danych z wersji $oldVersion do $newVersion")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SUBSCRIPTIONS")
        onCreate(db)
        Log.d("DatabaseHelper", "Baza danych zaktualizowana")
    }

    fun addUser(login: String, email: String, password: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_LOGIN, login)
            put(COLUMN_EMAIL, email)
            put(COLUMN_PASSWORD, password)
        }
        val result = db.insert(TABLE_USERS, null, values)
        Log.d("DatabaseHelper", "Dodano użytkownika: $values, wynik: $result")
        return result
    }

    fun addSubscription(name: String, price: Double, renewalDate: String, frequency: Frequency): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_SUBSCRIPTION_NAME, name)
            put(COLUMN_SUBSCRIPTION_PRICE, price)
            put(COLUMN_SUBSCRIPTION_RENEWAL_DATE, renewalDate)
            put(COLUMN_SUBSCRIPTION_FREQUENCY, frequency.name)
        }
        val result = db.insert(TABLE_SUBSCRIPTIONS, null, values)
        Log.d("DatabaseHelper", "Dodano subskrypcję: $values, wynik: $result")
        return result != -1L
    }

    fun deleteSubscription(id: Int): Boolean {
        val db = writableDatabase
        val rowsDeleted = db.delete(TABLE_SUBSCRIPTIONS, "$COLUMN_SUBSCRIPTION_ID=?", arrayOf(id.toString()))
        Log.d("DatabaseHelper", "Usunięto subskrypcję o ID $id, liczba usuniętych wierszy: $rowsDeleted")
        return rowsDeleted > 0
    }

    fun getAllSubscriptions(): List<Subscription> {
        val db = readableDatabase
        val cursor = db.query(TABLE_SUBSCRIPTIONS, null, null, null, null, null, null)
        val subscriptions = mutableListOf<Subscription>()
        while (cursor.moveToNext()) {
            subscriptions.add(
                Subscription(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SUBSCRIPTION_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SUBSCRIPTION_NAME)),
                    price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_SUBSCRIPTION_PRICE)),
                    renewalDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SUBSCRIPTION_RENEWAL_DATE)),
                    frequency = Frequency.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SUBSCRIPTION_FREQUENCY)))
                )
            )
        }
        cursor.close()
        Log.d("DatabaseHelper", "Pobrano subskrypcje: $subscriptions")
        return subscriptions
    }

    fun getAllUsers(): List<Map<String, String>> {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_USERS", null)
        val users = mutableListOf<Map<String, String>>()
        while (cursor.moveToNext()) {
            val user = mapOf(
                COLUMN_ID to cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)).toString(),
                COLUMN_LOGIN to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOGIN)),
                COLUMN_EMAIL to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                COLUMN_PASSWORD to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD))
            )
            users.add(user)
        }
        cursor.close()
        Log.d("DatabaseHelper", "Pobrano użytkowników: $users")
        return users
    }
}
