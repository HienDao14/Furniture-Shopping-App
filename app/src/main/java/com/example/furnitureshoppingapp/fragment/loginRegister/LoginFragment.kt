package com.example.furnitureshoppingapp.fragment.loginRegister

import android.content.Intent
import android.os.Bundle
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
import com.example.furnitureshoppingapp.activity.ShoppingActivity
import com.example.furnitureshoppingapp.databinding.FragmentLoginBinding
import com.example.furnitureshoppingapp.dialog.setUpBottomSheetDialog
import com.example.furnitureshoppingapp.resources.Resources
import com.example.furnitureshoppingapp.viewmodel.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel by viewModels<LoginViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            tvRegister.setOnClickListener {
                val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
                findNavController().navigate(action)
            }
            btnLogin.setOnClickListener {
                val email = edtEmail.text.toString().trim()
                val password = edtPass.text.toString()
                viewModel.login(email, password)
            }
            tvForgotPassword.setOnClickListener {
                setUpBottomSheetDialog {email ->
                    viewModel.resetPassword(email)
                }
            }
        }
        collectResetPasswordSharedFlow()
        lifecycleScope.launchWhenStarted {
            viewModel.login.collect{
                when(it){
                    is Resources.Loading ->{
                        binding.btnLogin.startAnimation()
                    }
                    is Resources.Success -> {
                        binding.btnLogin.revertAnimation()
                        Intent(requireActivity(), ShoppingActivity::class.java).also {intent ->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }
                    is Resources.Error -> {
                        binding.btnLogin.revertAnimation()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                    else ->{
                        Unit
                    }
                }
            }
        }
    }

    private fun collectResetPasswordSharedFlow() {
        lifecycleScope.launchWhenStarted {
            viewModel.resetPassword.collect{
                when(it){
                    is Resources.Loading ->{
                    }
                    is Resources.Success -> {
                        Snackbar.make(requireView(), "Reset link was sent successfully", Snackbar.LENGTH_LONG).show()
                    }
                    is Resources.Error -> {
                        Snackbar.make(requireView(), "Error: ${it.message}", Snackbar.LENGTH_LONG).show()
                    }
                    else ->{
                        Unit
                    }
                }
            }
        }
    }
}