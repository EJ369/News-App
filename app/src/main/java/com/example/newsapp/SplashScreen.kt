package com.example.newsapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen) // Set the layout for the splash screen

        // Use Handler to delay starting the MainActivity by 3000 milliseconds (3 seconds)
        Handler(Looper.getMainLooper()).postDelayed({
                                                        startActivity(Intent(this, MainActivity::class.java)) // Start the MainActivity
                                                        finish() // Finish the splash screen activity
                                                    }, 3000)
    }
}