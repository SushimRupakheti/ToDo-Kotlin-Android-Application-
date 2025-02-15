package com.example.to_do.utils

import android.app.AlertDialog
import com.example.to_do.R
import com.example.to_do.ui.fragment.ProfileFragment

class LoadingUtils(val activity: ProfileFragment) {
    lateinit var alertDialog: AlertDialog

    fun show() {
        // Use requireContext() to get the Context from the Fragment
        val builder = AlertDialog.Builder(activity.requireContext())

        val dialogView = activity.layoutInflater.inflate(R.layout.loading, null)

        builder.setView(dialogView)
        builder.setCancelable(false)

        alertDialog = builder.create()
        alertDialog.show()
    }

    fun dismiss() {
        alertDialog.dismiss()
    }
}
