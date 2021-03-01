package com.rs132studio.multiplerecycler.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.rs132studio.multiplerecycler.R
import com.rs132studio.multiplerecycler.util.EmailVerification
import com.rs132studio.multiplerecycler.util.ToastMessage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.alert_dialog_logout.view.*
import java.io.IOException

class ProfileActivity : AppCompatActivity() {

    private lateinit var profileImage: ImageView
    private lateinit var profileName: TextView
    private lateinit var profileAddress: TextView
    private lateinit var profilePin: TextView
    private lateinit var profilePhone: TextView
    private lateinit var profileEmail: TextView
    private lateinit var profileLogout: Button
    private lateinit var verifyEmail : TextView
    private lateinit var verifyLogo : ImageView

    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseFireStore : FirebaseFirestore
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var userId : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setSupportActionBar(profile_toolbar)

        auth = FirebaseAuth.getInstance()
        firebaseFireStore = FirebaseFirestore.getInstance()
        profileImage = findViewById(R.id.profile_picture)
        profileName = findViewById(R.id.profile_name)
        profileAddress = findViewById(R.id.profile_address)
        profilePin = findViewById(R.id.profile_pin)
        profilePhone = findViewById(R.id.profile_phone)
        profileEmail = findViewById(R.id.profile_email)
        profileLogout = findViewById(R.id.profile_logout)
        verifyEmail = findViewById(R.id.verify_email)
        verifyLogo = findViewById(R.id.profile_verified)

        userId = auth.currentUser?.uid.toString()
        firebaseUser = auth.currentUser!!

        displayProfileDetail()

        //logout
        profileLogout.setOnClickListener {
            logoutAlert()
        }
        //logout

        //verify email
        if (!firebaseUser.isEmailVerified){
            verifyEmail.text = "Verify Email"
            verifyEmail.setOnClickListener {
                EmailVerification.verifyEmail(this, it)
            }
        } else {
            verifyEmail.visibility = View.GONE
            verifyLogo.visibility = View.VISIBLE
            verifyEmail.setOnClickListener(null)
        }
    }

    private fun logoutAlert() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.alert_dialog_logout, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        builder.setCancelable(false)
        val mBuilder = builder.show()
        dialogView.alertYes.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            mBuilder.dismiss()
        }
        dialogView.alertNo.setOnClickListener {
            mBuilder.dismiss()
        }

        dialogView.cancel_alert.setOnClickListener {
            mBuilder.dismiss()
        }

    }

    private fun displayProfileDetail() {
        val documentReference = firebaseFireStore.collection("users").document(userId)
        documentReference.addSnapshotListener(EventListener{ snapshot, e ->
            try {

                Picasso
                    .get()
                    .load(snapshot?.getString("user_image"))
                    .noPlaceholder()
                    .fit()
                    .into(profileImage)
                profileName.text = snapshot?.getString("name")
                profileAddress.text = snapshot?.getString("address")
                profilePin.text = snapshot?.getString("pin")
                profileEmail.text = auth.currentUser?.email.toString()
                profilePhone.text = snapshot?.getString("phone")

            }catch (e : IOException){
                ToastMessage.displayToast(this, e.message)
                e.printStackTrace()
            }
        })
    }


    //menu options
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_edit -> {
                val intent = Intent(this@ProfileActivity, DisplayDetailActivity::class.java)
                startActivity(intent)
                finish()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    //menu options

}