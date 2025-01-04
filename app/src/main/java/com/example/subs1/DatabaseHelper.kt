package com.example.subs1

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "users.db"
        private const val DATABASE_VERSION = 1

        // Tabela użytkowników
        private const val TABLE_USERS = "users"
        private const val COLUMN_ID = "id"
        private const val COLUMN_LOGIN = "login"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PASSWORD = "password"

        // Tabela subskrypcji
        private const val TABLE_SUBSCRIPTIONS = "subscriptions"
        private const val COLUMN_SUBSCRIPTION_ID = "id"
        private const val COLUMN_SUBSCRIPTION_NAME = "name"
        private const val COLUMN_SUBSCRIPTION_PRICE = "price"
        private const val COLUMN_SUBSCRIPTION_RENEWAL_DATE = "renewal_date"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createUsersTableQuery = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_LOGIN TEXT NOT NULL UNIQUE,
                $COLUMN_EMAIL TEXT NOT NULL UNIQUE,
                $COLUMN_PASSWORD TEXT NOT NULL
            )
        """
        db.execSQL(createUsersTableQuery)

        val createSubscriptionsTableQuery = """
            CREATE TABLE $TABLE_SUBSCRIPTIONS (
                $COLUMN_SUBSCRIPTION_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_SUBSCRIPTION_NAME TEXT NOT NULL,
                $COLUMN_SUBSCRIPTION_PRICE REAL NOT NULL,
                $COLUMN_SUBSCRIPTION_RENEWAL_DATE TEXT NOT NULL
            )
        """
        db.execSQL(createSubscriptionsTableQuery)

        Log.d("DatabaseHelper", "Tables created: $TABLE_USERS, $TABLE_SUBSCRIPTIONS")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SUBSCRIPTIONS")
        onCreate(db)
        Log.d("DatabaseHelper", "Database upgraded from version $oldVersion to $newVersion")
    }

    fun addUser(login: String, email: String, password: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_LOGIN, login)
            put(COLUMN_EMAIL, email)
            put(COLUMN_PASSWORD, password)
        }
        val result = db.insert(TABLE_USERS, null, values)
        db.close()
        return result != -1L
    }

    fun isUserExists(login: String, email: String): Boolean {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_USERS WHERE $COLUMN_LOGIN = ? OR $COLUMN_EMAIL = ?"
        val cursor = db.rawQuery(query, arrayOf(login, email))
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    fun checkUser(login: String, password: String): Boolean {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_USERS WHERE $COLUMN_LOGIN = ? AND $COLUMN_PASSWORD = ?"
        val cursor = db.rawQuery(query, arrayOf(login, password))
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    fun addSubscription(name: String, price: Double, renewalDate: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_SUBSCRIPTION_NAME, name)
            put(COLUMN_SUBSCRIPTION_PRICE, price)
            put(COLUMN_SUBSCRIPTION_RENEWAL_DATE, renewalDate)
        }
        val result = db.insert(TABLE_SUBSCRIPTIONS, null, values)
        db.close()
        return result != -1L
    }

    fun getAllSubscriptions(): List<Subscription> {
        val subscriptions = mutableListOf<Subscription>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_SUBSCRIPTIONS", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SUBSCRIPTION_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SUBSCRIPTION_NAME))
                val price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_SUBSCRIPTION_PRICE))
                val renewalDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SUBSCRIPTION_RENEWAL_DATE))
                subscriptions.add(Subscription(id, name, price, renewalDate))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return subscriptions
    }

    fun deleteSubscription(subscriptionId: Int): Boolean {
        val db = writableDatabase
        val result = db.delete(TABLE_SUBSCRIPTIONS, "$COLUMN_SUBSCRIPTION_ID = ?", arrayOf(subscriptionId.toString()))
        db.close()
        return result > 0
    }

}
