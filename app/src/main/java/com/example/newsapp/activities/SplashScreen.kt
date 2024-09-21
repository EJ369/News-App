package com.example.newsapp.activities

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.newsapp.R
import com.example.newsapp.databinding.ActivitySplashscreenBinding

class SplashScreen : AppCompatActivity() {
    private lateinit var binding: ActivitySplashscreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashscreenBinding.inflate(layoutInflater);
        setContentView(binding.root);

        binding.newsIcon.post {
            // Get the original height of the icon
            val originalHeight = binding.newsIcon.height

            // ValueAnimator to animate from 0 to the full height
            val animator = ValueAnimator.ofInt(0, originalHeight)
            animator.duration = 1000 // Set the duration of the animation (1 second)

            // Listen to animation updates to apply the clipping
            animator.addUpdateListener { animation ->
                val animatedValue = animation.animatedValue as Int

                // Set clip bounds for the ImageView (clip top to animatedValue)
                binding.newsIcon.clipBounds = android.graphics.Rect(0, 0, binding.newsIcon.width, animatedValue)
            }

            // Start the animation
            animator.start()
        }

        val slideInAnimator = ObjectAnimator.ofFloat(binding.appName, "translationX", -1000f, 0f)
        slideInAnimator.duration = 1000 // Duration of the animation

        slideInAnimator.start()

        // Use Handler to delay starting the MainActivity by 2000 milliseconds (2 seconds)
        Handler(Looper.getMainLooper()).postDelayed({
                                                        startActivity(Intent(this, MainActivity::class.java)) // Start the MainActivity
                                                        finish() // Finish the splash screen activity
                                                    }, 2000)
    }
}