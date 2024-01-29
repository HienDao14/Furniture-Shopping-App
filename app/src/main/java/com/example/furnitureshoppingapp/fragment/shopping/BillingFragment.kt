package com.example.furnitureshoppingapp.fragment.shopping

import android.app.AlertDialog
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
import com.example.furnitureshoppingapp.model.Address
import com.example.furnitureshoppingapp.model.CartProduct
import com.example.furnitureshoppingapp.model.Order
import com.example.furnitureshoppingapp.resources.OrderStatus
import com.example.furnitureshoppingapp.resources.Resources
import com.example.furnitureshoppingapp.util.Constants.showTopSnackbar
import com.example.furnitureshoppingapp.viewmodel.BillingViewModel
import com.example.furnitureshoppingapp.viewmodel.OrderViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class BillingFragment : Fragment() {
    private lateinit var binding: FragmentBillingBinding
    private val args  by navArgs<BillingFragmentArgs>()
    private val addressAdapter: AddressAdapter by lazy { AddressAdapter() }
    private val productAdapter: BillingProductAdapter by lazy { BillingProductAdapter() }
    private val billingViewModel by viewModels<BillingViewModel>()
    private val orderViewModel by viewModels<OrderViewModel>()
    private var selectedAddress : Address?= null
    private var totalPrice : Float = 0f
    private var products : List<CartProduct> = ArrayList()
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
        totalPrice = orderPrice + 5f
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
            selectedAddress = address
        }

        lifecycleScope.launchWhenStarted {
            billingViewModel.address.collectLatest {
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
            billingViewModel.cartProducts.collectLatest {
                when(it){
                    is Resources.Loading -> {}
                    is Resources.Success -> {
                        productAdapter.differ.submitList(it.data)
                        it.data?.let{list ->
                            products = list
                        }
                    }
                    is Resources.Error -> {
                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            orderViewModel.order.collectLatest {
                when(it){
                    is Resources.Loading -> {
                        binding.btnSubmitOrder.startAnimation()
                    }
                    is Resources.Success -> {
                        binding.btnSubmitOrder.revertAnimation()
                        showTopSnackbar("Ordered successfully", requireView(), resources, 100)
                        val action = BillingFragmentDirections.actionBillingFragmentToSuccessFragment()
                        findNavController().navigate(action)
                    }
                    is Resources.Error -> {
                        binding.btnSubmitOrder.revertAnimation()
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
        binding.btnSubmitOrder.setOnClickListener {
            if(selectedAddress == null){
                Toast.makeText(requireContext(), "You must choose shipping address before submit order", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            showOrderConfirmDialog()
        }
    }

    private fun showOrderConfirmDialog() {
        val alertDialog = AlertDialog.Builder(requireContext()).apply {
            setTitle("Submit order")
            setMessage("Do you want to order your cart items?")
            setNegativeButton("Cancel"){dialog, _ ->
                dialog.dismiss()
            }
            setPositiveButton("Yes"){dialog, _ ->
                val order = Order(OrderStatus.Ordered.status, totalPrice, products, selectedAddress!!)
                orderViewModel.submitOrder(order)
                dialog.dismiss()
            }
        }
        alertDialog.create()
        alertDialog.show()
    }
}