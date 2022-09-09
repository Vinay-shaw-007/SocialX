package com.example.myapplication

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.example.myapplication.model.News
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONException

class MainViewModel(private val context: Context): ViewModel() {

    companion object{
        private const val TAG = "MainViewModel"
    }

    private var _allNewsData = MutableStateFlow<List<News>>(listOf())
    val allNewsData = _allNewsData.asStateFlow()

    private fun fetchData() {
        Log.d(TAG, "inside fetch data")
        val url = context.getString(R.string.url)
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                try {
                    val newsJsonArray = response.getJSONArray("articles")
                    val newsArray = ArrayList<News>()
                    for (i in 0 until newsJsonArray.length()) {
                        val newsJSONObject = newsJsonArray.getJSONObject(i)
                        val news = News(
                            newsJSONObject.getString("title"),
                            newsJSONObject.getString("description"),
                            newsJSONObject.getString("publishedAt"),
                            newsJSONObject.getJSONObject("source").getString("name"),
                            newsJSONObject.getString("urlToImage"),
                        )
                        newsArray.add(news)
                    }
                    _allNewsData.value = newsArray
                } catch (e: JSONException) {
                    Log.d(TAG, "Error " + e.message.toString())
                }

            },
            {
                Log.d(TAG, "Error " + it.message.toString())
            }
        )
        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest)
    }
    init {
        fetchData()
    }

}
class MainViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}