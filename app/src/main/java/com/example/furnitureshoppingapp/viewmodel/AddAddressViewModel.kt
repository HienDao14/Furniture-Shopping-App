package com.example.furnitureshoppingapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.furnitureshoppingapp.model.Address
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
class AddAddressViewModel @Inject constructor(
    val firestore: FirebaseFirestore,
    val auth: FirebaseAuth
): ViewModel(){
    private val _viewState = MutableStateFlow<Resources<Address>>(Resources.Unspecified())
    val addNewAddress = _viewState.asStateFlow()

    private val _error = MutableSharedFlow<String>()
    val error = _error.asSharedFlow()
    fun addAddress(address: Address){
        val validateInputs = validateInputs(address)
        if(validateInputs){
            viewModelScope.launch {
                _viewState.emit(Resources.Loading())
            }
            firestore.collection("user").document(auth.uid!!).collection("address").document()
                .set(address)
                .addOnSuccessListener {
                    viewModelScope.launch {
                        _viewState.emit(Resources.Success(address))
                    }
                }
                .addOnFailureListener {
                    viewModelScope.launch {
                        _viewState.emit(Resources.Error(it.message.toString()))
                    }
                }
        } else {
            viewModelScope.launch {
                _error.emit("Please fill all required fields")
            }
        }
    }

    private fun validateInputs(address: Address): Boolean{
        return address.fullName.isNotEmpty()
                && address.phoneNumber.isNotEmpty()
                && address.address.isNotEmpty()
                && address.city.isNotEmpty()
                && address.district.isNotEmpty()
                && address.ward.isNotEmpty()
                && address.addressTitle.isNotEmpty()
    }
}


//
//    val format = Json {
//        ignoreUnknownKeys = true
//        prettyPrint = true
//        isLenient = true
//    }
//
//    fun getAllLocation(context: Context) = viewModelScope.launch(Dispatchers.IO) {
//        try{
//            val myJson = context.assets.open("vn_provinces.json").bufferedReader().use {
//                it.readText()
//            }
//            val locationList = format.decodeFromString<Province>(myJson)
//            viewModelScope.launch {
//                _viewState.emit(Resources.Success(locationList))
//            }
//        } catch (e: Exception){
//            viewModelScope.launch {
//                _viewState.emit(Resources.Error(e.message.toString()))
//            }
//        }
//    }