package com.example.submissionawalstoryapp.ui.splashscreen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.submissionawalstoryapp.data.viewmodel.DataStoreViewModel
import com.example.submissionawalstoryapp.data.viewmodel.ViewModelFactory
import com.example.submissionawalstoryapp.databinding.ActivitySplashScreenBinding
import com.example.submissionawalstoryapp.ui.auth.AuthActivity
import com.example.submissionawalstoryapp.ui.home.MainActivity
import com.example.submissionawalstoryapp.ui.home.dataStore
import com.example.submissionawalstoryapp.utils.UserPreferences

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = UserPreferences.getInstance(dataStore)
        val loginViewModel =
            ViewModelProvider(this, ViewModelFactory(pref))[DataStoreViewModel::class.java]

        loginViewModel.getLoginSession().observe(this) { isLoggedIn ->
            val intent = if (isLoggedIn) {
                Intent(this, MainActivity::class.java)
            } else {
                Intent(this, AuthActivity::class.java)
            }
            startActivity(intent)
            finish()
        }
    }
}
