package com.example.homemaintenancetracker

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.os.Vibrator
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class TimerStopwatchActivity : AppCompatActivity() {

    private lateinit var timerTextView: TextView
    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private lateinit var resetButton: Button
    private lateinit var taskNameEditText: EditText
    private lateinit var durationEditText: EditText
    private var mediaPlayer: MediaPlayer? = null

    private var isRunning = false
    private var startTime = 0L
    private var remainingTime = 0L
    private var countdownTime = 0L
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer_stopwatch2)

        // Set up edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize UI components
        timerTextView = findViewById(R.id.textViewTimer)
        startButton = findViewById(R.id.buttonStart)
        stopButton = findViewById(R.id.buttonStop)
        resetButton = findViewById(R.id.buttonReset)
        taskNameEditText = findViewById(R.id.editTextTaskName)
        durationEditText = findViewById(R.id.editTextDuration)

        // Start Button
        startButton.setOnClickListener {
            if (!isRunning) {
                val durationInput = durationEditText.text.toString()
                if (durationInput.isNotEmpty()) {
                    countdownTime = durationInput.toLong() * 1000  // Convert seconds to milliseconds
                    startTimer()
                } else {
                    Toast.makeText(this, "Please enter a duration in seconds.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Stop Button
        stopButton.setOnClickListener {
            if (isRunning) {
                stopTimer()
            }
        }

        // Reset Button
        resetButton.setOnClickListener {
            resetTimer()
        }
    }

    // Start the countdown timer
    private fun startTimer() {
        isRunning = true
        startTime = SystemClock.elapsedRealtime()
        remainingTime = countdownTime
        handler.post(timerRunnable)
    }

    // Stop the timer
    private fun stopTimer() {
        isRunning = false
        handler.removeCallbacks(timerRunnable)
    }

    // Reset the timer
    private fun resetTimer() {
        isRunning = false
        handler.removeCallbacks(timerRunnable)
        remainingTime = 0L
        timerTextView.text = "00:00:00"
    }

    // Runnable to update the countdown timer
    private val timerRunnable: Runnable = object : Runnable {
        override fun run() {
            val elapsedTime = SystemClock.elapsedRealtime() - startTime
            remainingTime = countdownTime - elapsedTime

            if (remainingTime > 0) {
                val seconds = (remainingTime / 1000).toInt() % 60
                val minutes = (remainingTime / (1000 * 60) % 60).toInt()
                val hours = (remainingTime / (1000 * 60 * 60) % 24).toInt()
                timerTextView.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
                handler.postDelayed(this, 1000)
            } else {
                timerTextView.text = "00:00:00"
                handler.removeCallbacks(this)
                isRunning = false
                triggerAlarm()
            }
        }
    }

    // Trigger an alarm sound and vibration after the countdown finishes
    private fun triggerAlarm() {
        Toast.makeText(this, "Time's up!", Toast.LENGTH_SHORT).show()

        // Play the alarm sound
        mediaPlayer = MediaPlayer.create(this, R.raw.alarm_sound)
        mediaPlayer?.start()

        // Vibrate the phone
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(1000) // Vibrate for 1 second
    }

    override fun onDestroy() {
        super.onDestroy()
        // Release the MediaPlayer when the activity is destroyed
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
