package com.example.newsapp.services

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.newsapp.R
import com.example.newsapp.activities.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val executor: Executor = Executors.newSingleThreadExecutor()
    private val handler: Handler = Handler(Looper.getMainLooper())

    @SuppressLint("ServiceCast")
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        // Extract news details from the data payload
        val id = message.data["id"]
        val source = message.data["name"]
        val author = message.data["author"]
        val title = message.data["title"]
        val description = message.data["description"]
        val url = message.data["url"]
        val imageUrl = message.data["urlToImage"]
        val publishedAt = message.data["publishedAt"]
        val content = message.data["content"]

        // Load the image
        executor.execute {
            loadImageFromUrl(imageUrl, title, description)
        }
    }

    private fun loadImageFromUrl(imageUrl: String?, title: String?, description: String?) {
        imageUrl?.let { url ->
            try {
                val connection: HttpURLConnection =
                    URL(url).openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val inputStream: InputStream = connection.inputStream
                val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)
                handler.post {
                    showNotification(title, description, bitmap)
                }
            } catch (e: Exception) {
                Log.e("LoadImageTask", "Error loading image: ${e.message}")
            }
        }
    }

    private fun showNotification(title: String?, description: String?, bitmap: Bitmap) {
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build the custom notification layout with the loaded bitmap
        val notificationLayout = NotificationCompat.Builder(
            applicationContext,
            "channel_id"
        )
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title) // Title from parameter
            .setContentText(description) // Description from parameter
            .setLargeIcon(bitmap) // Set the loaded bitmap as the large icon
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // Display the notification
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notificationLayout)
    }
}