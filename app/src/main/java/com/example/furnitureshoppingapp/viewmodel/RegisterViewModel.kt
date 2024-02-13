package com.example.furnitureshoppingapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.furnitureshoppingapp.model.User
import com.example.furnitureshoppingapp.resources.Resources
import com.example.furnitureshoppingapp.util.Constants.USER_COLLECTION
import com.example.furnitureshoppingapp.util.RegisterFieldsState
import com.example.furnitureshoppingapp.util.RegisterValidation
import com.example.furnitureshoppingapp.util.validateConfirmPassword
import com.example.furnitureshoppingapp.util.validateEmail
import com.example.furnitureshoppingapp.util.validatePassword
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val db: FirebaseFirestore
): ViewModel() {

    private val _register = MutableStateFlow<Resources<User>>(Resources.Unspecified())
    val register : Flow<Resources<User>> = _register

    private val _validation = Channel<RegisterFieldsState>()
    val validation = _validation.receiveAsFlow()
    fun createAccountWithEmailAndPass(user: User, password: String, confirmPass: String){
        if(checkValidation(user, password, confirmPass)) {
            runBlocking {
                _register.emit(Resources.Loading())
            }
            firebaseAuth.createUserWithEmailAndPassword(user.email, password)
                .addOnSuccessListener {
                    it.user?.let {
                        saveUserInfo(it.uid, user)
                    }
                }
                .addOnFailureListener {
                }
        } else {
            val registerFieldsState = RegisterFieldsState(
                validateEmail(user.email), validatePassword(password), validateConfirmPassword(password, confirmPass)
            )
            runBlocking {
                _validation.send(registerFieldsState)
            }
        }
    }

    private fun saveUserInfo(userUId: String, user: User) {
        db.collection(USER_COLLECTION)
            .document(userUId)
            .set(user)
            .addOnSuccessListener {
                _register.value = Resources.Success(user)

            }
            .addOnFailureListener {
                _register.value = Resources.Error(it.message.toString())
            }
    }

    private fun checkValidation(user: User, password: String, confirmPass: String) : Boolean{
        val emailValidation = validateEmail(user.email)
        val passwordValidation = validatePassword(password)
        val confirmPasswordValidation = validateConfirmPassword(password, confirmPass)
        val shouldRegister = emailValidation is RegisterValidation.Success &&
                passwordValidation is RegisterValidation.Success &&
                confirmPasswordValidation is RegisterValidation.Success

        return shouldRegister
    }
}