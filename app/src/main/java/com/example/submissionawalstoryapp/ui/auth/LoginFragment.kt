package com.example.submissionawalstoryapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.example.submissionawalstoryapp.R
import com.example.submissionawalstoryapp.data.preferences.LoginPreference
import com.example.submissionawalstoryapp.data.response.Login
import com.example.submissionawalstoryapp.data.response.LoginResult
import com.example.submissionawalstoryapp.data.viewmodel.LoginViewModel
import com.example.submissionawalstoryapp.databinding.FragmentLoginBinding
import com.example.submissionawalstoryapp.ui.customview.CustomDialog
import com.example.submissionawalstoryapp.ui.home.MainActivity
import com.example.submissionawalstoryapp.utils.Helper


class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val loginViewModel by viewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            when {
                email.isEmpty() || password.isEmpty() -> {
                    CustomDialog(requireContext(), getString(R.string.empty_email_password), R.raw.error_anim).show()
                }
                !Helper.isValidEmail(email) && !Helper.validateMinLength(password) -> {
                    CustomDialog(requireContext(), getString(R.string.invalid_email_password), R.raw.error_anim).show()
                }
                !Helper.isValidEmail(email) -> {
                    CustomDialog(requireContext(), getString(R.string.invalid_email), R.raw.error_anim).show()
                }
                !Helper.validateMinLength(password) -> {
                    CustomDialog(requireContext(), getString(R.string.invalid_password), R.raw.error_anim).show()
                }
                else -> {
                    loginViewModel.postLogin(email, password)
                }
            }
        }

        binding.btnRegister.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.auth_container, RegisterFragment(), RegisterFragment::class.java.simpleName)
            }
        }

        loginViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.loading.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        loginViewModel.loginResponse.observe(viewLifecycleOwner) { loginResponse ->
            if (loginResponse != null) {
                if (!loginResponse.error) {
                    saveLoginData(loginResponse)
                    startActivity(Intent(requireActivity(), MainActivity::class.java))
                    requireActivity().finish()
                } else {
                    CustomDialog(
                        requireContext(),
                        loginResponse.message,
                        R.raw.error_anim
                    ).show()
                }
            } else {
                CustomDialog(
                    requireContext(),
                    getString(R.string.error_login),
                    R.raw.error_anim
                ).show()
            }
        }

        loginViewModel.isError.observe(viewLifecycleOwner) { isError ->
            if (isError) {
                // Show error message
                CustomDialog(
                    requireContext(),
                    getString(R.string.error_server),
                    R.raw.error_anim).show()
            }
        }
    }

    private fun saveLoginData(loginResponse: Login) {
        val loginPreference = LoginPreference(requireContext())
        val loginResult = loginResponse.loginResult
        val loginModel = LoginResult(
            name = loginResult.name, userId = loginResult.userId, token = loginResult.token
        )

        loginPreference.setLogin(loginModel)
    }
}



