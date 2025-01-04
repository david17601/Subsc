package com.example.subs1

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "users.db"
        private const val DATABASE_VERSION = 3

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
        private const val COLUMN_SUBSCRIPTION_ICON = "icon"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createUsersTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_LOGIN TEXT,
                $COLUMN_EMAIL TEXT,
                $COLUMN_PASSWORD TEXT
            )
        """.trimIndent()
        db.execSQL(createUsersTable)

        val createSubscriptionsTable = """
            CREATE TABLE $TABLE_SUBSCRIPTIONS (
                $COLUMN_SUBSCRIPTION_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_SUBSCRIPTION_NAME TEXT,
                $COLUMN_SUBSCRIPTION_PRICE REAL,
                $COLUMN_SUBSCRIPTION_RENEWAL_DATE TEXT,
                $COLUMN_SUBSCRIPTION_FREQUENCY TEXT,
                $COLUMN_SUBSCRIPTION_ICON BLOB
            )
        """.trimIndent()
        db.execSQL(createSubscriptionsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SUBSCRIPTIONS")
        onCreate(db)
    }

    fun addSubscription(name: String, price: Double, renewalDate: String, frequency: Frequency, icon: ByteArray?): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_SUBSCRIPTION_NAME, name)
            put(COLUMN_SUBSCRIPTION_PRICE, price)
            put(COLUMN_SUBSCRIPTION_RENEWAL_DATE, renewalDate)
            put(COLUMN_SUBSCRIPTION_FREQUENCY, frequency.name)
            put(COLUMN_SUBSCRIPTION_ICON, icon)
        }
        return db.insert(TABLE_SUBSCRIPTIONS, null, values) != -1L
    }

    fun deleteSubscription(id: Int): Boolean {
        val db = writableDatabase
        return db.delete(TABLE_SUBSCRIPTIONS, "$COLUMN_SUBSCRIPTION_ID=?", arrayOf(id.toString())) > 0
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
                    frequency = Frequency.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SUBSCRIPTION_FREQUENCY))),
                    icon = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_SUBSCRIPTION_ICON))
                )
            )
        }
        cursor.close()
        return subscriptions
    }
}
