package com.example.furnitureshoppingapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.furnitureshoppingapp.model.Address
import com.example.furnitureshoppingapp.resources.Resources
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShippingAddressViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
): ViewModel() {

    private val _address = MutableStateFlow<Resources<List<Address>>>(Resources.Unspecified())
    val address = _address.asStateFlow()

    init{
        getAddresses()
    }
    private fun getAddresses(){
        viewModelScope.launch {
            _address.emit(Resources.Loading())
        }
        firestore.collection("user").document(auth.uid!!).collection("address")
            .addSnapshotListener { value, error ->
                if(error != null || value == null){
                    viewModelScope.launch {
                        _address.emit(Resources.Error(error?.message.toString()))
                    }
                } else {
                    val allAddress = value.toObjects(Address::class.java)
                    viewModelScope.launch {
                        _address.emit(Resources.Success(allAddress))
                    }
                }
            }
    }
}