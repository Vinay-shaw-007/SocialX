package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.NewsAdapter.NewsViewHolder
import com.example.myapplication.databinding.NewsItemLayoutBinding
import com.example.myapplication.model.News

class NewsAdapter: ListAdapter<News, NewsViewHolder>(DiffCallback) {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        context = parent.context
        return NewsViewHolder(
            NewsItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ), context
        )
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val newsItem = getItem(position)
        holder.bind(newsItem)
    }

    class NewsViewHolder(private val binding: NewsItemLayoutBinding, private val context: Context) : RecyclerView.ViewHolder(binding.root){
        fun bind(newsItem: News) {
            binding.newsTitle.text = newsItem.title
            binding.newsDescription.text = newsItem.description
            binding.publishedAt.text = newsItem.publishedAt
            binding.source.text = newsItem.source
            val imageUrl = newsItem.newsImage
            Glide.with(context).load(imageUrl).into(binding.newsImage)
        }

    }
    companion object DiffCallback : DiffUtil.ItemCallback<News>() {
        override fun areItemsTheSame(oldItem: News, newItem: News): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: News, newItem: News): Boolean {
            return oldItem.title == newItem.title
        }

    }

}