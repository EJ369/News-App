package com.example.newsapp

data class Article(
    val id: String?,          // Unique identifier for the article
    val name: String?,        // Name of the source publishing the article
    val author: String?,      // Author of the article
    val title: String?,       // Title of the article
    val description: String?, // Short description of the article
    val url: String?,         // URL link to the full article
    val urlToImage: String?,  // URL link to the article's image
    val publishedAt: String?, // Date and time of publication
    val content: String?      // Full content of the article
)