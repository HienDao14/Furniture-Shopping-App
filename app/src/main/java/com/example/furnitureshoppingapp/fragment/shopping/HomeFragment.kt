package com.example.furnitureshoppingapp.fragment.shopping

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.example.furnitureshoppingapp.R
import com.example.furnitureshoppingapp.adapter.HomeViewPagerAdapter
import com.example.furnitureshoppingapp.databinding.FragmentHomeBinding
import com.example.furnitureshoppingapp.fragment.categories.AccessoryFragment
import com.example.furnitureshoppingapp.fragment.categories.ChairFragment
import com.example.furnitureshoppingapp.fragment.categories.CupboardFragment
import com.example.furnitureshoppingapp.fragment.categories.FurnitureFragment
import com.example.furnitureshoppingapp.fragment.categories.PopularCategoryFragment
import com.example.furnitureshoppingapp.fragment.categories.TableFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoriesFragments = arrayListOf<Fragment>(
            PopularCategoryFragment(),
            ChairFragment(),
            TableFragment(),
            CupboardFragment(),
            AccessoryFragment(),
            FurnitureFragment()
        )

        val viewPagerAdapter = HomeViewPagerAdapter(
            categoriesFragments,
            childFragmentManager,
            lifecycle
        )
        binding.viewPagerHome.adapter = viewPagerAdapter
        TabLayoutMediator(binding.tabLayout, binding.viewPagerHome){tab, position ->
            when(position){
                0 -> tab.text = "Popular"
                1 -> tab.text = "Chair"
                2 -> tab.text = "Table"
                3 -> tab.text = "Cupboard"
                4 -> tab.text = "Accessory"
                5 -> tab.text = "Furniture"
            }
        }.attach()
        binding.viewPagerHome.isUserInputEnabled = false

        binding.homeTopAppBar.setOnMenuItemClickListener {
            if(it.itemId == R.id.cartFragment){
                findNavController().navigate(R.id.action_homeFragment_to_cartFragment)
            }
            true
        }
        binding.homeTopAppBar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }
    }
}