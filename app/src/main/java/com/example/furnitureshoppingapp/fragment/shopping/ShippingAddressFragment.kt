package com.example.furnitureshoppingapp.fragment.shopping

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.furnitureshoppingapp.R
import com.example.furnitureshoppingapp.databinding.FragmentShippingAddressBinding


class ShippingAddressFragment : Fragment() {
    private lateinit var binding : FragmentShippingAddressBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentShippingAddressBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }
}