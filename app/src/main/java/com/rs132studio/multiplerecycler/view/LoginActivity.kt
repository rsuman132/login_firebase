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
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.rs132studio.multiplerecycler.R
import com.rs132studio.multiplerecycler.util.Constant.Companion.PASSWORD_PATTERN
import com.rs132studio.multiplerecycler.util.HideKeyBoard
import com.rs132studio.multiplerecycler.util.ProgressButton
import com.rs132studio.multiplerecycler.util.SnackBarMessage
import com.rs132studio.multiplerecycler.util.ToastMessage
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var view : View
    private lateinit var lUserName : TextInputLayout
    private lateinit var lPassword : TextInputLayout
    private lateinit var loginForgetPassword : Button
    private lateinit var loginAlready : Button
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContentView(R.layout.activity_login)

        lUserName = findViewById(R.id.login_username)
        lPassword = findViewById(R.id.login_password)
        loginForgetPassword = findViewById(R.id.login_forget_password)
        loginAlready = findViewById(R.id.login_already)
        view = findViewById(R.id.login_button)

        auth = FirebaseAuth.getInstance()

        //move user to new account
        loginAlready.setOnClickListener {
            val intent = Intent(this@LoginActivity, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }

        //login button image
        view.setOnClickListener {
            loginLoading()
        }

        //hide keyboard
        login_parentLayout.setOnClickListener {
            HideKeyBoard.hideSoftKeyBoard(this@LoginActivity, it)
        }
        login_childLayout.setOnClickListener {
            HideKeyBoard.hideSoftKeyBoard(this@LoginActivity, it)
        }
        //hide keyboard

    }

    private fun loginLoading() {
        val progressButton = ProgressButton(this, view)
        progressButton.buttonActivated()
        Handler().postDelayed({
            progressButton.buttonFinish()
            loginToDetail()
        }, 1000)
    }

    private fun loginToDetail() {
        val username = lUserName.editText?.text.toString()
        val password = lPassword.editText?.text.toString()
        val progressButton = ProgressButton(this, view)
        when {
            TextUtils.isEmpty(username) -> {
                lUserName.error = "Email should not be blank"
                lUserName.requestFocus()
                return
            }
            !Patterns.EMAIL_ADDRESS.matcher(username).matches() -> {
                lUserName.error = "Enter the valid email address"
                lUserName.requestFocus()
                return
            }
            else -> {
                lUserName.error = null
                lUserName.isErrorEnabled = false
            }
        }

        when {
            TextUtils.isEmpty(password) -> {
                lPassword.error = "Password should not be blank"
                lPassword.requestFocus()
                return
            }
            password.length < 6 -> {
                lPassword.error = "Password must contain at least 6 character"
                lPassword.requestFocus()
                return
            }
            else -> {
                lPassword.error = null
                lPassword.isErrorEnabled = false
            }

        }

        auth.signInWithEmailAndPassword(username.trim(), password.trim()).addOnCompleteListener(this, OnCompleteListener {task ->   
            if (task.isSuccessful){
                SnackBarMessage.displaySnack(window.decorView.rootView, "Login successfully")
                val intent = Intent(this, DisplayDetailActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                ToastMessage.displayToast(this, "Error ! " + task.exception?.message)
            }
        })

    }


    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null){
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    //"\\A\\w{4, 20}\\z" -> no white spaces
    //"^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$" -> password validation

}

