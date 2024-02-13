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
import com.example.furnitureshoppingapp.R
import com.example.furnitureshoppingapp.adapter.FavoriteAdapter
import com.example.furnitureshoppingapp.databinding.FragmentFavoriteBinding
import com.example.furnitureshoppingapp.model.Product
import com.example.furnitureshoppingapp.resources.Resources
import com.example.furnitureshoppingapp.viewmodel.FavoriteViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class FavoriteFragment : Fragment() {
    private lateinit var binding: FragmentFavoriteBinding
    private val favoriteAdapter : FavoriteAdapter by lazy { FavoriteAdapter() }
    private val viewModel by viewModels<FavoriteViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setOnItemClick()
        binding.rvFavorite.adapter = favoriteAdapter

        binding.topAppBar.setOnMenuItemClickListener {item ->
            if(item.itemId == R.id.cartFragment){
                findNavController().navigate(R.id.action_favoriteFragment_to_cartFragment)
            }
            true
        }

        lifecycleScope.launch {
            viewModel.favoriteProducts.collectLatest {
                when(it){
                    is Resources.Loading -> {

                    }
                    is Resources.Success -> {
                        favoriteAdapter.differ.submitList(it.data)
                    }
                    is Resources.Error -> {
                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.deleteDialog.collectLatest {
                val alertDialog = AlertDialog.Builder(requireContext()).apply {
                    setTitle("Remove product from favorite")
                    setMessage("Do you want to remove this product from favorite?")
                    setNegativeButton("Cancel"){dialog, _ ->
                        dialog.dismiss()
                    }
                    setPositiveButton("Yes"){dialog, _ ->
                        viewModel.deleteFavoriteItem(it)
                        dialog.dismiss()
                    }
                }
                alertDialog.create()
                alertDialog.show()
            }
        }

        binding.btnAddAll.setOnClickListener {

        }
    }

    private fun setOnItemClick() {
        favoriteAdapter.onProductClick = {
            navigateToDetailFragment(it)
        }
        favoriteAdapter.onDeleteClick = {
            viewModel.callDeleteDialog(it)
        }
    }

    private fun navigateToDetailFragment(product: Product){
        val b = Bundle().apply { putParcelable("product", product) }
        findNavController().navigate(R.id.action_favoriteFragment_to_detailProductFragment, b)
    }
}