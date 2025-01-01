package com.example.homemaintenancetracker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class IntroPage2Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro_page_2)

        // Find the next button
        val nextButtonPage2: Button = findViewById(R.id.nextButtonPage2)

        // Set OnClickListener to move to page 3
        nextButtonPage2.setOnClickListener {
            val intent = Intent(this@IntroPage2Activity, IntroPage3Activity::class.java)
            startActivity(intent)
        }

        var button1 = findViewById<Button>(R.id.nextButtonPage2)
        button1.setOnClickListener{
            val intent1 = Intent (this, IntroPage3Activity::class.java )
            startActivity(intent1)

        }
    }
}
