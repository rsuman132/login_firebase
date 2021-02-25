package com.rs132studio.multiplerecycler.util

import android.content.Context
import android.widget.Toast

object ToastMessage {
    fun displayToast(context: Context, message: String?){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}