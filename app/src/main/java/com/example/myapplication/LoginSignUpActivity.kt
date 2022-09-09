package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.example.myapplication.databinding.ActivityLoginSignupBinding
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

class LoginSignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginSignupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginSignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userAlreadyLoggedIn()

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Login"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("SignUp"))

        val adapter = FragmentAdapter(supportFragmentManager, lifecycle)
        binding.viewPager.adapter = adapter

        binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    binding.viewPager.currentItem = tab.position
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position))
            }
        })
    }

    private fun userAlreadyLoggedIn() {
        val user = FirebaseAuth.getInstance().currentUser
        val accessToken = AccessToken.getCurrentAccessToken()

        if ((user != null && user.isEmailVerified) || (accessToken != null && !accessToken.isExpired)){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}