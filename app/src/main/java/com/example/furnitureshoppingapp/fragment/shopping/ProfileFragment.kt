package com.example.furnitureshoppingapp.fragment.shopping

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.furnitureshoppingapp.R
import com.example.furnitureshoppingapp.databinding.FragmentProfileBinding
import com.example.furnitureshoppingapp.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val viewModel by viewModels<ProfileViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launchWhenStarted {
            viewModel.name.collectLatest {
                binding.tvName.text = it
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.email.collectLatest {
                binding.tvEmail.text = it
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.img.collectLatest {
                if(it != ""){
                    Glide.with(requireView()).load(it).into(binding.imgAvatar)
                }
            }
        }
    }
}