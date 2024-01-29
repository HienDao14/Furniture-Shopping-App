package com.example.furnitureshoppingapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.furnitureshoppingapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
): ViewModel() {

    private val _name = MutableSharedFlow<String>()
    val name = _name.asSharedFlow()

    private val _email = MutableSharedFlow<String>()
    val email = _email.asSharedFlow()

    private val _img = MutableSharedFlow<String>()
    val img = _img.asSharedFlow()

    init{
        getUserInfo()
    }
    private fun getUserInfo(){
        firestore.collection("user").document(auth.uid!!).get().addOnSuccessListener {
            val user = it.toObject(User::class.java)
            viewModelScope.launch {
                _name.emit(user!!.userName)
                _email.emit(user.email)
                _img.emit(user.imgPath)
            }
        }
            .addOnFailureListener {
                viewModelScope.launch {
                    _name.emit(it.message.toString())
                    _email.emit(it.message.toString())
                    _img.emit(it.message.toString())
                }
            }
    }

}