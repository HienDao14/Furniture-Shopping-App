package com.example.furnitureshoppingapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.furnitureshoppingapp.firebase.FirebaseCommon
import com.example.furnitureshoppingapp.model.CartProduct
import com.example.furnitureshoppingapp.model.Product
import com.example.furnitureshoppingapp.resources.Resources
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val fireAuth: FirebaseAuth,
    private val firebaseCommon: FirebaseCommon
): ViewModel() {
    private val _addToCart = MutableStateFlow<Resources<CartProduct>>(Resources.Unspecified())
    val addToCart = _addToCart.asStateFlow()

    private val _addToFavorite = MutableStateFlow<Resources<Product>>(Resources.Unspecified())
    val addToFavorite = _addToFavorite.asStateFlow()

    private val _checkInFavorite = MutableSharedFlow<Boolean>()
    val checkInFavorite = _checkInFavorite.asSharedFlow()

    fun addUpdateProductInCart(cartProduct: CartProduct){
        viewModelScope.launch {
            _addToCart.emit(Resources.Loading())
        }
        firestore.collection("user").document(fireAuth.uid!!).collection("cart")
            .whereEqualTo("product.id", cartProduct.product.id).get()
            .addOnSuccessListener {
                it.documents.let{
                    if(it.isEmpty()){
                        //Add new product to cart
                        addNewProductToCart(cartProduct)
                    } else {
                        val product = it.first().toObject(CartProduct::class.java)
                        if(product!!.product == cartProduct.product){
                            //increase the quantity
                            val documentId = it.first().id
                            increaseQuantity(documentId, cartProduct)
                        } else {
                            //Add new product to cart
                            addNewProductToCart(cartProduct)
                        }
                    }
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _addToCart.emit(Resources.Error(it.message.toString()))
                }
            }
    }

    fun addProductToFavorite(product: Product){
        viewModelScope.launch {
            _addToFavorite.emit(Resources.Loading())
        }
        firestore.collection("user").document(fireAuth.uid!!).collection("favorite")
            .whereEqualTo("id", product.id).get()
            .addOnSuccessListener {
                viewModelScope.launch {
                    _checkInFavorite.emit(false)
                }
                if(it.isEmpty){
                   addNewProductToFavorite(product)
                    Log.d("DetailViewModel", "don't")
                } else{
                    viewModelScope.launch {
                        _checkInFavorite.emit(true)
                    }
                    Log.d("DetailViewModel", "already")
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _addToFavorite.emit(Resources.Error(it.message.toString()))
                }
            }
    }

    private fun addNewProductToFavorite(product: Product){
        firebaseCommon.addNewItemToFavorite(product){product, e ->
            viewModelScope.launch {
                if(e == null){
                    _addToFavorite.emit(Resources.Success(product!!))
                } else {
                    _addToFavorite.emit(Resources.Error(e.message.toString()))
                }
            }
        }
    }

    private fun addNewProductToCart(cartProduct: CartProduct){
        firebaseCommon.addNewItemToCart(cartProduct){addedProduct, e ->
            viewModelScope.launch {
                if(e == null){
                    _addToCart.emit(Resources.Success(addedProduct!!))
                } else {
                    _addToCart.emit(Resources.Error(e.message.toString()))
                }
            }
        }
    }

    private fun increaseQuantity(documentId: String, cartProduct: CartProduct){
        firebaseCommon.increaseQuantity(documentId, cartProduct.quantity){_, e ->
            viewModelScope.launch {
                if(e == null){
                    _addToCart.emit(Resources.Success(cartProduct))
                } else {
                    _addToCart.emit(Resources.Error(e.message.toString()))
                }
            }
        }
    }
}