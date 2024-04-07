package com.example.newsapp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import org.w3c.dom.Text
import java.io.InputStream
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class ImageLoaderTask(private val executor: Executor = Executors.newSingleThreadExecutor()) {

    private val handler = Handler(Looper.getMainLooper())

    // Callback interface for image loading events
    interface ImageLoadListener {
        fun onImageLoaded(bitmap: Bitmap) // Called when image loading is successful
        fun onImageLoadFailed() // Called when image loading fails
    }

    // Load image asynchronously from the specified URL
    fun load(url: String?, listener: ImageLoadListener) {
        url ?: return // Return if URL is null
        executor.execute {
            try {
                // Download and decode the bitmap from the URL
                val bitmap = downloadBitmap(url)
                // Notify listener on the main thread when image is loaded successfully
                handler.post {
                    listener.onImageLoaded(bitmap)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Notify listener on the main thread when image loading fails
                handler.post {
                    listener.onImageLoadFailed()
                }
            }
        }
    }

    // Download bitmap from the given URL
    private fun downloadBitmap(url: String): Bitmap {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.connect()

        val inputStream: InputStream = connection.inputStream
        return BitmapFactory.decodeStream(inputStream) // Decode and return the bitmap
    }
}
