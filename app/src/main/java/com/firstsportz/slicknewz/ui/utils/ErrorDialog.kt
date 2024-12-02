package com.firstsportz.slicknewz.ui.utils

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import com.firstsportz.slicknewz.R

class ErrorDialog(private val context: Context) {
    fun showErrorDialog(title: String = "Error", message: String = "Something went wrong.") {
        val dialog = Dialog(context)
        val view = LayoutInflater.from(context).inflate(R.layout.custom_error_dialog, null)
        dialog.setContentView(view)
        dialog.setCancelable(true)
        dialog.window?.setBackgroundDrawableResource(android.R.color.white)
        dialog.window?.setBackgroundDrawableResource(R.drawable.bg_rounded_dialog)

        val tvTitle = view.findViewById<TextView>(R.id.tv_error_title)
        val tvMessage = view.findViewById<TextView>(R.id.tv_error_message)
        val btnDismiss = view.findViewById<Button>(R.id.btn_error_dismiss)

        tvTitle.text = title
        tvMessage.text = message

        btnDismiss.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}