package com.example.furnitureshoppingapp.fragment.shopping

import android.graphics.Paint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.furnitureshoppingapp.R
import com.example.furnitureshoppingapp.adapter.ColorAdapter
import com.example.furnitureshoppingapp.adapter.ViewPager2ImageAdapter
import com.example.furnitureshoppingapp.databinding.FragmentDetailProductBinding
import com.example.furnitureshoppingapp.model.Product


class DetailProductFragment : Fragment() {
    private val navArgs by navArgs<DetailProductFragmentArgs>()
    private lateinit var binding: FragmentDetailProductBinding
    private val viewPagerAdapter by lazy{ ViewPager2ImageAdapter() }
    private val colorAdapter by lazy{ ColorAdapter() }
    private lateinit var product : Product
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


}