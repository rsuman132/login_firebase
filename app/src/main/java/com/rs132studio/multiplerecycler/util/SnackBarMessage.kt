package com.rs132studio.multiplerecycler.util

import android.view.View
import com.google.android.material.snackbar.Snackbar

object SnackBarMessage {
    fun displaySnack(view: View, message : String?) : Any =
        Snackbar.make(view, message.toString(), Snackbar.LENGTH_LONG).setAction("Action", null).show()
}