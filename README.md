🎶 KMGC Android Application

The KMGC App is a community-focused Android application developed for the Kopano Manyano(KMGC).
It enables users to register, log in, make donations, and volunteer for choir activities and community initiatives.
This app supports both users and administrators in managing donations and volunteer participation efficiently.

📱 Features
👤 User Authentication

Secure registration and login system.

Passwords are hashed using SHA-256 for enhanced security.

Email uniqueness validation to prevent duplicate accounts.

💰 Donation Management

Donors can easily make donations by entering their details and the donation amount.

Donations are stored locally in a SQLite database with a timestamp and reference for record keeping.

Future versions can integrate with external payment gateways like PayFast or PayPal.

🙋 Volunteer Registration

Volunteers can register by providing their name, email, phone number, and preferred role.

Data is stored in the database and retrievable by admin users.

Ideal for organizing events and community activities.

🧑‍💼 Admin Functionality

Admins can view and manage user accounts, donations, and volunteer data.

Designed to support operational needs and maintain a clean record of all interactions.

🧱 Technologies Used
Component	Technology
Language	Kotlin
Framework	Android SDK
Database	SQLite (via SQLiteOpenHelper)
Testing	JUnit 4 + Robolectric
UI	XML Layouts with Material Design
IDE	Android Studio
🧩 Database Structure
Tables

users

id (INTEGER, Primary Key)

email (TEXT, Unique)

password (TEXT, Hashed)

name (TEXT)

is_admin (INTEGER, 0 or 1)

donations

id (INTEGER, Primary Key)

donor_name (TEXT)

donor_email (TEXT)

amount (REAL)

reference (TEXT)

timestamp (TEXT)

volunteers

id (INTEGER, Primary Key)

volunteer_name (TEXT)

volunteer_email (TEXT)

phone (TEXT)

role (TEXT)

timestamp (TEXT)

🧪 Unit Testing

Unit tests are included under the test/ directory using JUnit and Robolectric for Android simulation.

✅ Example Tests

DonationTest.kt — Tests database insertions and retrieval for donation records.

VolunteerTest.kt — Tests volunteer data storage and retrieval functionality.

AuthTest.kt — Tests user registration and authentication logic.

To run tests:

./gradlew test

🛠️ Setup and Installation

Clone this repository:

git clone https://github.com/Reatle/KMGC.git


Open the project in Android Studio.

Sync Gradle and ensure all dependencies are installed.

Build and run the app on:

Emulator (API Level 34 or above)

Android Device (Android 14 or higher, e.g., Samsung A13)

🧰 SDK Configuration

In your build.gradle (Module: app):

android {
    compileSdk 34

    defaultConfig {
        applicationId "com.example.kmgc"
        minSdk 24
        targetSdk 34
        versionCode 1
        versionName "1.0"
    }
}

💡 Future Improvements

Integrate online payment processing (PayFast, PayPal).

Add push notifications for donation campaigns and volunteer updates.

Include data backup using Firebase Realtime Database.

Enhance UI with Jetpack Compose or Material3 components.

👨‍💻 Developer Information

Developer: Reatlegile Mokhara, Thoriso Maake, Leah Aphane, & Sandile Mpinga.
Institution: IIE Rosebank Collage
Role: Android Developer & Database Designer
Languages Used: Kotlin, SQL
Year: 2025

📄 License

This project is developed for educational and non-commercial use.
You may modify and redistribute it for personal or academic purposes.