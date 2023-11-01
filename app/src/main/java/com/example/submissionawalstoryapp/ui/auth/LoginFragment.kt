package com.example.submissionawalstoryapp.ui.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
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

        playAnimation()

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginViewModel.postLogin(email, password)
            } else {
                CustomDialog(
                    requireContext(),
                    getString(R.string.empty_email_password),
                    R.raw.error_anim).show()
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

    private fun playAnimation() {
        val duration = 500L

        fun createAlphaAnimator(view: View) = ObjectAnimator.ofFloat(view, View.ALPHA, 1f).setDuration(duration)

        val labelTextView = createAlphaAnimator(binding.tvLabel)
        val emailEditTextLayout = createAlphaAnimator(binding.tilEmail)
        val emailEditText = createAlphaAnimator(binding.etEmail)

        val passwordEditTextLayout = createAlphaAnimator(binding.tilPassword)
        val passwordEditText = createAlphaAnimator(binding.etPassword)

        val loginButton = createAlphaAnimator(binding.btnLogin)
        val containerOpt = createAlphaAnimator(binding.containerOpt)

        val together = AnimatorSet().apply {
            playTogether(loginButton, containerOpt)
        }

        AnimatorSet().apply {
            playSequentially(
                emailEditTextLayout, emailEditText, passwordEditTextLayout,
                passwordEditText, labelTextView,
                together
            )
            start()
        }
    }
}



