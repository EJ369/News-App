package com.example.newsapp.activities

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.example.newsapp.databinding.ArticleCardviewItemBinding
import com.example.newsapp.databinding.ArticleListviewItemBinding
import com.example.newsapp.models.Article
import com.example.newsapp.services.ImageLoaderTask
import java.text.SimpleDateFormat
import java.util.TimeZone

class ArticleAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var articleList: List<Article> = mutableListOf()
    private var viewtype = "List"

    companion object {
        private const val VIEW_TYPE_LIST = 1
        private const val VIEW_TYPE_CARD = 2
    }

    // Determine the view type based on the position in the list
    override fun getItemViewType(position: Int): Int {
        val item = articleList[position]
        return if (viewtype == "List") {
            VIEW_TYPE_LIST
        } else {
            VIEW_TYPE_CARD
        }
    }

    // Create ViewHolders based on the view type
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_LIST -> {
                val binding = ArticleListviewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ArticleListViewHolder(binding)
            }

            VIEW_TYPE_CARD -> {
                val binding = ArticleCardviewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ArticleCardViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    // Bind data to ViewHolders based on the view type
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            VIEW_TYPE_CARD -> {
                val mHolder = holder as ArticleCardViewHolder
                val data = articleList[position]
                // Bind data to CardViewHolder
                mHolder.bind(data)
            }

            VIEW_TYPE_LIST -> {
                val mHolder = holder as ArticleListViewHolder
                val data = articleList[position]
                // Bind data to ListViewHolder
                mHolder.bind(data)
            }
        }
    }

    // Get the total number of items in the list
    override fun getItemCount(): Int {
        return articleList.size
    }

    // Load image asynchronously and display it in the ImageView
    private fun loadAndDisplayImage(url: String?, imageView: ImageView, loadingView: TextView) {
        val imageLoaderTask = ImageLoaderTask()
        imageLoaderTask.load(url, object : ImageLoaderTask.ImageLoadListener {
            override fun onImageLoaded(bitmap: Bitmap) {
                imageView.setImageBitmap(bitmap)
                loadingView.visibility = View.GONE
                imageView.visibility = View.VISIBLE
            }

            override fun onImageLoadFailed() {
                imageView.visibility = View.GONE
                loadingView.visibility = View.VISIBLE
            }
        })
    }

    // Format and set the published date in the specified TextView
    private fun setPublishedDate(publishedAt: String?, timeTextView: TextView) {
        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        inputDateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = inputDateFormat.parse(publishedAt ?: "")
        val outputDateFormat = SimpleDateFormat("MMM, dd HH:mm")
        val formattedDate = outputDateFormat.format(date)
        timeTextView.text = " - $formattedDate"
    }

    // Open the article URL in a browser
    private fun openArticleUrl(url: String?) {
        url?.let {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
            context.startActivity(intent)
        }
    }

    // Update the article list and notify the adapter
    fun setArticleList(articleList: List<Article>) {
        this.articleList = articleList
        notifyDataSetChanged()
    }

    // Update the view type and notify the adapter
    fun setViewType(type: String) {
        this.viewtype = type
        notifyDataSetChanged()
    }

    // ViewHolder for list view type
    inner class ArticleListViewHolder(private val binding: ArticleListviewItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(article: Article) {
            // Load image asynchronously
            loadAndDisplayImage(article.urlToImage, binding.itemImage, binding.itemLoading)
            // Set data to ListViewHolder views
            binding.itemHeadline.text = article.title
            binding.itemSource.text = article.name
            binding.itemAuthor.text = "By ${article.author}"
            // Format and set published date
            setPublishedDate(article.publishedAt, binding.itemTime)
            // Handle click event on headline
            binding.itemHeadline.setOnClickListener {
                openArticleUrl(article.url)
            }
        }
    }

    // ViewHolder for card view type
    inner class ArticleCardViewHolder(private val binding: ArticleCardviewItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(article: Article) {
            // Load image asynchronously
            loadAndDisplayImage(article.urlToImage, binding.itemImage, binding.itemLoading)
            // Set other data to CardViewHolder views
            binding.itemSource.text = article.name
            binding.itemAuthor.text = "By ${article.author}"
            // Format and set published date
            setPublishedDate(article.publishedAt, binding.itemTime)
            binding.itemTitle.text = article.title
            binding.itemDescription.text = article.description
            binding.itemContent.text = article.content
            // Handle click event on title
            binding.itemTitle.setOnClickListener {
                openArticleUrl(article.url)
            }
        }
    }
}