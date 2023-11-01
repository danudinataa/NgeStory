package com.example.submissionawalstoryapp.ui.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.submissionawalstoryapp.R
import com.example.submissionawalstoryapp.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(
                R.id.auth_container,
                LoginFragment()
            ).commit()
        }
    }

    // Fungsi untuk menavigasi ke RegisterFragment
    fun navigateToRegister() {
        supportFragmentManager.beginTransaction().replace(
            R.id.auth_container,
            RegisterFragment()
        ).addToBackStack(null).commit()
    }
}
