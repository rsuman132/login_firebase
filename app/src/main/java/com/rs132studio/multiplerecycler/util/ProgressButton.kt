package com.rs132studio.multiplerecycler.util

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.rs132studio.multiplerecycler.R

class ProgressButton(val context: Context, val view: View) {
    val progressCardView : CardView = view.findViewById(R.id.progress_cardView)
    val progressLinearLayout : LinearLayout = view.findViewById(R.id.progress_LinearLayout)
    val progressText : TextView = view.findViewById(R.id.progress_Text)
    val progressProgressBar : ProgressBar = view.findViewById(R.id.progress_button_progressbar)

    fun buttonActivated(){
        progressProgressBar.visibility = View.VISIBLE
        progressText.text = "Please wait..."
    }

    fun buttonFinish(){
        progressProgressBar.visibility = View.GONE
        progressText.text = "Login"
    }

    fun buttonTitle(){
        progressProgressBar.visibility = View.GONE
        progressText.text = "Sign In"
    }
}