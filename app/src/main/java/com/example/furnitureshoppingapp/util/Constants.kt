package com.example.furnitureshoppingapp.util

import android.content.res.Resources
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import com.example.furnitureshoppingapp.R
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

object Constants {
    const val USER_COLLECTION = "user"
    const val INTRODUCTION_SP = "Introduction SP"
    const val INTRODUCTION_KEY = "Introduction key"

    fun showTopSnackbar(message: String, view: View, resources: Resources, time : Int?=null) {
        var duration = 20
        if(time != null){
            duration = time
        }
        val snackbar = Snackbar.make(view, message, duration)
        val view = snackbar.view
        val params = view.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.TOP
        params.topMargin = resources.getDimension(R.dimen.edt_height).toInt()
        view.layoutParams = params
        snackbar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
        snackbar.show()
    }
}