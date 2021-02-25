package com.rs132studio.multiplerecycler.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.Button
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.rs132studio.multiplerecycler.R
import com.rs132studio.multiplerecycler.util.HideKeyBoard
import com.rs132studio.multiplerecycler.util.ProgressButton
import com.rs132studio.multiplerecycler.util.SnackBarMessage
import com.rs132studio.multiplerecycler.util.ToastMessage
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var sEmail : TextInputLayout
    private lateinit var sPassword : TextInputLayout
    private lateinit var sConfirmPassword : TextInputLayout
    private lateinit var view : View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        firebaseAuth = FirebaseAuth.getInstance()

        firebaseAuth = FirebaseAuth.getInstance()

        sEmail = findViewById(R.id.sign_in_email)
        sPassword = findViewById(R.id.sign_in_password)
        sConfirmPassword = findViewById(R.id.sign_in_confirm_password)
        view = findViewById(R.id.signIn_button)
        val sAlready : Button = findViewById(R.id.sign_in_already)


        //setting activity to already account activity
        sAlready.setOnClickListener {
            val intent = Intent(this@SignInActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        //saving data in firebase
        val progressButton = ProgressButton(this, view)
        progressButton.buttonTitle()
        view.setOnClickListener {
            signInLoading()
        }

        //hide keyboard
        signInParentLayout.setOnClickListener {
            HideKeyBoard.hideSoftKeyBoard(this@SignInActivity, it)
        }
        signInChildLayout.setOnClickListener {
            HideKeyBoard.hideSoftKeyBoard(this@SignInActivity, it)
        }
        //hide keyboard
    }

    private fun signInLoading() {
        val progressButton = ProgressButton(this@SignInActivity, view)
        progressButton.buttonActivated()
        Handler().postDelayed({
            progressButton.buttonFinish()
            signInToFireBase()
        }, 1000)
    }


    private fun signInToFireBase() {
        val email = sEmail.editText?.text.toString()
        val password = sPassword.editText?.text.toString()
        val confirmPassword = sConfirmPassword.editText?.text.toString()
        val progressButton = ProgressButton(this@SignInActivity, view)

        when {
            TextUtils.isEmpty(email) -> {
                sEmail.error = "Email should not be blank"
                sEmail.requestFocus()
                return
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                sEmail.error = "Enter the valid email"
                sEmail.requestFocus()
                return
            }
            else -> {
                sEmail.error = null
                sEmail.isErrorEnabled = false
            }

        }


        when {
            TextUtils.isEmpty(password) -> {
                sPassword.error = "Password should not be blank"
                sPassword.requestFocus()
                return
            }
            password.length < 6 -> {
                sPassword.error = "Password must contain at least 6 character"
                sPassword.requestFocus()
                return
            }
            else -> {
                sPassword.error = null
                sPassword.isErrorEnabled = false
            }
        }


        when {
            TextUtils.isEmpty(confirmPassword) -> {
                sConfirmPassword.error = "Confirm password should not be blank"
                sConfirmPassword.requestFocus()
                return
            }
            confirmPassword != password -> {
                sConfirmPassword.error = "Confirm password doesn't matches"
                sConfirmPassword.requestFocus()
                return
            }
            else -> {
                sConfirmPassword.error = null
                sConfirmPassword.isErrorEnabled = false
            }
        }


        firebaseAuth.createUserWithEmailAndPassword(email.trim(), password.trim()).addOnCompleteListener(this, OnCompleteListener { task ->
            if (task.isSuccessful){
                SnackBarMessage.displaySnack(window.decorView.rootView, "User account created successfully")
                val intent = Intent(this, DisplayDetailActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                ToastMessage.displayToast(this, "Error ! " + task.exception?.message)
            }
        })

        progressButton.buttonTitle()

    }
}
