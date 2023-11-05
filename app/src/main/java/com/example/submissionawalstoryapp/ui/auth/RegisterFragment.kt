package com.example.submissionawalstoryapp.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.example.submissionawalstoryapp.R
import com.example.submissionawalstoryapp.data.viewmodel.RegisterViewModel
import com.example.submissionawalstoryapp.databinding.FragmentRegisterBinding
import com.example.submissionawalstoryapp.ui.customview.CustomDialog
import com.example.submissionawalstoryapp.utils.Helper

class RegisterFragment : Fragment() {
    private val binding by lazy {
        FragmentRegisterBinding.inflate(layoutInflater)
    }
    private val registerViewModel by viewModels<RegisterViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnRegister.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val name = binding.etName.text.toString()

            when {
                email.isEmpty() || password.isEmpty() || name.isEmpty() -> {
                    CustomDialog(requireContext(), getString(R.string.empty_email_password_name), R.raw.error_anim).show()
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
                    registerViewModel.postRegister(name, email, password)
                }
            }
        }


        binding.btnLogin.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.auth_container, LoginFragment(), LoginFragment::class.java.simpleName)
            }
        }

        registerViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.loading.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        registerViewModel.isSuccess.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                CustomDialog(
                    requireContext(),
                    getString(R.string.successful_register),
                    R.raw.success_anim).show()

                binding.etEmail.text?.clear()
                binding.etPassword.text?.clear()
                binding.etName.text?.clear()

                val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.auth_container, LoginFragment())
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
            }
        }

        registerViewModel.isError.observe(viewLifecycleOwner) { isError ->
            if (isError) {
                CustomDialog(
                    requireContext(),
                    getString(R.string.error_register),
                    R.raw.error_anim).show()
            }
        }
    }
}
