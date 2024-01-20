package com.example.furnitureshoppingapp.fragment.categories

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat.NestedScrollType
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.furnitureshoppingapp.R
import com.example.furnitureshoppingapp.adapter.BestDealAdapter
import com.example.furnitureshoppingapp.adapter.ProductAdapter
import com.example.furnitureshoppingapp.adapter.SpecialAdapter
import com.example.furnitureshoppingapp.databinding.FragmentPopularCategoryBinding
import com.example.furnitureshoppingapp.model.Product
import com.example.furnitureshoppingapp.resources.Resources
import com.example.furnitureshoppingapp.util.Constants.showTopSnackbar
import com.example.furnitureshoppingapp.viewmodel.PopularCategoryViewModel
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class PopularCategoryFragment : Fragment() {
    private lateinit var binding: FragmentPopularCategoryBinding
    private lateinit var specialAdapter: SpecialAdapter
    private lateinit var productAdapter: ProductAdapter
    private lateinit var bestDealAdapter: BestDealAdapter
    private val viewModel by viewModels<PopularCategoryViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPopularCategoryBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        specialAdapter = SpecialAdapter{
            navigateToDetailFragment(it)
        }
        binding.rvSpecial.adapter = specialAdapter

        productAdapter = ProductAdapter{
            navigateToDetailFragment(it)
        }
        binding.rvBestProducts.adapter = productAdapter

        bestDealAdapter = BestDealAdapter{
            navigateToDetailFragment(it)
        }
        binding.rvBestDeals.adapter = bestDealAdapter

        lifecycleScope.launchWhenStarted {
            viewModel.specialProducts.collectLatest {
                when(it){
                    is Resources.Loading ->{
                        showLoading()
                    }
                    is Resources.Success -> {
                        specialAdapter.differ.submitList(it.data)
                        hideLoading()
                    }
                    is Resources.Error -> {
                        hideLoading()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.popularProduct.collectLatest {
                when(it){
                    is Resources.Loading ->{
                        showTopSnackbar("Loading products...", requireView(), resources)
                    }
                    is Resources.Success -> {
                        productAdapter.differ.submitList(it.data)
                    }
                    is Resources.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.bestDeals.collectLatest {
                when(it){
                    is Resources.Loading ->{
                    }
                    is Resources.Success -> {
                        bestDealAdapter.differ.submitList(it.data)
                    }
                    is Resources.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }
        binding.popularScrollLayout.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
            if(v.getChildAt(0).bottom <= v.height + scrollY){
                viewModel.callGetProducts()
            }
        })
    }

    private fun hideLoading() {
        binding.specialLoading.visibility = View.GONE
    }
    private fun showLoading() {
        binding.specialLoading.visibility = View.VISIBLE
    }

    private fun navigateToDetailFragment(product: Product){
        val b = Bundle().apply { putParcelable("product", product) }
        findNavController().navigate(R.id.action_homeFragment_to_detailProductFragment, b)
    }

}