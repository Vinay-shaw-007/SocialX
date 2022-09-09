package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.model.News
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.collectLatest

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var adapter: NewsAdapter

    companion object {
        private const val TAG = "MainActivity123"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.onBackPressedDispatcher.addCallback(callBack)

        mainViewModel = ViewModelProvider(
            this,
            MainViewModelFactory(this)
        )[MainViewModel::class.java]

        binding.searchView.clearFocus()

        binding.signOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            LoginManager.getInstance().logOut()
            finish()
        }

        adapter = NewsAdapter()
        binding.recyclerView.adapter = adapter
        lifecycleScope.launchWhenStarted {
            mainViewModel.allNewsData.collectLatest {
                if (it.isEmpty()) return@collectLatest
                adapter.submitList(it)
                searchNews(it)
            }
        }
    }

    private fun searchNews(it: List<News>) {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText, it)
                return true
            }

        })
    }

    private fun filterList(newText: String?, list: List<News>) {
        val filterList = ArrayList<News>()
        newText?.let {
            if (newText.isEmpty()){
                adapter.submitList(list)
            }

            for (item in list){
                if (item.source.lowercase().contains(newText.lowercase())){
                    filterList.add(item)
                }
            }

            if (filterList.isEmpty()){
                Log.d(TAG, "No data found")
            }else{
                adapter.submitList(filterList)
            }
        }
    }

    private val callBack = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finishAffinity()
        }

    }
}