package com.example.kmgc

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Base64
import java.security.MessageDigest

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "kmgc.db"
        private const val DATABASE_VERSION = 4

        private const val TABLE_USERS = "users"
        private const val TABLE_DONATIONS = "donations"
        private const val TABLE_VOLUNTEERS = "volunteers"

        private const val COLUMN_ID = "id"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PASSWORD = "password"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_IS_ADMIN = "is_admin"

        private const val COLUMN_DONOR_NAME = "donor_name"
        private const val COLUMN_DONOR_EMAIL = "donor_email"
        private const val COLUMN_AMOUNT = "amount"
        private const val COLUMN_REFERENCE = "reference"
        private const val COLUMN_TIMESTAMP = "timestamp"

        private const val COLUMN_VOLUNTEER_NAME = "volunteer_name"
        private const val COLUMN_VOLUNTEER_EMAIL = "volunteer_email"
        private const val COLUMN_PHONE = "phone"
        private const val COLUMN_ROLE = "role"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createUsersTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_EMAIL TEXT UNIQUE,
                $COLUMN_PASSWORD TEXT,
                $COLUMN_NAME TEXT,
                $COLUMN_IS_ADMIN INTEGER DEFAULT 0
            )
        """.trimIndent()
        db.execSQL(createUsersTable)

        val createDonationsTable = """
            CREATE TABLE $TABLE_DONATIONS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_DONOR_NAME TEXT,
                $COLUMN_DONOR_EMAIL TEXT,
                $COLUMN_AMOUNT REAL,
                $COLUMN_REFERENCE TEXT,
                $COLUMN_TIMESTAMP TEXT
            )
        """.trimIndent()
        db.execSQL(createDonationsTable)

        val createVolunteersTable = """
            CREATE TABLE $TABLE_VOLUNTEERS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_VOLUNTEER_NAME TEXT,
                $COLUMN_VOLUNTEER_EMAIL TEXT,
                $COLUMN_PHONE TEXT,
                $COLUMN_ROLE TEXT,
                $COLUMN_TIMESTAMP TEXT
            )
        """.trimIndent()
        db.execSQL(createVolunteersTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE $TABLE_USERS ADD COLUMN $COLUMN_IS_ADMIN INTEGER DEFAULT 0")
        }
        if (oldVersion < 3) {
            db.execSQL("""CREATE TABLE IF NOT EXISTS $TABLE_DONATIONS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_DONOR_NAME TEXT,
                $COLUMN_DONOR_EMAIL TEXT,
                $COLUMN_AMOUNT REAL,
                $COLUMN_REFERENCE TEXT,
                $COLUMN_TIMESTAMP TEXT
            )""".trimIndent())
        }
        if (oldVersion < 4) {
            db.execSQL("""CREATE TABLE IF NOT EXISTS $TABLE_VOLUNTEERS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_VOLUNTEER_NAME TEXT,
                $COLUMN_VOLUNTEER_EMAIL TEXT,
                $COLUMN_PHONE TEXT,
                $COLUMN_ROLE TEXT,
                $COLUMN_TIMESTAMP TEXT
            )""".trimIndent())
        }
    }

    // --- User functions ---
    fun addUser(email: String, password: String, name: String, isAdmin: Boolean = false): Boolean {
        val db = writableDatabase
        val hashedPassword = hashPassword(password)
        val values = ContentValues().apply {
            put(COLUMN_EMAIL, email)
            put(COLUMN_PASSWORD, hashedPassword)
            put(COLUMN_NAME, name)
            put(COLUMN_IS_ADMIN, if (isAdmin) 1 else 0)
        }
        val result = db.insert(TABLE_USERS, null, values)
        db.close()
        return result != -1L
    }

    fun isEmailRegistered(email: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(TABLE_USERS, arrayOf(COLUMN_ID), "$COLUMN_EMAIL = ?", arrayOf(email), null, null, null)
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    fun checkUser(email: String, password: String): Boolean {
        val db = readableDatabase
        val hashedPassword = hashPassword(password)
        val cursor = db.query(TABLE_USERS, arrayOf(COLUMN_ID), "$COLUMN_EMAIL = ? AND $COLUMN_PASSWORD = ?", arrayOf(email, hashedPassword), null, null, null)
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    fun getUserName(email: String): String? {
        val db = readableDatabase
        val cursor = db.query(TABLE_USERS, arrayOf(COLUMN_NAME), "$COLUMN_EMAIL = ?", arrayOf(email), null, null, null)
        val name = if (cursor.moveToFirst()) cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)) else null
        cursor.close()
        db.close()
        return name
    }

    fun isAdmin(email: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(TABLE_USERS, arrayOf(COLUMN_IS_ADMIN), "$COLUMN_EMAIL = ?", arrayOf(email), null, null, null)
        val isAdmin = if (cursor.moveToFirst()) cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_ADMIN)) == 1 else false
        cursor.close()
        db.close()
        return isAdmin
    }

    fun getAllUsers(): List<User> {
        val users = mutableListOf<User>()
        val db = readableDatabase
        val cursor = db.query(TABLE_USERS, arrayOf(COLUMN_ID, COLUMN_NAME, COLUMN_EMAIL, COLUMN_IS_ADMIN), null, null, null, null, "$COLUMN_NAME ASC")
        while (cursor.moveToNext()) {
            val user = User(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                isAdmin = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_ADMIN)) == 1
            )
            users.add(user)
        }
        cursor.close()
        db.close()
        return users
    }

    fun updateUser(id: Int, email: String, name: String, isAdmin: Boolean): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_EMAIL, email)
            put(COLUMN_IS_ADMIN, if (isAdmin) 1 else 0)
        }
        val result = db.update(TABLE_USERS, values, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
        return result > 0
    }

    fun deleteUser(id: Int): Boolean {
        val db = writableDatabase
        val result = db.delete(TABLE_USERS, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
        return result > 0
    }

    fun validateUser(email: String, password: String): User? {
        val db = readableDatabase
        val hashedPassword = hashPassword(password)
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_ID, COLUMN_NAME, COLUMN_EMAIL, COLUMN_IS_ADMIN),
            "$COLUMN_EMAIL = ? AND $COLUMN_PASSWORD = ?",
            arrayOf(email, hashedPassword),
            null,
            null,
            null
        )

        var user: User? = null
        if (cursor.moveToFirst()) {
            user = User(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                isAdmin = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_ADMIN)) == 1
            )
        }

        cursor.close()
        db.close()
        return user
    }

    fun getUserByEmail(email: String): User? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_ID, COLUMN_NAME, COLUMN_EMAIL, COLUMN_IS_ADMIN),
            "$COLUMN_EMAIL = ?",
            arrayOf(email),
            null,
            null,
            null
        )
        var user: User? = null
        if (cursor.moveToFirst()) {
            user = User(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                isAdmin = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_ADMIN)) == 1
            )
        }
        cursor.close()
        db.close()
        return user
    }

    // --- Donation functions ---
    fun insertDonation(donorName: String, donorEmail: String, amount: Double, reference: String, timestamp: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_DONOR_NAME, donorName)
            put(COLUMN_DONOR_EMAIL, donorEmail)
            put(COLUMN_AMOUNT, amount)
            put(COLUMN_REFERENCE, reference)
            put(COLUMN_TIMESTAMP, timestamp)
        }
        val result = db.insert(TABLE_DONATIONS, null, values)
        db.close()
        return result != -1L
    }

    fun getAllDonations(): List<Donation> {
        val donations = mutableListOf<Donation>()
        val db = readableDatabase
        val cursor = db.query(TABLE_DONATIONS, arrayOf(COLUMN_DONOR_NAME, COLUMN_DONOR_EMAIL, COLUMN_AMOUNT, COLUMN_REFERENCE, COLUMN_TIMESTAMP), null, null, null, null, "$COLUMN_TIMESTAMP DESC")
        while (cursor.moveToNext()) {
            donations.add(Donation(
                donorName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DONOR_NAME)),
                donorEmail = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DONOR_EMAIL)),
                amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT)),
                reference = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REFERENCE)),
                timestamp = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP))
            ))
        }
        cursor.close()
        db.close()
        return donations
    }

    // --- Volunteer functions ---
    fun insertVolunteer(volunteerName: String, volunteerEmail: String, phone: String, role: String, timestamp: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_VOLUNTEER_NAME, volunteerName)
            put(COLUMN_VOLUNTEER_EMAIL, volunteerEmail)
            put(COLUMN_PHONE, phone)
            put(COLUMN_ROLE, role)
            put(COLUMN_TIMESTAMP, timestamp)
        }
        val result = db.insert(TABLE_VOLUNTEERS, null, values)
        db.close()
        return result != -1L
    }

    fun getAllVolunteers(): List<Volunteer> {
        val volunteers = mutableListOf<Volunteer>()
        val db = readableDatabase
        val cursor = db.query(TABLE_VOLUNTEERS, arrayOf(COLUMN_VOLUNTEER_NAME, COLUMN_VOLUNTEER_EMAIL, COLUMN_PHONE, COLUMN_ROLE, COLUMN_TIMESTAMP), null, null, null, null, "$COLUMN_TIMESTAMP DESC")
        while (cursor.moveToNext()) {
            volunteers.add(Volunteer(
                volunteerName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VOLUNTEER_NAME)),
                volunteerEmail = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VOLUNTEER_EMAIL)),
                phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)),
                role = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE)),
                timestamp = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP))
            ))
        }
        cursor.close()
        db.close()
        return volunteers
    }

    // --- Password hashing ---
    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(password.toByteArray())
        return Base64.encodeToString(hash, Base64.DEFAULT).trim()
    }

    // --- Data classes ---
    data class User(
        val id: Int,
        val name: String,
        val email: String,
        val isAdmin: Boolean
    ) : java.io.Serializable

    data class Donation(
        val donorName: String,
        val donorEmail: String,
        val amount: Double,
        val reference: String,
        val timestamp: String
    )

    data class Volunteer(
        val volunteerName: String,
        val volunteerEmail: String,
        val phone: String,
        val role: String,
        val timestamp: String
    )
}
