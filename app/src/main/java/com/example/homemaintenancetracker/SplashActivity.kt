package com.example.homemaintenancetracker

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Delayed action to move to the IntroPage1Activity after 4 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this@SplashActivity, IntroPage1Activity::class.java)
            startActivity(intent)
            finish()  // Close the SplashActivity so it's not on the back stack
        }, 4000) // 4 seconds delay (4000 milliseconds)
    }
}
