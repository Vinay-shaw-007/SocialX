package com.example.myapplication.model

data class News(
    val title: String,
    val description: String,
    val publishedAt: String,
    val source: String,
    val newsImage: String
) {
    override fun toString(): String {
        return super.toString()
    }
}
