package com.example.newsapp

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.messaging.FirebaseMessaging
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity(), FetchAPIDataClass.OnDataFetchedListener {
    private lateinit var fetchAPIDataClass: FetchAPIDataClass
    private lateinit var rv: RecyclerView
    private lateinit var adapter: ArticleAdapter
    private var articleList = listOf<Article>()
    private var viewType = "List"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        val listView: ImageView = findViewById(R.id.listView)
        val cardView: ImageView = findViewById(R.id.cardView)
        val sort_up: ImageView = findViewById(R.id.sort_up)
        val sort_down: ImageView = findViewById(R.id.sort_down)
        rv = findViewById(R.id.rv)
        rv.layoutManager = LinearLayoutManager(this)

        // Initialize adapter and attach it to RecyclerView
        adapter = ArticleAdapter(this)
        rv.adapter = adapter

        // Initialize PagerSnapHelper for snapping behavior
        val pagerSnapHelper = PagerSnapHelper()

        // List view click listener
        listView.setOnClickListener {
            if (viewType != "List") {
                viewType = "List"
                // Adjust view sizes for list view
                adjustViewSizes(listView, 100, 100)
                adjustViewSizes(cardView, 80, 80)
                adjustRecyclerViewLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT)
                pagerSnapHelper.attachToRecyclerView(null)
                adapter.setViewType(viewType)
                adapter.notifyDataSetChanged()
            }
        }

        // Card view click listener
        cardView.setOnClickListener {
            if (viewType != "Card") {
                viewType = "Card"
                // Adjust view sizes for card view
                adjustViewSizes(listView, 80, 80)
                adjustViewSizes(cardView, 100, 100)
                adjustRecyclerViewLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT)
                pagerSnapHelper.attachToRecyclerView(rv)
                adapter.setViewType(viewType)
                adapter.notifyDataSetChanged()
            }
        }

        // Sort up click listener
        sort_up.setOnClickListener {
            sort_up.visibility = View.GONE
            sort_down.visibility = View.VISIBLE
            // Sort article list by ascending published date
            adapter.setArticleList(articleList.sortedBy { it.publishedAt })
        }

        // Sort down click listener
        sort_down.setOnClickListener {
            sort_down.visibility = View.GONE
            sort_up.visibility = View.VISIBLE
            // Sort article list by descending published date
            adapter.setArticleList(articleList.sortedByDescending { it.publishedAt })
        }

        // Search view query listener
        val searchView: SearchView = findViewById(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { filterList(it) }
                return true
            }
        })

        // Fetch data from API
        fetchAPIDataClass = FetchAPIDataClass(executor = Executors.newSingleThreadExecutor(), callback = this)
        val url = getString(R.string.api_url)
        fetchAPIDataClass.fetchData(url)

        // Retrieve FCM token
        retrieveFCMToken()

        notification()
    }

    private fun retrieveFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("FCM Token", token ?: "Token retrieval failed")
            }
        }
    }

    private fun notification() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("FCM Token", token ?: "Token retrieval failed")
            }
        }
    }

    private fun filterList(query: String) {
        // Filter article list based on query
        val filteredList = articleList.filter { article ->
            article.title?.contains(query, ignoreCase = true) ?: false ||
                    article.author?.contains(query, ignoreCase = true) ?: false ||
                    article.description?.contains(query, ignoreCase = true) ?: false
        }
        adapter.setArticleList(filteredList)
    }

    override fun onDataFetched(result: List<Article>?) {
        result?.let { articles ->
            articleList = articles
            adapter.setArticleList(articles)
        }
    }

    private fun adjustViewSizes(view: View, width: Int, height: Int) {
        val params = view.layoutParams as ViewGroup.LayoutParams
        params.width = width
        params.height = height
        view.layoutParams = params
    }

    private fun adjustRecyclerViewLayoutParams(width: Int) {
        val params = rv.layoutParams as ViewGroup.LayoutParams
        params.width = width
        rv.layoutParams = params
    }

    override fun onError(error: String) {
        // Handle error, such as displaying a toast or logging
        showToast("Error: $error")
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}