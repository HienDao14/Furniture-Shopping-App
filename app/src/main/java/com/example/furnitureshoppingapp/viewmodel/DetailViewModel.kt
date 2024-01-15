package com.example.furnitureshoppingapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.furnitureshoppingapp.firebase.FirebaseCommon
import com.example.furnitureshoppingapp.model.CartProduct
import com.example.furnitureshoppingapp.resources.Resources
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
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
                        addNewProduct(cartProduct)
                    } else {
                        val product = it.first().toObject(CartProduct::class.java)
                        if(product!!.product == cartProduct.product){
                            //increase the quantity
                            val documentId = it.first().id
                            increaseQuantity(documentId, cartProduct)
                        } else {
                            //Add new product to cart
                            addNewProduct(cartProduct)
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

    private fun addNewProduct(cartProduct: CartProduct){
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