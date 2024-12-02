package com.firstsportz.slicknewz.ui.utils

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import com.firstsportz.slicknewz.R

class LoadingDialog(context: Context) {
    private val dialog: Dialog = Dialog(context)

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.custom_loading_dialog, null)
        dialog.setContentView(view)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    fun show(message: String? = "Loading...") {
        val textView = dialog.findViewById<TextView>(R.id.tv_loading_message)
        textView.text = message
        dialog.show()
    }

    fun dismiss() {
        if (dialog.isShowing) {
            dialog.dismiss()
        }
    }
}