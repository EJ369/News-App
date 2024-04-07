package com.example.newsapp

import android.os.Handler
import android.os.Looper
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class FetchAPIDataClass(
    private val executor: Executor = Executors.newSingleThreadExecutor(),
    private val callback: OnDataFetchedListener
) {
    // Define the callback interface
    interface OnDataFetchedListener {
        fun onDataFetched(result: List<Article>?)
        fun onError(error: String)
    }

    // Fetch data from the API using the provided URL
    fun fetchData(urlString: String) {
        executor.execute {
            try {
                val url = URL(urlString)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                val bufferedReader = BufferedReader(InputStreamReader(connection.inputStream))
                val stringBuilder = StringBuilder()
                var line: String?

                // Read the response from the API
                while (bufferedReader.readLine().also { line = it } != null) {
                    stringBuilder.append(line).append("\n")
                }

                bufferedReader.close()
                connection.disconnect()

                val jsonResponse = stringBuilder.toString()
                val articles = parseJson(jsonResponse)

                // Notify the callback with the fetched data on the main thread
                Handler(Looper.getMainLooper()).post {
                    callback.onDataFetched(articles)
                }
            } catch (e: Exception) {
                // Handle errors and notify the callback with the error message
                Handler(Looper.getMainLooper()).post {
                    callback.onError(e.message ?: "Unknown error occurred")
                }
            }
        }
    }

    // Parse the JSON response into a list of Article objects
    private fun parseJson(jsonString: String?): List<Article>? {
        if (jsonString.isNullOrEmpty()) return null

        val articlesList = mutableListOf<Article>()
        try {
            val jsonObject = JSONObject(jsonString)
            val articlesArray = jsonObject.getJSONArray("articles")

            // Iterate through the JSON array and create Article objects
            for (i in 0 until articlesArray.length()) {
                val articleObject = articlesArray.getJSONObject(i)
                val sourceObject = articleObject.getJSONObject("source")

                val article = Article(
                    sourceObject.optString("id"),
                    sourceObject.optString("name"),
                    articleObject.optString("author"),
                    articleObject.optString("title"),
                    articleObject.optString("description"),
                    articleObject.optString("url"),
                    articleObject.optString("urlToImage"),
                    articleObject.optString("publishedAt"),
                    articleObject.optString("content")
                )
                articlesList.add(article)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return articlesList
    }
}