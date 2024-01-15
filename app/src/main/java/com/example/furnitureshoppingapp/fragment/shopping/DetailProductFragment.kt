package com.example.furnitureshoppingapp.fragment.shopping

import android.graphics.Paint
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.get
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.furnitureshoppingapp.R
import com.example.furnitureshoppingapp.adapter.ColorAdapter
import com.example.furnitureshoppingapp.adapter.ViewPager2ImageAdapter
import com.example.furnitureshoppingapp.databinding.FragmentDetailProductBinding
import com.example.furnitureshoppingapp.model.CartProduct
import com.example.furnitureshoppingapp.model.Product
import com.example.furnitureshoppingapp.resources.Resources
import com.example.furnitureshoppingapp.viewmodel.DetailViewModel
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class DetailProductFragment : Fragment() {
    private val navArgs by navArgs<DetailProductFragmentArgs>()
    private lateinit var binding: FragmentDetailProductBinding
    private val viewPagerAdapter by lazy{ ViewPager2ImageAdapter() }
    private val colorAdapter by lazy{ ColorAdapter() }
    private lateinit var product : Product
    private var selectedColor: String? = null
    private val viewModel by viewModels<DetailViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailProductBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        product = navArgs.product

        binding.rvColor.adapter = colorAdapter
        binding.viewPagerImagesProduct.adapter = viewPagerAdapter

        bindView()
        viewPagerAdapter.differ.submitList(product.images)
        product.colors?.let {
            colorAdapter.differ.submitList(it)
        }

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
        colorAdapter.onItemClick = {
            selectedColor = it
        }

        binding.ivAdd.setOnClickListener {
            binding.tvCount.text = String.format("%02d", (binding.tvCount.text.toString().toInt() + 1))
        }
        binding.ivSubtract.setOnClickListener {
            if(binding.tvCount.text.toString().toInt() > 1){
                binding.tvCount.text = String.format("%02d", (binding.tvCount.text.toString().toInt() - 1))
            }
        }
        binding.btnAddToCart.setOnClickListener {
            val qty = binding.tvCount.text.toString().toInt()
            viewModel.addUpdateProductInCart(CartProduct(product, qty, selectedColor))
        }
        lifecycleScope.launchWhenStarted {
            viewModel.addToCart.collectLatest {
                when(it){
                    is Resources.Loading -> {
                    }
                    is Resources.Success -> {
                        showTopSnackbar("Added ${it.data!!.quantity} products to cart!!!")
                    }
                    is Resources.Error -> {
                        showTopSnackbar(it.message.toString())
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun bindView() {
        binding.apply{
            tvDetailProductName.text = product.name
            if(product.offerPercentage!! > 0){
                val currentPrice = product.price * (1 - product.offerPercentage!! / 100)
                tvDetailProductOldPrice.visibility = View.VISIBLE
                tvDetailProductOldPrice.text = "$ ${product.price}"
                tvDetailProductOldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                tvDetailProductPrice.text = "$ ${currentPrice}"
            } else {
                tvDetailProductPrice.text = "$ ${product.price}"
            }
            tvDetailProductDescription.text = product.description
        }
    }

    private fun showTopSnackbar(message: String) {
        val snackbar = Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT)
        val view = snackbar.view
        val params = view.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.TOP
        params.topMargin = resources.getDimension(R.dimen.edt_height).toInt()
        view.layoutParams = params
        snackbar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
        snackbar.show()
    }
}