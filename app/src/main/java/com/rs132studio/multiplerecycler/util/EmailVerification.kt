package com.rs132studio.multiplerecycler.util

import android.content.Context
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

object EmailVerification {
    private lateinit var auth : FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser
    fun verifyEmail(context: Context, view: View){
        auth = FirebaseAuth.getInstance()
        firebaseUser = auth.currentUser!!
        firebaseUser.sendEmailVerification().addOnSuccessListener {
           ToastMessage.displayToast(context, "Verification Email sent")
        }.addOnFailureListener {
            ToastMessage.displayToast(context, "Error Occurred $it.message")
        }
    }
}