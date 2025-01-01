package com.example.homemaintenancetracker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class IntroPage3Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro_page_3)

        // Find the button
        val nextButtonPage3: Button = findViewById(R.id.nextButtonPage3)

        // Set an OnClickListener to handle button click
        nextButtonPage3.setOnClickListener {
            // Navigate to MainActivity (Home Page)
            val intent = Intent(this@IntroPage3Activity, MainActivity::class.java)
            startActivity(intent)
            finish()  // Optionally, close the intro pages to avoid going back to them
        }
    }
}
