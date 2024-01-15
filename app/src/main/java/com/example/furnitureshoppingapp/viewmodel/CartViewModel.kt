package com.example.furnitureshoppingapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.furnitureshoppingapp.firebase.FirebaseCommon
import com.example.furnitureshoppingapp.model.CartProduct
import com.example.furnitureshoppingapp.resources.Resources
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseCommon: FirebaseCommon
): ViewModel() {

    private val _cartItem = MutableStateFlow<Resources<List<CartProduct>>>(Resources.Unspecified())
    val cartItem = _cartItem.asStateFlow()

    private val _deleteDialog = MutableSharedFlow<CartProduct>()
    val deleteDialog = _deleteDialog.asSharedFlow()

    private var documents = emptyList<DocumentSnapshot>()
    init {
        getCartItem()
    }

    val totalPrice = cartItem.map{
        when(it){
            is Resources.Success -> {
                calculatePrice(it.data!!)
            }
            else -> null
        }
    }

    private fun calculatePrice(data: List<CartProduct>): Float{
        var total = 0.0
        data.forEach{cartProduct ->
            var price = cartProduct.product.price
            if(cartProduct.product.offerPercentage != null && cartProduct.product.offerPercentage > 0){
                price *= (1 - cartProduct.product.offerPercentage / 100)
            }
            total += price * cartProduct.quantity
        }
        return total.toFloat()
    }


    fun changeQuantity(
        cartProduct: CartProduct,
        quantityChanging: FirebaseCommon.QuantityChanging
    ){
        val index = cartItem.value.data?.indexOf(cartProduct)
        if(index != null && index != -1){
            val documentId = documents[index].id
            when(quantityChanging){
                FirebaseCommon.QuantityChanging.INCREASE -> {
                    viewModelScope.launch {
                        _cartItem.emit(Resources.Loading())
                    }
                    increaseQuantity(documentId)
                }
                FirebaseCommon.QuantityChanging.DECREASE -> {
                    if(cartProduct.quantity == 1){
                        viewModelScope.launch {
                            _deleteDialog.emit(cartProduct)
                        }
                        return
                    }
                    viewModelScope.launch {
                        _cartItem.emit(Resources.Loading())
                    }
                    decreaseQuantity(documentId)
                }
            }
        }
    }

    private fun decreaseQuantity(documentId: String) {
        firebaseCommon.decreaseQuantity(documentId, 1){_, e ->
            if(e != null){
                viewModelScope.launch {
                    _cartItem.emit(Resources.Error(e.message.toString()))
                }
            }
        }
    }

    private fun increaseQuantity(documentId: String) {
        firebaseCommon.increaseQuantity(documentId, 1){_, e ->
            if(e != null){
                viewModelScope.launch {
                    _cartItem.emit(Resources.Error(e.message.toString()))
                }
            }
        }
    }

    fun deleteCartItem(cartProduct: CartProduct){
        val index = cartItem.value.data?.indexOf(cartProduct)
        if(index != null && index != -1){
            val documentId = documents[index].id
            firestore.collection("user").document(auth.uid!!)
                .collection("cart").document(documentId).delete()
        }
    }

    fun getCartItem(){
        viewModelScope.launch {
            _cartItem.emit(Resources.Loading())
        }
        firestore.collection("user").document(auth.uid!!).collection("cart")
            .addSnapshotListener { value, error ->
                if(error != null || value == null){
                    viewModelScope.launch {
                        _cartItem.emit(Resources.Error(error?.message.toString()))
                    }
                } else {
                    documents = value.documents
                    val cartProducts = value.toObjects(CartProduct::class.java)
                    viewModelScope.launch {
                        _cartItem.emit(Resources.Success(cartProducts))
                    }
                }
            }
    }

}