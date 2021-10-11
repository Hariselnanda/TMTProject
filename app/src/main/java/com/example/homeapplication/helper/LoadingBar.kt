package com.example.homeapplication.helper

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import com.example.homeapplication.R

object LoadingBar {
    fun show(context: Context): ProgressDialog {
        val dialog =
            ProgressDialog(context, AlertDialog.THEME_HOLO_DARK)
        dialog.setMessage(context.resources.getString(R.string.please_wait))
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        dialog.setCancelable(false)
        return dialog
    }
}