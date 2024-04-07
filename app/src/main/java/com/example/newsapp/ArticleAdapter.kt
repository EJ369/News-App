package com.example.newsapp

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
import java.text.SimpleDateFormat
import java.util.TimeZone

class ArticleAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var articlelist: List<Article> = mutableListOf()
    private var viewtype = "List"

    companion object {
        private const val VIEW_TYPE_LIST = 1
        private const val VIEW_TYPE_CARD = 2
    }

    // Determine the view type based on the position in the list
    override fun getItemViewType(position: Int): Int {
        val item = articlelist[position]
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
                val view = LayoutInflater.from(parent.context).inflate(R.layout.article_listview_item, parent, false)
                Article_ListViewHolder(view)
            }

            VIEW_TYPE_CARD -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.article_cardview_item, parent, false)
                Article_CardViewHolder(view)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    // Bind data to ViewHolders based on the view type
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            VIEW_TYPE_CARD -> {
                val mholder = holder as Article_CardViewHolder
                val data = articlelist[position]
                // Bind data to CardViewHolder
                bindDataToCardViewHolder(mholder, data)
            }

            VIEW_TYPE_LIST -> {
                val mholder = holder as Article_ListViewHolder
                val data = articlelist[position]
                // Bind data to ListViewHolder
                bindDataToListViewHolder(mholder, data)
            }
        }
    }

    // Get the total number of items in the list
    override fun getItemCount(): Int {
        return articlelist.size
    }

    // Bind data to CardViewHolder
    private fun bindDataToCardViewHolder(mholder: Article_CardViewHolder, data: Article) {
        // Load image asynchronously
        loadAndDisplayImage(data.urlToImage, mholder.image, mholder.loading)
        // Set other data to CardViewHolder views
        mholder.source.text = data.name
        mholder.author.text = "By ${data.author}"
        // Format and set published date
        setPublishedDate(data.publishedAt, mholder.time)
        mholder.title.text = data.title
        mholder.description.text = data.description
        mholder.content.text = data.content
        // Handle click event on title
        mholder.title.setOnClickListener {
            openArticleUrl(data.url)
        }
    }

    // Bind data to ListViewHolder
    private fun bindDataToListViewHolder(mholder: Article_ListViewHolder, data: Article) {
        // Load image asynchronously
        loadAndDisplayImage(data.urlToImage, mholder.image, mholder.loading)
        // Set data to ListViewHolder views
        mholder.headline.text = data.title
        mholder.source.text = data.name
        mholder.author.text = "By ${data.author}"
        // Format and set published date
        setPublishedDate(data.publishedAt, mholder.time)
        // Handle click event on headline
        mholder.headline.setOnClickListener {
            openArticleUrl(data.url)
        }
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
    fun setArticleList(articlelist: List<Article>) {
        this.articlelist = articlelist
        notifyDataSetChanged()
    }

    // Update the view type and notify the adapter
    fun setViewType(type: String) {
        this.viewtype = type
        notifyDataSetChanged()
    }

    // ViewHolder for list view type
    inner class Article_ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val loading: TextView = itemView.findViewById(R.id.item_loading)
        val image: ImageView = itemView.findViewById(R.id.item_image)
        val headline: TextView = itemView.findViewById(R.id.item_headline)
        val source: TextView = itemView.findViewById(R.id.item_source)
        val time: TextView = itemView.findViewById(R.id.item_time)
        val author: TextView = itemView.findViewById(R.id.item_author)
    }

    // ViewHolder for card view type
    inner class Article_CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val loading: TextView = itemView.findViewById(R.id.item_loading)
        val image: ImageView = itemView.findViewById(R.id.item_image)
        val source: TextView = itemView.findViewById(R.id.item_source)
        val time: TextView = itemView.findViewById(R.id.item_time)
        val author: TextView = itemView.findViewById(R.id.item_author)
        val title: TextView = itemView.findViewById(R.id.item_title)
        val description: TextView = itemView.findViewById(R.id.item_description)
        val content: TextView = itemView.findViewById(R.id.item_content)
    }
}