package com.example.furnitureshoppingapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.furnitureshoppingapp.model.Category
import com.example.furnitureshoppingapp.model.Product
import com.example.furnitureshoppingapp.resources.Resources
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CategoryViewModel constructor(
    private val firestore: FirebaseFirestore,
    private val category: Category
): ViewModel() {

    private val _onSaleProducts = MutableStateFlow<Resources<List<Product>>>(Resources.Unspecified())
    val onSaleProducts: StateFlow<Resources<List<Product>>> = _onSaleProducts

    private val _allProducts = MutableStateFlow<Resources<List<Product>>>(Resources.Unspecified())
    val allProducts: StateFlow<Resources<List<Product>>> = _allProducts

    private val pagingInfo = PagingInfo()
    init{
        getOnSaleProducts()
        getAllProducts()
    }

    fun callGetAllProducts(){
        if(!pagingInfo.isPagingEnd){
            getAllProducts()
        }
    }

    private fun getOnSaleProducts(){
        viewModelScope.launch {
            _onSaleProducts.emit(Resources.Loading())
        }
        firestore.collection("products").whereEqualTo("category", category.category)
            .whereGreaterThan("offerPercentage", 0)
            .orderBy("offerPercentage", Query.Direction.DESCENDING).get()
            .addOnSuccessListener {
                val onSaleProducts = it.toObjects(Product::class.java)
                viewModelScope.launch {
                    _onSaleProducts.emit(Resources.Success(onSaleProducts))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _onSaleProducts.emit(Resources.Error(it.message.toString()))
                }
            }
    }

    private fun getAllProducts(){
        viewModelScope.launch {
            _allProducts.emit(Resources.Loading())
        }
        firestore.collection("products").whereEqualTo("category", category.category)
            .orderBy("price", Query.Direction.ASCENDING)
            .limit(pagingInfo.page * 3).get()
            .addOnSuccessListener {
                val allProducts = it.toObjects(Product::class.java)
                pagingInfo.isPagingEnd = allProducts == pagingInfo.oldProducts
                pagingInfo.oldProducts = allProducts
                viewModelScope.launch {
                    _allProducts.emit(Resources.Success(allProducts))
                }
                pagingInfo.page++
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _allProducts.emit(Resources.Error(it.message.toString()))
                }
            }
    }
}

