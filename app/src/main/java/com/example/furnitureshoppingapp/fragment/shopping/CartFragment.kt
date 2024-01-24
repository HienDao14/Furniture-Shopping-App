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
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.Navigator
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.furnitureshoppingapp.R
import com.example.furnitureshoppingapp.adapter.CartAdapter
import com.example.furnitureshoppingapp.databinding.FragmentCartBinding
import com.example.furnitureshoppingapp.firebase.FirebaseCommon
import com.example.furnitureshoppingapp.model.Product
import com.example.furnitureshoppingapp.model.Voucher
import com.example.furnitureshoppingapp.resources.Resources
import com.example.furnitureshoppingapp.util.Constants.showTopSnackbar
import com.example.furnitureshoppingapp.viewmodel.CartViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class CartFragment : Fragment() {
    private lateinit var binding: FragmentCartBinding
    private val listVoucher : ArrayList<Voucher> = ArrayList()
    private val viewModel by viewModels<CartViewModel>()
    var totalPrice : Float = 0f
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

        setOnLayoutClick()
        setOnItemClick()

        lifecycleScope.launch {
            viewModel.totalPrice.collectLatest {price ->
                price?.let {
                    totalPrice = it
                    val total = String.format("%.2f", it)
                    binding.tvTotalPrice.text = "$ $total"
                }
            }
        }

        lifecycleScope.launch {
            viewModel.voucher.collectLatest {vouchers ->
                vouchers.data?.let {
                    listVoucher.addAll(it)
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
    private fun setOnLayoutClick() {
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.btnCheckVoucher.setOnClickListener{
            val voucherText = binding.edtVoucher.text.toString()
            listVoucher.forEach {voucher ->
                if(voucherText.lowercase() == voucher.text.lowercase()){
                    binding.tvDiscountApply.visibility = View.VISIBLE
                    binding.tvDiscountApply.text = "Apply voucher successfully!!!\nYou save ${voucher.discount} ${voucher.unit} for your bill!!!"
                    if(voucher.unit == "%"){
                        totalPrice -= totalPrice * voucher.discount / 100
                    } else {
                        totalPrice -= voucher.discount
                        if(totalPrice < 0) totalPrice = 0f
                    }
                    val textTotal ="$ " + String.format("%.1f", totalPrice)
                    binding.tvTotalPrice.text = textTotal
                    showTopSnackbar("Your voucher added successfully!!!", requireView(), resources)
                    return@setOnClickListener
                }
            }
            binding.tvDiscountApply.visibility = View.GONE
            showTopSnackbar("Your voucher is not available or is outdated", requireView(), resources)
        }
        binding.btnCheckOut.setOnClickListener {
            val action = CartFragmentDirections.actionCartFragmentToBillingFragment(totalPrice)
            findNavController().navigate(action)
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
        cartAdapter.onDeleteClick = {
            viewModel.callDeleteProduct(it)
        }
    }

    private fun hideEmptyCart() {
        binding.rvCart.visibility = View.VISIBLE
        binding.textLayout.visibility = View.VISIBLE
        binding.tvTotal.visibility = View.VISIBLE
        binding.tvTotalPrice.visibility = View.VISIBLE
        binding.btnCheckOut.visibility = View.VISIBLE
        binding.btnCheckVoucher.visibility = View.VISIBLE
        binding.emptyLayout.visibility = View.GONE
    }

    private fun showEmptyCart() {
        binding.rvCart.visibility = View.GONE
        binding.textLayout.visibility = View.GONE
        binding.tvTotal.visibility = View.GONE
        binding.tvTotalPrice.visibility = View.GONE
        binding.btnCheckOut.visibility = View.GONE
        binding.btnCheckVoucher.visibility = View.GONE
        binding.emptyLayout.visibility = View.VISIBLE
    }

    private fun navigateToDetailFragment(product: Product){
        val b = Bundle().apply { putParcelable("product", product) }
        findNavController().navigate(R.id.action_cartFragment_to_detailProductFragment, b)
    }

}