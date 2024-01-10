package com.example.furnitureshoppingapp.fragment.loginRegister

import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.furnitureshoppingapp.R
import com.example.furnitureshoppingapp.databinding.FragmentRegisterBinding
import com.example.furnitureshoppingapp.model.User
import com.example.furnitureshoppingapp.resources.Resources
import com.example.furnitureshoppingapp.util.RegisterValidation
import com.example.furnitureshoppingapp.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private val viewModel by viewModels<RegisterViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            tvSignIn.setOnClickListener {
                val action = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
                findNavController().navigate(action)
            }
            btnSignUp.setOnClickListener {
                val user = User(
                    edtName.text.toString().trim(),
                    edtEmail.text.toString().trim(),
                )
                val password = edtPass.text.toString()
                val confirmPass = edtConfirmPass.text.toString()
                viewModel.createAccountWithEmailAndPass(user, password, confirmPass)
            }
        }

        buttonAnimation()
        checkValidation()

    }

    private fun checkValidation() {
        lifecycleScope.launchWhenStarted {
            viewModel.validation.collect { validation ->
                if (validation.email is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.edtEmail.apply {
                            requestFocus()
                            error = validation.email.message
                        }
                    }
                }
                if (validation.password is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.edtPass.apply {
                            requestFocus()
                            error = validation.password.message
                        }
                    }
                }
                if (validation.confirmPass is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.edtConfirmPass.apply {
                            requestFocus()
                            error = validation.confirmPass.message
                        }
                    }
                }
            }
        }
    }

    private fun buttonAnimation() {
        lifecycleScope.launchWhenStarted {
            viewModel.register.collect {
                when (it) {
                    is Resources.Loading -> {
                        binding.btnSignUp.startAnimation()
                    }

                    is Resources.Success -> {
                        Log.d("test", it.data.toString())
                        binding.btnSignUp.revertAnimation()
                        Toast.makeText(requireContext(), "Sign-up successful", Toast.LENGTH_LONG).show()
                        val action = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
                        findNavController().navigate(action)
                    }

                    is Resources.Error -> {
                        Log.d("RegisterFragment", it.message.toString())
                        binding.btnSignUp.revertAnimation()
                    }

                    else -> {
                        Unit
                    }
                }
            }
        }
    }

    fun isPasswordValid(text: Editable?): Boolean {
        return text != null && text.length >= 8
    }
}