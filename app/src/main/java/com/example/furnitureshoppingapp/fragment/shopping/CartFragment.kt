package com.example.furnitureshoppingapp.fragment.shopping

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.furnitureshoppingapp.R
import com.example.furnitureshoppingapp.adapter.CartAdapter
import com.example.furnitureshoppingapp.databinding.FragmentCartBinding
import com.example.furnitureshoppingapp.firebase.FirebaseCommon
import com.example.furnitureshoppingapp.model.Product
import com.example.furnitureshoppingapp.resources.Resources
import com.example.furnitureshoppingapp.viewmodel.CartViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class CartFragment : Fragment() {
    private lateinit var binding: FragmentCartBinding
    private val viewModel by viewModels<CartViewModel>()
    private val cartAdapter: CartAdapter by lazy { CartAdapter() }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvCart.apply{
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = cartAdapter
        }

        setOnItemClick()

        lifecycleScope.launch {
            viewModel.totalPrice.collectLatest {price ->
                price?.let {
                    val price = String.format("%.2f", it)
                    binding.tvTotalPrice.text = "$ $price"
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.cartItem.collectLatest {
                when(it){
                    is Resources.Loading -> {
                        binding.cartLoading.visibility = View.VISIBLE
                    }
                    is Resources.Success -> {
                        binding.cartLoading.visibility = View.GONE
                        if(it.data!!.isEmpty()){
                            showEmptyCart()
                        }
                        else {
                            hideEmptyCart()
                            cartAdapter.differ.submitList(it.data)
                        }
                    }
                    is Resources.Error -> {
                        binding.cartLoading.visibility = View.GONE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        binding.cartLoading.visibility = View.GONE
                    }
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.deleteDialog.collectLatest {
                val alertDialog = AlertDialog.Builder(requireContext()).apply {
                    setTitle("Delete product from cart")
                    setMessage("Do you want to delete this product from cart?")
                    setNegativeButton("Cancel"){dialog, _ ->
                        dialog.dismiss()
                    }
                    setPositiveButton("Yes"){dialog, _ ->
                        viewModel.deleteCartItem(it)
                        dialog.dismiss()
                    }
                }
                alertDialog.create()
                alertDialog.show()
            }
        }
    }

    private fun setOnItemClick() {
        cartAdapter.onProductClick = {
            navigateToDetailFragment(it.product)
        }
        cartAdapter.onIncreaseClick = {
            viewModel.changeQuantity(it, FirebaseCommon.QuantityChanging.INCREASE)
        }
        cartAdapter.onDecreaseClick = {
            viewModel.changeQuantity(it, FirebaseCommon.QuantityChanging.DECREASE)
        }
    }

    private fun hideEmptyCart() {
        binding.rvCart.visibility = View.VISIBLE
        binding.textLayout.visibility = View.VISIBLE
        binding.tvTotal.visibility = View.VISIBLE
        binding.tvTotalPrice.visibility = View.VISIBLE
        binding.btnCheckOut.visibility = View.VISIBLE
        binding.btnCheckPromo.visibility = View.VISIBLE
        binding.emptyLayout.visibility = View.GONE
    }

    private fun showEmptyCart() {
        binding.rvCart.visibility = View.GONE
        binding.textLayout.visibility = View.GONE
        binding.tvTotal.visibility = View.GONE
        binding.tvTotalPrice.visibility = View.GONE
        binding.btnCheckOut.visibility = View.GONE
        binding.btnCheckPromo.visibility = View.GONE
        binding.emptyLayout.visibility = View.VISIBLE
    }

    private fun navigateToDetailFragment(product: Product){
        val b = Bundle().apply { putParcelable("product", product) }
        findNavController().navigate(R.id.action_cartFragment_to_detailProductFragment, b)
    }

}