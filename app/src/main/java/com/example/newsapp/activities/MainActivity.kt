package com.example.newsapp.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.example.newsapp.databinding.ActivityMainBinding
import com.example.newsapp.models.Article
import com.example.newsapp.models.FetchAPIDataClass
import com.google.firebase.messaging.FirebaseMessaging
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity(), FetchAPIDataClass.OnDataFetchedListener {
    private lateinit var fetchAPIDataClass: FetchAPIDataClass
    private lateinit var rv: RecyclerView
    private lateinit var adapter: ArticleAdapter
    private var articleList = listOf<Article>()
    private var viewType = "Card"
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rv.layoutManager = LinearLayoutManager(this)

        // Initialize adapter and attach it to RecyclerView
        adapter = ArticleAdapter(this)

        // Initialize PagerSnapHelper for snapping behavior
        val pagerSnapHelper = PagerSnapHelper()

        // set initial adapter
        setAdapter(viewType, adapter, pagerSnapHelper)

        // List view click listener
        binding.listView.setOnClickListener {
            when (viewType) {
                "Card" -> {
                    viewType = "List"
                    setAdapter(viewType, adapter, pagerSnapHelper)
                    // Adjust view sizes for list view
                    /*adjustViewSizes(binding.listView, 100, 100)
                    adjustViewSizes(binding.cardView, 80, 80)
                    binding.listView.setColorFilter(ContextCompat.getColor(this, R.color.orange))
                    binding.cardView.setColorFilter(ContextCompat.getColor(this, R.color.black))
                    adjustRecyclerViewLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT)
                    pagerSnapHelper.attachToRecyclerView(null)
                    adapter.setViewType(viewType)
                    adapter.notifyDataSetChanged()*/
                }
            }
        }

        // Card view click listener
        binding.cardView.setOnClickListener {
            when (viewType) {
                "List" -> {
                    viewType = "Card"
                    setAdapter(viewType, adapter, pagerSnapHelper)
                    /*adjustViewSizes(binding.listView, 80, 80)
                    adjustViewSizes(binding.cardView, 90, 90)
                    binding.cardView.setColorFilter(ContextCompat.getColor(this, R.color.orange))
                    binding.listView.setColorFilter(ContextCompat.getColor(this, R.color.black))
                    adjustRecyclerViewLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT)
                    pagerSnapHelper.attachToRecyclerView(binding.rv)
                    adapter.setViewType(viewType)
                    adapter.notifyDataSetChanged()*/
                }
            }
        }

        // Sort up click listener
        binding.sortUp.setOnClickListener {
            binding.sortUp.visibility = View.GONE
            binding.sortDown.visibility = View.VISIBLE
            // Sort article list by ascending published date
            adapter.setArticleList(articleList.sortedBy { it.publishedAt })
        }

        // Sort down click listener
        binding.sortDown.setOnClickListener {
            binding.sortDown.visibility = View.GONE
            binding.sortUp.visibility = View.VISIBLE
            // Sort article list by descending published date
            adapter.setArticleList(articleList.sortedByDescending { it.publishedAt })
        }

        // Search view query listener
        //val searchView: SearchView = findViewById(R.id.searchView)
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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

    private fun setAdapter(viewType: String, adapter: ArticleAdapter, pagerSnapHelper: PagerSnapHelper) {
        if (viewType == "Card") {
            adjustViewSizes(binding.listView, 80, 80)
            adjustViewSizes(binding.cardView, 90, 90)
            binding.cardView.setColorFilter(ContextCompat.getColor(this, R.color.orange))
            binding.listView.setColorFilter(ContextCompat.getColor(this, R.color.black))
            adjustRecyclerViewLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT)
            pagerSnapHelper.attachToRecyclerView(binding.rv)
        } else {
            adjustViewSizes(binding.listView, 100, 100)
            adjustViewSizes(binding.cardView, 80, 80)
            binding.listView.setColorFilter(ContextCompat.getColor(this, R.color.orange))
            binding.cardView.setColorFilter(ContextCompat.getColor(this, R.color.black))
            adjustRecyclerViewLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT)
            pagerSnapHelper.attachToRecyclerView(null)
        }
        adapter.setViewType(viewType)
        binding.rv.adapter = adapter
        adapter.notifyDataSetChanged()
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
        val params = binding.rv.layoutParams as ViewGroup.LayoutParams
        params.width = width
        binding.rv.layoutParams = params
    }

    override fun onError(error: String) {
        // Handle error, such as displaying a toast or logging
        showToast("Error: $error")
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}