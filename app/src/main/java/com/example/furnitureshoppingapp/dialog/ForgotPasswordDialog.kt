package com.example.furnitureshoppingapp.dialog

import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.furnitureshoppingapp.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

fun Fragment.setUpBottomSheetDialog(
    onSendClick: (String) -> Unit
){
    val dialog = BottomSheetDialog(requireContext(), R.style.DialogStyle)
    val view = layoutInflater.inflate(R.layout.forgot_pass_dialog, null)
    dialog.setContentView(view)
    dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
    dialog.show()

    val edtEmail = view.findViewById<EditText>(R.id.edt_forgot_email)
    val btnSend = view.findViewById<Button>(R.id.btn_send)
    val btnCancel = view.findViewById<Button>(R.id.btn_cancel)

    btnSend.setOnClickListener {
        val email = edtEmail.text.toString().trim()
        onSendClick(email)
        dialog.dismiss()
    }
    btnCancel.setOnClickListener {
        dialog.dismiss()
    }
}