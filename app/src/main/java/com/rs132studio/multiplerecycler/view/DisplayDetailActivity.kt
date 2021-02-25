package com.rs132studio.multiplerecycler.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.hbb20.CountryCodePicker
import com.rs132studio.multiplerecycler.R
import com.rs132studio.multiplerecycler.util.Constant.Companion.CHOOSE_IMG
import com.rs132studio.multiplerecycler.util.HideKeyBoard
import com.rs132studio.multiplerecycler.util.ToastMessage
import kotlinx.android.synthetic.main.activity_display_detail.*
import java.io.IOException

class DisplayDetailActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var customerImg : ImageView
    private lateinit var customerName : EditText
    private lateinit var customerAddress : EditText
    private lateinit var customerPin : EditText
    private lateinit var customerCCP : CountryCodePicker
    private lateinit var customerPhone : EditText
    private lateinit var submitButton : Button
    private lateinit var progressBar: ProgressBar

    private lateinit var uriProfileImage : Uri

    private lateinit var storageReference : StorageReference
    private lateinit var profileImageUrl : String

    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var documentReference: DocumentReference

    override fun onCreate(savedInstanceState: Bundle?) {

        auth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_detail)
        setSupportActionBar(display_toolBar)

        customerImg = findViewById(R.id.customer_img)
        customerName = findViewById(R.id.customer_name)
        customerAddress = findViewById(R.id.customer_address)
        customerPin = findViewById(R.id.customer_pin)
        customerCCP = findViewById(R.id.customer_ccp)
        customerPhone = findViewById(R.id.customer_phonenumber)
        submitButton = findViewById(R.id.submitBtn)
        progressBar = findViewById(R.id.imageUploadProgressBar)

        customerCCP.registerCarrierNumberEditText(customerPhone)


        customerImg.setOnClickListener {
            choseCustomerImage()
        }

        submitButton.setOnClickListener {
            saveUserInformation()
        }


        //hide key board
        parent_scrollView.setOnClickListener {
            HideKeyBoard.hideSoftKeyBoard(this@DisplayDetailActivity, it)
        }
        display_toolBar.setOnClickListener{
            HideKeyBoard.hideSoftKeyBoard(this@DisplayDetailActivity, it)
        }
        //hide key board
    }

    private fun saveUserInformation() {
        val name = customerName.text.toString()
        val address = customerAddress.text.toString()
        val pin = customerPin.text.toString()
        val phone = customerCCP.fullNumberWithPlus.toString()

        when {
            TextUtils.isEmpty(name) -> {
                customerName.error = "Name should not blank"
                customerName.requestFocus()
                return
            } else -> {
                    customerName.error = null
            }
        }

        when {
            TextUtils.isEmpty(address) -> {
                customerAddress.error = "Address should not blank"
                customerAddress.requestFocus()
                return
            }
            address.length < 10 -> {
                customerAddress.error = "Please provide correct address"
                customerAddress.requestFocus()
                return
            }
            else -> {
                customerAddress.error = null
            }
        }

        when {
            TextUtils.isEmpty(pin) -> {
                customerPin.error = "Pin code should not blank"
                customerPin.requestFocus()
                return
            }
            pin.length != 6 -> {
                customerPin.error = "Your pin code is invalid"
                customerPin.requestFocus()
                return
            }
            else -> {
                customerPin.error = null
            }
        }

        when {
            TextUtils.isEmpty(phone) -> {
                customerPhone.error = "Phone number should not blank"
                customerPhone.requestFocus()
                return
            }
            else -> {
                customerPhone.error = null
            }
        }

        val userId : String? = auth.currentUser?.uid

        documentReference = firebaseFirestore.collection("users").document(userId.toString())

        val user : MutableMap<String, Any> = HashMap()
        user["name"] = name
        user["address"] = address
        user["pin"] = pin
        user["phone"] = phone

        documentReference.set(user).addOnSuccessListener {
            ToastMessage.displayToast(this, "User : $name created successfully")
        }.addOnFailureListener {
            ToastMessage.displayToast(this, it.message)
        }
    }

    //choose image from gallery
    private fun choseCustomerImage() {
       val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select profile image"), CHOOSE_IMG)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CHOOSE_IMG && resultCode == RESULT_OK && data != null && data.data != null){
            uriProfileImage = data.data!!
            try {
                customerImg.setImageURI(uriProfileImage)
                uploadImageInFB()

            }catch (e : IOException){
                e.printStackTrace()
            }
        }
    }

    private fun uploadImageInFB() {
        var initialNumber = 1
        var number = initialNumber
        if (number == initialNumber){
            number = initialNumber
        } else {
            initialNumber++
        }
        storageReference = FirebaseStorage.getInstance().getReference("profile_img$number.jpg")
        if (uriProfileImage != null){
            progressBar.visibility = View.VISIBLE
            storageReference.putFile(uriProfileImage)
                .addOnSuccessListener {
                    OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
                        progressBar.visibility = View.GONE
                        profileImageUrl = taskSnapshot.metadata?.reference?.downloadUrl.toString()
                    }
                }
                .addOnFailureListener {
                    OnFailureListener {
                        progressBar.visibility = View.GONE
                        ToastMessage.displayToast(this@DisplayDetailActivity, it.message)
                    }
                }
        }
    }

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
    //menu options
}