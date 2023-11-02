package com.example.submissionawalstoryapp.ui.home

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.submissionawalstoryapp.R
import com.example.submissionawalstoryapp.data.preferences.LoginPreference
import com.example.submissionawalstoryapp.data.response.Login
import com.example.submissionawalstoryapp.data.response.LoginResult
import com.example.submissionawalstoryapp.data.viewmodel.ProfileViewModel
import com.example.submissionawalstoryapp.data.viewmodel.setting.SettingViewModel
import com.example.submissionawalstoryapp.data.viewmodel.setting.SettingViewModelFactory
import com.example.submissionawalstoryapp.databinding.FragmentProfileBinding
import com.example.submissionawalstoryapp.ui.auth.AuthActivity
import com.example.submissionawalstoryapp.utils.SettingPreferences

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var mLoginPreference: LoginPreference
    private lateinit var login: LoginResult

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        val root: View = binding.root
        mLoginPreference = LoginPreference(root.context)
        login = mLoginPreference.getUser()

        with(binding) {
            tvUsername.text = login.userId
            tvUserId.text = login.name

            btnChangeLanguage.setOnClickListener {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }

            btnLogout.setOnClickListener {
                mLoginPreference.removeUser()
                val intent = Intent(activity, AuthActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
