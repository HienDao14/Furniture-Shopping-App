package com.example.furnitureshoppingapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.furnitureshoppingapp.model.Product
import com.example.furnitureshoppingapp.resources.Resources
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PopularCategoryViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
): ViewModel(){
    private val _specialProducts = MutableStateFlow<Resources<List<Product>>>(Resources.Unspecified())
    val specialProducts: StateFlow<Resources<List<Product>>> = _specialProducts

    private val _bestDeals = MutableStateFlow<Resources<List<Product>>>(Resources.Unspecified())
    val bestDeals : StateFlow<Resources<List<Product>>> = _bestDeals

    private val _popularProducts = MutableStateFlow<Resources<List<Product>>>(Resources.Unspecified())
    val popularProduct: StateFlow<Resources<List<Product>>> = _popularProducts

    private val pagingInfo = PagingInfo()
    init {
        getSpecialProduct()
        getBestDeals()
        getPopularProducts()
    }

    fun callGetProducts(){
        if(!pagingInfo.isPagingEnd){
            getPopularProducts()
        }
    }
    private fun getSpecialProduct(){
        viewModelScope.launch {
            _specialProducts.emit(Resources.Loading())
        }
        firestore.collection("products").whereEqualTo("category", "Special Products")
            .get()
            .addOnSuccessListener {result ->
                val specialProductList = result.toObjects(Product::class.java)
                viewModelScope.launch {
                    _specialProducts.emit(Resources.Success(specialProductList))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _specialProducts.emit(Resources.Error(it.message.toString()))
                }
            }
    }

    private fun getPopularProducts(){
            viewModelScope.launch {
                _popularProducts.emit(Resources.Loading())
            }
            firestore.collection("products").limit(pagingInfo.page * 5).orderBy("name", Query.Direction.ASCENDING).get()
                .addOnSuccessListener {
                    val popularProductList = it.toObjects(Product::class.java)
                    pagingInfo.isPagingEnd = popularProductList == pagingInfo.oldProducts
                    pagingInfo.oldProducts = popularProductList
                    viewModelScope.launch {
                        _popularProducts.emit(Resources.Success(popularProductList))
                    }
                    pagingInfo.page++
                }
                .addOnFailureListener {
                    viewModelScope.launch {
                        _popularProducts.emit(Resources.Error(it.message.toString()))
                    }
                }
    }

    private fun getBestDeals(){
        viewModelScope.launch {
            _bestDeals.emit(Resources.Loading())
        }
        firestore.collection("products").whereGreaterThan("offerPercentage", 0)
            .orderBy("offerPercentage", Query.Direction.DESCENDING).get()
            .addOnSuccessListener {
                val bestDeals = it.toObjects(Product::class.java)
                viewModelScope.launch {
                    _bestDeals.emit(Resources.Success(bestDeals))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _bestDeals.emit(Resources.Error(it.message.toString()))
                }
            }
    }
}

internal data class PagingInfo(
    var page: Long = 1,
    var oldProducts: List<Product> = emptyList(),
    var isPagingEnd : Boolean = false
)