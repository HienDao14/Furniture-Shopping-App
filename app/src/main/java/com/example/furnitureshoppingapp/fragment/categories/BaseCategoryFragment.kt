package com.example.furnitureshoppingapp.fragment.categories

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.widget.NestedScrollView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.furnitureshoppingapp.R
import com.example.furnitureshoppingapp.adapter.BestDealAdapter
import com.example.furnitureshoppingapp.adapter.ProductAdapter
import com.example.furnitureshoppingapp.databinding.FragmentBaseCategoryBinding
import com.example.furnitureshoppingapp.model.Product
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar


open class BaseCategoryFragment : Fragment(R.layout.fragment_base_category) {

    private lateinit var binding: FragmentBaseCategoryBinding
    protected val onSaleAdapter : BestDealAdapter by lazy {
        BestDealAdapter{
            navigateToDetailFragment(it)
        }
    }
    protected val allProductsAdapter: ProductAdapter by lazy {
        ProductAdapter{
            navigateToDetailFragment(it)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBaseCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvOnSale.apply{
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = onSaleAdapter
        }
        binding.rvProducts.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = allProductsAdapter
        }

        binding.nestedBaseLayout.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
            if(v.getChildAt(0).bottom <= v.height + scrollY){
                onProductsPagingRequest()
            }
        })

    }

    open fun onProductsPagingRequest(){

    }
    fun showTopSnackbar() {
        val snackbar = Snackbar.make(requireView(), "Loading products...", 10)
        val view = snackbar.view
        val params = view.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.TOP
        params.topMargin = resources.getDimension(R.dimen.edt_height).toInt()
        view.layoutParams = params
        snackbar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
        snackbar.show()
    }

    open fun onNoneOnSaleProducts(){
        binding.tvOnSale.visibility = View.GONE
        binding.rvOnSale.visibility = View.GONE
    }

    open fun onNoneProducts(){
        binding.tvOnSale.visibility = View.GONE
        binding.rvProducts.visibility = View.GONE
        binding.rvOnSale.visibility = View.GONE
        binding.tvNoProduct.visibility = View.VISIBLE
    }
    private fun navigateToDetailFragment(product: Product){
        val b = Bundle().apply { putParcelable("product", product) }
        findNavController().navigate(R.id.action_homeFragment_to_detailProductFragment, b)
    }
}