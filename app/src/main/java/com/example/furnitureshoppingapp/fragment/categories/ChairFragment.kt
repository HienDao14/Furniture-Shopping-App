package com.example.furnitureshoppingapp.fragment.categories

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.furnitureshoppingapp.R
import com.example.furnitureshoppingapp.databinding.FragmentChairBinding
import com.example.furnitureshoppingapp.model.Category
import com.example.furnitureshoppingapp.model.Product
import com.example.furnitureshoppingapp.resources.Resources
import com.example.furnitureshoppingapp.viewmodel.CategoryViewModel
import com.example.furnitureshoppingapp.viewmodel.factory.BaseCategoryViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class ChairFragment : BaseCategoryFragment() {

    @Inject
    lateinit var firestore: FirebaseFirestore
    private var onSaleProducts = ArrayList<Product>()
    private var allProducts = ArrayList<Product>()

    val viewModel by viewModels<CategoryViewModel>{
        BaseCategoryViewModelFactory(
            firestore, Category.Chair
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenStarted {
            viewModel.onSaleProducts.collectLatest {
                when(it){
                    is Resources.Loading -> {

                    }
                    is Resources.Success -> {
                        onSaleAdapter.differ.submitList(it.data)
                        if(it.data!!.isEmpty()){
                            onNoneOnSaleProducts()

                        }
                    }
                    is Resources.Error -> {
                        Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.allProducts.collectLatest {
                when(it){
                    is Resources.Loading -> {
                        showTopSnackbar()
                    }
                    is Resources.Success -> {
                        allProductsAdapter.differ.submitList(it.data)
                        if(it.data!!.isEmpty()){
                            onNoneProducts()
                        }
                    }
                    is Resources.Error -> {
                        Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
    }

    override fun onProductsPagingRequest() {
        super.onProductsPagingRequest()
        viewModel.callGetAllProducts()
    }
}