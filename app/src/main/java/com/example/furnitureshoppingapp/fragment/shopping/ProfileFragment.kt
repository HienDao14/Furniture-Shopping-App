package com.example.furnitureshoppingapp.fragment.shopping

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.furnitureshoppingapp.R
import com.example.furnitureshoppingapp.databinding.FragmentProfileBinding
import com.example.furnitureshoppingapp.resources.Resources
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
            viewModel.user.collectLatest {
                it.data?.let {
                    binding.tvName.text = it.userName
                    binding.tvEmail.text = it.email
                    if(it.imgPath != ""){
                        Glide.with(requireView()).load(it.imgPath).into(binding.imgAvatar)
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.addresses.collectLatest {
                when(it){
                    is Resources.Success -> {
                        binding.tvCountAddresses.text = "${it.data} addresses"
                    }
                    is Resources.Error -> {
                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.orders.collectLatest {
                when(it){
                    is Resources.Success -> {
                        binding.tvCountOrders.text = "Already have ${it.data} orders"
                    }
                    is Resources.Error -> {
                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        binding.cardViewAddress.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileFragmentToShippingAddressFragment()
            findNavController().navigate(action)
        }
        binding.imgAvatar.setOnClickListener {

        }
    }
}