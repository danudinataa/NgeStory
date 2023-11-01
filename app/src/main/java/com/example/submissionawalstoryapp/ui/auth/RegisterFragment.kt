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

class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private val registerViewModel by viewModels<RegisterViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnRegister.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val name = binding.etName.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty()) {
                registerViewModel.postRegister(name, email, password)
            } else {
                CustomDialog(
                    requireContext(),
                    getString(R.string.empty_email_password_name),
                    R.raw.error_anim).show()
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
