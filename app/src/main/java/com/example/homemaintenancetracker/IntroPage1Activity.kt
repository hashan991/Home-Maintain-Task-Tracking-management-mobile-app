package com.example.homemaintenancetracker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class IntroPage1Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro_page_1)

        // Find the get started button
        val getStartedButton: Button = findViewById(R.id.getStartedButton)

        // Set OnClickListener to move to page 2
        getStartedButton.setOnClickListener {
            val intent = Intent(this@IntroPage1Activity, IntroPage2Activity::class.java)
            startActivity(intent)
        }

        var button1 = findViewById<Button>(R.id.getStartedButton)
        button1.setOnClickListener{
            val intent1 = Intent (this, IntroPage2Activity::class.java )
            startActivity(intent1)

        }


    }
}
