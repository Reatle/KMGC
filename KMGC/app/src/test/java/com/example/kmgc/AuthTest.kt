package com.example.kmgc

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AuthTest {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        dbHelper = DatabaseHelper(context)

        // Clean start: drop & recreate users table
        val db = dbHelper.writableDatabase
        db.execSQL("DROP TABLE IF EXISTS users")
        db.execSQL("""
            CREATE TABLE users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                email TEXT UNIQUE,
                password TEXT,
                name TEXT,
                is_admin INTEGER DEFAULT 0
            )
        """.trimIndent())
        db.close()
    }

    @Test
    fun testAddUser() {
        val result = dbHelper.addUser("Reatlegileconfidence254@gmail.com", "password123", "Alice")
        assertTrue(result)
    }

    @Test
    fun testCheckUserSuccess() {
        dbHelper.addUser("Reatlegileconfidence254@gmail.com", "secret", "Rea")
        val exists = dbHelper.checkUser("Reatlegileconfidence254@gmail.com", "secret")
        assertTrue(exists)
    }

    @Test
    fun testCheckUserFail() {
        dbHelper.addUser("thorisomaake@gmail.com", "mypassword", "Thoriso")
        val exists = dbHelper.checkUser("thorisomaake@gmail", "wrongpass")
        assertFalse(exists)
    }

    @Test
    fun testValidateUserReturnsUser() {
        dbHelper.addUser("kopanomanyano@gmail.com", "pass123", "Kopano")
        val user = dbHelper.validateUser("kopanomanyano@gmail.com", "pass123")
        assertNotNull(user)
        assertEquals("Kopano", user?.name)
    }

    @Test
    fun testValidateUserReturnsNullForWrongPassword() {
        dbHelper.addUser("Leahaphane@gmail.com", "secretpass", "Leah")
        val user = dbHelper.validateUser("Leahaphane@gmail.com", "wrongpass")
        assertNull(user)
    }
}
