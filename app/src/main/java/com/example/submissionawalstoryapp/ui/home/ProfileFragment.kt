package com.example.submissionawalstoryapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.submissionawalstoryapp.data.viewmodel.DataStoreViewModel
import com.example.submissionawalstoryapp.data.viewmodel.ViewModelFactory
import com.example.submissionawalstoryapp.databinding.FragmentProfileBinding
import com.example.submissionawalstoryapp.ui.auth.AuthActivity
import com.example.submissionawalstoryapp.utils.UserPreferences

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val pref by lazy {
        UserPreferences.getInstance(requireContext().dataStore)
    }
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        val root: View = binding.root

        val dataStoreViewModel =
            ViewModelProvider(this, ViewModelFactory(pref))[DataStoreViewModel::class.java]

        with(binding) {
            dataStoreViewModel.getName().observe(viewLifecycleOwner)  {
                tvUsername.text = it
            }

            dataStoreViewModel.getUser().observe(viewLifecycleOwner) {
                tvUserId.text = it
            }

            btnChangeLanguage.setOnClickListener {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }

            btnLogout.setOnClickListener {
                logout()
            }
        }

        return root
    }

    private fun logout() {
        val loginViewModel =
            ViewModelProvider(this, ViewModelFactory(pref))[DataStoreViewModel::class.java]
        loginViewModel.clearDataLogin()
        val intent = Intent(activity, AuthActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
