package com.example.furnitureshoppingapp.fragment.shopping

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.furnitureshoppingapp.R
import com.example.furnitureshoppingapp.adapter.ShippingAddressAdapter
import com.example.furnitureshoppingapp.databinding.FragmentShippingAddressBinding
import com.example.furnitureshoppingapp.resources.Resources
import com.example.furnitureshoppingapp.viewmodel.ShippingAddressViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ShippingAddressFragment : Fragment() {
    private lateinit var binding : FragmentShippingAddressBinding
    private val adapter : ShippingAddressAdapter by lazy { ShippingAddressAdapter() }
    private val viewModel by viewModels<ShippingAddressViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentShippingAddressBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvShippingAddress.adapter = adapter
        binding.fab.setOnClickListener{
            val action = ShippingAddressFragmentDirections.actionShippingAddressFragmentToAddAddressFragment()
            findNavController().navigate(action)
        }
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        lifecycleScope.launchWhenStarted {
            viewModel.address.collectLatest {
                when(it){
                    is Resources.Loading -> {
                        binding.loading.visibility = View.VISIBLE
                    }
                    is Resources.Success -> {
                        binding.loading.visibility = View.GONE
                        adapter.differ.submitList(it.data)
                    }
                    is Resources.Error -> {
                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
                        binding.loading.visibility = View.GONE
                    }
                    else -> Unit
                }
            }
        }
    }
}