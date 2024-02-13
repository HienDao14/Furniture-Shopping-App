package com.example.furnitureshoppingapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.furnitureshoppingapp.model.Address
import com.example.furnitureshoppingapp.model.Order
import com.example.furnitureshoppingapp.model.User
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
class ProfileViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
): ViewModel() {

   private val _user = MutableStateFlow<Resources<User>>(Resources.Unspecified())
    val user = _user.asStateFlow()

    private val _addresses = MutableStateFlow<Resources<String>>(Resources.Unspecified())
    val addresses = _addresses.asStateFlow()

    private val _orders = MutableStateFlow<Resources<String>>(Resources.Unspecified())
    val orders = _orders.asStateFlow()
    init{
        getUserInfo()
        getAddresses()
        getOrders()
    }
    private fun getUserInfo(){
        firestore.collection("user").document(auth.uid!!).get().addOnSuccessListener {
            val userr =it.toObject(User::class.java)
            viewModelScope.launch {
                _user.emit(Resources.Success(userr!!))
            }
        }
            .addOnFailureListener {
                viewModelScope.launch {
                    _user.emit(Resources.Error(it.message.toString()))
                }
            }
    }

    private fun getAddresses(){
        firestore.collection("user").document(auth.uid!!).collection("address").get()
            .addOnSuccessListener {
                val addresses = it.toObjects(Address::class.java)
                viewModelScope.launch {
                    _addresses.emit(Resources.Success(addresses.size.toString()))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _addresses.emit(Resources.Error(it.message.toString()))
                }
            }
    }

    private fun getOrders(){
        firestore.collection("user").document(auth.uid!!).collection("orders").get()
            .addOnSuccessListener {
                val orders = it.toObjects(Order::class.java)
                viewModelScope.launch {
                    _orders.emit(Resources.Success(orders.size.toString()))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _orders.emit(Resources.Error(it.message.toString()))
                }
            }
    }
}