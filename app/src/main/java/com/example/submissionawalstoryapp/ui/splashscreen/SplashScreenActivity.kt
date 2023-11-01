package com.example.submissionawalstoryapp.ui.splashscreen

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.submissionawalstoryapp.R
import com.example.submissionawalstoryapp.data.preferences.LoginPreference
import com.example.submissionawalstoryapp.data.response.LoginResult
import com.example.submissionawalstoryapp.data.viewmodel.setting.SettingViewModel
import com.example.submissionawalstoryapp.data.viewmodel.setting.SettingViewModelFactory
import com.example.submissionawalstoryapp.databinding.ActivitySplashScreenBinding
import com.example.submissionawalstoryapp.ui.auth.AuthActivity
import com.example.submissionawalstoryapp.ui.home.MainActivity
import com.example.submissionawalstoryapp.utils.Constants
import com.example.submissionawalstoryapp.utils.SettingPreferences

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var mLoginPreference: LoginPreference
    private lateinit var login: LoginResult

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mLoginPreference = LoginPreference(this)
        login = mLoginPreference.getUser()
        handleSplash()
    }

    private fun handleSplash() {
        if (login.name != null && login.userId != null && login.token != null) {
            navigateTo(MainActivity::class.java)
        } else {
            navigateTo(AuthActivity::class.java)
        }
        handleThemeSettings()
    }

    private fun navigateTo(destination: Class<*>) {
        val intent = Intent(this, destination)
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(intent)
            finish()
        }, Constants.SPLASH_SCREEN_TIMER)
    }

    private fun handleThemeSettings() {
        val pref = SettingPreferences.getInstance(dataStore)
        val settingViewModel = ViewModelProvider(this, SettingViewModelFactory(pref)).get(
            SettingViewModel::class.java)
        settingViewModel.getThemeSettings().observe(this) { isDarkModeActive ->
            AppCompatDelegate.setDefaultNightMode(if (isDarkModeActive) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}
