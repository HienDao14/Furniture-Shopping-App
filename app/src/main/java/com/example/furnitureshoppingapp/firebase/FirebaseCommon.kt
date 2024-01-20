package com.example.furnitureshoppingapp.firebase

import com.example.furnitureshoppingapp.model.CartProduct
import com.example.furnitureshoppingapp.model.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseCommon(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    private val cartCollection = firestore.collection("user").document(auth.uid!!).collection("cart")
    private val favoriteCollection = firestore.collection("user").document(auth.uid!!).collection("favorite")

    fun addNewItemToCart(cartProduct: CartProduct, onResult: (CartProduct?, Exception?) -> Unit){
        cartCollection.document().set(cartProduct)
            .addOnSuccessListener {
                onResult(cartProduct, null)
            }
            .addOnFailureListener {
                onResult(null, it)
            }
    }

    fun addNewItemToFavorite(product: Product, onResult: (Product?, Exception?) -> Unit){
        favoriteCollection.document().set(product)
            .addOnSuccessListener {
                onResult(product, null)
            }
            .addOnFailureListener {
                onResult(null, it)
            }
    }

    fun increaseQuantity(documentId: String,qty: Int, onResult: (String?, Exception?) -> Unit){
        firestore.runTransaction {transaction ->
            val documentRef = cartCollection.document(documentId)
            val document = transaction.get(documentRef)
            val productObject = document.toObject(CartProduct::class.java)
            productObject?.let {cartProduct ->
                val newQuantity = cartProduct.quantity + qty
                val newProductObject = cartProduct.copy(quantity = newQuantity)
                transaction.set(documentRef, newProductObject)
            }
        }
            .addOnSuccessListener {
                onResult(documentId, null)
            }
            .addOnFailureListener {
                onResult(null, it)
            }
    }

    fun decreaseQuantity(documentId: String,qty: Int, onResult: (String?, Exception?) -> Unit){
        firestore.runTransaction {transaction ->
            val documentRef = cartCollection.document(documentId)
            val document = transaction.get(documentRef)
            val productObject = document.toObject(CartProduct::class.java)
            productObject?.let {cartProduct ->
                val newQuantity = cartProduct.quantity - qty
                val newProductObject = cartProduct.copy(quantity = newQuantity)
                transaction.set(documentRef, newProductObject)
            }
        }
            .addOnSuccessListener {
                onResult(documentId, null)
            }
            .addOnFailureListener {
                onResult(null, it)
            }
    }

    enum class QuantityChanging{
        INCREASE, DECREASE
    }
}