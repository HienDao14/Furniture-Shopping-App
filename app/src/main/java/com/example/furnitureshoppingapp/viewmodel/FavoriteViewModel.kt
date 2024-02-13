package com.example.furnitureshoppingapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.furnitureshoppingapp.model.CartProduct
import com.example.furnitureshoppingapp.model.Product
import com.example.furnitureshoppingapp.resources.Resources
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
): ViewModel(){
    private val _favoriteProducts = MutableStateFlow<Resources<List<Product>>>(Resources.Unspecified())
    val favoriteProducts = _favoriteProducts.asStateFlow()

    private val _deleteDialog = MutableSharedFlow<Product>()
    val deleteDialog = _deleteDialog.asSharedFlow()

    private var documents = emptyList<DocumentSnapshot>()
    init{
        getFavoriteProducts()
    }

    fun getFavoriteProducts(){
        viewModelScope.launch {
            _favoriteProducts.emit(Resources.Loading())
        }
        firestore.collection("user").document(auth.uid!!).collection("favorite")
            .addSnapshotListener{value, error ->
                if(error != null || value == null){
                    viewModelScope.launch {
                        _favoriteProducts.emit(Resources.Error(error?.message.toString()))
                    }
                } else {
                    documents = value.documents
                    val favoriteProducts = value.toObjects(Product::class.java)
                    viewModelScope.launch {
                        _favoriteProducts.emit(Resources.Success(favoriteProducts))
                    }
                }
            }
    }

    fun callDeleteDialog(product: Product){
        viewModelScope.launch {
            _deleteDialog.emit(product)
        }
    }

    fun deleteFavoriteItem(product: Product){
        val index = favoriteProducts.value.data?.indexOf(product)
        if(index != null && index != -1){
            val documentId = documents[index].id
            firestore.collection("user").document(auth.uid!!)
                .collection("favorite").document(documentId).delete()
        }
    }

//    fun deleteCartItem(cartProduct: CartProduct){
//        val index = cartItem.value.data?.indexOf(cartProduct)
//        if(index != null && index != -1){
//            val documentId = documents[index].id
//            firestore.collection("user").document(auth.uid!!)
//                .collection("cart").document(documentId).delete()
//        }
//    }
}