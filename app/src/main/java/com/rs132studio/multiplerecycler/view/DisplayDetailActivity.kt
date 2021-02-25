package com.rs132studio.multiplerecycler.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.rs132studio.multiplerecycler.R
import com.rs132studio.multiplerecycler.util.ToastMessage
import kotlinx.android.synthetic.main.activity_display_detail.*

class DisplayDetailActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_detail)
        setSupportActionBar(display_toolBar)
    }




    //hide key board


    //menu options
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_logout -> {
                auth.signOut()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
                return true
            }
            R.id.action_verify_email -> {
                ToastMessage.displayToast(this, "Verification email send to your email account. Please check")
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}