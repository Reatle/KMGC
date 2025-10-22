package com.example.kmgc

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.robolectric.RobolectricTestRunner
import org.junit.runner.RunWith

@RunWith(RobolectricTestRunner::class)
class DonationTest {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        // Pass null as database name for in-memory DB
        dbHelper = DatabaseHelper(context)
        // Clean start: drop & recreate donations table
        val db = dbHelper.writableDatabase
        db.execSQL("DROP TABLE IF EXISTS donations")
        db.execSQL("""
            CREATE TABLE donations (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                donor_name TEXT,
                donor_email TEXT,
                amount REAL,
                reference TEXT,
                timestamp TEXT
            )
        """.trimIndent())
        db.close()
    }

    @Test
    fun testInsertDonation() {
        val result = dbHelper.insertDonation(
            donorName = "Reatlegile",
            donorEmail = "Reatlegileconfidence254@gmail.com",
            amount = 50.0,
            reference = "REF123",
            timestamp = "2025-10-21 11:00"
        )
        assertTrue(result)
    }

    @Test
    fun testGetAllDonations() {
        dbHelper.insertDonation("Alice", "alice@example.com", 50.0, "REF123", "2025-10-21 11:00")
        dbHelper.insertDonation("Bob", "bob@example.com", 75.0, "REF456", "2025-10-21 12:00")

        val donations = dbHelper.getAllDonations()
        assertEquals(2, donations.size)
        assertEquals("Bob", donations[0].donorName)  // Descending timestamp
        assertEquals("Alice", donations[1].donorName)
    }
}
