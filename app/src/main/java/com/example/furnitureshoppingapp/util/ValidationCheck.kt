package com.example.furnitureshoppingapp.util

import android.util.Patterns
import java.util.regex.Pattern

fun validateEmail(email: String): RegisterValidation{
    if(email.isEmpty())
        return RegisterValidation.Failed("Email cannot be empty")

    if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        return RegisterValidation.Failed("Wrong email format")

    return RegisterValidation.Success
}

fun validatePassword(password: String): RegisterValidation{
    if(password.isEmpty())
        return RegisterValidation.Failed("Password cannot be empty")

    if(password.length < 8)
        return RegisterValidation.Failed("Password must have at least 8 characters")

    return RegisterValidation.Success
}

fun validateConfirmPassword(password: String, confirmPass: String): RegisterValidation{
    if(password != confirmPass)
        return RegisterValidation.Failed("Confirm password must be the same as password")

    return RegisterValidation.Success
}