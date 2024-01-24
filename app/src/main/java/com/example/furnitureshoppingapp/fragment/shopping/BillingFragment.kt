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
import androidx.navigation.fragment.navArgs
import com.example.furnitureshoppingapp.adapter.AddressAdapter
import com.example.furnitureshoppingapp.adapter.BillingProductAdapter
import com.example.furnitureshoppingapp.databinding.FragmentBillingBinding
import com.example.furnitureshoppingapp.resources.Resources
import com.example.furnitureshoppingapp.viewmodel.BillingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class BillingFragment : Fragment() {
    private lateinit var binding: FragmentBillingBinding
    private val args  by navArgs<BillingFragmentArgs>()
    private val addressAdapter: AddressAdapter by lazy { AddressAdapter() }
    private val productAdapter: BillingProductAdapter by lazy { BillingProductAdapter() }
    private val viewModel by viewModels<BillingViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBillingBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvAddress.adapter = addressAdapter
        binding.rvBillingItem.adapter = productAdapter

        val orderPrice = args.totalPrice
        val totalPrice = orderPrice + 5f
        val orderPriceText = "$ " + String.format("%.1f", orderPrice)
        val totalPriceText = "$ " + String.format("%.1f", totalPrice)
        binding.tvOrderPrice.text = orderPriceText
        binding.tvTotalPrice.text = totalPriceText

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        addressAdapter.onClick = {address ->
            var addressDetail = address.fullName + " " + address.phoneNumber + "\n"
            addressDetail += address.city + " " + address.district + " " + address.ward + "\n"
            addressDetail += address.address + "\n"
            addressDetail += address.additionalInfo
            binding.tvAddressDetail.apply{
                visibility = View.VISIBLE
                text = addressDetail
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.address.collectLatest {
                when(it){
                    is Resources.Loading -> {}
                    is Resources.Success -> {
                        addressAdapter.differ.submitList(it.data)
                    }
                    is Resources.Error -> {
                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.cartProducts.collectLatest {
                when(it){
                    is Resources.Loading -> {}
                    is Resources.Success -> {
                        productAdapter.differ.submitList(it.data)
                    }
                    is Resources.Error -> {
                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
        binding.btnAddAddress.setOnClickListener {
            val action = BillingFragmentDirections.actionBillingFragmentToAddAddressFragment()
            findNavController().navigate(action)
        }
    }
}